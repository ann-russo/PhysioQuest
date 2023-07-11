package com.example.physioquest.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.physioquest.ACCOUNT_ROUTE
import com.example.physioquest.DUELLMODUS_ROUTE
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.LEADERBOARD_SCREEN
import com.example.physioquest.LERNMODUS_ROUTE
import com.example.physioquest.START_SCREEN
import com.example.physioquest.model.User
import com.example.physioquest.screens.PhysioQuestViewModel
import com.example.physioquest.service.AccountService
import com.example.physioquest.service.LevelService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountService: AccountService,
    private val levelService: LevelService
) :
    PhysioQuestViewModel() {
    private val _user: MutableState<User> = mutableStateOf(User())
    val user: State<User> = _user

    init {
        launchCatching {
            accountService.currentUser.collect { user ->
                _user.value = user
            }
        }
    }

    fun calculateXpProgress(): Float {
        val xpInCurrentLevel = levelService.calculateXpInCurrentLevel(user.value.xp, user.value.level)
        val xpNeededForNextLevel = levelService.calculateXpForNextLevel(user.value.level)
        return xpInCurrentLevel.toFloat() / xpNeededForNextLevel.toFloat()
    }

    fun onSignOutClick(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.signOut()
            restartApp(START_SCREEN)
        }
    }

    fun onLernmodusClick(openScreen: (String) -> Unit) {
        launchCatching {
            openScreen(LERNMODUS_ROUTE)
        }
    }

    fun onDuellmodusClick(openScreen: (String) -> Unit) {
        launchCatching {
            openScreen(DUELLMODUS_ROUTE)
        }
    }

    fun onHomeClick(openScreen: (String) -> Unit) {
        launchCatching {
            openScreen(HOME_SCREEN)
        }
    }

    fun onLeaderboardClick(openScreen: (String) -> Unit) {
        launchCatching {
            openScreen(LEADERBOARD_SCREEN)
        }
    }

    fun onAccountClick(openScreen: (String) -> Unit) {
        launchCatching {
            openScreen(ACCOUNT_ROUTE)
        }
    }
}