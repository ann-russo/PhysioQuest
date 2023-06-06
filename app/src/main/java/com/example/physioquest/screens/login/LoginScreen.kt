package com.example.physioquest.screens.login

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
import com.example.physioquest.common.util.basicButton
import com.example.physioquest.common.util.smallFieldModifier
import com.example.physioquest.common.util.textButton
import com.example.physioquest.R.string as AppText

@Composable
fun LoginScreen(
    openScreen: (String) -> Unit,
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    Scaffold(
        topBar = {
            CenteredTopAppBar(
                title = AppText.sign_in,
                onClosePressed = { viewModel.onClosePressed(openScreen) }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EmailField(
                value = uiState.email,
                onNewValue = viewModel::onEmailChange,
                label = stringResource(AppText.email),
                isError = viewModel.emailErrorState.value,
                errorText = viewModel.emailErrorMessage,
                supportingText = null,
                Modifier.smallFieldModifier()
            )
            PasswordField(
                value = uiState.password,
                onNewValue = viewModel::onPasswordChange,
                label = stringResource(AppText.password),
                isError = viewModel.passwordErrorState.value,
                errorText = viewModel.passwordErrorMessage,
                supportingText = null,
                Modifier.smallFieldModifier()
            )

            BasicButton(
                AppText.sign_in,
                Modifier.basicButton()
            ) {
                viewModel.isFieldEmpty()
                if (!viewModel.emailErrorState.value && !viewModel.passwordErrorState.value) {
                    viewModel.onSignInClick(openAndPopUp)
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                BasicTextButton(AppText.forgot_password, Modifier.textButton()) {
                    viewModel.onForgotPasswordClick()
                }

                BasicTextButton(AppText.register_option, Modifier.textButton()) {
                    viewModel.onSignUpClick(openAndPopUp)
                }
            }
        }
    }
}
