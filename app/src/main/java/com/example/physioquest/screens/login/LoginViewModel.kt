package com.example.physioquest.screens.login

import androidx.compose.runtime.mutableStateOf
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.LOGIN_SCREEN
import com.example.physioquest.REGISTRATION_SCREEN
import com.example.physioquest.common.snackbar.SnackbarManager
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

    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(AppText.error_email_generic)
            return
        }

        if (password.isBlank()) {
            SnackbarManager.showMessage(AppText.error_password_empty)
            return
        }

        launchCatching {
            accountService.authenticate(email, password)
            openAndPopUp(HOME_SCREEN, LOGIN_SCREEN)
        }
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        openAndPopUp(REGISTRATION_SCREEN, LOGIN_SCREEN)
    }

    fun onForgotPasswordClick() {
        TODO()
    }
}