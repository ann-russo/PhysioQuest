package com.example.physioquest.screens.duellmodus

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.model.User
import com.example.physioquest.screens.PhysioQuestViewModel
import com.example.physioquest.service.AccountService
import com.example.physioquest.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class DuellmodusViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) : PhysioQuestViewModel() {

    private val _currentDestination = mutableStateOf(DuellmodusDestination.FIND_OPPONENT)

    private var _surveyScreenData = mutableStateOf(createDuellmodusScreenData())
    val surveyScreenData: DuellmodusScreenData
        get() = _surveyScreenData.value

    private val _opponent = mutableStateOf<User?>(null)
    val opponent: User?
        get() = _opponent.value

    init {
        selectRandomOpponent()
    }

    fun onClosePressed(openScreen: (String) -> Unit) {
        _surveyScreenData.value.destination = null
        launchCatching {
            openScreen(HOME_SCREEN)
        }
    }

    private fun selectRandomOpponent() {
        launchCatching {
            try {
                val availableUsers = storageService.getUsersFromDatabase()
                if (availableUsers.isNotEmpty()) {
                    val randomIndex = Random.nextInt(0, availableUsers.size)
                    val randomOpponent = availableUsers[randomIndex]
                    Log.d("selectRandomOpponent", "random opponent: $randomOpponent")

                    //_surveyScreenData.value.destination = DuellmodusDestination.QUESTIONS
                    _opponent.value = randomOpponent
                } else {
                    Log.d("selectRandomOpponent", "no possible opponent found")
                }
            } catch (exception: Exception) {
                Log.d("selectRandomOpponent", exception.toString())
            }
        }
    }


    private fun createDuellmodusScreenData(): DuellmodusScreenData {
        return DuellmodusScreenData(
            destination = _currentDestination.value
        )
    }
}

enum class DuellmodusDestination {
    FIND_OPPONENT,
    QUESTIONS,
    RESULT
}

data class DuellmodusScreenData(
    var destination: DuellmodusDestination?
)