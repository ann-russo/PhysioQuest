package com.example.physioquest.screens.lernmodus

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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            MedalBadge(correct, total)
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
fun MedalBadge(correct: Int, total: Int) {
    val progress = correct.toFloat() / total.toFloat()
    val angle = progress * 360f

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(180.dp)) {
            val radius = size.minDimension / 2
            val strokeWidth = 20f

            // Draw the background circle
            drawCircle(
                color = Color.Gray,
                radius = radius,
                style = Stroke(strokeWidth)
            )

            // Draw the progress arc
            drawArc(
                color = Color.Green,
                startAngle = -90f,
                sweepAngle = angle,
                useCenter = false,
                style = Stroke(strokeWidth)
            )
        }

        Text(
            text = "$correct/$total",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}
