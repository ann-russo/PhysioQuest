package com.example.physioquest.screens.account

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.common.composable.EmailField
import com.example.physioquest.common.composable.PasswordField
import com.example.physioquest.common.composable.UsernameField
import kotlinx.coroutines.launch
import com.example.physioquest.R.drawable as AppIcon
import com.example.physioquest.R.string as AppText

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    modifier: Modifier,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val newUsernameState = remember { mutableStateOf("") }
    val newEmailState = remember { mutableStateOf("") }
    val newEmailConfirmState = remember { mutableStateOf("") }
    val currentPasswordState = remember { mutableStateOf("") }
    val newPasswordState = remember { mutableStateOf("") }
    val newPasswordConfirmState = remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        confirmValueChange = { it != SheetValue.PartiallyExpanded },
        skipPartiallyExpanded = true
    )
    var selectedOption by remember { mutableStateOf<EditOption?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        EditOptionItem(
            currentData = viewModel.username.collectAsState().value,
            optionText = AppText.username,
            optionIcon = AppIcon.person_48px,
            onClick = {
                selectedOption = EditOption.Username
                scope.launch {
                    sheetState.show()
                }
            }
        )
        EditOptionItem(
            currentData = viewModel.email.value,
            optionText = AppText.email,
            optionIcon = AppIcon.mail_48px,
            onClick = {
                selectedOption = EditOption.Email
                scope.launch {
                    sheetState.show()
                }
            }
        )
        EditOptionItem(
            currentData = stringResource(AppText.edit_pwd_placeholder),
            optionText = AppText.password,
            optionIcon = AppIcon.lock_48px,
            onClick = {
                selectedOption = EditOption.Password
                scope.launch {
                    sheetState.show()
                }
            }
        )
    }

    if (sheetState.isVisible) {
        val content: @Composable ColumnScope.() -> Unit = {
            when (selectedOption) {
                EditOption.Username -> {
                    ChangeUsernameContent(
                        newUsernameState = newUsernameState,
                        onSaveClick = {
                            viewModel.validateAndChangeUsername(newUsernameState.value)
                            if (!viewModel.usernameErrorState.value) {
                                scope.launch { sheetState.hide() }
                            }
                        }
                    )
                }

                EditOption.Email -> {
                    ChangeEmailContent(
                        newEmailState = newEmailState,
                        newEmailConfirmState = newEmailConfirmState,
                        currentPasswordState = currentPasswordState,
                        onSaveClick = {
                            viewModel.isFieldEmpty(
                                newEmailState.value,
                                newEmailConfirmState.value,
                                currentPasswordState.value
                            )
                            if (
                                !viewModel.emailErrorState.value &&
                                !viewModel.confirmEmailErrorState.value &&
                                !viewModel.passwordErrorState.value &&
                                viewModel.isPasswordValid(currentPasswordState.value)
                            ) {
                                viewModel.changeEmail(newEmailState.value)
                                scope.launch { sheetState.hide() }
                            }
                        }
                    )
                }

                EditOption.Password -> {
                    ChangePasswordContent(
                        currentPasswordState = currentPasswordState,
                        newPasswordState = newPasswordState,
                        newPasswordConfirmState = newPasswordConfirmState,
                        onSaveClick = {
                            viewModel.checkEmptyPasswordFields(
                                currentPasswordState.value,
                                newPasswordState.value,
                                newPasswordConfirmState.value
                            )
                            if (
                                !viewModel.passwordErrorState.value &&
                                !viewModel.newPasswordErrorState.value &&
                                !viewModel.confirmPasswordErrorState.value &&
                                viewModel.isPasswordValid(currentPasswordState.value)
                            ) {
                                viewModel.changePassword(newPasswordState.value)
                                scope.launch { sheetState.hide() }
                            }
                        }
                    )
                }

                else -> null
            }
        }

        ModalBottomSheet(
            sheetState = sheetState,
            shape = BottomSheetDefaults.ExpandedShape,
            scrimColor = BottomSheetDefaults.ScrimColor,
            containerColor = Color.White,
            contentColor = contentColorFor(Color.White),
            tonalElevation = BottomSheetDefaults.Elevation,
            modifier = Modifier.height(500.dp),
            onDismissRequest = { scope.launch { sheetState.hide() } },
            content = content
        )
    }
}

@Composable
fun ChangeUsernameContent(
    newUsernameState: MutableState<String>,
    onSaveClick: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    Surface(
        color = Color.White,
        contentColor = contentColorFor(Color.White),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(AppText.edit_username),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(30.dp))
            UsernameField(
                value = newUsernameState.value,
                onNewValue = {
                    newUsernameState.value = it
                    viewModel.usernameErrorState.value = false
                    viewModel.validateUsername(newUsernameState.value)
                },
                label = stringResource(AppText.edit_username_new),
                isError = viewModel.usernameErrorState.value,
                errorText = viewModel.usernameErrorMessage,
                supportingText = AppText.hint_username_length,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onSaveClick() },
                enabled = true,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(AppText.save))
            }
        }
    }
}

@Composable
fun ChangeEmailContent(
    newEmailState: MutableState<String>,
    newEmailConfirmState: MutableState<String>,
    currentPasswordState: MutableState<String>,
    onSaveClick: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    Surface(
        color = Color.White,
        contentColor = contentColorFor(Color.White),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(AppText.edit_email),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(30.dp))
            EmailField(
                value = newEmailState.value,
                onNewValue = {
                    newEmailState.value = it
                    viewModel.emailErrorState.value = false
                    viewModel.validateNewEmail(newEmailState.value)
                },
                label = stringResource(AppText.edit_email_new),
                isError = viewModel.emailErrorState.value,
                errorText = viewModel.emailErrorMessage,
                supportingText = AppText.hint_email_fh,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            EmailField(
                value = newEmailConfirmState.value,
                onNewValue = {
                    newEmailConfirmState.value = it
                    viewModel.confirmEmailErrorState.value = false
                    viewModel.validateNewConfirmEmail(
                        newEmailState.value,
                        newEmailConfirmState.value
                    )
                },
                label = stringResource(AppText.edit_email_confirm),
                isError = viewModel.confirmEmailErrorState.value,
                errorText = viewModel.confirmEmailErrorMessage,
                supportingText = null,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordField(
                value = currentPasswordState.value,
                onNewValue = {
                    currentPasswordState.value = it
                    viewModel.passwordErrorState.value = false
                },
                label = stringResource(AppText.password),
                isError = viewModel.passwordErrorState.value,
                errorText = viewModel.passwordErrorMessage,
                supportingText = null,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onSaveClick() },
                enabled = true,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(AppText.save))
            }
        }
    }
}

@Composable
fun ChangePasswordContent(
    currentPasswordState: MutableState<String>,
    newPasswordState: MutableState<String>,
    newPasswordConfirmState: MutableState<String>,
    onSaveClick: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    Surface(
        color = Color.White,
        contentColor = contentColorFor(Color.White),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(AppText.edit_pwd),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(30.dp))
            PasswordField(
                value = currentPasswordState.value,
                onNewValue = {
                    currentPasswordState.value = it
                    viewModel.passwordErrorState.value = false
                },
                label = stringResource(AppText.password),
                isError = viewModel.passwordErrorState.value,
                errorText = viewModel.passwordErrorMessage,
                supportingText = AppText.hint_password_current,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordField(
                value = newPasswordState.value,
                onNewValue = {
                    newPasswordState.value = it
                    viewModel.newPasswordErrorState.value = false
                    viewModel.validateNewPassword(newPasswordState.value)
                },
                label = stringResource(AppText.edit_pwd_new),
                isError = viewModel.newPasswordErrorState.value,
                errorText = viewModel.newPasswordErrorMessage,
                supportingText = AppText.hint_password,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordField(
                value = newPasswordConfirmState.value,
                onNewValue = {
                    newPasswordConfirmState.value = it
                    viewModel.confirmPasswordErrorState.value = false
                    viewModel.validateNewConfirmPassword(
                        newPasswordState.value,
                        newPasswordConfirmState.value
                    )
                },
                label = stringResource(AppText.edit_pwd_confirm),
                isError = viewModel.confirmPasswordErrorState.value,
                errorText = viewModel.confirmPasswordErrorMessage,
                supportingText = null,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onSaveClick() },
                enabled = true,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(AppText.save))
            }
        }
    }
}

@Composable
fun EditOptionItem(
    currentData: String,
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
            .height(80.dp)
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(optionText),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentData,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 14.sp
                )
            }
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

enum class EditOption {
    Username,
    Email,
    Password
}