package com.example.physioquest.model

import com.google.firebase.firestore.DocumentId

data class Frage(
    @DocumentId val id: String = "",
    val kategorie: String = "",
    val frageInhalt: String = "",
    val antworten: List<Antwort> = listOf()
)