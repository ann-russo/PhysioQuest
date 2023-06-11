package com.example.physioquest.screens.quiz

import com.example.physioquest.model.Answer
import com.example.physioquest.model.Question
import com.example.physioquest.model.QuizResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface QuizRepository {
    var questions: List<Question>
    var currentQuestionIndex: StateFlow<Int>
    var currentQuestion: Question?
    var selectedAnswers: MutableList<Int>
    var evaluationStatus: MutableList<Boolean>
    var isEvaluateEnabled: Boolean
    var isLastQuestion: Boolean
    var isLoading: Boolean
    var result: MutableStateFlow<Double>

    suspend fun getRandomQuestions(): List<Question>
    fun resetQuizState()
    fun toggleAnswerSelection(answerIndex: Int)
    fun evaluateCurrentQuestion()
    fun calculateScore(selectedAnswers: List<Answer>, correctAnswers: List<Answer>): Double
    fun onNextClicked()
    fun changeQuestion(newQuestionIndex: Int)
    fun getQuizResult(): QuizResult
}