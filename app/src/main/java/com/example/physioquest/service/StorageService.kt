package com.example.physioquest.service

import com.example.physioquest.model.Question
import com.example.physioquest.model.User
import kotlinx.coroutines.flow.Flow

interface StorageService {
    val questions: Flow<List<Question>>
    suspend fun getQuestions(questionId: String): Question?
    suspend fun getQuestionsForCategory(category: String): List<Question>
    suspend fun getUsersFromDatabase(): List<User>
}