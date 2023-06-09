package com.example.physioquest.screens.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.common.composable.BasicButton
import com.example.physioquest.common.composable.BasicTextButton
import com.example.physioquest.common.composable.CenteredTopAppBar
import com.example.physioquest.common.composable.EmailField
import com.example.physioquest.common.composable.PasswordField
import com.example.physioquest.common.composable.UsernameField
import com.example.physioquest.common.util.basicButton
import com.example.physioquest.common.util.fieldModifier
import com.example.physioquest.common.util.textButton
import com.example.physioquest.R.string as AppText

@Composable
fun RegistrationScreen(
    openScreen: (String) -> Unit,
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            CenteredTopAppBar(
                title = AppText.register_option,
                onClosePressed = { viewModel.onClosePressed(openScreen) }
            )
        }
    ) { paddingValues ->
        val uiState by viewModel.uiState
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UsernameField(
                value = uiState.username,
                onNewValue = viewModel::onUsernameChange,
                label = stringResource(AppText.username),
                isError = viewModel.usernameErrorState.value,
                errorText = viewModel.usernameErrorMessage,
                supportingText = AppText.hint_username_length,
                Modifier.fieldModifier()
            )
            EmailField(
                value = uiState.email,
                onNewValue = viewModel::onEmailChange,
                label = stringResource(AppText.email),
                isError = viewModel.emailErrorState.value,
                errorText = viewModel.emailErrorMessage,
                supportingText = AppText.hint_email_fh,
                Modifier.fieldModifier()
            )
            PasswordField(
                value = uiState.password,
                onNewValue = viewModel::onPasswordChange,
                label = stringResource(AppText.password),
                isError = viewModel.passwordErrorState.value,
                errorText = viewModel.passwordErrorMessage,
                supportingText = AppText.hint_password,
                Modifier.fieldModifier()
            )

            BasicButton(
                AppText.sign_up,
                Modifier.basicButton()
            ) {
                viewModel.isFieldEmpty()
                if (!viewModel.emailErrorState.value &&
                    !viewModel.passwordErrorState.value &&
                    !viewModel.usernameErrorState.value
                ) {
                    viewModel.onSignUpClick(openAndPopUp)
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                BasicTextButton(AppText.login_option, Modifier.textButton()) {
                    viewModel.onSignInClick(openAndPopUp)
                }
            }
        }
    }
}