package com.example.physioquest.screens.account

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.R
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    modifier: Modifier,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val currentUsername = viewModel.user.value?.username ?: ""
    val currentEmail = viewModel.user.value?.email ?: ""
    val currentPwd = "**********"

    val newUsernameState = remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        confirmValueChange = { it != SheetValue.PartiallyExpanded },
        skipPartiallyExpanded = true
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        EditOptionItem(
            currentData = currentUsername,
            optionText = R.string.username,
            optionIcon = R.drawable.person_48px,
            onClick = {
                scope.launch {
                    sheetState.show()
                }
            }
        )
        EditOptionItem(
            currentData = currentEmail,
            optionText = R.string.email,
            optionIcon = R.drawable.mail_48px,
            onClick = { /* todo */ }
        )
        EditOptionItem(
            currentData = currentPwd,
            optionText = R.string.password,
            optionIcon = R.drawable.lock_48px,
            onClick = { /* todo */ }
        )
    }

    if (sheetState.isVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            shape = BottomSheetDefaults.ExpandedShape,
            scrimColor = BottomSheetDefaults.ScrimColor,
            containerColor = Color.White,
            contentColor = contentColorFor(Color.White),
            tonalElevation = BottomSheetDefaults.Elevation,
            modifier = Modifier.height(500.dp),
            onDismissRequest = { scope.launch { sheetState.hide() } },
            content = {
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
                            text = "Benutzername ändern",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = newUsernameState.value,
                            onValueChange = { newUsernameState.value = it },
                            label = { Text("Neuer Benutzername") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (newUsernameState.value.isNotBlank()) {
                                    viewModel.changeUsername(newUsernameState.value)
                                    scope.launch { sheetState.hide() } // Hide bottom sheet
                                }
                            },
                            enabled = newUsernameState.value.isNotBlank(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Speichern")
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun ChangeUsernameContent() {

}

@Composable
fun ChangeEmailContent() {

}

@Composable
fun ChangePasswordContent() {

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