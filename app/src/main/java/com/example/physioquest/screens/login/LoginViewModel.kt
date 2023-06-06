package com.example.physioquest.screens.login

import androidx.compose.runtime.mutableStateOf
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.LOGIN_SCREEN
import com.example.physioquest.REGISTRATION_SCREEN
import com.example.physioquest.WELCOME_SCREEN
import com.example.physioquest.common.util.isValidEmail
import com.example.physioquest.screens.PhysioQuestViewModel
import com.example.physioquest.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.physioquest.R.string as AppText

@HiltViewModel
class LoginViewModel @Inject constructor(private val accountService: AccountService) :
    PhysioQuestViewModel() {
    var uiState = mutableStateOf(LoginUiState())
        private set

    var emailErrorState = mutableStateOf(false)
    var passwordErrorState = mutableStateOf(false)

    private var _emailErrorMessage = mutableStateOf<Int?>(null)
    val emailErrorMessage
        get() = _emailErrorMessage.value

    private var _passwordErrorMessage = mutableStateOf<Int?>(null)
    val passwordErrorMessage
        get() = _passwordErrorMessage.value

    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password

    fun onEmailChange(newValue: String) {
        emailErrorState.value = false
        uiState.value = uiState.value.copy(email = newValue)
        validateEmail(newValue)
    }

    fun onPasswordChange(newValue: String) {
        passwordErrorState.value = false
        uiState.value = uiState.value.copy(password = newValue)
    }

    private fun validateEmail(newEmail: String) {
        if (!newEmail.isValidEmail()) {
            emailErrorState.value = true
            _emailErrorMessage.value = AppText.error_email_generic
        }
    }

    fun isFieldEmpty() {
        if (password.isBlank()) {
            passwordErrorState.value = true
            _passwordErrorMessage.value = AppText.error_empty_field
        }
        if (email.isBlank()) {
            emailErrorState.value = true
            _emailErrorMessage.value = AppText.error_empty_field
        }
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            accountService.authenticate(email, password)
            openAndPopUp(HOME_SCREEN, LOGIN_SCREEN)
        }
    }

    fun onClosePressed(openScreen: (String) -> Unit) {
        clearFieldsAndErrors()
        launchCatching {
            openScreen(WELCOME_SCREEN)
        }
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        openAndPopUp(REGISTRATION_SCREEN, LOGIN_SCREEN)
    }

    fun onForgotPasswordClick() {
        TODO()
    }

    private fun clearFieldsAndErrors() {
        uiState.value = uiState.value.copy(email = "")
        uiState.value = uiState.value.copy(password = "")
        emailErrorState.value = false
        passwordErrorState.value = false
        _emailErrorMessage.value = null
        _passwordErrorMessage.value = null
    }
}