package com.example.physioquest.model

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: String = "",
    val category: String = "",
    val content: String = "",
    var answers: List<Answer> = listOf()
)