package com.example.physioquest.common.composable

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.example.physioquest.R

@Composable
fun AnimatedDialog(
    visible: Boolean,
    onClose: () -> Unit,
    onConfirm: () -> Unit,
    @StringRes title: Int,
    @StringRes content: Int,
    @StringRes actionButton: Int,
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
        exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
    ) {
        AlertDialog(
            onDismissRequest = { onClose() },
            title = {
                Text(stringResource(title))
            },
            text = {
                Text(stringResource(content))
            },
            confirmButton = {
                TextButton(
                    onClick = { onConfirm() }
                ) {
                    Text(
                        text = stringResource(actionButton),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onClose() }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}