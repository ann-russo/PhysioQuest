package com.example.physioquest.screens.account

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.physioquest.ACCOUNT_SCREEN
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.LEADERBOARD_SCREEN
import com.example.physioquest.SETTINGS_SCREEN
import com.example.physioquest.START_SCREEN
import com.example.physioquest.model.User
import com.example.physioquest.screens.PhysioQuestViewModel
import com.example.physioquest.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountService: AccountService
): PhysioQuestViewModel() {

    private val _user: MutableState<User?> = mutableStateOf(null)
    val user: State<User?> = _user

    init {
        launchCatching {
            accountService.currentUser.collect { user ->
                _user.value = user
            }
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
            openScreen(ACCOUNT_SCREEN)
        }
    }

    fun onSettingsClick(openScreen: (String) -> Unit) {
        launchCatching {
            openScreen(SETTINGS_SCREEN)
        }
    }

    fun goBack(goBack: () -> Unit) {
        launchCatching {
            goBack()
        }
    }

    fun onSignOutClick(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.signOut()
            restartApp(START_SCREEN)
        }
    }
}