package com.example.physioquest.screens.lernmodus

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.physioquest.R.drawable as AppIcon
import com.example.physioquest.R.string as AppText

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CategoryScreen(
    categories: List<String>,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                onCategorySelected = onCategorySelected
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: String,
    onCategorySelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onCategorySelected(category) },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(),
        colors = CardDefaults.elevatedCardColors()
    ) {
        val footImage = ImageVector.vectorResource(AppIcon.ankle_128)
        val legImage = ImageVector.vectorResource(AppIcon.leg_128)
        val armImage = ImageVector.vectorResource(AppIcon.arm_128)
        val shoulderImage = ImageVector.vectorResource(AppIcon.shoulder_128)
        val hipsImage = ImageVector.vectorResource(AppIcon.hips_128)
        val generalImage = ImageVector.vectorResource(AppIcon.physiology_128)

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Image(
                    imageVector =
                    when (category) {
                        stringResource(AppText.shoulder) -> shoulderImage
                        stringResource(AppText.foot) -> footImage
                        stringResource(AppText.knees) -> legImage
                        stringResource(AppText.arm) -> armImage
                        stringResource(AppText.hips) -> hipsImage
                        else -> generalImage
                    },
                    contentDescription = category,
                    modifier = Modifier.size(94.dp)
                )
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryTopAppBar(
    onClosePressed: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        CenterAlignedTopAppBar(
            title = { TopAppBarTitle(title = AppText.categories) },
            actions = {
                IconButton(
                    onClick = onClosePressed,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = stringResource(AppText.cancel),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }
}

@Composable
private fun TopAppBarTitle(
    @StringRes title: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
