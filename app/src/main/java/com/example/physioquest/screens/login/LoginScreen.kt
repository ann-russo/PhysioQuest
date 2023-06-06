package com.example.physioquest.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.R
import com.example.physioquest.common.composable.BasicButton
import com.example.physioquest.common.composable.BasicTextButton
import com.example.physioquest.common.composable.EmailField
import com.example.physioquest.common.composable.PasswordField
import com.example.physioquest.common.util.basicButton
import com.example.physioquest.common.util.fieldModifier
import com.example.physioquest.common.util.supportWideScreen
import com.example.physioquest.common.util.textButton
import com.example.physioquest.R.string as AppText

@Composable
fun LoginScreen(
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    var showBranding by remember { mutableStateOf(true) }

    Surface(modifier = Modifier.supportWideScreen()) {
        Column(
            modifier = modifier
                .padding(20.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier
                    .weight(1f, fill = showBranding)
                    .animateContentSize()
            )
            AnimatedVisibility(
                showBranding,
                Modifier.fillMaxWidth()
            ) {
                Branding()
            }
            Spacer(
                modifier = Modifier
                    .weight(1f, fill = showBranding)
                    .animateContentSize()
            )

            Text(
                text = stringResource(id = AppText.login_details),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 64.dp, bottom = 12.dp)
            )

            EmailField(
                value = uiState.email,
                onNewValue = viewModel::onEmailChange,
                label = stringResource(AppText.email),
                isError = false,
                errorText = null,
                Modifier.fieldModifier()
            )
            PasswordField(
                value = uiState.password,
                onNewValue = viewModel::onPasswordChange,
                label = stringResource(AppText.password),
                isError = false,
                errorText = null,
                Modifier.fieldModifier()
            )

            BasicButton(
                AppText.sign_in,
                Modifier.basicButton()
            ) { viewModel.onSignInClick(openAndPopUp) }

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

@Composable
fun Branding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Logo(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 76.dp)
        )
    }
}

@Composable
private fun Logo(
    modifier: Modifier = Modifier,
    lightTheme: Boolean = LocalContentColor.current.luminance() < 0.5f,
) {
    val assetId = R.drawable.physioquest_logo_3
    Image(
        painter = painterResource(id = assetId),
        modifier = modifier,
        contentDescription = null
    )
}