package com.example.physioquest.service

import android.util.Log
import com.example.physioquest.R
import com.example.physioquest.common.snackbar.SnackbarManager
import com.example.physioquest.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AccountService {
    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val hasUser: Boolean
        get() = auth.currentUser != null

    override val currentUser: Flow<User>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                this.trySend(auth.currentUser?.let {
                    it.email?.let { it1 -> it.displayName?.let { it2 -> User(it.uid, it1, it2) } }
                } ?: User())
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    override suspend fun authenticate(email: String, password: String): AuthResult {
        val result = CompletableDeferred<AuthResult>()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val isEmailVerified = auth.currentUser!!.isEmailVerified
                if (isEmailVerified) {
                    // User is signed in and email is verified
                    result.complete(AuthResult.Success)
                } else {
                    // User's email is not verified
                    Log.d("AccountService", "User's email is not verified.")
                    result.complete(AuthResult.Failure(R.string.error_login_unverified))
                }
            } else {
                // Sign in failed
                Log.d("AccountService", "Sign in failed.")
                result.complete(AuthResult.Failure(R.string.error_login_wrong_email_pwd))
            }
        }
        return result.await()
    }

    override suspend fun reAuthenticate(email: String, password: String): Boolean {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val credential = EmailAuthProvider.getCredential(email, password)
            return try {
                currentUser.reauthenticate(credential)
                true
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                false
            }
        }
        return false
    }

    override suspend fun createAccount(nickname: String, email: String, password: String): AuthResult {
        val result = CompletableDeferred<AuthResult>()

        auth.createUserWithEmailAndPassword(email, password).await().user?.let { firebaseUser ->
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(nickname).build()
            firebaseUser.updateProfile(profileUpdates).await()

            firebaseUser.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AccountService", "Verification email sent.")
                    result.complete(AuthResult.Info(R.string.verify_email))
                    val userData = User(
                        id = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        username = firebaseUser.displayName ?: ""
                    )
                    firestore.collection(USERS_COLLECTION)
                        .document(firebaseUser.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            Log.d("AccountService", "Added user ${userData.id} to Firestore")
                        }
                        .addOnFailureListener { exception ->
                            Log.d("AccountService", exception.toString())
                        }
                }
                else {
                    // Failed to send verification email
                    Log.d("AccountService", "Failed to send verification email.")
                    result.complete(AuthResult.Failure(R.string.error_generic))
                }
            }
        }
        return result.await()
    }

    override suspend fun updateNickname(newNickname: String) {
        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val profileUpdates = userProfileChangeRequest { displayName = newNickname }
                currentUser.updateProfile(profileUpdates).await()

                val userDocRef = firestore.collection(USERS_COLLECTION).document(currentUser.uid)
                userDocRef.update("username", newNickname)
                    .addOnSuccessListener {
                        Log.d("AccountService", "Updated nickname in Firestore")
                        SnackbarManager.showMessage(R.string.success_update_nickname)
                    }
                    .addOnFailureListener { exception ->
                        Log.d("AccountService", "Failed to update nickname in Firestore: ${exception.message}")
                        SnackbarManager.showMessage(R.string.error_update_nickname)
                    }
            } else {
                Log.d("AccountService", "Current user is null")
            }
        } catch (e: Exception) {
            Log.d("updateNickname exception", e.toString())
            SnackbarManager.showMessage(R.string.error_update_nickname)
        }
    }

    override suspend fun updateEmail(newEmail: String) {
        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                currentUser.verifyBeforeUpdateEmail(newEmail).await()
                val userDocRef = firestore.collection(USERS_COLLECTION).document(currentUser.uid)
                userDocRef.update("email", newEmail)
                    .addOnSuccessListener {
                        Log.d("AccountService", "Updated email in Firestore")
                        SnackbarManager.showMessage(R.string.success_update_email)
                    }
                    .addOnFailureListener { exception ->
                        Log.d("AccountService", "Failed to update email in Firestore: ${exception.message}")
                        SnackbarManager.showMessage(R.string.error_update_email)
                    }
            } else {
                Log.d("AccountService", "Current user is null")
            }
        } catch (e: Exception) {
            Log.d("updateEmail exception", e.toString())
            SnackbarManager.showMessage(R.string.error_update_email)
        }
    }

    override suspend fun updatePassword(newPassword: String) {
        try {
            auth.currentUser?.updatePassword(newPassword)?.await()
            SnackbarManager.showMessage(R.string.success_update_pwd)
        } catch (e: Exception) {
            Log.d("updatePassword exception", e.toString())
            SnackbarManager.showMessage(R.string.error_update_pwd)
        }
    }

    override suspend fun deleteAccount() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userDocRef = firestore.collection(USERS_COLLECTION).document(currentUser.uid)
            try {
                userDocRef.delete().await()
                currentUser.delete().await()
                auth.signOut()
            } catch (e: Exception) {
                Log.d("deleteAccount exception", e.toString())
                SnackbarManager.showMessage(R.string.error_account_delete)
            }
        } else {
            Log.d("deleteAccount", "Current user is null")
            SnackbarManager.showMessage(R.string.error_account_delete)
        }
    }


    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun resetPassword(email: String) {
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    SnackbarManager.showMessage(R.string.reset_password)
                }
                else {
                    SnackbarManager.showMessage(R.string.reset_password_error)
                }
            }
    }

    companion object {
        private const val USERS_COLLECTION = "users"
    }
}

sealed class AuthResult {
    object Success : AuthResult()
    data class Failure(val message: Int) : AuthResult()
    data class Info(val message: Int) : AuthResult()
}