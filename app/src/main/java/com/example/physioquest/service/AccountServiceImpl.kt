package com.example.physioquest.service

import com.example.physioquest.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
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

    override suspend fun createAccount(nickname: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await().user?.let { firebaseUser ->
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(nickname).build()
            firebaseUser.updateProfile(profileUpdates).await()
        }
    }

    override suspend fun deleteAccount() {
        auth.currentUser?.delete()
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}