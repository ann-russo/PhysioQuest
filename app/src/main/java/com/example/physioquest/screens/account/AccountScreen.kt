package com.example.physioquest.screens.account

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.common.composable.ActionToolBar
import com.example.physioquest.common.composable.BottomNavBar
import com.example.physioquest.common.util.toolbarActions
import com.example.physioquest.R.drawable as AppIcon
import com.example.physioquest.R.string as AppText

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    restartApp: (String) -> Unit,
    openScreen: (String) -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    var isScreenVisible by remember { mutableStateOf(true) }
    val username = viewModel.user.value?.username
    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedScreen = stringResource(AppText.account),
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
    ) {
        AnimatedVisibility(
            visible = isScreenVisible,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { -it })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {
                ActionToolBar(
                    title = AppText.account,
                    modifier = Modifier.toolbarActions(),
                    endActionIcon = AppIcon.ic_exit,
                    endAction = { viewModel.onSignOutClick(restartApp) }
                )

                ProfileHeader("$username")

                AccountOptionItem(
                    optionText = AppText.edit,
                    optionIcon = AppIcon.edit_48px,
                    onClick = { /* Handle Edit Profile click */ }
                )
                AccountOptionItem(
                    optionText = AppText.settings,
                    optionIcon = AppIcon.settings_48px,
                    onClick = {
                        viewModel.onSettingsClick(openScreen)
                        isScreenVisible = false
                    }
                )
                AccountOptionItem(
                    optionText = AppText.stats,
                    optionIcon = AppIcon.analytics_48px,
                    onClick = { /* Handle Notifications click */ }
                )
                AccountOptionItem(
                    optionText = AppText.help,
                    optionIcon = AppIcon.help_48px,
                    onClick = { /* Handle Support click */ }
                )

                Spacer(modifier = Modifier.height(95.dp))
                Divider(modifier = Modifier.padding(horizontal = 30.dp))

                LogoutButton { viewModel.onSignOutClick(restartApp) }
            }
        }
    }
}

@Composable
fun ProfileHeader(username: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) { LevelProgressCircle() }
    Text(
        text = username,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        textAlign = TextAlign.Center
    )
    Text(
        text = "Beginner",
        style = MaterialTheme.typography.bodyLarge,
        color = Color.Gray,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
fun AccountOptionItem(
    @StringRes optionText: Int,
    @DrawableRes optionIcon: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick() })
            .padding(horizontal = 30.dp, vertical = 12.dp)
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(optionIcon),
            contentDescription = stringResource(optionText),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(30.dp))
        Text(
            text = stringResource(optionText),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            onClick = { onClick() }
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                tint = MaterialTheme.colorScheme.tertiary,
                contentDescription = stringResource(AppText.logout),
                modifier = Modifier.size(19.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(AppText.logout),
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 18.sp,
            )
        }
    }
}

@Composable
fun LevelProgressCircle() {
    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(100.dp)) {
            val radius = size.minDimension / 2
            val strokeWidth = 15f
            drawCircle(
                color = Color.Gray,
                radius = radius,
                style = Stroke(strokeWidth)
            )
            drawArc(
                color = Color.Magenta,
                startAngle = -90f,
                sweepAngle = 180f,
                useCenter = false,
                style = Stroke(strokeWidth)
            )
        }
        Text(
            text = "1",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}


