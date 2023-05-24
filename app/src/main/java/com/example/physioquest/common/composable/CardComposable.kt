package com.example.physioquest.common.composable

import androidx.annotation.StringRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.physioquest.R
import com.example.physioquest.common.util.cardButton
import kotlinx.coroutines.launch

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

@Composable
fun SelectableAnswerOption(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    isEnabled: Boolean,
    correctChoice: Boolean,
    title: String,
    titleColor: Color =
        when {
            isEnabled && isSelected -> MaterialTheme.colorScheme.primary
            !isEnabled && isSelected && correctChoice -> Color(0xFF26BB5D)
            !isEnabled && isSelected && !correctChoice -> Color(0xFFE91E63)
            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        },
    titleSize: TextUnit = MaterialTheme.typography.titleMedium.fontSize,
    titleWeight: FontWeight = FontWeight.Normal,
    subtitle: String? = null,
    subtitleColor: Color =
        if (isEnabled && isSelected) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
    borderWidth: Dp = 1.dp,
    borderColor: Color =
        when {
            isEnabled && isSelected -> MaterialTheme.colorScheme.primary
            !isEnabled && isSelected && correctChoice -> Color(0xFF26BB5D)
            !isEnabled && isSelected && !correctChoice -> Color(0xFFE91E63)
            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        },
    borderShape: Shape = RoundedCornerShape(size = 10.dp),
    icon: Painter =
        when {
            isEnabled && isSelected -> painterResource(R.drawable.circle_checked)
            !isEnabled && isSelected && !correctChoice -> painterResource(R.drawable.circle_false)
            !isEnabled && isSelected && correctChoice -> painterResource(R.drawable.circle_true)
            !isEnabled && !isSelected && !correctChoice -> painterResource(R.drawable.circle_true)
            else -> painterResource(R.drawable.circle_empty)
        },
    onClick: () -> Unit
) {
    val scaleA = remember { Animatable(initialValue = 1f) }
    val scaleB = remember { Animatable(initialValue = 1f) }
    val clickEnabled = remember { mutableStateOf(true) }

    LaunchedEffect(key1 = isSelected) {
        if (isSelected) {
            clickEnabled.value = false

            val jobA = launch {
                scaleA.animateTo(
                    targetValue = 0.3f,
                    animationSpec = tween(
                        durationMillis = 50
                    )
                )
                scaleA.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            val jobB = launch {
                scaleB.animateTo(
                    targetValue = 0.9f,
                    animationSpec = tween(
                        durationMillis = 50
                    )
                )
                scaleB.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }

            jobA.join()
            jobB.join()
            clickEnabled.value = true
        }
    }

    Column(
        modifier = modifier
            .scale(scale = scaleB.value)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = borderShape
            )
            .clip(borderShape)
            .clickable(enabled = clickEnabled.value && isEnabled) {
                onClick()
            }
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(8f)
                    .padding(vertical = 10.dp),
                text = title,
                style = TextStyle(
                    color = titleColor,
                    fontSize = titleSize,
                    fontWeight = titleWeight
                ),
                maxLines = Int.MAX_VALUE,
                overflow = TextOverflow.Visible
            )
            Box(
                modifier = Modifier
                    .weight(2f)
                    .scale(scale = scaleA.value)
                    .clickable(enabled = clickEnabled.value && isEnabled) {
                        onClick()
                    }
            ) {
                Image(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(30.dp),
                    painter = icon,
                    contentDescription = "Icon",
                    colorFilter = ColorFilter.tint(
                        when {
                            isEnabled && isSelected -> MaterialTheme.colorScheme.primary
                            !isEnabled && isSelected && correctChoice -> Color(0xFF26BB5D)
                            !isEnabled && isSelected && !correctChoice -> Color(0xFFE91E63)
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        }
                    )
                )
            }
        }
        if (subtitle != null) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
                text = subtitle,
                style = TextStyle(
                    color = subtitleColor
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}