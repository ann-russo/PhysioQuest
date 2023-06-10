package com.example.physioquest.screens.quiz.duellmodus

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.physioquest.common.composable.CenteredTopAppBar
import com.example.physioquest.R.string as AppText

@Composable
fun FindOpponentScreen(
    opponent: String,
    onClosePressed: () -> Unit,
    onStartPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenteredTopAppBar(
                title = AppText.duellmodus_title,
                onClosePressed = onClosePressed
            )
        },
        content = { paddingValues ->
            val modifier = Modifier.padding(paddingValues)
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
        },
    )
}