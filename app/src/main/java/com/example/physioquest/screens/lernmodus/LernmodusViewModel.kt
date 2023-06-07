package com.example.physioquest.screens.lernmodus

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.model.Answer
import com.example.physioquest.model.Question
import com.example.physioquest.model.QuizResult
import com.example.physioquest.screens.PhysioQuestViewModel
import com.example.physioquest.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LernmodusViewModel @Inject constructor(
    private val storageService: StorageService
) : PhysioQuestViewModel() {

    private var questions by mutableStateOf(emptyList<Question>())

    private var _currentQuestionIndex = MutableStateFlow(0)
    private var currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex

    private val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex.value)

    // ----- Selected answers exposed as State -----

    private val _selectedCategory = mutableStateOf<String?>(null)
    private val selectedCategory: String?
        get() = _selectedCategory.value

    private val _selectedAnswers = mutableStateListOf<Int>()
    private val selectedAnswers: List<Int>
        get() = _selectedAnswers

    private var numCorrectAnswers = 0
    private var totalPossiblePoints = 0

    private var _result = MutableStateFlow(0.0) // Change the type to Double
    val result: StateFlow<Double> = _result // Expose the StateFlow as Double

    // ----- Survey status exposed as State -----

    private val _isEvaluateEnabled = mutableStateOf(false)
    val isEvaluateEnabled: Boolean
        get() = _isEvaluateEnabled.value

    private val _isLastQuestion = mutableStateOf(false)
    val isLastQuestion: Boolean
        get() = _isLastQuestion.value

    private val _evaluationStatus = mutableStateListOf<Boolean>()
    val evaluationStatus: List<Boolean> = _evaluationStatus

    private val _surveyScreenData = mutableStateOf(createLernmodusScreenData())
    val surveyScreenData: LernmodusScreenData
        get() = _surveyScreenData.value

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            storageService.questions
                .onStart { _isLoading.value = true }
                .onCompletion { _isLoading.value = false }
                .collect {
                    questions = it
                }
            _selectedAnswers.clear()
            _evaluationStatus.clear()
            _evaluationStatus.addAll(List(questions.size) { false })
        }
    }

    fun getCategories(): List<String> {
        return questions.map { it.category }.distinct()
    }

    fun countQuestionsForCategory(category: String): Int {
        return questions.count { it.category == category }
    }


    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
        loadQuestionsForCategory(category)
        _isEvaluateEnabled.value = true
    }

    private fun loadQuestionsForCategory(category: String) {
        viewModelScope.launch {
            val filteredQuestions = storageService.getQuestionsForCategory(category)
            _isLoading.value = true
            questions = filteredQuestions
            resetQuizState()
            _isLoading.value = false
        }
    }

    private fun resetQuizState() {
        val currentQuestion = currentQuestion
        if (currentQuestion != null) {
            _evaluationStatus.clear()
            _evaluationStatus.addAll(List(currentQuestion.answers.size) { false })
        }
        _selectedAnswers.clear()
        numCorrectAnswers = 0
        changeQuestion(0)
    }

    fun toggleAnswerSelection(answerIndex: Int) {
        if (isEvaluateEnabled) {
            val selectedAnswers = _selectedAnswers.toMutableList()
            if (selectedAnswers.contains(answerIndex)) {
                selectedAnswers.remove(answerIndex)
            } else {
                selectedAnswers.add(answerIndex)
            }
            _evaluationStatus[answerIndex] = false
            _selectedAnswers.clear()
            _selectedAnswers.addAll(selectedAnswers)
        }
    }

    fun evaluateCurrentQuestion() {
        _isEvaluateEnabled.value = false
        val question = currentQuestion ?: return
        val correctAnswers = question.answers.filter { it.isCorrect }
        val selectedCorrectAnswers = selectedAnswers.map { question.answers[it] }
        val isCorrect =
            correctAnswers.size == selectedCorrectAnswers.size &&
                    selectedCorrectAnswers.containsAll(correctAnswers)
        for (i in question.answers.indices) {
            _evaluationStatus[i] =
                i in selectedAnswers &&
                        isCorrect ||
                        i !in selectedAnswers
                        && !isCorrect
        }

        val points = calculateScore(selectedCorrectAnswers, correctAnswers)
        _result.value += points

        /*
        val question = currentQuestion ?: return
        val correctAnswers = question.answers.filter { it.isCorrect }
        val selectedCorrectAnswers = selectedAnswers.map { question.answers[it] }
        val selectedIncorrectAnswers = selectedAnswers.map { question.answers[it] }
            .filterNot { it.isCorrect }

        val isCorrect =
            correctAnswers.size == selectedCorrectAnswers.size &&
                    selectedCorrectAnswers.containsAll(correctAnswers)

        val numSelectedCorrectAnswers = selectedCorrectAnswers.size
        val numCorrect = correctAnswers.size
        val numIncorrect = selectedIncorrectAnswers.size

        val score = when {
            isCorrect -> 1.0 // All correct answers selected
            numSelectedCorrectAnswers > 0 -> numSelectedCorrectAnswers.toDouble() / numCorrect // Partial points based on correct answers selected
            else -> 0.0 // No correct answers selected
        }

        for (i in question.answers.indices) {
            _evaluationStatus[i] =
                i in selectedAnswers &&
                        isCorrect ||
                        i !in selectedAnswers
                        && !isCorrect
        }
        if (isCorrect) {
            numCorrectAnswers++
        }
        _result.value += score

         */
    }

    private fun calculateScore(selectedAnswers: List<Answer>, correctAnswers: List<Answer>): Double {
        val totalPoints = 1.0 // Total points assigned to the question
        val numAnswerChoices = 4 // Total number of answer choices

        val numCorrectSelected = selectedAnswers.count { it.isCorrect }
        val numIncorrectSelected = selectedAnswers.count { !it.isCorrect }

        val pointsPerCorrectAnswer = totalPoints / correctAnswers.size.toDouble()
        val pointsPerIncorrectAnswer = totalPoints / (numAnswerChoices - correctAnswers.size).toDouble()

        val score = (numCorrectSelected * pointsPerCorrectAnswer) - (numIncorrectSelected * pointsPerIncorrectAnswer)

        return if (score >= 0) score else 0.0
    }

    fun onClosePressed(openScreen: (String) -> Unit) {
        launchCatching {
            openScreen(HOME_SCREEN)
        }
    }

    fun onNextClicked() {
        _selectedAnswers.clear()
        _isEvaluateEnabled.value = true
        changeQuestion(currentQuestionIndex.value + 1)
    }

    fun getQuizResult(): QuizResult {
        val quizResult = QuizResult()
        quizResult.scorePoints = _result.value
        quizResult.scorePercent = (_result.value / questions.size) * 100
        Log.d("getQuizResult", "result points: ${quizResult.scorePoints}")
        Log.d("getQuizResult", "result percent: ${quizResult.scorePercent}")
        return quizResult
    }

    private fun changeQuestion(newQuestionIndex: Int) {
        _currentQuestionIndex.value = newQuestionIndex
        _surveyScreenData.value = createLernmodusScreenData()
        _isLastQuestion.value = _currentQuestionIndex.value == questions.size - 1
    }

    private fun createLernmodusScreenData(): LernmodusScreenData {
        return LernmodusScreenData(
            questionIndex = _currentQuestionIndex.value,
            questionCount = questions.size,
            selectedAnswers = selectedAnswers,
            isEvaluationEnabled = _isEvaluateEnabled.value,
            quizQuestion = currentQuestion,
            selectedCategory = selectedCategory
        )
    }
}

data class LernmodusScreenData(
    val questionIndex: Int,
    val questionCount: Int,
    val selectedAnswers: List<Int>,
    val isEvaluationEnabled: Boolean,
    val quizQuestion: Question?,
    val selectedCategory: String?
)