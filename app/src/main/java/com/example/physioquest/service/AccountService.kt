package com.example.physioquest.service

import com.example.physioquest.model.User
import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentUserId: String
    val hasUser: Boolean
    val currentUser: Flow<User>

    suspend fun authenticate(email: String, password: String)
    suspend fun createAccount(nickname: String, email: String, password: String)
    suspend fun deleteAccount()
    suspend fun signOut()
}