package com.example.physioquest.screens.welcome

import com.example.physioquest.LOGIN_SCREEN
import com.example.physioquest.REGISTRATION_SCREEN
import com.example.physioquest.WELCOME_SCREEN
import com.example.physioquest.screens.PhysioQuestViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor() : PhysioQuestViewModel() {

    fun onLoginClick(openAndPopUp: (String, String) -> Unit) {
        openAndPopUp(LOGIN_SCREEN, WELCOME_SCREEN)
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        openAndPopUp(REGISTRATION_SCREEN, WELCOME_SCREEN)
    }
}