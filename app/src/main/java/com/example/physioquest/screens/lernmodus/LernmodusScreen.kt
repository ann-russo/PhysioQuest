package com.example.physioquest.screens.lernmodus

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.R
import com.example.physioquest.common.composable.ActionToolBar
import com.example.physioquest.common.composable.AntwortCard
import com.example.physioquest.common.util.antwortCard
import com.example.physioquest.common.util.fieldModifier
import com.example.physioquest.common.util.smallSpacer
import com.example.physioquest.common.util.toolbarActions
import com.example.physioquest.model.Antwort
import com.example.physioquest.model.Frage
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LernmodusScreen(
    restartApp: (String) -> Unit,
    openScreen: (String) -> Unit,
    viewModel: LernmodusViewModel = hiltViewModel()
) {
    val fragen = viewModel.fragen
    var currentQuestionIndex by rememberSaveable { mutableStateOf(0) }
    var selectedIndex by rememberSaveable { mutableStateOf(-1) }
    var numCorrectAnswers by rememberSaveable { mutableStateOf(0) }
    val isLoading = viewModel.isLoading

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

            if (isLoading.value) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (currentQuestionIndex < fragen.size) {
                Spacer(Modifier.smallSpacer())

                Text(
                    text = fragen.getOrNull(currentQuestionIndex)?.kategorie ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fieldModifier()
                )

                fragen.getOrNull(currentQuestionIndex)?.let { currentQuestion ->
                    FrageItem(frage = currentQuestion, currentQuestionIndex+1, fragen.size)
                    AntwortList(
                        antworten = currentQuestion.antworten,
                        onAnswerSelected = {
                            selectedIndex = -1
                            currentQuestionIndex++
                        },
                        selectedIndex = selectedIndex,
                        onCardClicked = { index ->
                            if (selectedIndex == -1) {
                                selectedIndex = index
                                if (currentQuestion.antworten[index].antwortKorrekt) {
                                    numCorrectAnswers++
                                }
                            }
                        }
                    )
                }

            } else {
                Text(
                    text = "$numCorrectAnswers/${fragen.size} richtig beantwortet",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center).wrapContentWidth(),
                )
                LaunchedEffect(true) {
                    delay(3000)
                    openScreen(HOME_SCREEN)
                }
            }
        }
    }
}

@Composable
fun FrageItem(frage: Frage, index: Int, fragenAnzahl: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "$index/$fragenAnzahl ${frage.frageInhalt}",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(Modifier.smallSpacer())
    }
}

@Composable
fun AntwortList(
    antworten: List<Antwort>,
    onAnswerSelected: () -> Unit,
    selectedIndex: Int,
    onCardClicked: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight()
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            userScrollEnabled = false,
            contentPadding = PaddingValues(0.dp, 50.dp)
        ) {
            items(4) { index ->
                val antwort = antworten[index]
                val isEnabled = selectedIndex == -1

                AntwortCard(
                    antwortText = antwort.antwortInhalt,
                    onCardClick = {
                        if (isEnabled) {
                            onCardClicked(index)
                        }
                    },
                    isSelected = index == selectedIndex,
                    isCorrect = antwort.antwortKorrekt,
                    isEnabled = isEnabled,
                    modifier = Modifier.antwortCard()
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Button(
                modifier = Modifier.padding(16.dp, 8.dp, 10.dp, 0.dp),
                enabled = selectedIndex != -1,
                onClick = onAnswerSelected
            )
            {
                Text(text = stringResource(R.string.next_question))
            }
        }
    }
}