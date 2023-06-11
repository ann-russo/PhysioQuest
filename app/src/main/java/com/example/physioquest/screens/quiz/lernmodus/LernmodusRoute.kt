package com.example.physioquest.screens.quiz.lernmodus

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.model.QuizResult

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun LernmodusRoute(
    onQuizComplete: (result: QuizResult) -> Unit,
    openScreen: (String) -> Unit,
    onNavUp: () -> Unit,
    viewModel: LernmodusViewModel = hiltViewModel()
) {
    val surveyScreenData = viewModel.surveyScreenData

    BackHandler {
        onNavUp()
    }

    LernmodusContent(
        surveyScreenData = surveyScreenData,
        isEvaluationEnabled = viewModel.isEvaluateEnabled,
        onEvaluateClicked = { viewModel.evaluateCurrentQuestion() },
        onNextClicked = { viewModel.onNextClicked() },
        onClosePressed = { viewModel.onClosePressed(openScreen) },
        isLastQuestion = viewModel.isLastQuestion,
        onQuizComplete = { onQuizComplete(viewModel.getQuizResult()) }
    ) { paddingValues ->

        val modifier = Modifier.padding(paddingValues)
        AnimatedContent(
            targetState = surveyScreenData,
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
            when {
                targetState.selectedCategory == null -> {
                    CategoryScreen(
                        categories = viewModel.categories,
                        onCategorySelected = { category ->
                            viewModel.onCategorySelected(category)
                        },
                        modifier = modifier
                    )
                }

                targetState.quizQuestion != null -> {
                    LernmodusQuestion(
                        quizQuestion = targetState.quizQuestion,
                        selectedAnswers = targetState.selectedAnswers,
                        isEvaluationEnabled = viewModel.isEvaluateEnabled,
                        onAnswerSelected = { answerIndex ->
                            viewModel.toggleAnswerSelection(answerIndex)
                        },
                        modifier = modifier
                    )
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