package com.example.physioquest.screens.quiz.duellmodus

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.screens.quiz.shared.QuizQuestion

@Composable
fun DuellmodusRoute(
    onQuizComplete: (duelId: String) -> Unit,
    openScreen: (String) -> Unit,
    onNavUp: () -> Unit,
    viewModel: DuellmodusViewModel = hiltViewModel()
) {
    val screenData = viewModel.surveyScreenData

    BackHandler {
        onNavUp()
    }

    val navigateToResultsScreen by viewModel.navigateToResultsScreen.observeAsState()
    navigateToResultsScreen?.getContentIfNotHandled()?.let { duel ->
        onQuizComplete(duel.id)
    }

    DuellmodusScreen(
        surveyScreenData = screenData,
        isEvaluationEnabled = viewModel.isEvaluateEnabled,
        onEvaluateClicked = { viewModel.evaluateCurrentQuestion() },
        onNextClicked = { viewModel.onNextClicked() },
        onClosePressed = { viewModel.onClosePressed(openScreen) },
        onCancelDuel = { viewModel.onCancelDuel(openScreen) },
        isLastQuestion = viewModel.isLastQuestion,
        onQuizComplete = { viewModel.onFinishClicked() },
        onStartUnfinishedDuel = { viewModel.startUnfinishedDuel() },
        onStartNewDuel = { viewModel.startNewDuel() }
    ) { paddingValues ->

        val modifier = Modifier.padding(paddingValues)
        AnimatedContent(
            targetState = screenData,
            transitionSpec = {
                val animationSpec: TweenSpec<IntOffset> = tween(300)
                val direction = getTransitionDirection(
                    initialIndex = initialState.questionIndex,
                    targetIndex = targetState.questionIndex,
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
            when (targetState.destination) {

                DuellmodusDestination.LOADING -> {
                    LoadingScreen(modifier = modifier)
                }
                DuellmodusDestination.UNFINISHED_DUEL -> {
                    UnfinishedDuelScreen(
                        opponent = viewModel.getOpponentName(),
                        modifier = modifier
                    )
                }
                DuellmodusDestination.NEW_DUEL -> {
                    StartNewDuelScreen(
                        opponent = viewModel.duelOpponentUser.username,
                        modifier = modifier
                    )
                }
                DuellmodusDestination.QUESTIONS -> {
                    if (targetState.quizQuestion != null) {
                        QuizQuestion(
                            quizQuestion = targetState.quizQuestion,
                            selectedAnswers = targetState.selectedAnswers,
                            isEvaluationEnabled = viewModel.isEvaluateEnabled,
                            onAnswerSelected = { answerIndex ->
                                viewModel.toggleAnswerSelection(answerIndex) },
                            modifier = modifier
                        )
                    }
                }
                DuellmodusDestination.WAIT_FOR_RESULT -> {
                    WaitForOpponentFinish(
                        opponent = viewModel.duelOpponentUser.username,
                        modifier = modifier
                    )
                }
                DuellmodusDestination.RESULT -> {
                    onQuizComplete(viewModel.currentDuel.id)
                }
                else -> {
                    null
                }
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