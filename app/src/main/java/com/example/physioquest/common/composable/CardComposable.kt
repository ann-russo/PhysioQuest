package com.example.physioquest.common.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
    isSelected: Boolean,
    isEnabled: Boolean,
    correctChoice: Boolean,
    onSelectAnswer: () -> Unit,
    modifier: Modifier
) {
    Card(
        onClick = { onSelectAnswer() },
        modifier = modifier,
        border = when {
            isEnabled && isSelected -> BorderStroke(3.dp, Brush.horizontalGradient(
                colors = listOf(Color(0xFFABABBF), Color(0xFF221D4D))
            ))
            !isEnabled && !isSelected && !correctChoice -> BorderStroke(3.dp, Brush.horizontalGradient(
                colors = listOf(Color(0xFFBCFB69), Color(0xFF26BBAC))
            ))
            else -> null
        },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onTertiary
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush =
                    if (!isEnabled && isSelected && correctChoice) {
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFFBCFB69), Color(0xFF26BBAC))
                        )
                    } else if (!isEnabled && isSelected && !correctChoice) {
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFFF6D4D4), Color(0xFFE91E63))
                        )
                    } else {
                        SolidColor(MaterialTheme.colorScheme.surfaceVariant)
                    }
                )
        ) {
            Text(
                text = antwortText,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

    }
}