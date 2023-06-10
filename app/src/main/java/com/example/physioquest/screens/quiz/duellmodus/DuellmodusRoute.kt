package com.example.physioquest.screens.quiz.duellmodus

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DuellmodusRoute(
    openScreen: (String) -> Unit,
    onNavUp: () -> Unit,
    viewModel: DuellmodusViewModel = hiltViewModel()
) {
    val screenData = viewModel.surveyScreenData

    BackHandler {
        onNavUp()
    }

    AnimatedContent(
        targetState = screenData,
        transitionSpec = {
            val animationSpec: TweenSpec<IntOffset> = tween(300)
            val direction = getTransitionDirection(
                initialIndex = 0,
                targetIndex = 3,
            )
            slideIntoContainer(
                towards = direction,
                animationSpec = animationSpec,
            ) togetherWith slideOutOfContainer(
                towards = direction,
                animationSpec = animationSpec
            )
        }
    ) { targetState ->
        Log.d("duellmodusroute", "targetState: ${targetState.destination}")
        when (targetState.destination) {
            DuellmodusDestination.FIND_OPPONENT -> {
                viewModel.opponent?.let {
                    FindOpponentScreen(
                        opponent = it.username,
                        onClosePressed = { viewModel.onClosePressed(openScreen) },
                        onStartPressed = { viewModel.startDuel() }
                    )
                }
            }

            DuellmodusDestination.QUESTIONS -> {
                Log.d("Duellmodusroute", "in QUESTIONS")
                targetState.duel?.randomQuestionsList?.let { questions ->
                    Log.d("Duellmodusroute", "questions are not empty")
                    QuestionsScreen(
                        questions = questions
                    )
                }
            }

            else -> {
                Log.d("Duellmodusroute", "in else branch")
            }
        }

    }


}

private fun getTransitionDirection(
    initialIndex: Int,
    targetIndex: Int
): AnimatedContentTransitionScope.SlideDirection {
    return if (targetIndex > initialIndex || initialIndex == 0) {
        AnimatedContentTransitionScope.SlideDirection.Left
    } else {
        AnimatedContentTransitionScope.SlideDirection.Right
    }
}