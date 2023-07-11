package com.example.physioquest.common.composable

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.physioquest.R
import com.example.physioquest.ui.theme.PhysioQuestTheme
import com.example.physioquest.ui.theme.md_theme_light_inverseOnSurface
import com.example.physioquest.R.drawable as AppIcon
import com.example.physioquest.R.string as AppText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicToolBar(@StringRes title: Int) {
    TopAppBar(
        title = { Text(stringResource(title)) },
        modifier = Modifier.background(toolbarColor())
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionToolBar(
    @StringRes title: Int? = null,
    titleAsString: String? = null,
    level: Int = 0,
    xpProgress: Float = 0f,
    @DrawableRes endActionIcon: Int,
    modifier: Modifier,
    endAction: () -> Unit,
    onBackPressed: (() -> Unit)? = null
) {
    var dropDownMenuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (title != null) {
                Text(stringResource(title))
            } else if (titleAsString != null) {
                Text(text = titleAsString)
            }
        },
        modifier = Modifier.background(toolbarColor()),
        navigationIcon = {
            if (onBackPressed != null) {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            SmallLevelProgressCircle(level, xpProgress)
            TopAppBarMenu(
                imageVector = Icons.Outlined.MoreVert,
                description = stringResource(AppText.dropdown)
            ) {
                dropDownMenuExpanded = true
            }
            DropdownMenu(
                expanded = dropDownMenuExpanded,
                onDismissRequest = {
                    dropDownMenuExpanded = false
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(AppText.logout),
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 16.sp
                        )
                    },
                    onClick = endAction,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(AppIcon.logout),
                            contentDescription = stringResource(AppText.logout),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenteredTopAppBar(
    title: Int,
    onClosePressed: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        CenterAlignedTopAppBar(
            title = { TopAppBarTitle(title = title) },
            actions = {
                IconButton(
                    onClick = onClosePressed,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = stringResource(AppText.cancel),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTopAppBar(
    questionIndex: Int,
    totalQuestionsCount: Int,
    onClosePressed: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        CenterAlignedTopAppBar(
            title = {
                QuizTopAppBarTitle(
                    questionIndex = questionIndex,
                    totalQuestionsCount = totalQuestionsCount,
                )
            },
            actions = {
                IconButton(
                    onClick = onClosePressed,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )

        val animatedProgress by animateFloatAsState(
            targetValue = (questionIndex + 1) / totalQuestionsCount.toFloat(),
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
        )
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        )
    }
}

@Composable
private fun TopAppBarTitle(
    @StringRes title: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun QuizTopAppBarTitle(
    questionIndex: Int,
    totalQuestionsCount: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = (questionIndex + 1).toString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = " von $totalQuestionsCount",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    }
}

@Composable
private fun toolbarColor(darkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (darkTheme) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
}

@Composable
fun TopAppBarMenu(
    imageVector: ImageVector,
    description: String,
    onClick: () -> Unit
) {
    IconButton(onClick = {
        onClick()
    }) {
        Icon(imageVector = imageVector, contentDescription = description)
    }
}

@Composable
fun BottomNavBar(
    selectedScreen: String,
    onScreenSelected: (String) -> Unit
) {
    val items = listOf("Home", "Leaderboard", "Profil")
    val selectedItem = items.indexOf(selectedScreen)
    val navBarColor = md_theme_light_inverseOnSurface

    PhysioQuestTheme(navigationBarColor = navBarColor) {
        NavigationBar {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = {
                        when (item) {
                            "Home" -> Icon(Icons.Filled.Home, contentDescription = item)
                            "Leaderboard" -> Icon(
                                painterResource(AppIcon.leaderboard),
                                modifier = Modifier.size(26.dp),
                                contentDescription = item
                            )

                            "Profil" -> Icon(Icons.Filled.AccountCircle, contentDescription = item)
                        }
                    },
                    label = { Text(item) },
                    selected = selectedItem == index,
                    onClick = { onScreenSelected(item) }
                )
            }
        }
    }
}

@Composable
fun SmallLevelProgressCircle(level: Int, xpProgress: Float) {
    val color = colorResource(R.color.teal_200)
    val bgColor = MaterialTheme.colorScheme.secondaryContainer
    val progressAngle = xpProgress * 360f

    Box(
        modifier = Modifier.size(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(30.dp)) {
            val radius = size.minDimension / 2
            val strokeWidth = 15f
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
        Text(
            text = level.toString(),
            fontWeight = FontWeight.Bold
        )
    }
}