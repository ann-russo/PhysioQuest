package com.example.physioquest.screens.duellmodus

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DuellmodusRoute(
    openScreen: (String) -> Unit,
    onNavUp: () -> Unit,
    viewModel: DuellmodusViewModel = hiltViewModel()
) {
    val screenData = viewModel.surveyScreenData
    val targetState = remember { mutableStateOf<DuellmodusDestination?>(null) }

    BackHandler {
        onNavUp()
    }

    LaunchedEffect(Unit) {
        targetState.value = DuellmodusDestination.FIND_OPPONENT
    }

    FindOpponentScreen(
        onClosePressed = {
            viewModel.onClosePressed(openScreen)
        }
    )
}