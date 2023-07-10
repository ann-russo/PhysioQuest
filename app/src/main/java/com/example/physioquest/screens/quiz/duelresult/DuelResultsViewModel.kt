package com.example.physioquest.screens.quiz.duelresult

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.physioquest.model.Duel
import com.example.physioquest.model.QuizResult
import com.example.physioquest.model.User
import com.example.physioquest.screens.PhysioQuestViewModel
import com.example.physioquest.service.AccountService
import com.example.physioquest.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DuelResultsViewModel @Inject constructor(
    private val accountService: AccountService,
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

    private val _currentUser: MutableState<User> = mutableStateOf(User())
    val currentUser: State<User> = _currentUser

    init {
        launchCatching {
            accountService.currentUser.collect { user ->
                _currentUser.value = user
            }
        }
    }

    fun findDuelById(duelId: String) {
        launchCatching {
            storageService.getDuel(duelId).collect { fetchedDuel ->
                _duel.value = fetchedDuel
                _initUserResult.value = fetchedDuel.initUserResult
                _opponentUserResult.value = fetchedDuel.opponentUserResult
            }
        }
    }

    fun isCurrentUserWinner(): Boolean {
        return _currentUser.value.id == _duel.value.winnerUser.id
    }

    fun isDraw(): Boolean {
        return _duel.value.initUser.id != _duel.value.winnerUser.id && _duel.value.opponentUser.id != _duel.value.winnerUser.id
    }
}