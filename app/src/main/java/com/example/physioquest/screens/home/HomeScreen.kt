package com.example.physioquest.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.common.composable.ActionToolBar
import com.example.physioquest.common.composable.BottomNavBar
import com.example.physioquest.common.composable.ElevatedCard
import com.example.physioquest.common.util.bigSpacer
import com.example.physioquest.common.util.card
import com.example.physioquest.common.util.smallSpacer
import com.example.physioquest.common.util.toolbarActions
import com.example.physioquest.R.drawable as AppIcon
import com.example.physioquest.R.string as AppText

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    restartApp: (String) -> Unit,
    openScreen: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            ActionToolBar(
                title = AppText.app_name,
                modifier = Modifier.toolbarActions(),
                endActionIcon = AppIcon.ic_exit,
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
                val currentUser = viewModel.user.value?.username
                Text(
                    text = stringResource(AppText.welcome),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Text(
                    text = "$currentUser!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.bigSpacer())
                ElevatedCard(
                    title = AppText.lernmodus_title,
                    subtitle = AppText.lernmodus_subtitle,
                    actionText = AppText.lernmodus_action,
                    modifier = Modifier.card(),
                    onButtonClick = { viewModel.onLernmodusClick(openScreen) }
                )
                Spacer(modifier = Modifier.smallSpacer())
                ElevatedCard(
                    title = AppText.duellmodus_title,
                    subtitle = AppText.duellmodus_subtitle,
                    actionText = AppText.duellmodus_action,
                    modifier = Modifier.card(),
                    onButtonClick = { /*TODO*/ }
                )
            }
        },
        bottomBar = {
            BottomNavBar(
                selectedScreen = stringResource(AppText.home),
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
                }
            )
        }
    )
}