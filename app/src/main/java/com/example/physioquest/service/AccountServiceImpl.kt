package com.example.physioquest.service

import android.util.Log
import com.example.physioquest.R
import com.example.physioquest.common.snackbar.SnackbarManager
import com.example.physioquest.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(private val auth: FirebaseAuth) : AccountService {
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

    override suspend fun authenticate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
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

    override suspend fun createAccount(nickname: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await().user?.let { firebaseUser ->
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(nickname).build()
            firebaseUser.updateProfile(profileUpdates).await()
        }
    }

    override suspend fun updateNickname(newNickname: String) {
        val profileUpdates = userProfileChangeRequest { displayName = newNickname }
        try {
            auth.currentUser?.updateProfile(profileUpdates)?.await()
            SnackbarManager.showMessage(R.string.success_update_nickname)
        } catch (e: Exception) {
            Log.d("updateNickname exception", e.toString())
            SnackbarManager.showMessage(R.string.error_update_nickname)
        }
    }

    override suspend fun updateEmail(newEmail: String) {
        try {
            auth.currentUser?.verifyBeforeUpdateEmail(newEmail)?.await()
            SnackbarManager.showMessage(R.string.success_update_email)
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
        auth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                auth.signOut()
            } else {
                val exception = task.exception
                Log.d("deleteAccount exception", exception.toString())
                SnackbarManager.showMessage(R.string.error_account_delete)
            }
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}