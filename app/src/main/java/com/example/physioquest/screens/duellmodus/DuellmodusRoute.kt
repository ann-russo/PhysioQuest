package com.example.physioquest.screens.duellmodus

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
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

    Box {
        // Your other composable content

        AnimatedContent(
            targetState = targetState.value,
            transitionSpec = {
                fadeIn() + slideInVertically(
                    animationSpec = tween(700),
                    initialOffsetY = { fullHeight -> fullHeight }
                ) togetherWith slideOutVertically(
                    animationSpec = tween(700),
                    targetOffsetY = { fullHeight -> fullHeight }
                ) + fadeOut()
            }
        ) { target ->
            when (target) {
                DuellmodusDestination.FIND_OPPONENT -> {
                    FindOpponentScreen(
                        onClosePressed = {
                            viewModel.onClosePressed(openScreen)
                            targetState.value = null
                        }
                    )
                }
                DuellmodusDestination.QUESTIONS -> {
                    TODO()
                }
                DuellmodusDestination.RESULT -> {
                    //SurveyResultScreen(result = target)
                }
                else -> {null}
            }
        }
    }
}