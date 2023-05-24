package com.example.physioquest.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.R
import com.example.physioquest.common.composable.ActionToolBar
import com.example.physioquest.common.composable.ElevatedCard
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
    Scaffold(bottomBar = { BottomNavBar() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            ActionToolBar(
                title = AppText.app_name,
                modifier = Modifier.toolbarActions(),
                endActionIcon = AppIcon.ic_exit,
                endAction = { viewModel.onSignOutClick(restartApp) }
            )

            val currentUser = viewModel.user.value?.username
            Text(
                text = stringResource(AppText.welcome) + " $currentUser!",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.smallSpacer())
            Spacer(modifier = Modifier.smallSpacer())

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
    }
}

@Composable
fun BottomNavBar() {
    val items = listOf("Home", "Leaderboard", "Account")
    var selectedItem by remember { mutableStateOf(0) }
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    when (item) {
                        "Home" -> Icon(Icons.Filled.Home, contentDescription = item)
                        "Leaderboard" -> Icon(
                            painterResource(R.drawable.leaderboard),
                            modifier = Modifier.size(26.dp),
                            contentDescription = item
                        )

                        "Account" -> Icon(Icons.Filled.AccountCircle, contentDescription = item)
                    }
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )
        }
    }
}