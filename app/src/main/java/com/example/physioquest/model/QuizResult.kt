package com.example.physioquest.model

import kotlinx.serialization.Serializable

@Serializable
data class QuizResult(
    var scorePoints: Double = 0.0,
    var scorePercent: Double = 0.0,
    var totalPoints: Int = 0,
    var category: String? = "",
    var userId: String? = ""
)