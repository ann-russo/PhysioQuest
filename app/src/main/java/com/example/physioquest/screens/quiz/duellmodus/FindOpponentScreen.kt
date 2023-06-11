package com.example.physioquest.screens.quiz.duellmodus

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun FindOpponentScreen(
    opponent: String,
    onStartPressed: () -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Dein Gegner ist: $opponent",
            textAlign = TextAlign.Center
        )
        OutlinedButton(
            onClick = { onStartPressed() }
        ) {
            Text(
                text = "Quiz-Duell beginnen",
                textAlign = TextAlign.Center
            )
        }
    }
}