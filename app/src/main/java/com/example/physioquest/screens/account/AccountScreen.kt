package com.example.physioquest.screens.account

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.R
import com.example.physioquest.common.composable.ActionToolBar
import com.example.physioquest.common.composable.BottomNavBar
import com.example.physioquest.R.drawable as AppIcon
import com.example.physioquest.R.string as AppText

@Composable
fun AccountContent(
    data: AccountScreenData,
    userLevel: Int,
    xpProgress: Float,
    accountActions: AccountActions,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            ActionToolBar(
                titleAsString = data.title,
                level = userLevel,
                xpProgress = xpProgress,
                endAction = { accountActions.onSignOutClick() },
                onBackPressed = if (data.destination != AccountDestination.PROFIL) accountActions.onBackClick else null
            )
        },
        content = content,
        bottomBar = {
            BottomNavBar(
                selectedScreen = stringResource(AppText.account),
                onScreenSelected = { screen ->
                    when (screen) {
                        "Home" -> {
                            accountActions.onHomeClick()
                        }

                        "Leaderboard" -> {
                            accountActions.onLeaderboardClick()
                        }

                        "Profil" -> {
                            accountActions.onAccountClick()
                        }
                    }
                }
            )
        }
    )
}

@Composable
fun AccountScreen(
    accountData: AccountData,
    restartApp: (String) -> Unit,
    modifier: Modifier,
    viewModel: AccountViewModel = hiltViewModel()
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        ProfileHeader(
            accountData = accountData
        )
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
fun ProfileHeader(
    accountData: AccountData
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 40.dp)
            .height(150.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            LevelProgressCircle(accountData.userLevel, accountData.xpInCurrentLevel, accountData.xpNeededForNextLevel)
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = accountData.username,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = accountData.rank,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
                val remainingXp = accountData.xpNeededForNextLevel - accountData.xpInCurrentLevel
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, color = Color.Gray)) {
                            append("Noch ")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Gray)) {
                            append("$remainingXp XP")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, color = Color.Gray)) {
                            append(" bis Level ")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Gray)) {
                            append("${accountData.userLevel+1}")
                        }
                    },
                    style = MaterialTheme.typography.bodyLarge,
                )

            }
        }
    }
}

@Composable
fun AccountOptionItem(
    @StringRes optionText: Int,
    @DrawableRes optionIcon: Int,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
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
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(optionText),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                modifier = Modifier.weight(1.2f),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
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
        color = MaterialTheme.colorScheme.surfaceVariant,
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
fun LevelProgressCircle(
    level: Int,
    xpInCurrentLevel: Int,
    xpNeededForNextLevel: Int
) {
    val color = colorResource(R.color.teal_200)
    val bgColor = MaterialTheme.colorScheme.secondaryContainer

    val xpProgress = xpInCurrentLevel.toFloat() / xpNeededForNextLevel.toFloat()
    val progressAngle = xpProgress * 360f

    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(100.dp)) {
            val radius = size.minDimension / 2
            val strokeWidth = 25f
            drawCircle(
                color = bgColor,
                radius = radius,
                style = Stroke(strokeWidth)
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = progressAngle,
                useCenter = false,
                style = Stroke(strokeWidth)
            )
        }
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "$level",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "$xpInCurrentLevel/$xpNeededForNextLevel XP",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}