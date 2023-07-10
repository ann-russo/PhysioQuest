package com.example.physioquest.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import kotlinx.serialization.Serializable

@Serializable
data class Answer(
    @DocumentId val id: String = "",
    val content: String = "",
    @get:PropertyName("isCorrect")
    val isCorrect: Boolean = false
)