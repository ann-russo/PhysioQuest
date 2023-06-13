package com.example.physioquest.screens.account

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.physioquest.ACCOUNT_ROUTE
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.LEADERBOARD_SCREEN
import com.example.physioquest.START_SCREEN
import com.example.physioquest.common.util.isAllowedEmail
import com.example.physioquest.common.util.isValidEmail
import com.example.physioquest.common.util.isValidPassword
import com.example.physioquest.common.util.isValidUsername
import com.example.physioquest.model.User
import com.example.physioquest.screens.PhysioQuestViewModel
import com.example.physioquest.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import com.example.physioquest.R.string as AppText

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountService: AccountService
) : PhysioQuestViewModel() {

    private val _user: MutableState<User?> = mutableStateOf(null)
    val user: State<User?> = _user

    private val _username: MutableStateFlow<String> = MutableStateFlow("username")
    val username: StateFlow<String> = _username

    private val _email: MutableState<String> = mutableStateOf("email")
    val email: State<String> = _email

    var usernameErrorState = mutableStateOf(false)
    var emailErrorState = mutableStateOf(false)
    var confirmEmailErrorState = mutableStateOf(false)

    var passwordErrorState = mutableStateOf(false)
    var newPasswordErrorState = mutableStateOf(false)
    var confirmPasswordErrorState = mutableStateOf(false)

    private var _usernameErrorMessage = mutableStateOf<Int?>(null)
    val usernameErrorMessage
        get() = _usernameErrorMessage.value

    private var _emailErrorMessage = mutableStateOf<Int?>(null)
    val emailErrorMessage
        get() = _emailErrorMessage.value

    private var _confirmEmailErrorMessage = mutableStateOf<Int?>(null)
    val confirmEmailErrorMessage
        get() = _confirmEmailErrorMessage.value

    private var _passwordErrorMessage = mutableStateOf<Int?>(null)
    val passwordErrorMessage
        get() = _passwordErrorMessage.value

    private var _newPasswordErrorMessage = mutableStateOf<Int?>(null)
    val newPasswordErrorMessage
        get() = _newPasswordErrorMessage.value

    private var _confirmPasswordErrorMessage = mutableStateOf<Int?>(null)
    val confirmPasswordErrorMessage
        get() = _confirmPasswordErrorMessage.value

    private val _currentTitle = mutableStateOf("Profil")
    private val _currentDestination = mutableStateOf(AccountDestination.PROFIL)

    private var _accountScreenData = mutableStateOf(createAccountScreenData())
    val accountScreenData: AccountScreenData
        get() = _accountScreenData.value

    init {
        launchCatching {
            accountService.currentUser.collect { user ->
                _user.value = user
                _username.value = user.username
                _email.value = user.email
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
        _accountScreenData.value = createAccountScreenData()
    }

    fun onStatisticClick() {
        _currentDestination.value = AccountDestination.STATISTIK
        _currentTitle.value = "Statistik"
        _accountScreenData.value = createAccountScreenData()
    }

    fun onSettingsClick() {
        _currentDestination.value = AccountDestination.EINSTELLUNGEN
        _currentTitle.value = "Einstellungen"
        _accountScreenData.value = createAccountScreenData()
    }

    fun onHelpClick() {
        _currentDestination.value = AccountDestination.HELP
        _currentTitle.value = "App Info"
        _accountScreenData.value = createAccountScreenData()
    }

    fun goBack() {
        _currentDestination.value = AccountDestination.PROFIL
        _currentTitle.value = "Profil"
        _accountScreenData.value = createAccountScreenData()
    }

    fun deleteAccount(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.deleteAccount()
            restartApp(START_SCREEN)
        }
    }

    fun validateUsername(newUsername: String) {
        if (!newUsername.isValidUsername()) {
            usernameErrorState.value = true
            _usernameErrorMessage.value = AppText.error_username_length
            return
        }
        usernameErrorState.value = false
        _usernameErrorMessage.value = null
    }

    fun validateAndChangeUsername(newUsername: String) {
        if (newUsername.isBlank()) {
            usernameErrorState.value = true
            _usernameErrorMessage.value = AppText.error_empty_field
            return
        }
        usernameErrorState.value = false
        _usernameErrorMessage.value = null

        launchCatching {
            accountService.updateNickname(newUsername)
            accountService.currentUser.collect { user ->
                _username.value = user.username
            }
        }
    }

    fun validateNewEmail(newEmail: String) {
        if (!newEmail.isValidEmail()) {
            emailErrorState.value = true
            _emailErrorMessage.value = AppText.error_email_generic
            return
        }
        if (!newEmail.isAllowedEmail()) {
            emailErrorState.value = true
            _emailErrorMessage.value = AppText.error_email_fh
            return
        }
    }

    fun validateNewConfirmEmail(newEmail: String, confirmNewEmail: String) {
        if (confirmNewEmail != newEmail) {
            confirmEmailErrorState.value = true
            _confirmEmailErrorMessage.value = AppText.error_emails_match
            return
        }
    }

    fun isFieldEmpty(newEmail: String, confirmNewEmail: String, password: String) {
        if (newEmail.isBlank()) {
            emailErrorState.value = true
            _emailErrorMessage.value = AppText.error_empty_field
        }
        if (confirmNewEmail.isBlank()) {
            confirmEmailErrorState.value = true
            _confirmEmailErrorMessage.value = AppText.error_empty_field
        }
        if (password.isBlank()) {
            passwordErrorState.value = true
            _passwordErrorMessage.value = AppText.error_empty_field
        }
    }

    fun isPasswordValid(password: String): Boolean {
        if (!isUserAuthenticated(password)) {
            passwordErrorState.value = true
            _passwordErrorMessage.value = AppText.error_wrong_pwd
            return false
        }
        return true
    }

    fun changeEmail(newEmail: String) {
        launchCatching {
            accountService.updateEmail(newEmail)
            accountService.currentUser.collect { user ->
                _email.value = user.email
            }
        }
    }

    fun validateNewPassword(newPassword: String) {
        if (!newPassword.isValidPassword()) {
            newPasswordErrorState.value = true
            _newPasswordErrorMessage.value = AppText.error_password_pattern
            return
        }
    }

    fun validateNewConfirmPassword(newPassword: String, confirmPassword: String) {
        if (newPassword != confirmPassword) {
            confirmPasswordErrorState.value = true
            _confirmPasswordErrorMessage.value = AppText.error_pwd_match
            return
        }
    }

    fun checkEmptyPasswordFields(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        if (currentPassword.isBlank()) {
            passwordErrorState.value = true
            _passwordErrorMessage.value = AppText.error_empty_field
        }
        if (newPassword.isBlank()) {
            newPasswordErrorState.value = true
            _newPasswordErrorMessage.value = AppText.error_empty_field
        }
        if (confirmPassword.isBlank()) {
            confirmPasswordErrorState.value = true
            _confirmPasswordErrorMessage.value = AppText.error_empty_field
        }
    }

    fun changePassword(newPassword: String) {
        launchCatching {
            accountService.updatePassword(newPassword)
        }
    }

    private fun isUserAuthenticated(password: String): Boolean {
        var authenticated = false
        launchCatching {
            authenticated = accountService.reAuthenticate(_email.value, password)
        }
        return authenticated
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