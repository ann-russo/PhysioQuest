package com.example.physioquest.service

import com.example.physioquest.model.Question
import com.example.physioquest.model.User

interface StorageService {
    suspend fun getQuestions(): List<Question>
    suspend fun getQuestionsForCategory(category: String): List<Question>
    suspend fun getRandomQuestions(): List<Question>
    suspend fun getUsersFromDatabase(): List<User>
    suspend fun getRandomUserFromDatabase(currentUserId: String): User
}