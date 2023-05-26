package com.example.physioquest.common.composable

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
    @StringRes title: Int,
    @DrawableRes endActionIcon: Int,
    modifier: Modifier,
    endAction: () -> Unit
) {
    var dropDownMenuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(stringResource(title)) },
        modifier = Modifier.background(toolbarColor()),
        actions = {
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
                            stringResource(AppText.logout),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = endAction,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(endActionIcon),
                            contentDescription = stringResource(AppText.logout)
                        )
                    }
                )
            }
        }
    )
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
