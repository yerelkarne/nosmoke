package com.leosoft.smokefree.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
            items = state.badges,
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
    items: List<BadgeItemState>,
    modifier: Modifier = Modifier,
    onSelected: (BadgeItemState) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.m),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.m)
    ) {
        items(items) { item ->
            BadgeCard(
                title = item.title,
                progress = if (item.targetValue == 0L) 0f else (item.progressValue.toFloat() / item.targetValue.toFloat()).coerceIn(0f, 1f),
                iconRes = drawableByName(item.iconName),
                isUnlocked = item.isUnlocked,
                modifier = Modifier.padding(2.dp),
                onClick = { onSelected(item) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TrophiesPreview() {
    SmokeFreeTheme {
        TrophiesContent(
            items = listOf(
                BadgeItemState("1", "20 sigara içmedin", "badge_smoke_20", 20, 10, false),
                BadgeItemState("2", "7 gün sigarasız", "badge_days_7", 7, 7, true)
            ),
            modifier = Modifier.padding(AppSpacing.m),
            onSelected = {}
        )
    }
}

private fun drawableByName(name: String): Int {
    return when (name) {
        "badge_smoke_20" -> com.leosoft.smokefree.R.drawable.badge_smoke_20
        "badge_smoke_100" -> com.leosoft.smokefree.R.drawable.badge_smoke_100
        "badge_smoke_1000" -> com.leosoft.smokefree.R.drawable.badge_smoke_1000
        "badge_smoke_10000" -> com.leosoft.smokefree.R.drawable.badge_smoke_10000
        "badge_days_1" -> com.leosoft.smokefree.R.drawable.badge_days_1
        "badge_days_3" -> com.leosoft.smokefree.R.drawable.badge_days_3
        "badge_days_7" -> com.leosoft.smokefree.R.drawable.badge_days_7
        "badge_days_10" -> com.leosoft.smokefree.R.drawable.badge_days_10
        "badge_days_14" -> com.leosoft.smokefree.R.drawable.badge_days_14
        "badge_days_30" -> com.leosoft.smokefree.R.drawable.badge_days_30
        "badge_life_1" -> com.leosoft.smokefree.R.drawable.badge_life_1
        "badge_life_3" -> com.leosoft.smokefree.R.drawable.badge_life_3
        "badge_life_7" -> com.leosoft.smokefree.R.drawable.badge_life_7
        "badge_life_10" -> com.leosoft.smokefree.R.drawable.badge_life_10
        "badge_life_14" -> com.leosoft.smokefree.R.drawable.badge_life_14
        "badge_life_30" -> com.leosoft.smokefree.R.drawable.badge_life_30
        "achievement_decision" -> com.leosoft.smokefree.R.drawable.achievement_decision
        "achievement_premium" -> com.leosoft.smokefree.R.drawable.achievement_premium
        else -> com.leosoft.smokefree.R.drawable.badge_smoke_20
    }
}
