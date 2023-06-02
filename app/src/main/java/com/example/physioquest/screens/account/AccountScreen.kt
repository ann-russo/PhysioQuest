package com.example.physioquest.screens.account

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.R
import com.example.physioquest.common.composable.ActionToolBar
import com.example.physioquest.common.composable.BottomNavBar
import com.example.physioquest.common.util.toolbarActions
import com.example.physioquest.R.drawable as AppIcon
import com.example.physioquest.R.string as AppText

@Composable
fun AccountContent(
    data: AccountScreenData,
    onHomeClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onAccountClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onBackClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            ActionToolBar(
                titleAsString = data.title,
                modifier = Modifier.toolbarActions(),
                endActionIcon = AppIcon.ic_exit,
                endAction = { onSignOutClick() },
                onBackPressed = if (data.destination != AccountDestination.PROFIL) onBackClick else null
            )
        },
        content = content,
        bottomBar = {
            BottomNavBar(
                selectedScreen = stringResource(AppText.account),
                onScreenSelected = { screen ->
                    when (screen) {
                        "Home" -> {
                            onHomeClick()
                        }

                        "Leaderboard" -> {
                            onLeaderboardClick()
                        }

                        "Profil" -> {
                            onAccountClick()
                        }
                    }
                }
            )
        }
    )
}

@Composable
fun AccountScreen(
    restartApp: (String) -> Unit,
    modifier: Modifier,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val username = viewModel.user.value?.username

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        ProfileHeader("$username")

        AccountOptionItem(
            optionText = AppText.edit,
            optionIcon = AppIcon.edit_48px,
            onClick = { viewModel.onEditClick() }
        )
        AccountOptionItem(
            optionText = AppText.stats,
            optionIcon = AppIcon.analytics_48px,
            onClick = { viewModel.onStatisticClick() }
        )
        AccountOptionItem(
            optionText = AppText.settings,
            optionIcon = AppIcon.settings_48px,
            onClick = { viewModel.onSettingsClick() }
        )
        AccountOptionItem(
            optionText = AppText.help,
            optionIcon = AppIcon.help_48px,
            onClick = { viewModel.onHelpClick() }
        )
        LogoutOptionItem(
            optionText = AppText.logout,
            optionIcon = AppIcon.logout,
            onClick = { viewModel.onSignOutClick(restartApp) }
        )
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
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = { onClick() }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painter = painterResource(optionIcon),
                contentDescription = stringResource(optionText),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(optionText),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                modifier = Modifier.weight(1.2f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Composable
fun LogoutOptionItem(
    @StringRes optionText: Int,
    @DrawableRes optionIcon: Int,
    onClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 40.dp)
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = { onClick() }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painter = painterResource(optionIcon),
                contentDescription = stringResource(optionText),
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(optionText),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1.2f)
            )
        }
    }
}


@Composable
fun LevelProgressCircle() {
    val color = colorResource(R.color.teal_200)
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
                color = color,
                startAngle = -90f,
                sweepAngle = 210f,
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


