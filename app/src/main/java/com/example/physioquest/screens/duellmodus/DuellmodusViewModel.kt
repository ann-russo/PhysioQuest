package com.example.physioquest.screens.duellmodus

import androidx.compose.runtime.mutableStateOf
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.screens.PhysioQuestViewModel
import com.example.physioquest.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DuellmodusViewModel @Inject constructor(
    private val accountService: AccountService
) : PhysioQuestViewModel() {

    private val _currentDestination = mutableStateOf(DuellmodusDestination.FIND_OPPONENT)

    private var _surveyScreenData = mutableStateOf(createDuellmodusScreenData())
    val surveyScreenData: DuellmodusScreenData
        get() = _surveyScreenData.value

    fun onClosePressed(openScreen: (String) -> Unit) {
        _surveyScreenData.value.destination = null
        launchCatching {
            openScreen(HOME_SCREEN)
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