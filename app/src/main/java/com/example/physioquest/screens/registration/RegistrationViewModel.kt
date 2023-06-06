package com.example.physioquest.screens.registration

import androidx.compose.runtime.mutableStateOf
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.LOGIN_SCREEN
import com.example.physioquest.REGISTRATION_SCREEN
import com.example.physioquest.WELCOME_SCREEN
import com.example.physioquest.common.util.isAllowedEmail
import com.example.physioquest.common.util.isValidEmail
import com.example.physioquest.common.util.isValidPassword
import com.example.physioquest.common.util.isValidUsername
import com.example.physioquest.screens.PhysioQuestViewModel
import com.example.physioquest.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.physioquest.R.string as AppText

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val accountService: AccountService) :
    PhysioQuestViewModel() {
    var uiState = mutableStateOf(RegistrationUiState())
        private set

    private val username
        get() = uiState.value.username
    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password

    var usernameErrorState = mutableStateOf(false)
    var emailErrorState = mutableStateOf(false)
    var passwordErrorState = mutableStateOf(false)

    private var _usernameErrorMessage = mutableStateOf<Int?>(null)
    val usernameErrorMessage
        get() = _usernameErrorMessage.value

    private var _emailErrorMessage = mutableStateOf<Int?>(null)
    val emailErrorMessage
        get() = _emailErrorMessage.value

    private var _passwordErrorMessage = mutableStateOf<Int?>(null)
    val passwordErrorMessage
        get() = _passwordErrorMessage.value

    fun onUsernameChange(newValue: String) {
        usernameErrorState.value = false
        uiState.value = uiState.value.copy(username = newValue)
        validateUsername(newValue)
    }

    fun onEmailChange(newValue: String) {
        emailErrorState.value = false
        uiState.value = uiState.value.copy(email = newValue)
        validateEmail(newValue)
    }

    fun onPasswordChange(newValue: String) {
        passwordErrorState.value = false
        uiState.value = uiState.value.copy(password = newValue)
        validatePassword(newValue)
    }

    private fun validateEmail(newEmail: String) {
        if (!newEmail.isValidEmail()) {
            emailErrorState.value = true
            _emailErrorMessage.value = AppText.error_email_generic
        }
        if (!newEmail.isAllowedEmail()) {
            emailErrorState.value = true
            _emailErrorMessage.value = AppText.error_email_fh
        }
    }

    private fun validateUsername(newUsername: String) {
        if (!newUsername.isValidUsername()) {
            usernameErrorState.value = true
            _usernameErrorMessage.value = AppText.error_username_length
        }
    }

    private fun validatePassword(newPassword: String) {
        if (!newPassword.isValidPassword()) {
            passwordErrorState.value = true
            _passwordErrorMessage.value = AppText.error_password_pattern
        }
    }

    fun isFieldEmpty() {
        if (email.isBlank()) {
            emailErrorState.value = true
            _emailErrorMessage.value = AppText.error_empty_field
        }
        if (username.isBlank()) {
            usernameErrorState.value = true
            _usernameErrorMessage.value = AppText.error_empty_field
        }
        if (password.isBlank()) {
            passwordErrorState.value = true
            _passwordErrorMessage.value = AppText.error_empty_field
        }
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            accountService.createAccount(username, email, password)
            openAndPopUp(HOME_SCREEN, REGISTRATION_SCREEN)
        }
    }

    fun onClosePressed(openScreen: (String) -> Unit) {
        clearFieldsAndErrors()
        launchCatching {
            openScreen(WELCOME_SCREEN)
        }
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        openAndPopUp(LOGIN_SCREEN, REGISTRATION_SCREEN)
    }

    private fun clearFieldsAndErrors() {
        uiState.value = uiState.value.copy(email = "")
        uiState.value = uiState.value.copy(password = "")
        uiState.value = uiState.value.copy(username = "")
        emailErrorState.value = false
        usernameErrorState.value = false
        passwordErrorState.value = false
        _emailErrorMessage.value = null
        _passwordErrorMessage.value = null
        _usernameErrorMessage.value = null
    }
}