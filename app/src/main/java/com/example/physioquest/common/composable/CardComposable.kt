package com.example.physioquest.common.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.physioquest.common.util.cardButton

@Composable
@ExperimentalMaterial3Api
fun ElevatedCard(
    @StringRes title: Int,
    @StringRes subtitle: Int,
    @StringRes actionText: Int,
    modifier: Modifier,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(),
        colors = CardDefaults.elevatedCardColors()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(subtitle),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier
                    .align(Alignment.End)
                    .cardButton()
            ) {
                Text(text = stringResource(actionText))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AntwortCard(
    antwortText: String,
    onCardClick: () -> Unit,
    isSelected: Boolean,
    isCorrect: Boolean,
    isEnabled: Boolean,
    modifier: Modifier
) {
    Card(
        onClick = onCardClick,
        modifier = modifier,
        enabled = isEnabled,
        border = if (isSelected) {
            if (isCorrect) {
                BorderStroke(4.dp, Color.Green)
            } else {
                BorderStroke(4.dp, Color.Red)
            }
        } else {
            null
        },
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
    ) {
        Text(
            text = antwortText,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(24.dp)
        )
    }
}