package com.example.physioquest.screens.duellmodus

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.common.composable.CenteredTopAppBar
import com.example.physioquest.R.string as AppText

@Composable
fun FindOpponentScreen(
    onClosePressed: () -> Unit,
    viewModel: DuellmodusViewModel = hiltViewModel()
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
            val randomOpponent = viewModel.opponent

            Column(modifier = modifier) {
                Text(text = "Dein Gegner ist: ${randomOpponent?.username}")
            }
        },
    )
}