package com.example.physioquest.screens.quiz.duelresult

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.R
import com.example.physioquest.common.composable.MedalBadge
import com.example.physioquest.ui.theme.md_theme_light_onSurfaceVariant

@Composable
fun DuelResultsScreen(
    duelId: String,
    openScreen: (String) -> Unit,
    viewModel: DuelResultsViewModel = hiltViewModel()
) {
    viewModel.findDuelById(duelId)
    val initUserPoints = String.format("%.2f", viewModel.initUserResult.scorePoints)
    val initUserPercent = String.format("%.2f", viewModel.initUserResult.scorePercent)
    val initUserProgress = initUserPercent.toFloat()
    val initUserAngle = remember { Animatable(0f) }

    val opponentUserPoints = String.format("%.2f", viewModel.opponentUserResult.scorePoints)
    val opponentUserPercent = String.format("%.2f", viewModel.opponentUserResult.scorePercent)
    val opponentUserProgress = opponentUserPercent.toFloat()
    val opponentUserAngle = remember { Animatable(0f) }

    LaunchedEffect(initUserProgress) {
        initUserAngle.animateTo(
            targetValue = initUserProgress,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessVeryLow
            )
        )
    }
    LaunchedEffect(opponentUserProgress) {
        opponentUserAngle.animateTo(
            targetValue = opponentUserProgress,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessVeryLow
            )
        )
    }

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

        val context = LocalContext.current
        Text(
            text = if (viewModel.isCurrentUserWinner()) {
                context.getString(R.string.duel_result_current_won)
            } else if (viewModel.isDraw()) {
                context.getString(R.string.duel_result_draw)
            } else {
                context.getString(R.string.duel_result_current_lost,
                viewModel.duel.winnerUser.username)
            },
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        )
        Text(
            text = if (viewModel.isCurrentUserWinner()) {
                context.getString(R.string.duel_result_won_tip)
            } else if (viewModel.isDraw()) {
                context.getString(R.string.duel_result_draw_tip)
            } else {
                context.getString(R.string.duel_result_lost_tip)
            },
            style = MaterialTheme.typography.titleSmall,
            color = Color.Gray,
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                MedalBadge(initUserAngle.value, 90.dp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = viewModel.duel.initUser.username,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.CenterHorizontally)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "$initUserPoints : $opponentUserPoints",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.duel_result_total),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Light
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                MedalBadge(opponentUserAngle.value, 90.dp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = viewModel.duel.opponentUser.username,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(R.drawable.success),
            contentDescription = null
        )
        Button(
            onClick = {
                viewModel.awardWinnerXP()
                openScreen(HOME_SCREEN) },
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