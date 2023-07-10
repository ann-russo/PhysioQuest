package com.example.physioquest.screens.quiz.lernmodus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.common.composable.AnimatedDialog
import com.example.physioquest.common.composable.CenteredTopAppBar
import com.example.physioquest.common.composable.QuizTopAppBar
import com.example.physioquest.screens.quiz.shared.QuizBottomBar
import com.example.physioquest.R.string as AppText

@Composable
fun LernmodusScreen(
    surveyScreenData: LernmodusScreenData,
    isEvaluationEnabled: Boolean,
    onEvaluateClicked: () -> Unit,
    onNextClicked: () -> Unit,
    onClosePressed: () -> Unit,
    isLastQuestion: Boolean,
    onQuizComplete: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    val viewModel: LernmodusViewModel = hiltViewModel()
    if (viewModel.isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val showDialog = remember { mutableStateOf(false) }
        Scaffold(
            topBar = {
                if (surveyScreenData.selectedCategory == null) {
                    CenteredTopAppBar(
                        title = AppText.categories,
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
                        title = AppText.lernmodus_cancel,
                        content = AppText.lernmodus_cancel_desc,
                        actionButton = AppText.lernmodus_cancel_confirm
                    )
                }
            },
            content = content,
            bottomBar = {
                if (surveyScreenData.selectedCategory != null) {
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



}