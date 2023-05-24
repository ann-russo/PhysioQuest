package com.example.physioquest.screens.leaderboard

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.R
import com.example.physioquest.common.composable.ActionToolBar
import com.example.physioquest.common.composable.BottomNavBar
import com.example.physioquest.common.util.toolbarActions

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    restartApp: (String) -> Unit,
    openScreen: (String) -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedScreen = "Leaderboard",
                onScreenSelected = { screen ->
                    when (screen) {
                        "Home" -> {
                            viewModel.onHomeClick(openScreen)
                        }
                        "Leaderboard" -> {
                            viewModel.onLeaderboardClick(openScreen)
                        }
                        "Account" -> {
                            viewModel.onAccountClick(openScreen)
                        }
                    }
                })
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            ActionToolBar(
                title = R.string.leaderboard_title,
                modifier = Modifier.toolbarActions(),
                endActionIcon = R.drawable.ic_exit,
                endAction = { viewModel.onSignOutClick(restartApp) }
            )
        }
    }
}