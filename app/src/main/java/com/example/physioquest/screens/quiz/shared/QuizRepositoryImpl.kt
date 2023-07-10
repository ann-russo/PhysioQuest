package com.example.physioquest.screens.quiz.shared

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.physioquest.model.Answer
import com.example.physioquest.model.Duel
import com.example.physioquest.model.Question
import com.example.physioquest.model.QuizResult
import com.example.physioquest.service.LevelService
import com.example.physioquest.service.StorageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val storageService: StorageService,
    private val levelService: LevelService
    ): QuizRepository {

    override var questions: List<Question> by mutableStateOf(emptyList())
    override var currentQuestionIndex: StateFlow<Int> = MutableStateFlow(0)
    override var currentQuestion: Question? = questions.getOrNull(0)
    override var selectedAnswers: MutableList<Int> = mutableStateListOf()
        set(value) {
            field.clear()
            field.addAll(value)
        }
    override var evaluationStatus: MutableList<Boolean> = mutableStateListOf()
    override var isEvaluateEnabled: Boolean by mutableStateOf(true)
    override var isLastQuestion: Boolean by mutableStateOf(false)
    override var isLoading: Boolean by mutableStateOf(true)
    override var result: MutableStateFlow<Double> = MutableStateFlow(0.0)
    override var xpPoints: MutableStateFlow<Int> = MutableStateFlow(0)

    override suspend fun getRandomQuestions(): List<Question> {
        questions = storageService.getRandomQuestions()
        selectedAnswers.clear()
        evaluationStatus.addAll(List(questions.size) { false })
        isLoading = false

        return questions
    }

    override suspend fun findUnfinishedDuel(currentUserId: String): Duel? {
        val unfinishedDuel = storageService.findUnfinishedDuelByUserId(currentUserId)

        return if (unfinishedDuel != null) {
            questions = unfinishedDuel.randomQuestionsList
            selectedAnswers.clear()
            evaluationStatus.addAll(List(questions.size) { false })
            isLoading = false
            unfinishedDuel
        } else {
            null
        }
    }

    override fun resetQuizState() {
        val currentQuestion = currentQuestion
        if (currentQuestion != null) {
            evaluationStatus.clear()
            evaluationStatus.addAll(List(currentQuestion.answers.size) { false })
        }
        selectedAnswers.clear()
        changeQuestion(0)
    }

    override fun changeQuestion(newQuestionIndex: Int) {
        currentQuestionIndex = MutableStateFlow(newQuestionIndex)
        currentQuestion = questions.getOrNull(newQuestionIndex)
        isLastQuestion = currentQuestionIndex.value == questions.size - 1
    }

    override fun toggleAnswerSelection(answerIndex: Int) {
        if (isEvaluateEnabled) {
            val updatedSelectedAnswers = selectedAnswers.toMutableList()
            if (updatedSelectedAnswers.contains(answerIndex)) {
                updatedSelectedAnswers.remove(answerIndex)
            } else {
                updatedSelectedAnswers.add(answerIndex)
            }
            evaluationStatus[answerIndex] = false
            selectedAnswers = updatedSelectedAnswers
        }
    }

    override fun evaluateCurrentQuestion() {
        isEvaluateEnabled = false
        val question = currentQuestion ?: return
        val correctAnswers = question.answers.filter { it.isCorrect }
        val selectedCorrectAnswers = selectedAnswers.map { question.answers[it] }
        val isCorrect =
            correctAnswers.size == selectedCorrectAnswers.size &&
                    selectedCorrectAnswers.containsAll(correctAnswers)
        for (i in question.answers.indices) {
            evaluationStatus[i] =
                i in selectedAnswers &&
                        isCorrect ||
                        i !in selectedAnswers
                        && !isCorrect
        }
        val points = calculateScore(selectedCorrectAnswers, correctAnswers)
        if (points == 1.0) {
            xpPoints.value += 10
        } else if (points > 0.0 && points < 1.0) {
            xpPoints.value += 5
        }
        result.value += points
    }

    override fun calculateScore(
        selectedAnswers: List<Answer>,
        correctAnswers: List<Answer>
    ): Double {
        val totalPoints = 1.0
        val numAnswerChoices = 4

        val numCorrectSelected = selectedAnswers.count { it.isCorrect }
        val numIncorrectSelected = selectedAnswers.count { !it.isCorrect }

        val pointsPerCorrectAnswer = totalPoints / correctAnswers.size.toDouble()
        val pointsPerIncorrectAnswer =
            totalPoints / (numAnswerChoices - correctAnswers.size).toDouble()

        val score =
            (numCorrectSelected * pointsPerCorrectAnswer) - (numIncorrectSelected * pointsPerIncorrectAnswer)

        return if (score >= 0) score else 0.0
    }

    override fun onNextClicked() {
        selectedAnswers.clear()
        isEvaluateEnabled = true
        changeQuestion(currentQuestionIndex.value + 1)
    }

    override fun getQuizResult(): QuizResult {
        val quizResult = QuizResult()
        quizResult.scorePoints = result.value
        quizResult.scorePercent = (result.value / questions.size) * 100
        quizResult.totalPoints = questions.size

        var xpOnComplete = 30
        if (quizResult.scorePoints == quizResult.totalPoints.toDouble()) {
            xpOnComplete += 50
        }
        xpPoints.value += xpOnComplete

        return quizResult
    }
}