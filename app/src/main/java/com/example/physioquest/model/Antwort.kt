package com.example.physioquest.model

import com.google.firebase.firestore.DocumentId

data class Antwort(
    @DocumentId val id: String = "",
    val antwortInhalt: String = "",
    val antwortKorrekt: Boolean = false
)