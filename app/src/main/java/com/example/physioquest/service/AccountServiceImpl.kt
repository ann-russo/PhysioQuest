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
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
                    firebaseUser.email?.let { email ->
                        firebaseUser.displayName?.let { username ->
                            launch {
                                val token = fetchTokenForUser(firebaseUser.uid)
                                trySend(User(firebaseUser.uid, token, email, username))
                            }
                        }
                    } ?: trySend(User())
                }
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    private suspend fun fetchTokenForUser(uid: String): String {
        return withContext(Dispatchers.IO) {
            val userDocRef = firestore.collection(USERS_COLLECTION).document(uid)
            val snapshot = userDocRef.get().await()
            snapshot.getString("token") ?: ""
        }
    }

    override suspend fun authenticate(email: String, password: String): AuthResult {
        val result = CompletableDeferred<AuthResult>()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                    Log.d(TAG, "token: ${tokenTask.result}")
                    if (tokenTask.isSuccessful) {
                        val token = tokenTask.result
                        updateToken(token)
                    } else {
                        Log.d(TAG, "Fetching FCM registration token failed", tokenTask.exception)
                    }
                }

                // Do not check if email is verified for testing purposes
                // Must uncomment for prod (TODO)

                result.complete(AuthResult.Success)

                /*
                val isEmailVerified = auth.currentUser!!.isEmailVerified
                if (isEmailVerified) {
                    // User is signed in and email is verified
                    result.complete(AuthResult.Success)
                } else {
                    // User's email is not verified
                    Log.d("AccountService", "User's email is not verified.")
                    result.complete(AuthResult.Failure(R.string.error_login_unverified))
                }
                */

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
                            Log.w(TAG, "Fetching FCM registration token failed", tokenTask.exception)
                            return@addOnCompleteListener
                        }

                        val token = tokenTask.result
                        val userData = User(
                            id = firebaseUser.uid,
                            token = token,
                            email = firebaseUser.email ?: "",
                            username = firebaseUser.displayName ?: ""
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
                        Log.d(TAG, "Updated nickname in Firestore")
                        SnackbarManager.showMessage(R.string.success_update_nickname)
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "Failed to update nickname in Firestore: ${exception.message}")
                        SnackbarManager.showMessage(R.string.error_update_nickname)
                    }
            } else {
                Log.d(TAG, "Current user is null")
            }
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
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
                Log.d(TAG, "Current user is null")
            }
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            SnackbarManager.showMessage(R.string.error_update_email)
        }
    }

    override suspend fun updatePassword(newPassword: String) {
        try {
            auth.currentUser?.updatePassword(newPassword)?.await()
            SnackbarManager.showMessage(R.string.success_update_pwd)
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
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
                    Log.w(TAG, "Error updating user FCM token.", exception)
                }
        } else {
            Log.w(TAG, "Cannot update token: no user is currently logged in.")
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
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val docRef = firestore.collection(USERS_COLLECTION).document(userId)
            docRef
                .update("token", "")
                .addOnSuccessListener {
                    Log.d(TAG, "User FCM token cleared successfully.")
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error clearing user FCM token.", exception)
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
    }
}

sealed class AuthResult {
    object Success : AuthResult()
    data class Failure(val message: Int) : AuthResult()
    data class Info(val message: Int) : AuthResult()
}