package com.example.physioquest.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.physioquest.LERNMODUS_SCREEN
import com.example.physioquest.START_SCREEN
import com.example.physioquest.model.Antwort
import com.example.physioquest.model.Frage
import com.example.physioquest.model.User
import com.example.physioquest.screens.PhysioQuestViewModel
import com.example.physioquest.service.AccountService
import com.example.physioquest.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) :
    PhysioQuestViewModel() {
    private val _user: MutableState<User?> = mutableStateOf(null)
    val user: State<User?> = _user

    init {
        launchCatching {
            accountService.currentUser.collect { user ->
                _user.value = user
            }
        }
    }

    fun onSignOutClick(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.signOut()
            restartApp(START_SCREEN)
        }
    }

    fun onLernmodusClick(openScreen: (String) -> Unit) {
        launchCatching {
            openScreen(LERNMODUS_SCREEN)
        }
    }

    fun addFrage() {
        val frage = Frage(
            kategorie = "Physiologie",
            frageInhalt = "Was ist die Funktion des Lymphsystems?",
            antworten = listOf(
                Antwort(
                    antwortInhalt = "Abwehr von Krankheitserregern im Körper",
                    antwortKorrekt = true
                ),
                Antwort(
                    antwortInhalt = "Transport von Nährstoffen und Sauerstoff im Körper",
                    antwortKorrekt = false
                ),
                Antwort(
                    antwortInhalt = "Regulation des Blutdrucks im Körper",
                    antwortKorrekt = false
                ),
                Antwort(
                    antwortInhalt = "Produktion von Hormonen im Körper",
                    antwortKorrekt = false
                )
            )
        )
        launchCatching {
            storageService.addFrage(frage)
        }
    }

}