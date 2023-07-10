package com.example.physioquest.screens.quiz.duelresult

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.R
import com.example.physioquest.ui.theme.md_theme_light_onSurfaceVariant

@Composable
fun DuelResultsScreen(
    duelId: String,
    openScreen: (String) -> Unit,
    viewModel: DuelResultsViewModel = hiltViewModel()
) {
    viewModel.findDuelById(duelId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.duel_result),
                style = MaterialTheme.typography.headlineSmall,
                color = md_theme_light_onSurfaceVariant,
                fontWeight = FontWeight.W400,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Divider(modifier = Modifier.weight(1f))
        }

        Text(
            text = "${viewModel.duel.winnerUser.username} hat das Duell gewonnen!",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        )

        Text(
            text = "${viewModel.duel.initUser.username} hat ${viewModel.initUserResult.scorePoints} erreicht",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.CenterHorizontally)
        )

        Text(
            text = "${viewModel.duel.opponentUser.username} hat ${viewModel.opponentUserResult.scorePoints} erreicht",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.CenterHorizontally)
        )

        Image(
            painter = painterResource(R.drawable.success),
            contentDescription = null
        )
        Button(
            onClick = { openScreen(HOME_SCREEN) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = stringResource(R.string.duel_finish),
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(R.string.duel_finish).uppercase())
        }
    }
}