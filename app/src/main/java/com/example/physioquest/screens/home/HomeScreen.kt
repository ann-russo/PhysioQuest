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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Scaffold() {
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()) {
            ActionToolBar(
                title = AppText.app_name,
                modifier = Modifier.toolbarActions(),
                endActionIcon = AppIcon.ic_exit,
                endAction = { viewModel.onSignOutClick(restartApp)}
            )
            Spacer(modifier = Modifier.smallSpacer())

            ElevatedCard(
                title = AppText.lernmodus_title,
                subtitle = AppText.lernmodus_subtitle,
                actionText = AppText.lernmodus_action,
                modifier = Modifier.card(),
                onButtonClick = { /*TODO*/ }
            )

            Spacer(modifier = Modifier.smallSpacer())

            ElevatedCard(
                title = AppText.duellmodus_title,
                subtitle = AppText.duellmodus_subtitle,
                actionText = AppText.duellmodus_action,
                modifier = Modifier.card(),
                onButtonClick = { /*TODO*/ }
            )

            val currentUser = viewModel.user.value?.username

            Text(
                text = "Hello $currentUser!",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}