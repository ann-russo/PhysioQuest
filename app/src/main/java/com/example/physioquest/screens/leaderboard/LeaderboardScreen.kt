package com.example.physioquest.screens.leaderboard

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.R
import com.example.physioquest.common.composable.ActionToolBar
import com.example.physioquest.common.composable.BottomNavBar
import com.example.physioquest.common.util.toolbarActions

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LeaderboardScreen(
    restartApp: (String) -> Unit,
    openScreen: (String) -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            ActionToolBar(
                title = R.string.leaderboard,
                level = viewModel.user.value.level,
                xp = viewModel.user.value.xp,
                modifier = Modifier.toolbarActions(),
                endActionIcon = R.drawable.ic_exit,
                endAction = { viewModel.onSignOutClick(restartApp) }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(it)
                    .padding(horizontal = 16.dp)
            ) {
                Text(stringResource(R.string.coming_soon))
            }
        },
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

                        "Profil" -> {
                            viewModel.onAccountClick(openScreen)
                        }
                    }
                })
        }
    )
}