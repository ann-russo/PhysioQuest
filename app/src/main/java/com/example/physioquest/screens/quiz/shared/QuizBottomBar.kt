package com.example.physioquest.screens.quiz.shared

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.physioquest.R

@Composable
fun QuizBottomBar(
    isEvaluationEnabled: Boolean,
    selectedAnswers: List<Int>,
    onEvaluateClicked: () -> Unit,
    onNextClicked: () -> Unit,
    isLastQuestion: Boolean,
    onQuizComplete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 7.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            if (isEvaluationEnabled) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedAnswers.isNotEmpty(),
                    onClick = onEvaluateClicked
                ) {
                    Text(stringResource(R.string.submit).uppercase())
                }
            } else if (isLastQuestion)
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedAnswers.isNotEmpty(),
                    onClick = onQuizComplete
                ) {
                    Text(stringResource(R.string.done).uppercase())
                }
            else {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedAnswers.isNotEmpty(),
                    onClick = onNextClicked
                ) {
                    Text(stringResource(R.string.next_question).uppercase())
                }
            }
        }
    }
}

@Composable
fun SimpleBottomBar(
    buttonText: Int,
    onButtonClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 7.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onButtonClick
            ) {
                Text(stringResource(buttonText).uppercase())
            }
        }
    }
}