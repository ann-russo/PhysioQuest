package com.example.physioquest.screens.lernmodus

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.physioquest.R
import com.example.physioquest.common.composable.ActionToolBar
import com.example.physioquest.common.composable.AntwortCard
import com.example.physioquest.common.util.antwortCard
import com.example.physioquest.common.util.fieldModifier
import com.example.physioquest.common.util.smallSpacer
import com.example.physioquest.common.util.toolbarActions
import com.example.physioquest.model.Antwort
import com.example.physioquest.model.Frage

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LernmodusScreen(
    restartApp: (String) -> Unit,
    openScreen: (String) -> Unit,
    viewModel: LernmodusViewModel = hiltViewModel()
) {
    val fragen = viewModel.fragen.collectAsStateWithLifecycle(emptyList())

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            ActionToolBar(
                title = R.string.lernmodus_title,
                modifier = Modifier.toolbarActions(),
                endActionIcon = R.drawable.ic_exit,
                endAction = { viewModel.onSignOutClick(restartApp) }
            )
            Spacer(Modifier.smallSpacer())

            LazyColumn {
                items(fragen.value.size) { index ->
                    val frage = fragen.value[index]
                    Text(
                        text = frage.kategorie,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fieldModifier()
                    )
                    FrageItem(frage = frage)
                    AntwortList(antworten = frage.antworten, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun FrageItem(frage: Frage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = frage.frageInhalt, style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.smallSpacer())
    }
}

@Composable
fun AntwortList(
    antworten: List<Antwort>,
    viewModel: LernmodusViewModel
) {
    Box(
        modifier = Modifier
            .height(550.dp)
            .fillMaxHeight()
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(4) { index ->
                val antwort = antworten[index]
                AntwortCard(
                    antwortText = antwort.antwortInhalt,
                    onCardClick = { viewModel.validateAntwort(antwort) },
                    modifier = Modifier.antwortCard()
                )
            }
        }
    }
}