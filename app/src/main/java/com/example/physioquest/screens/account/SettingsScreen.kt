package com.example.physioquest.screens.account

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
fun SettingsScreen(
    restartApp: (String) -> Unit,
    openScreen: (String) -> Unit,
    goBack: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedScreen = "Account",
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
                .fillMaxHeight(),
        ) {
            ActionToolBar(
                title = R.string.settings,
                modifier = Modifier.toolbarActions(),
                endActionIcon = R.drawable.ic_exit,
                endAction = { viewModel.onSignOutClick(restartApp) },
                onBackPressed = {
                    viewModel.goBack(goBack)
                }
            )
            Text(text = "Test")
        }
    }
}