package com.example.physioquest.screens.lernmodus

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physioquest.R.drawable as AppIcon
import com.example.physioquest.R.string as AppText

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CategoryScreen(
    categories: List<String>,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier,
    viewModel: LernmodusViewModel = hiltViewModel()
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                questionCount = viewModel.countQuestionsForCategory(category),
                onCategorySelected = onCategorySelected
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: String,
    questionCount: Int,
    onCategorySelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(220.dp)
            .clickable { onCategorySelected(category) },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.outlinedCardElevation(),
        colors = CardDefaults.outlinedCardColors(Color.White),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
    ) {
        val footImage = ImageVector.vectorResource(AppIcon.medical4)
        val legImage = ImageVector.vectorResource(AppIcon.medical2)
        val armImage = ImageVector.vectorResource(AppIcon.medical6)
        val shoulderImage = ImageVector.vectorResource(AppIcon.medical3)
        val hipsImage = ImageVector.vectorResource(AppIcon.medical5)
        val generalImage = ImageVector.vectorResource(AppIcon.medical1)

        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.15f)
                    .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            ) {
                Image(
                    imageVector = when (category) {
                        stringResource(AppText.shoulder) -> shoulderImage
                        stringResource(AppText.foot) -> footImage
                        stringResource(AppText.knees) -> legImage
                        stringResource(AppText.arm) -> armImage
                        stringResource(AppText.hips) -> hipsImage
                        else -> generalImage
                    },
                    contentDescription = category,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Spacer(modifier = Modifier.weight(0.8f))
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "$questionCount Fragen",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
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
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
