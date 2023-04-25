package com.example.physioquest.model

data class Frage(
    val frageText: String,
    val antwortOptionen: List<Antwort>
)