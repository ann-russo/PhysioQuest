package com.example.physioquest.model

import com.google.firebase.firestore.DocumentId

data class Answer(
    @DocumentId val id: String = "",
    val content: String = "",
    val isCorrect: Boolean = false
)