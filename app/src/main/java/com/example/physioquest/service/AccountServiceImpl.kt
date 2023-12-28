package com.example.physioquest.service

import android.util.Log
import com.example.physioquest.R
import com.example.physioquest.common.snackbar.SnackbarManager
import com.example.physioquest.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
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
                auth.currentUser?.let { firebaseUser ->
                    launch {
                        val user = getUserData(firebaseUser.uid)
                        trySend(user)
                    }
                } ?: trySend(User())
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    private suspend fun getUserData(userId: String): User {
        val docRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        return try {
            val snapshot = docRef.get().await()
            snapshot.toObject(User::class.java) ?: User()
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            User()
        }
    }

    override suspend fun authenticate(email: String, password: String): AuthResult {
        val result = CompletableDeferred<AuthResult>()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                    if (tokenTask.isSuccessful) {
                        val token = tokenTask.result
                        updateToken(token)
                    } else {
                        Log.d(TAG, "Fetching FCM registration token failed", tokenTask.exception)
                    }
                }

                val isEmailVerified = auth.currentUser!!.isEmailVerified
                if (isEmailVerified) {
                    // User is signed in and email is verified
                    result.complete(AuthResult.Success)
                } else {
                    // User's email is not verified
                    Log.d(TAG, "User's email is not verified.")
                    result.complete(AuthResult.Failure(R.string.error_login_unverified))
                }

            } else {
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
                    Log.d(TAG, "Verification email sent")
                    result.complete(AuthResult.Info(R.string.verify_email))

                    FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                        if (!tokenTask.isSuccessful) {
                            Log.d(TAG, "Fetching FCM registration token failed", tokenTask.exception)
                            return@addOnCompleteListener
                        }

                        val token = tokenTask.result
                        val userData = User(
                            id = firebaseUser.uid,
                            token = token,
                            email = firebaseUser.email ?: "",
                            username = firebaseUser.displayName ?: "",
                            level = 1,
                            xp = 0
                        )
                        firestore.collection(USERS_COLLECTION)
                            .document(firebaseUser.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                Log.d(TAG, "Added user ${userData.id} to Firestore")
                            }
                            .addOnFailureListener { exception ->
                                Log.d(TAG, exception.toString())
                            }
                    }
                }
                else {
                    Log.d(TAG, "Failed to send verification email.")
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
                        SnackbarManager.showMessage(R.string.success_update_nickname)
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "Failed to update nickname in Firestore: ${exception.message}")
                        SnackbarManager.showMessage(R.string.error_update_nickname)
                    }
            } else {
                Log.d(TAG, USER_NULL)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
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
                        Log.d(TAG, "Updated email in Firestore")
                        SnackbarManager.showMessage(R.string.success_update_email)
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "Failed to update email in Firestore: ${exception.message}")
                        SnackbarManager.showMessage(R.string.error_update_email)
                    }
            } else {
                Log.d(TAG, USER_NULL)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            SnackbarManager.showMessage(R.string.error_update_email)
        }
    }

    override suspend fun updatePassword(newPassword: String) {
        try {
            auth.currentUser?.updatePassword(newPassword)?.await()
            SnackbarManager.showMessage(R.string.success_update_pwd)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            SnackbarManager.showMessage(R.string.error_update_pwd)
        }
    }

    override fun updateToken(token: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val docRef = firestore.collection(USERS_COLLECTION).document(userId)
            docRef
                .update("token", token)
                .addOnSuccessListener {
                    Log.d(TAG, "User FCM token updated successfully.")
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error updating user FCM token.", exception)
                }
        } else {
            Log.d(TAG, "Cannot update token: no user is currently logged in.")
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
            Log.d(DELETE_ACCOUNT_TAG, USER_NULL)
            SnackbarManager.showMessage(R.string.error_account_delete)
        }
    }

    override suspend fun signOut() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val docRef = firestore.collection(USERS_COLLECTION).document(userId)
            docRef
                .update("token", "")
                .addOnSuccessListener {
                    Log.d(TAG, "User FCM token cleared successfully.")
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error clearing user FCM token.", exception)
                }
        }
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
        private const val TAG = "AccountService"
        private const val USERS_COLLECTION = "users"
        private const val USER_NULL = "Current user is null"
        private const val DELETE_ACCOUNT_TAG = "deleteAccount()"
    }
}

sealed class AuthResult {
    object Success : AuthResult()
    data class Failure(val message: Int) : AuthResult()
    data class Info(val message: Int) : AuthResult()
}