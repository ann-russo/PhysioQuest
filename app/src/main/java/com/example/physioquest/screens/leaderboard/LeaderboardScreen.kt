package com.example.physioquest.screens.leaderboard

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.R
import com.example.physioquest.common.composable.ActionToolBar
import com.example.physioquest.common.composable.BottomNavBar
import com.example.physioquest.model.User

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LeaderboardScreen(
    restartApp: (String) -> Unit,
    openScreen: (String) -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val topUsers = viewModel.topUsers
    val currentUser = viewModel.user.value

    Scaffold(
        topBar = {
            ActionToolBar(
                title = R.string.leaderboard,
                level = currentUser.level,
                xpProgress = viewModel.calculateXpProgress(),
                endAction = { viewModel.onSignOutClick(restartApp) }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(paddingValues)
            ) {
                LazyColumn {
                    itemsIndexed(topUsers) { index, user ->
                        val rank = viewModel.retrieveRankName(user.level)
                        LeaderboardItem(index + 1, user, stringResource(rank), currentUser)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        },
        bottomBar = {
            BottomNavBar(
                selectedScreen = "Leaderboard",
                onScreenSelected = { screen ->
                    when (screen) {
                        "Home" -> {
                            viewModel.onHomeClick(openScreen)
                        }

                        "Leaderboard" -> {
                            viewModel.onLeaderboardClick(openScreen)
                        }

                        "Profil" -> {
                            viewModel.onAccountClick(openScreen)
                        }
                    }
                })
        }
    )
}

@Composable
fun LeaderboardItem(
    position: Int,
    user: User,
    rank: String,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val isCurrentUser = user.id == currentUser.id
    val backgroundColor = if (isCurrentUser) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface

    Surface(
        color = backgroundColor,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .height(70.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 11.dp, horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "$position",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f).padding(start = 5.dp)
            )
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = rank,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            val color = Color(0xffffd94a)
            if (position == 1) {
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    painter = painterResource(R.drawable.trophy_48px),
                    contentDescription = "First Position",
                    tint = color,
                    modifier = Modifier.size(35.dp)
                )
                Spacer(modifier = Modifier.weight(2f))
            } else {
                Spacer(modifier = Modifier.weight(3f))
            }
            Text(
                text = "${user.xp} XP",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}