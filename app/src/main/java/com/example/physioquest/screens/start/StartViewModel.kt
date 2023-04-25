package com.example.physioquest.screens.start

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.LOGIN_SCREEN
import com.example.physioquest.START_SCREEN
import com.example.physioquest.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(private val accountService: AccountService) : ViewModel() {
    val showError = mutableStateOf(false)

    fun onAppStart(openAndPopUp: (String, String) -> Unit) {
        showError.value = false
        if (accountService.hasUser) {
            openAndPopUp(HOME_SCREEN, START_SCREEN)
        } else {
            openAndPopUp(LOGIN_SCREEN, START_SCREEN)
        }
    }
}