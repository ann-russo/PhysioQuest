package com.example.physioquest.model

import com.google.firebase.Timestamp
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Duel(
    var id: String = "",
    var initUser: User = User(),
    var opponentUser: User = User(),
    var initUserFinished: Boolean = false,
    var opponentUserFinished: Boolean = false,
    var initUserResult: QuizResult = QuizResult(),
    var opponentUserResult: QuizResult = QuizResult(),
    @Contextual var startTimestamp: Timestamp = Timestamp.now(),
    @Contextual var finishTimestamp: Timestamp = Timestamp.now(),
    var randomQuestionsList: List<Question> = listOf(),
    var winnerUser: User = User()
)