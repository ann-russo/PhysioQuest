package com.example.physioquest.service

import com.example.physioquest.model.Question
import kotlinx.coroutines.flow.Flow

interface StorageService {
    val questions: Flow<List<Question>>
    suspend fun getQuestions(questionId: String): Question?
    suspend fun addQuestions(question: Question)
}