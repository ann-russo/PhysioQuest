package com.example.physioquest.screens.registration

import androidx.compose.runtime.mutableStateOf
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.LOGIN_SCREEN
import com.example.physioquest.R.string as AppText
import com.example.physioquest.REGISTRATION_SCREEN
import com.example.physioquest.common.snackbar.SnackbarManager
import com.example.physioquest.common.util.isAllowedEmail
import com.example.physioquest.common.util.isValidEmail
import com.example.physioquest.common.util.isValidPassword
import com.example.physioquest.common.util.isValidUsername
import com.example.physioquest.screens.PhysioQuestViewModel
import com.example.physioquest.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

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

    fun onUsernameChange(newValue: String) {
        uiState.value = uiState.value.copy(username = newValue)
    }

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        if (username.isBlank() && email.isBlank() && password.isBlank()) {
            SnackbarManager.showMessage(AppText.error_fields_empty)
            return
        }

        if (username.isBlank()) {
            SnackbarManager.showMessage(AppText.error_username_empty)
            return
        }

        if (!username.isValidUsername()) {
            SnackbarManager.showMessage(AppText.error_username_length)
            return
        }

        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(AppText.error_email_generic)
            return
        }

        if (!email.isAllowedEmail()) {
            SnackbarManager.showMessage(AppText.error_email_fh)
            return
        }

        if (password.isBlank()) {
            SnackbarManager.showMessage(AppText.error_password_empty)
            return
        }

        if (!password.isValidPassword()) {
            SnackbarManager.showMessage(AppText.error_password_pattern)
            return
        }

        launchCatching {
            accountService.createAccount(username, email, password)
            openAndPopUp(HOME_SCREEN, REGISTRATION_SCREEN)
        }
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        openAndPopUp(LOGIN_SCREEN, REGISTRATION_SCREEN)
    }
}