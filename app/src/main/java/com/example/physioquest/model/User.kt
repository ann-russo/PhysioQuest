package com.example.physioquest.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val token: String = "",
    val email: String = "",
    val username: String = "",
    var level: Int = 1,
    var xp: Int = 0
)
