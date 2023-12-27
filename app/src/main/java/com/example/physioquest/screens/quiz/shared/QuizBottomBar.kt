package com.example.physioquest.screens.quiz.shared

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.physioquest.R
import kotlinx.coroutines.delay

@Composable
fun QuizBottomBar(
    isDuelMode: Boolean,
    selectedAnswers: List<Int>,
    quizActions: QuizActions
) {
    var timer by remember { mutableIntStateOf(30) }
    val animatedTimer by animateIntAsState(timer, label = "Quiz Question Timer")
    val progressColor = lerp(Color.Green, Color.Red, 1f - animatedTimer / 30f)

    LaunchedEffect(key1 = quizActions.isEvaluationEnabled, key2 = isDuelMode) {
        timer = 30
    }
    LaunchedEffect(key1 = timer, key2 = isDuelMode, key3 = quizActions.isEvaluationEnabled) {
        if (timer > 0 && isDuelMode && quizActions.isEvaluationEnabled) {
            delay(1000)
            timer--
        } else if (timer == 0) {
            quizActions.onEvaluateClicked()
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 7.dp,
    ) {
        Column {
            if (quizActions.isEvaluationEnabled && isDuelMode) {
                LinearProgressIndicator(
                    progress = { animatedTimer / 30f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = progressColor
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                if (quizActions.isEvaluationEnabled) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedAnswers.isNotEmpty(),
                        onClick = quizActions.onEvaluateClicked
                    ) {
                        Text(stringResource(R.string.submit).uppercase())
                    }
                } else if (quizActions.isLastQuestion)
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = quizActions.onQuizComplete
                    ) {
                        Text(stringResource(R.string.done).uppercase())
                    }
                else {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = quizActions.onNextClicked
                    ) {
                        Text(stringResource(R.string.next_question).uppercase())
                    }
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