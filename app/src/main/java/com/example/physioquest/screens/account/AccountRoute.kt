package com.example.physioquest.screens.account

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AccountRoute(
    restartApp: (String) -> Unit,
    openScreen: (String) -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val data = viewModel.accountScreenData

    AccountContent(
        data = data,
        onHomeClick = { viewModel.onHomeClick(openScreen) },
        onLeaderboardClick = { viewModel.onLeaderboardClick(openScreen) },
        onAccountClick = { viewModel.onAccountClick(openScreen) },
        onSignOutClick = { viewModel.onSignOutClick(restartApp) },
        onBackClick = { viewModel.goBack() }
    ) { paddingValues ->

        val modifier = Modifier.padding(paddingValues)

        AnimatedContent(
            targetState = data,
            transitionSpec = {
                val initialOffset = { fullWidth: Int ->
                    if (targetState.destination == AccountDestination.PROFIL) {
                        -fullWidth // Slide from left
                    } else {
                        fullWidth // Slide from right
                    }
                }
                val targetOffset = { fullWidth: Int ->
                    if (targetState.destination == AccountDestination.PROFIL) {
                        fullWidth // Slide to right
                    } else {
                        -fullWidth // Slide to left
                    }
                }
                slideInHorizontally(
                    animationSpec = tween(400),
                    initialOffsetX = initialOffset
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(400),
                    targetOffsetX = targetOffset
                )
            }
        ) { targetState ->
            when (targetState.destination) {
                AccountDestination.PROFIL -> {
                    AccountScreen(
                        restartApp = restartApp,
                        modifier = modifier
                    )
                }

                AccountDestination.EINSTELLUNGEN -> {
                    SettingsScreen(
                        restartApp = restartApp,
                        modifier = modifier)
                }

                AccountDestination.EDIT -> {
                    EditScreen(modifier = modifier)
                }

                AccountDestination.STATISTIK -> {
                    StatsScreen(modifier = modifier)
                }

                AccountDestination.HELP -> {
                    HelpScreen(modifier = modifier)
                }
            }
        }
    }
}