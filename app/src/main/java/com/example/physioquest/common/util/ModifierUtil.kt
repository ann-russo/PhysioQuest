package com.example.physioquest.common.util

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.fieldModifier(): Modifier {
    return this
        .fillMaxWidth()
        .padding(16.dp, 10.dp)
}

fun Modifier.smallFieldModifier(): Modifier {
    return this
        .fillMaxWidth()
        .padding(16.dp, 6.dp)
}

fun Modifier.basicButton(): Modifier {
    return this
        .fillMaxWidth()
        .padding(16.dp, 8.dp)
}

fun Modifier.textButton(): Modifier {
    return this.padding(16.dp, 8.dp, 16.dp, 0.dp)
}

fun Modifier.cardButton(): Modifier {
    return this
        .padding(16.dp, 10.dp, 0.dp, 0.dp)
}

fun Modifier.toolbarActions(): Modifier {
    return this.wrapContentSize(Alignment.TopEnd)
}

fun Modifier.smallSpacer(): Modifier {
    return this
        .fillMaxWidth()
        .height(8.dp)
}

fun Modifier.bigSpacer(): Modifier {
    return this
        .fillMaxWidth()
        .height(40.dp)
}

fun Modifier.card(): Modifier {
    return this
        .padding(16.dp, 0.dp, 16.dp, 8.dp)
        .fillMaxWidth()
        .height(220.dp)
}

fun Modifier.answerCard(): Modifier {
    return this
        .padding(16.dp, 0.dp, 16.dp, 16.dp)
        .fillMaxWidth()
        .height(IntrinsicSize.Max)
}

/**
 * Support wide screen by making the content width max 840dp, centered horizontally.
 */
fun Modifier.supportWideScreen() = this
    .fillMaxWidth()
    .wrapContentWidth(align = Alignment.CenterHorizontally)
    .widthIn(max = 840.dp)