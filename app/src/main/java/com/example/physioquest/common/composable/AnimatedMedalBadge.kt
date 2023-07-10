package com.example.physioquest.common.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MedalBadge(angle: Float, badgeRadius: Dp) {
    val sweepAngle by animateFloatAsState(targetValue = angle * 3.6f)
    Box(
        modifier = Modifier.size(badgeRadius),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val radius = size.minDimension / 2
            val strokeWidth = 30f
            val startColor = Color(0xFF9CD67D)
            val endColor = Color(0xFF58A28F)

            drawCircle(
                color = Color.LightGray,
                radius = radius,
                style = Stroke(strokeWidth)
            )

            drawArc(
                brush = Brush.verticalGradient(listOf(startColor, endColor)),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(strokeWidth)
            )
        }

        val percentage = String.format("%.2f", angle)
        Text(
            text = "${percentage}%",
            style = when {
                badgeRadius >= 200.dp -> {
                    MaterialTheme.typography.headlineLarge
                }
                badgeRadius > 100.dp -> {
                    MaterialTheme.typography.headlineMedium
                }
                badgeRadius <= 100.dp -> {
                    MaterialTheme.typography.titleMedium
                }
                else -> {
                    MaterialTheme.typography.headlineSmall
                }
            },
            fontWeight = FontWeight.W400,
        )
    }
}