package com.example.physioquest.screens.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.common.composable.AnimatedDialog
import com.example.physioquest.R.drawable as AppIcon
import com.example.physioquest.R.string as AppText

@Composable
fun SettingsScreen(
    restartApp: (String) -> Unit,
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
        AnimatedDialog(
            visible = showDialog.value,
            onClose = { showDialog.value = false },
            onConfirm = {
                viewModel.deleteAccount(restartApp)
                showDialog.value = false },
            title = AppText.delete_account,
            content = AppText.delete_description,
            actionButton = AppText.delete_confirm
        )
    }
}