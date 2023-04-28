package com.example.physioquest.common.util

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.fieldModifier(): Modifier {
    return this.fillMaxWidth().padding(16.dp, 4.dp)
}

fun Modifier.basicButton(): Modifier {
    return this.fillMaxWidth().padding(16.dp, 8.dp)
}

fun Modifier.textButton(): Modifier {
    return this.padding(16.dp, 8.dp, 16.dp, 0.dp)
}

fun Modifier.cardButton(): Modifier {
    return this.padding(16.dp, 10.dp, 0.dp, 0.dp).width(130.dp)
}

fun Modifier.toolbarActions(): Modifier {
    return this.wrapContentSize(Alignment.TopEnd)
}

fun Modifier.smallSpacer(): Modifier {
    return this.fillMaxWidth().height(8.dp)
}

fun Modifier.card(): Modifier {
    return this.padding(16.dp, 0.dp, 16.dp, 8.dp).fillMaxWidth().height(IntrinsicSize.Max)
}

fun Modifier.antwortCard(): Modifier {
    return this.padding(8.dp).width(150.dp).height(150.dp)
}