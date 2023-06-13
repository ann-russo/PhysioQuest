package com.example.physioquest.screens.quiz.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.physioquest.common.composable.SelectableAnswerOption
import com.example.physioquest.common.util.answerCard
import com.example.physioquest.model.Answer
import com.example.physioquest.model.Question
import com.example.physioquest.ui.theme.slightlyDeemphasizedAlpha

@Composable
fun QuizQuestion(
    quizQuestion: Question,
    selectedAnswers: List<Int>,
    isEvaluationEnabled: Boolean,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        CategoryItem(quizQuestion.category)
        QuestionItem(quizQuestion.content)
        AnswerItemsList(
            answers = quizQuestion.answers,
            selectedAnswers = selectedAnswers,
            isEvaluationEnabled = isEvaluationEnabled,
            onAnswerSelected = onAnswerSelected
        )
    }
}

@Composable
fun AnswerItemsList(
    answers: List<Answer>,
    selectedAnswers: List<Int>,
    isEvaluationEnabled: Boolean,
    onAnswerSelected: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 20.dp)
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
                        modifier = Modifier.answerCard(),
                        onClick = { onAnswerSelected(index) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 16.dp)
    ) {
        Text(
            text = category.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun QuestionItem(questionContent: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 0.dp),
    ) {
        Text(
            text = questionContent,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha),
            fontWeight = FontWeight.W500,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    shape = MaterialTheme.shapes.small
                )
                .padding(vertical = 24.dp, horizontal = 16.dp)
        )
    }
}