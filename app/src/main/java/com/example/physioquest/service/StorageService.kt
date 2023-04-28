package com.example.physioquest.service

import com.example.physioquest.model.Frage
import kotlinx.coroutines.flow.Flow

interface StorageService {
    val fragen: Flow<List<Frage>>
    suspend fun getFrage(frageId: String): Frage?
    suspend fun addFrage(frage: Frage)
}