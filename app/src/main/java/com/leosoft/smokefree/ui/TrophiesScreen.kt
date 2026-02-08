package com.leosoft.smokefree.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leosoft.smokefree.ui.viewmodel.BadgesViewModel
import com.leosoft.smokefree.ui.viewmodel.BadgeItemState
import com.leosoft.smokefree.R
import com.leosoft.smokefree.ui.SmokeFreeTheme

@Composable
fun TrophiesScreen() {
    val viewModel: BadgesViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    var selected by mutableStateOf<BadgeItemState?>(null)

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.title_badges)) }
    ) { padding ->
        TrophiesContent(
            badges = state.badges,
            modifier = Modifier
                .padding(padding)
                .padding(AppSpacing.m),
            onSelected = { selected = it }
        )
    }

    selected?.let { item ->
        val percent = if (item.targetValue == 0L) 0f else {
            (item.progressValue.toFloat() / item.targetValue.toFloat()).coerceIn(0f, 1f)
        }
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text(item.title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "İlerleme: ${(percent * 100).toInt()}%")
                }
            },
            confirmButton = {
                TextButton(onClick = { selected = null }) { Text("Kapat") }
            }
        )
    }
}

@Composable
private fun TrophiesContent(
    badges: List<BadgeItemState>,
    modifier: Modifier = Modifier,
    onSelected: (BadgeItemState) -> Unit
) {
    val groupedItems = badges
        .groupBy { badge -> badge.category }
        .toSortedMap(compareBy<String> { categorySortOrder(it) })
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.m),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.m)
    ) {
        groupedItems.entries.forEach { entry ->
            val category = entry.key
            val categoryItems = entry.value
            items(listOf(category), span = { GridItemSpan(maxLineSpan) }) { title: String ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = AppSpacing.xs)
                )
            }
            val sortedItems = categoryItems.sortedWith(
                compareBy<BadgeItemState> { !it.isUnlocked }
                    .thenBy { it.targetValue }
                    .thenBy { it.title }
            )
            items(sortedItems) { item ->
                BadgeCard(
                    title = item.title,
                    progress = if (item.targetValue == 0L) 0f else (item.progressValue.toFloat() / item.targetValue.toFloat()).coerceIn(0f, 1f),
                    icon = iconByName(item.iconName),
                    isUnlocked = item.isUnlocked,
                    modifier = Modifier.padding(2.dp),
                    onClick = { onSelected(item) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TrophiesPreview() {
    SmokeFreeTheme {
        TrophiesContent(
            badges = listOf(
                BadgeItemState("1", "20 sigara içmedin", "badge_smoke_20", 20, 10, false),
                BadgeItemState("2", "7 gün sigarasız", "badge_days_7", 7, 7, true)
            ),
            modifier = Modifier.padding(AppSpacing.m),
            onSelected = {}
        )
    }
}

private fun iconByName(name: String): ImageVector {
    return Icons.Filled.EmojiEvents
}

private fun categorySortOrder(category: String): Int {
    return when (category) {
        "Sigarasızlık" -> 0
        "Gün Sayısı" -> 1
        "Ömür Kazancı" -> 2
        "Uygulama" -> 3
        else -> 4
    }
}
