package com.example.physioquest.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

class Duel(
    @DocumentId val id: String = "",
    val initUser: User,
    val opponentUser: User,
    val startTimestamp: Timestamp,
    val finishTimestamp: Timestamp,
    val randomQuestionsList: List<Question> = listOf(),
    val winnerUser: User
)