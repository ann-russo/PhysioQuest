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
import com.example.physioquest.service.LevelService
import com.example.physioquest.service.StorageService
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DuellmodusViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService,
    private val quizRepository: QuizRepository,
    private val levelService: LevelService
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

    private val xpPoints: StateFlow<Int>
        get() = quizRepository.xpPoints

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

    /**
     * Checks the status of a duel for a given user and updates the UI accordingly.
     *
     * This method retrieves the user's current unfinished duel from the quiz repository.
     * Based on the duel's status and the user's role in it (either as the initiator or the opponent),
     * it updates the current UI destination to reflect the appropriate state of the duel.
     *
     * The potential states are:
     * 1. WAIT_FOR_RESULT: When the user has finished their part in the duel and is waiting for the result.
     * 2. UNFINISHED_DUEL: When the user is the opponent and has not yet finished the duel.
     * 3. RESULT: When both participants have finished the duel, and the result is ready to be displayed.
     * 4. NEW_DUEL: When there are no unfinished duels for the user, indicating a new duel should be started.
     *
     * Additionally, this method handles updating the duel and screen data based on the current duel status,
     * and observes the result if the user is waiting for it.
     *
     * @param userId The unique identifier of the user whose duel status is to be checked.
     */
    private fun checkDuelStatus(userId: String) {
        launchCatching {
            val duel = quizRepository.findUnfinishedDuel(userId)
            if (duel != null) {
                when {
                    (duel.initUser.id == userId && duel.initUserFinished) ||
                    (duel.opponentUser.id == userId && duel.opponentUserFinished) -> {
                        currentDestination.value = DuellmodusDestination.WAIT_FOR_RESULT
                        updateUsersAndDuel(duel)
                        observeResult(duel.id)
                        updateScreenData()
                    }

                    duel.opponentUser.id == userId && !duel.opponentUserFinished -> {
                        currentDestination.value = DuellmodusDestination.UNFINISHED_DUEL
                        updateUsersAndDuel(duel)
                        updateScreenData()
                    }
                    duel.initUserFinished && duel.opponentUserFinished -> {
                        currentDestination.value = DuellmodusDestination.RESULT
                        updateUsersAndDuel(duel)
                        updateScreenData()
                    }
                }
            } else {
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

    fun onCancelDuel(openScreen: (String) -> Unit) {
        removeXp()
        launchCatching {
            storageService.deleteDuel(currentDuel.id)
        }
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

    private fun addXp(points: Int) {
        launchCatching {
            levelService.awardXp(currentUser, points)
        }
    }
    private fun removeXp(points: Int = 10) {
        launchCatching {
            levelService.removeXp(currentUser, points)
        }
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
        addXp(xpPoints.value)

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
        currentDuel.winnerUser = when {
            currentDuel.initUserResult.scorePoints > currentDuel.opponentUserResult.scorePoints -> currentDuel.initUser
            currentDuel.initUserResult.scorePoints < currentDuel.opponentUserResult.scorePoints -> currentDuel.opponentUser
            else -> User()
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