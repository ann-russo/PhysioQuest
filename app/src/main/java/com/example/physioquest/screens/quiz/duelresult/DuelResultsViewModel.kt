package com.example.physioquest.screens.quiz.duelresult

import androidx.compose.runtime.mutableStateOf
import com.example.physioquest.model.Duel
import com.example.physioquest.model.QuizResult
import com.example.physioquest.screens.PhysioQuestViewModel
import com.example.physioquest.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DuelResultsViewModel @Inject constructor(
    private val storageService: StorageService,
) : PhysioQuestViewModel() {

    private val _initUserResult = mutableStateOf(QuizResult())
    val initUserResult: QuizResult
        get() = _initUserResult.value

    private val _opponentUserResult = mutableStateOf(QuizResult())
    val opponentUserResult: QuizResult
        get() = _opponentUserResult.value

    private val _duel = mutableStateOf(Duel())
    val duel: Duel
        get() = _duel.value

    fun findDuelById(duelId: String) {
        launchCatching {
            storageService.getDuel(duelId).collect { fetchedDuel ->
                _duel.value = fetchedDuel
                _initUserResult.value = fetchedDuel.initUserResult
                _opponentUserResult.value = fetchedDuel.opponentUserResult
            }
        }
    }
}