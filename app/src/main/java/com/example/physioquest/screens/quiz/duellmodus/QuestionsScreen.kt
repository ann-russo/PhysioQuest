package com.example.physioquest.screens.quiz.duellmodus

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.physioquest.R
import com.example.physioquest.common.composable.CenteredTopAppBar
import com.example.physioquest.model.Question

@Composable
fun QuestionsScreen(
    questions: List<Question>
) {
    Scaffold(
        topBar = {
            CenteredTopAppBar(
                title = R.string.duellmodus_title,
                onClosePressed = {}
            )
        },
        content = { paddingValues ->
            val modifier = Modifier.padding(paddingValues)
            Column(modifier = modifier) {
                Text(
                    text = "Erste Frage: ${questions[0].content}",
                    textAlign = TextAlign.Center
                )
            }
        },
    )
}