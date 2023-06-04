package com.example.physioquest.common.composable

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.example.physioquest.R.string as AppText

@Composable
fun EmailField(
    value: String,
    onNewValue: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorText: Int?,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        label = { Text(label) },
        singleLine = true,
        modifier = modifier,
        value = value,
        placeholder = { Text(stringResource(AppText.email)) },
        isError = isError,
        supportingText = {
            if (isError && errorText != null) {
                Text(stringResource(errorText))
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
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        label = { Text(label) },
        singleLine = true,
        modifier = modifier,
        value = value,
        placeholder = { Text(stringResource(AppText.username)) },
        isError = isError,
        supportingText = {
            if (isError && errorText != null) {
                Text(stringResource(errorText))
            } else {
                Text(stringResource(AppText.error_username_length))
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
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    val icon =
        if (isVisible) painterResource(AppIcon.ic_visibility_on)
        else painterResource(AppIcon.ic_visibility_off)

    val visualTransformation =
        if (isVisible) VisualTransformation.None else PasswordVisualTransformation()

    OutlinedTextField(
        label = { Text(label) },
        modifier = modifier,
        value = value,
        placeholder = { Text(stringResource(AppText.password)) },
        onValueChange = { onNewValue(it) },
        isError = isError,
        supportingText = {
            if (isError && errorText != null) {
                Text(stringResource(errorText))
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