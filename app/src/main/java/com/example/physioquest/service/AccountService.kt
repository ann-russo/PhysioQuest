package com.example.physioquest.service

import com.example.physioquest.model.User
import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentUserId: String
    val hasUser: Boolean
    val currentUser: Flow<User>

    suspend fun authenticate(email: String, password: String): AuthResult
    suspend fun reAuthenticate(email: String, password: String): Boolean
    suspend fun createAccount(nickname: String, email: String, password: String): AuthResult
    suspend fun updateNickname(newNickname: String)
    suspend fun updateEmail(newEmail: String)
    suspend fun updatePassword(newPassword: String)
    fun updateToken(token: String)
    suspend fun deleteAccount()
    suspend fun signOut()
    suspend fun resetPassword(email: String)
}