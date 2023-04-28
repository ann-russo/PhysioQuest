package com.example.physioquest.screens.lernmodus

import com.example.physioquest.START_SCREEN
import com.example.physioquest.model.Antwort
import com.example.physioquest.screens.PhysioQuestViewModel
import com.example.physioquest.service.AccountService
import com.example.physioquest.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LernmodusViewModel @Inject constructor(
    private val storageService: StorageService,
    private val accountService: AccountService
) :
    PhysioQuestViewModel() {

    val fragen = storageService.fragen

    fun validateAntwort(antwort: Antwort): Boolean {
        return antwort.antwortKorrekt
    }

    fun onSignOutClick(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.signOut()
            restartApp(START_SCREEN)
        }
    }
}