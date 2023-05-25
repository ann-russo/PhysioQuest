package com.example.physioquest.screens.lernmodus

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.physioquest.HOME_SCREEN
import com.example.physioquest.R.string as AppText

@Composable
fun ResultsScreen(correct: Int, total: Int, openScreen: (String) -> Unit) {
    val progress = correct.toFloat() / total.toFloat()
    val angle = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        angle.animateTo(
            targetValue = progress * 360f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(AppText.results),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        )
        Text(
            text = "$correct/$total " + stringResource(AppText.questions_correct),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            MedalBadge(angle.value)
        }
        Button(
            onClick = { openScreen(HOME_SCREEN) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = stringResource(AppText.finish),
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(AppText.finish).uppercase())
        }
    }
}

@Composable
fun MedalBadge(angle: Float) {
    val sweepAngle by animateFloatAsState(targetValue = angle)

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(180.dp)) {
            val radius = size.minDimension / 2
            val strokeWidth = 20f

            drawCircle(
                color = Color.Gray,
                radius = radius,
                style = Stroke(strokeWidth)
            )

            drawArc(
                color = Color.Green,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(strokeWidth)
            )
        }

        Text(
            text = "${(sweepAngle / 360f * 100).toInt()}%",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}