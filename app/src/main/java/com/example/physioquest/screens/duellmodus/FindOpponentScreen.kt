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
                title = AppText.find_opponent,
                onClosePressed = onClosePressed
            )
        },
        content = { paddingValues ->
            val modifier = Modifier.padding(paddingValues)
            Column(modifier = modifier) {
                Text(text = "Test")
            }
        },
    )
}