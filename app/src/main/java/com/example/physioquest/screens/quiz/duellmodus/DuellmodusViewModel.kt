package com.example.physioquest.screens.quiz.duellmodus

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.model.Duel
import com.example.physioquest.model.Question
import com.example.physioquest.model.User
import com.example.physioquest.screens.PhysioQuestViewModel
import com.example.physioquest.service.AccountService
import com.example.physioquest.service.StorageService
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DuellmodusViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) : PhysioQuestViewModel() {

    private var currentDuel: Duel? = null
    private var questions by mutableStateOf(emptyList<Question>())

    private val _currentDestination = mutableStateOf(DuellmodusDestination.FIND_OPPONENT)

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
        _surveyScreenData.value.destination = null
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
            questions = storageService.getRandomQuestions()
        }
        Log.d("selectRandomQuestions", "questions size: ${questions.size}")
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

        Log.d("startDuel", "questionsList: ${currentDuel!!.randomQuestionsList.size}")
        _currentDestination.value = DuellmodusDestination.QUESTIONS
        updateScreenData()
    }

    private fun createDuellmodusScreenData(): DuellmodusScreenData {
        return DuellmodusScreenData(
            destination = _currentDestination.value,
            duel = currentDuel
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
    var duel: Duel?
)