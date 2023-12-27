package com.example.physioquest.screens.quiz.shared

data class QuizActions(
    val isEvaluationEnabled: Boolean,
    val onEvaluateClicked: () -> Unit,
    val onNextClicked: () -> Unit,
    val isLastQuestion: Boolean,
    val onQuizComplete: () -> Unit
)