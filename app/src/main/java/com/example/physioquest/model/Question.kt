package com.example.physioquest.model

import com.google.firebase.firestore.DocumentId

data class Question(
    @DocumentId val id: String = "",
    val category: String = "",
    val content: String = "",
    val answers: List<Answer> = listOf()
)