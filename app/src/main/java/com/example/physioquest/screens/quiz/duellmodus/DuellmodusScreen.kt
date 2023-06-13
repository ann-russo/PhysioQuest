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

@Composable
fun DuellmodusScreen(
    surveyScreenData: DuellmodusScreenData,
    isEvaluationEnabled: Boolean,
    onEvaluateClicked: () -> Unit,
    onNextClicked: () -> Unit,
    onClosePressed: () -> Unit,
    isLastQuestion: Boolean,
    onQuizComplete: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    val showDialog = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            if (surveyScreenData.destination == DuellmodusDestination.FIND_OPPONENT) {
                CenteredTopAppBar(
                    title = R.string.find_opponent,
                    onClosePressed = onClosePressed
                )
            } else {
                QuizTopAppBar(
                    questionIndex = surveyScreenData.questionIndex,
                    totalQuestionsCount = surveyScreenData.questionCount,
                    onClosePressed = { showDialog.value = true }
                )
                AnimatedDialog(
                    visible = showDialog.value,
                    onClose = { showDialog.value = false },
                    onConfirm = {
                        onClosePressed()
                        showDialog.value = false
                    },
                    title = R.string.lernmodus_cancel,
                    content = R.string.lernmodus_cancel_desc,
                    actionButton = R.string.lernmodus_cancel_confirm
                )
            }
        },
        content = content,
        bottomBar = {
            if (surveyScreenData.destination == DuellmodusDestination.QUESTIONS) {
                QuizBottomBar(
                    isEvaluationEnabled = isEvaluationEnabled,
                    selectedAnswers = surveyScreenData.selectedAnswers,
                    onEvaluateClicked = onEvaluateClicked,
                    onNextClicked = onNextClicked,
                    isLastQuestion = isLastQuestion,
                    onQuizComplete = onQuizComplete
                )
            }
        }
    )
}