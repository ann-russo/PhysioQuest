package com.example.physioquest.screens.lernmodus

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.R
import com.example.physioquest.common.composable.ActionToolBar
import com.example.physioquest.common.composable.SelectableAnswerOption
import com.example.physioquest.common.util.card
import com.example.physioquest.common.util.toolbarActions
import com.example.physioquest.model.Answer
import com.example.physioquest.model.Question
import com.example.physioquest.R.string as AppText

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LernmodusScreen(
    restartApp: (String) -> Unit,
    openScreen: (String) -> Unit,
    viewModel: LernmodusViewModel = hiltViewModel()
) {
    var questions by rememberSaveable { mutableStateOf(viewModel.questions) }
    var currentQuestionIndex by rememberSaveable { mutableStateOf(0) }
    var numCorrectAnswers by rememberSaveable { mutableStateOf(0) }
    val selectedAnswersState = rememberSaveable { mutableStateOf(mutableListOf<Int>()) }
    val selectedAnswers = selectedAnswersState.value

    var isEvaluationEnabled by remember { mutableStateOf(true) }
    val evaluationStatus by remember(questions) {
        derivedStateOf {
            List(questions[currentQuestionIndex].answers.size) { index ->
                selectedAnswers.contains(index) && questions[currentQuestionIndex].answers[index].isCorrect
            }.toMutableStateList()
        }
    }

    var displayCategories by remember { mutableStateOf(true) }
    var selectedCategory by rememberSaveable { mutableStateOf("") }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            ActionToolBar(
                title = AppText.lernmodus_title,
                modifier = Modifier.toolbarActions(),
                endActionIcon = R.drawable.ic_exit,
                endAction = { viewModel.onSignOutClick(restartApp) }
            )

            if (viewModel.isLoading.value) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else if (displayCategories) {
                CategoryScreen(
                    categories = viewModel.getCategories(),
                    onCategorySelected = { category ->
                        selectedCategory = category
                        questions = viewModel.questions.filter { it.category == selectedCategory }
                        displayCategories = false
                    }
                )
            }
            else if (currentQuestionIndex < questions.size) {
                ProgressIndicator(
                    progressText = "${currentQuestionIndex + 1}/${questions.size}",
                    modifier = Modifier.padding(vertical = 5.dp)
                )
                questions.getOrNull(currentQuestionIndex)?.let { question ->
                    QuestionItem(question)
                    AnswersList(
                        answers = question.answers,
                        selectedAnswers = selectedAnswers,
                        isEvaluationEnabled = isEvaluationEnabled,
                        onAnswerSelected = { answerIndex ->
                            if (isEvaluationEnabled) {
                                selectedAnswersState.value = selectedAnswers.toMutableList().apply {
                                    if (contains(answerIndex)) {
                                        remove(answerIndex)
                                    } else {
                                        add(answerIndex)
                                    }
                                }
                                evaluationStatus[answerIndex] = false
                            }
                        },
                        onEvaluateClicked = {
                            val correctAnswers = question.answers.filter { it.isCorrect }
                            val selectedCorrectAnswers =
                                selectedAnswers.map { question.answers[it] }
                            val isCorrect =
                                correctAnswers.size == selectedCorrectAnswers.size && selectedCorrectAnswers.containsAll(
                                    correctAnswers
                                )

                            for (i in question.answers.indices) {
                                evaluationStatus[i] =
                                    i in selectedAnswers && isCorrect || i !in selectedAnswers && !isCorrect
                            }

                            if (isCorrect) {
                                numCorrectAnswers++
                            }
                            isEvaluationEnabled = false
                        },
                        onNextClicked = {
                            currentQuestionIndex++
                            selectedAnswers.clear()
                            isEvaluationEnabled = true
                        }
                    )
                }
            } else {
                ResultsScreen(
                    correct = numCorrectAnswers,
                    total = questions.size,
                    openScreen = openScreen
                )
            }
        }
    }
}

@Composable
fun AnswersList(
    answers: List<Answer>,
    selectedAnswers: List<Int>,
    isEvaluationEnabled: Boolean,
    onAnswerSelected: (Int) -> Unit,
    onEvaluateClicked: () -> Unit,
    onNextClicked: () -> Unit
) {
    val isAnyAnswerSelected = selectedAnswers.isNotEmpty()
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Box(Modifier.weight(1f)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                contentPadding = PaddingValues(0.dp, 10.dp)
            ) {
                itemsIndexed(answers) { index, answer ->
                    val isSelected = selectedAnswers.contains(index)
                    val isCorrect = answer.isCorrect

                    SelectableAnswerOption(
                        isSelected = isSelected,
                        isEnabled = isEvaluationEnabled,
                        correctChoice = isSelected == isCorrect,
                        title = answer.content,
                        modifier = Modifier.card(),
                        onClick = { onAnswerSelected(index) }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (isEvaluationEnabled) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = isAnyAnswerSelected,
                onClick = onEvaluateClicked
            ) {
                Text(stringResource(AppText.submit).uppercase())
            }
        } else {
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedAnswers.isNotEmpty(),
                onClick = onNextClicked
            ) {
                Text(stringResource(AppText.next_question).uppercase())
            }
        }
    }
}

@Composable
fun QuestionItem(question: Question) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp)
    ) {
        Text(
            text = question.content,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun ProgressIndicator(progressText: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp)
    ) {
        LinearProgressIndicator(
            progress = calculateProgress(progressText),
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = progressText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
private fun calculateProgress(progressText: String): Float {
    val current = progressText.substringBefore("/")
    val total = progressText.substringAfter("/")
    return current.toFloat() / total.toFloat()
}
