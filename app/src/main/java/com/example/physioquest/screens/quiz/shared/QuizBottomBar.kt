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
    isEvaluationEnabled: Boolean,
    selectedAnswers: List<Int>,
    onEvaluateClicked: () -> Unit,
    onNextClicked: () -> Unit,
    isLastQuestion: Boolean,
    onQuizComplete: () -> Unit
) {
    var timer by remember { mutableIntStateOf(30) }
    val animatedTimer by animateIntAsState(timer)
    val progressColor = lerp(Color.Green, Color.Red, 1f - animatedTimer / 30f)

    LaunchedEffect(key1 = isEvaluationEnabled, key2 = isDuelMode) {
        timer = 30
    }
    LaunchedEffect(key1 = timer, key2 = isDuelMode, key3 = isEvaluationEnabled) {
        if (timer > 0 && isDuelMode && isEvaluationEnabled) {
            delay(1000)
            timer--
        } else if (timer == 0) {
            onEvaluateClicked()
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 7.dp,
    ) {
        Column {
            if (isEvaluationEnabled && isDuelMode) {
                LinearProgressIndicator(
                    progress = animatedTimer / 30f,
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
                        onClick = onQuizComplete
                    ) {
                        Text(stringResource(R.string.done).uppercase())
                    }
                else {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onNextClicked
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