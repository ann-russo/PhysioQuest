package com.example.physioquest.screens.account

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.R.drawable as AppIcon
import com.example.physioquest.R.string as AppText

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SettingsScreen(
    modifier: Modifier,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        AccountOptionItem(
            optionText = AppText.delete_account,
            optionIcon = AppIcon.delete,
            onClick = { showDialog.value = true }
        )
    }

    AnimatedVisibility(
        visible = showDialog.value,
        enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
        exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
    ) {
        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = {
                showDialog.value = false
            },
            title = {
                Text(stringResource(AppText.delete_account))
            },
            text = {
                Text(stringResource(AppText.delete_description))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAccount()
                        showDialog.value = false
                    }
                ) {
                    Text(
                        text = stringResource(AppText.delete_confirm),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text(stringResource(AppText.cancel))
                }
            }
        )
    }
}