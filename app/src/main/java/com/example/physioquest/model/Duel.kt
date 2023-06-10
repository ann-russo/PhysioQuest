package com.example.physioquest.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

class Duel(
    @DocumentId val id: String = "",
    var initUser: User,
    var opponentUser: User,
    var initUserFinished: Boolean,
    var opponentUserFinished: Boolean,
    var startTimestamp: Timestamp,
    var finishTimestamp: Timestamp,
    var randomQuestionsList: List<Question> = listOf(),
    var winnerUser: User
)