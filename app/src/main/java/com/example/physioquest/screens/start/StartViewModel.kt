package com.example.physioquest.screens.start

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.physioquest.START_SCREEN
import com.example.physioquest.WELCOME_SCREEN
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor() : ViewModel() {
    val showError = mutableStateOf(false)

    fun onAppStart(openAndPopUp: (String, String) -> Unit) {
        showError.value = false
        openAndPopUp(WELCOME_SCREEN, START_SCREEN)
    }
}