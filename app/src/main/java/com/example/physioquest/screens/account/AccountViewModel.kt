package com.example.physioquest.screens.account

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.physioquest.ACCOUNT_ROUTE
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.LEADERBOARD_SCREEN
import com.example.physioquest.START_SCREEN
import com.example.physioquest.model.User
import com.example.physioquest.screens.PhysioQuestViewModel
import com.example.physioquest.screens.registration.RegistrationUiState
import com.example.physioquest.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountService: AccountService
) : PhysioQuestViewModel() {

    private val _user: MutableState<User?> = mutableStateOf(null)
    val user: State<User?> = _user

    var uiState = mutableStateOf(RegistrationUiState())
        private set

    private val username
        get() = uiState.value.username
    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password

    private val _currentTitle = mutableStateOf("Profil")
    private val _currentDestination = mutableStateOf(AccountDestination.PROFIL)

    private var _surveyScreenData = mutableStateOf(createAccountScreenData())
    val surveyScreenData: AccountScreenData
        get() = _surveyScreenData.value

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
            openScreen(ACCOUNT_ROUTE)
        }
    }

    fun onEditClick() {
        _currentDestination.value = AccountDestination.EDIT
        _currentTitle.value = "Profil bearbeiten"
        _surveyScreenData.value = createAccountScreenData()
    }

    fun onStatisticClick() {
        _currentDestination.value = AccountDestination.STATISTIK
        _currentTitle.value = "Statistik"
        _surveyScreenData.value = createAccountScreenData()
    }

    fun onSettingsClick() {
        _currentDestination.value = AccountDestination.EINSTELLUNGEN
        _currentTitle.value = "Einstellungen"
        _surveyScreenData.value = createAccountScreenData()
    }

    fun onHelpClick() {
        _currentDestination.value = AccountDestination.HELP
        _currentTitle.value = "App Info"
        _surveyScreenData.value = createAccountScreenData()
    }

    fun goBack() {
        _currentDestination.value = AccountDestination.PROFIL
        _currentTitle.value = "Profil"
        _surveyScreenData.value = createAccountScreenData()
    }

    fun deleteAccount() {
        launchCatching {
            accountService.deleteAccount()
        }
    }

    fun changeUsername(newUsername: String) {
        launchCatching {
            accountService.updateNickname(newUsername)
        }
    }

    fun changeEmail(newEmail: String) {
        launchCatching {
            accountService.updateEmail(newEmail)
        }
    }

    fun changePassword(newPassword: String) {
        launchCatching {
            accountService.updatePassword(newPassword)
        }
    }

    fun onSignOutClick(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.signOut()
            restartApp(START_SCREEN)
        }
    }

    private fun createAccountScreenData(): AccountScreenData {
        return AccountScreenData(
            title = _currentTitle.value,
            destination = _currentDestination.value
        )
    }
}

enum class AccountDestination {
    PROFIL,
    EDIT,
    STATISTIK,
    EINSTELLUNGEN,
    HELP
}

data class AccountScreenData(
    var title: String,
    var destination: AccountDestination
)

data class DataUiState(
    val username: String = "",
    val email: String = "",
    val password: String = ""
)