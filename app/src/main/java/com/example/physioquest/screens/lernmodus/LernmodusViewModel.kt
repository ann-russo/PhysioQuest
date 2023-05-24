package com.example.physioquest.screens.lernmodus

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.physioquest.START_SCREEN
import com.example.physioquest.model.Question
import com.example.physioquest.screens.PhysioQuestViewModel
import com.example.physioquest.service.AccountService
import com.example.physioquest.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LernmodusViewModel @Inject constructor(
    private val storageService: StorageService,
    private val accountService: AccountService
) :
    PhysioQuestViewModel() {

    var questions by mutableStateOf(emptyList<Question>())
        private set

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            storageService.questions
                .onStart { _isLoading.value = true }
                .onCompletion { _isLoading.value = false }
                .collect { questions = it }
        }
    }

    fun onSignOutClick(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.signOut()
            restartApp(START_SCREEN)
        }
    }
}