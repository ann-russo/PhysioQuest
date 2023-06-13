package com.example.physioquest.screens.quiz.duellmodus

import androidx.compose.runtime.mutableStateOf
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.model.Duel
import com.example.physioquest.model.Question
import com.example.physioquest.model.QuizResult
import com.example.physioquest.model.User
import com.example.physioquest.screens.PhysioQuestViewModel
import com.example.physioquest.screens.quiz.shared.QuizRepository
import com.example.physioquest.service.AccountService
import com.example.physioquest.service.StorageService
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DuellmodusViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService,
    private val quizRepository: QuizRepository
) : PhysioQuestViewModel() {

    private val questions: List<Question>
        get() = quizRepository.questions
    private val currentQuestionIndex: StateFlow<Int>
        get() = quizRepository.currentQuestionIndex
    private val currentQuestion: Question?
        get() = quizRepository.currentQuestion
    private var selectedAnswers: MutableList<Int>
        get() = quizRepository.selectedAnswers
        set(value) {
            quizRepository.selectedAnswers.clear()
            quizRepository.selectedAnswers.addAll(value)
        }
    var isEvaluateEnabled: Boolean
        get() = quizRepository.isEvaluateEnabled
        set(value) {
            quizRepository.isEvaluateEnabled = value
        }
    var isLastQuestion: Boolean
        get() = quizRepository.isLastQuestion
        set(value) {
            quizRepository.isLastQuestion = value
        }
    val result: MutableStateFlow<Double>
        get() = quizRepository.result

    private var currentDuel: Duel? = null
    private val currentDestination = mutableStateOf(DuellmodusDestination.FIND_OPPONENT)
    private var _surveyScreenData = mutableStateOf(createDuellmodusScreenData())
    val surveyScreenData: DuellmodusScreenData
        get() = _surveyScreenData.value

    private val _user = mutableStateOf<User?>(null)
    val user: User?
        get() = _user.value

    private val _opponent = mutableStateOf<User?>(null)
    val opponent: User?
        get() = _opponent.value

    init {
        launchCatching {
            accountService.currentUser.collect { user ->
                _user.value = user
            }
        }
        selectRandomOpponent()
        selectRandomQuestions()
    }

    fun onClosePressed(openScreen: (String) -> Unit) {
        launchCatching {
            openScreen(HOME_SCREEN)
        }
    }

    private fun selectRandomOpponent() {
        launchCatching {
            val currentUserId = accountService.currentUserId
            _opponent.value = storageService.getRandomUserFromDatabase(currentUserId)
        }
    }

    private fun selectRandomQuestions() {
        launchCatching {
            quizRepository.getRandomQuestions()
        }
    }

    fun startDuel() {
        val currentUser = user ?: return
        val opponentUser = opponent ?: return
        val startTimestamp = Timestamp.now()

        currentDuel = Duel(
            initUser = currentUser,
            opponentUser = opponentUser,
            initUserFinished = false,
            opponentUserFinished = false,
            startTimestamp = startTimestamp,
            finishTimestamp = startTimestamp,
            randomQuestionsList = questions,
            winnerUser = User()
        )
        currentDestination.value = DuellmodusDestination.QUESTIONS
        resetQuizState()
    }

    fun toggleAnswerSelection(answerIndex: Int) {
        quizRepository.toggleAnswerSelection(answerIndex)
    }

    fun evaluateCurrentQuestion() {
        quizRepository.evaluateCurrentQuestion()
    }

    fun onNextClicked() {
        quizRepository.onNextClicked()
        updateScreenData()
    }

    fun getQuizResult(): QuizResult {
        return quizRepository.getQuizResult()
    }

    private fun resetQuizState() {
        quizRepository.resetQuizState()
        updateScreenData()
    }

    private fun createDuellmodusScreenData(): DuellmodusScreenData {
        return DuellmodusScreenData(
            destination = currentDestination.value,
            duel = currentDuel,
            questionIndex = currentQuestionIndex.value,
            questionCount = questions.size,
            selectedAnswers = selectedAnswers,
            isEvaluationEnabled = isEvaluateEnabled,
            quizQuestion = currentQuestion,
        )
    }

    private fun updateScreenData() {
        _surveyScreenData.value = createDuellmodusScreenData()
    }
}

enum class DuellmodusDestination {
    FIND_OPPONENT,
    QUESTIONS,
    WAITING,
    RESULT
}

data class DuellmodusScreenData(
    var destination: DuellmodusDestination?,
    var duel: Duel?,
    val questionIndex: Int,
    val questionCount: Int,
    var selectedAnswers: List<Int>,
    var isEvaluationEnabled: Boolean,
    val quizQuestion: Question?,
)