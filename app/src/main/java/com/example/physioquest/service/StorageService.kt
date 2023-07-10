package com.example.physioquest.service

import com.example.physioquest.model.Duel
import com.example.physioquest.model.Question
import com.example.physioquest.model.User
import kotlinx.coroutines.flow.Flow

interface StorageService {
    suspend fun getQuestions(): List<Question>
    suspend fun getQuestionsForCategory(category: String): List<Question>
    suspend fun getRandomQuestions(): List<Question>
    suspend fun getUsersFromDatabase(): List<User>
    suspend fun getRandomUserFromDatabase(currentUserId: String): User
    suspend fun getDuel(duelId: String): Flow<Duel>
    suspend fun saveDuel(duel: Duel)
    suspend fun updateDuel(duel: Duel)
    suspend fun getUnfinishedDuels(): List<Duel>
    suspend fun findUnfinishedDuelByOpponentUserId(userId: String): Duel?
    suspend fun findUnfinishedDuelByUserId(userId: String): Duel?
    fun listenForDuelFinish(duelId: String): Flow<Duel>
}