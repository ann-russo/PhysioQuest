package com.example.physioquest.common.composable

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.physioquest.R.drawable as AppIcon

@Composable
fun EmailField(
    value: String,
    onNewValue: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorText: Int?,
    supportingText: Int?,
    modifier: Modifier = Modifier
) {
    TextField(
        label = { Text(label) },
        singleLine = true,
        modifier = modifier,
        value = value,
        isError = isError,
        supportingText = {
            if (isError && errorText != null) {
                Text(stringResource(errorText))
            } else if (supportingText != null) {
                Text(stringResource(supportingText))
            }
        },
        onValueChange = { onNewValue(it) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = label
            )
        }
    )
}

@Composable
fun UsernameField(
    value: String,
    onNewValue: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorText: Int?,
    supportingText: Int?,
    modifier: Modifier = Modifier
) {
    TextField(
        label = { Text(label) },
        singleLine = true,
        modifier = modifier,
        value = value,
        isError = isError,
        supportingText = {
            if (isError && errorText != null) {
                Text(stringResource(errorText))
            } else if (supportingText != null) {
                Text(stringResource(supportingText))
            }
        },
        onValueChange = { onNewValue(it) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = label
            )
        }
    )
}

@Composable
fun PasswordField(
    value: String,
    onNewValue: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorText: Int?,
    supportingText: Int?,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    val icon =
        if (isVisible) painterResource(AppIcon.ic_visibility_on)
        else painterResource(AppIcon.ic_visibility_off)

    val visualTransformation =
        if (isVisible) VisualTransformation.None else PasswordVisualTransformation()

    TextField(
        label = { Text(label) },
        modifier = modifier,
        value = value,
        onValueChange = { onNewValue(it) },
        isError = isError,
        supportingText = {
            if (isError && errorText != null) {
                Text(stringResource(errorText))
            } else if (supportingText != null) {
                Text(stringResource(supportingText))
            }
        },
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock") },
        trailingIcon = {
            IconButton(onClick = { isVisible = !isVisible }) {
                Icon(painter = icon, contentDescription = "Visibility")
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = visualTransformation
    )
}