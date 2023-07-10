package com.example.physioquest.screens.quiz.duellmodus

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.common.event.Event
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


    var currentDuel: Duel = Duel()
    private val currentDestination = MutableLiveData<DuellmodusDestination>()
    private var _surveyScreenData = mutableStateOf(createDuellmodusScreenData())
    val surveyScreenData: DuellmodusScreenData
        get() = _surveyScreenData.value

    private val _currentUser = mutableStateOf(User())
    private val currentUser: User
        get() = _currentUser.value

    private val _duelOpponentUser = mutableStateOf(User())
    val duelOpponentUser: User
        get() = _duelOpponentUser.value

    private val _duelInitUser = mutableStateOf(User())
    private val duelInitUser: User
        get() = _duelInitUser.value

    private val _navigateToResultsScreen = MutableLiveData<Event<Duel>>()
    val navigateToResultsScreen : LiveData<Event<Duel>> = _navigateToResultsScreen

    init {
        launchCatching {
            currentDestination.value = DuellmodusDestination.LOADING
            updateScreenData()

            accountService.currentUser.collect { user ->
                _currentUser.value = user
                checkDuelStatus(user.id)
            }
        }
    }

    private fun checkDuelStatus(userId: String) {
        launchCatching {
            val duel = quizRepository.findUnfinishedDuel(userId)
            if (duel != null) {
                when {
                    duel.initUser.id == userId && duel.initUserFinished -> {
                        // Case 1: Current user initiated the duel and finished. Wait for result.
                        currentDestination.value = DuellmodusDestination.WAIT_FOR_RESULT
                        updateUsersAndDuel(duel)
                        observeResult(duel.id)
                        updateScreenData()
                    }
                    duel.opponentUser.id == userId && duel.opponentUserFinished -> {
                        // Case 2: Current user is the opponent in the duel and finished. Wait for result.
                        currentDestination.value = DuellmodusDestination.WAIT_FOR_RESULT
                        updateUsersAndDuel(duel)
                        observeResult(duel.id)
                        updateScreenData()
                    }

                    duel.opponentUser.id == userId && !duel.opponentUserFinished -> {
                        // Case 3: Current user was selected as opponent and hasn't finished. Show unfinished duel.
                        currentDestination.value = DuellmodusDestination.UNFINISHED_DUEL
                        updateUsersAndDuel(duel)
                        updateScreenData()
                    }
                    duel.initUserFinished && duel.opponentUserFinished -> {
                        // Case 4: Both users have finished. Show result.
                        currentDestination.value = DuellmodusDestination.RESULT
                        updateUsersAndDuel(duel)
                        updateScreenData()
                    }
                }
            } else {
                Log.d(TAG, "No unfinished duels!")
                // Case 5: User is neither initUser nor opponentUser in any unfinished duels. Start new duel.
                currentDestination.value = DuellmodusDestination.NEW_DUEL
                selectRandomOpponent()
                selectRandomQuestions()
                updateScreenData()
            }
        }
    }

    private fun observeResult(duelId: String) {
        launchCatching {
            storageService.listenForDuelFinish(duelId).collect { updatedDuel ->
                if (updatedDuel.opponentUserFinished && updatedDuel.initUserFinished) {
                    _navigateToResultsScreen.value = Event(updatedDuel)
                }
            }
        }
    }

    private fun updateUsersAndDuel(duel: Duel) {
        _duelInitUser.value = duel.initUser
        _duelOpponentUser.value = duel.opponentUser
        currentDuel = duel
    }

    fun getOpponentName(): String {
        if (currentUser.id == duelInitUser.id) {
            return duelOpponentUser.username
        }
        if (currentUser.id == duelOpponentUser.id) {
            return duelInitUser.username
        }
        return "username"
    }

    fun onClosePressed(openScreen: (String) -> Unit) {
        launchCatching {
            openScreen(HOME_SCREEN)
        }
    }

    private fun selectRandomOpponent() {
        launchCatching {
            _duelOpponentUser.value = storageService.getRandomUserFromDatabase(currentUser.id)
            Log.d(TAG, "Selected opponent: ${duelOpponentUser.username}")
        }
    }

    private fun selectRandomQuestions() {
        launchCatching {
            quizRepository.getRandomQuestions()
        }
    }

    fun startUnfinishedDuel() {
        currentDestination.value = DuellmodusDestination.QUESTIONS
        resetQuizState()
    }

    fun startNewDuel() {
        val startTimestamp = Timestamp.now()
        val duel = Duel(
            initUser = currentUser,
            opponentUser = duelOpponentUser,
            initUserFinished = false,
            opponentUserFinished = false,
            initUserResult = QuizResult(),
            opponentUserResult = QuizResult(),
            startTimestamp = startTimestamp,
            finishTimestamp = startTimestamp,
            randomQuestionsList = questions,
            winnerUser = User()
        )

        launchCatching {
            storageService.saveDuel(duel)
        }

        currentDuel = duel
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

    fun onFinishClicked() {
        val result = getQuizResult()
        when (currentUser.id) {
            currentDuel.initUser.id -> {
                // Current user is the initUser
                currentDuel.initUserFinished = true
                currentDuel.initUserResult = result
                if (currentDuel.opponentUserFinished) {
                    determineWinner()
                }
                launchCatching {
                    storageService.updateDuel(currentDuel)
                }
                checkOpponentAndProceed(currentDuel.opponentUserFinished)
            }

            currentDuel.opponentUser.id -> {
                // Current user is the opponentUser
                currentDuel.opponentUserFinished = true
                currentDuel.opponentUserResult = result
                if (currentDuel.initUserFinished) {
                    determineWinner()
                }
                launchCatching {
                    storageService.updateDuel(currentDuel)
                }
                checkOpponentAndProceed(currentDuel.initUserFinished)
            }
        }
    }

    private fun checkOpponentAndProceed(opponentFinished: Boolean) {
        if (opponentFinished) {
            Log.d(TAG, "opponentFinished = true")
            currentDestination.value = DuellmodusDestination.RESULT
            updateScreenData()
        } else {
            Log.d(TAG, "opponentFinished = false")
            currentDestination.value = DuellmodusDestination.WAIT_FOR_RESULT
            observeResult(currentDuel.id)
            updateScreenData()
        }
    }

    private fun determineWinner() {
        if (currentDuel.initUserResult.scorePoints > currentDuel.opponentUserResult.scorePoints) {
            currentDuel.winnerUser = currentDuel.initUser
        } else if (currentDuel.initUserResult.scorePoints < currentDuel.opponentUserResult.scorePoints) {
            currentDuel.winnerUser = currentDuel.opponentUser
        } else if (currentDuel.initUserResult.scorePoints == currentDuel.opponentUserResult.scorePoints) {
            currentDuel.winnerUser = User()
        }
    }

    private fun getQuizResult(): QuizResult {
        val result = quizRepository.getQuizResult()
        result.userId = currentUser.id
        return result
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

    companion object {
        private const val TAG = "DuellmodusViewModel"
    }
}

enum class DuellmodusDestination {
    NEW_DUEL,
    UNFINISHED_DUEL,
    QUESTIONS,
    LOADING,
    WAIT_FOR_RESULT,
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