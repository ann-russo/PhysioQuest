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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.physioquest.common.util.cardButton
import kotlinx.coroutines.launch
import com.example.physioquest.R.drawable as AppIcon
import com.example.physioquest.R.string as AppText

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
        colors = CardDefaults.elevatedCardColors(),
    ) {
        val learnImage = painterResource(AppIcon.idea_144)
        val duelImage = painterResource(AppIcon.boxing_glove_144)

        Box {
            when (title) {
                AppText.lernmodus_title -> learnImage
                AppText.duellmodus_title -> duelImage
                else -> null
            }?.let {
                Image(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier
                        .size(105.dp)
                        .padding(top = 8.dp, end = 10.dp)
                        .alpha(0.9f)
                        .align(Alignment.TopEnd),
                    alignment = Alignment.TopEnd
                )
            }

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
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(subtitle),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (title == AppText.lernmodus_title) {
                    Text(
                        text = stringResource(AppText.lernmodus_description),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (title == AppText.duellmodus_title) {
                    Text(
                        text = stringResource(AppText.duellmodus_description),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

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
                        .wrapContentWidth()
                ) {
                    BoxWithConstraints {
                        val maxWidthDp = constraints.maxWidth.dp
                        Text(
                            text = stringResource(actionText).uppercase(),
                            modifier = Modifier.widthIn(max = maxWidthDp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getTitleColor(isEnabled: Boolean, isSelected: Boolean, correctChoice: Boolean): Color {
    return when {
        isEnabled && isSelected -> MaterialTheme.colorScheme.primary
        !isEnabled && isSelected && correctChoice -> Color(0xFF26BB5D)
        !isEnabled && isSelected && !correctChoice -> Color(0xFFE91E63)
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    }
}

@Composable
private fun getBorderColor(isEnabled: Boolean, isSelected: Boolean, correctChoice: Boolean): Color {
    return when {
        isEnabled && isSelected -> MaterialTheme.colorScheme.primary
        !isEnabled && isSelected && correctChoice -> Color(0xFF26BB5D)
        !isEnabled && isSelected && !correctChoice -> Color(0xFFE91E63)
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    }
}

@Composable
private fun getIcon(isEnabled: Boolean, isSelected: Boolean, correctChoice: Boolean): Painter {
    return when {
        isEnabled && isSelected -> painterResource(AppIcon.circle_checked)
        !isEnabled && isSelected && !correctChoice -> painterResource(AppIcon.circle_false)
        !isEnabled && isSelected && correctChoice -> painterResource(AppIcon.circle_true)
        !isEnabled && !isSelected && !correctChoice -> painterResource(AppIcon.circle_true)
        else -> painterResource(AppIcon.circle_empty)
    }
}

@Composable
private fun getColorFilter(isEnabled: Boolean, isSelected: Boolean, correctChoice: Boolean): Color {
    return when {
        isEnabled && isSelected -> MaterialTheme.colorScheme.primary
        !isEnabled && isSelected && correctChoice -> Color(0xFF26BB5D)
        !isEnabled && !isSelected && !correctChoice -> Color(0xFF26BB5D)
        !isEnabled && isSelected && !correctChoice -> Color(0xFFE91E63)
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    }
}

@Composable
fun SelectableAnswerOption(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    isEnabled: Boolean,
    correctChoice: Boolean,
    title: String,
    onClick: () -> Unit
) {
    val titleColor: Color = getTitleColor(isEnabled, isSelected, correctChoice)
    val borderColor: Color = getBorderColor(isEnabled, isSelected, correctChoice)
    val icon: Painter = getIcon(isEnabled, isSelected, correctChoice)

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
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(size = 10.dp)
            )
            .clip(RoundedCornerShape(size = 10.dp))
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
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Normal
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
                    colorFilter = ColorFilter.tint( getColorFilter(isEnabled, isSelected, correctChoice) )
                )
            }
        }
    }
}