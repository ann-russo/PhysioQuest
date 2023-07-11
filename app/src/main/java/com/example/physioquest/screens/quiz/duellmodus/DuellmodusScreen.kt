package com.example.physioquest.screens.quiz.duellmodus

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.physioquest.R
import com.example.physioquest.common.composable.AnimatedDialog
import com.example.physioquest.common.composable.CenteredTopAppBar
import com.example.physioquest.common.composable.QuizTopAppBar
import com.example.physioquest.screens.quiz.shared.QuizBottomBar
 import com.example.physioquest.screens.quiz.shared.SimpleBottomBar

@Composable
fun DuellmodusScreen(
    surveyScreenData: DuellmodusScreenData,
    isEvaluationEnabled: Boolean,
    onEvaluateClicked: () -> Unit,
    onNextClicked: () -> Unit,
    onClosePressed: () -> Unit,
    onCancelDuel: () -> Unit,
    isLastQuestion: Boolean,
    onQuizComplete: () -> Unit,
    onStartUnfinishedDuel: () -> Unit,
    onStartNewDuel: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    val showDialog = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            when (surveyScreenData.destination) {
                DuellmodusDestination.NEW_DUEL -> {
                    CenteredTopAppBar(
                        title = R.string.new_duel,
                        onClosePressed = onClosePressed
                    )
                }
                DuellmodusDestination.UNFINISHED_DUEL -> {
                    CenteredTopAppBar(
                        title = R.string.unfinished_duel,
                        onClosePressed = onClosePressed
                    )
                }
                DuellmodusDestination.WAIT_FOR_RESULT -> {
                    CenteredTopAppBar(
                        title = R.string.wait_for_opponent,
                        onClosePressed = onClosePressed
                    )
                }
                DuellmodusDestination.QUESTIONS -> {
                    QuizTopAppBar(
                        questionIndex = surveyScreenData.questionIndex,
                        totalQuestionsCount = surveyScreenData.questionCount,
                        onClosePressed = { showDialog.value = true }
                    )
                    AnimatedDialog(
                        visible = showDialog.value,
                        onClose = { showDialog.value = false },
                        onConfirm = {
                            onCancelDuel()
                            showDialog.value = false
                        },
                        title = R.string.lernmodus_cancel,
                        content = R.string.lernmodus_cancel_desc,
                        actionButton = R.string.lernmodus_cancel_confirm
                    )
                }
                else -> {
                    null
                }
            }
        },
        content = content,
        bottomBar = {
            when (surveyScreenData.destination) {
                DuellmodusDestination.UNFINISHED_DUEL -> {
                    SimpleBottomBar(
                        buttonText = R.string.unfinished_duel_start,
                        onButtonClick = onStartUnfinishedDuel
                    )
                }
                DuellmodusDestination.NEW_DUEL -> {
                    SimpleBottomBar(
                        buttonText = R.string.new_duel_start,
                        onButtonClick = onStartNewDuel
                    )
                }
                DuellmodusDestination.QUESTIONS -> {
                    QuizBottomBar(
                        isEvaluationEnabled = isEvaluationEnabled,
                        selectedAnswers = surveyScreenData.selectedAnswers,
                        onEvaluateClicked = onEvaluateClicked,
                        onNextClicked = onNextClicked,
                        isLastQuestion = isLastQuestion,
                        onQuizComplete = onQuizComplete
                    )
                }
                else -> {null}
            }
        }
    )
}