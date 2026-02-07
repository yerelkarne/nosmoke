package com.leosoft.smokefree.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leosoft.smokefree.ui.viewmodel.ProgressViewModel
import com.leosoft.smokefree.ui.viewmodel.ProgressUiState
import com.leosoft.smokefree.R

@Composable
fun ProgressScreen() {
    val viewModel: ProgressViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.title_progress)) }
    ) { padding ->
        ProgressContent(
            state = state,
            modifier = Modifier
                .padding(padding)
                .padding(AppSpacing.m)
        )
    }
}

@Composable
fun ProgressContent(state: ProgressUiState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.l)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Gün ${state.dayCount}", style = MaterialTheme.typography.headlineMedium)
                Text(text = "Streak: ${state.streakText}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            CircularProgressIndicator(progress = state.progress, modifier = Modifier.size(84.dp), color = MaterialTheme.colorScheme.secondary)
        }

        LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(AppSpacing.m), horizontalArrangement = Arrangement.spacedBy(AppSpacing.m)) {
            item {
                StatCard(
                    iconRes = com.leosoft.smokefree.R.drawable.health_timer,
                    value = "${state.streakText}",
                    label = "Sigara içmiyorum"
                )
            }
            item {
                StatCard(
                    iconRes = com.leosoft.smokefree.R.drawable.reward_gift,
                    value = "${state.savedMoney.toInt()}₺",
                    label = "Tasarruf"
                )
            }
            item {
                StatCard(
                    iconRes = com.leosoft.smokefree.R.drawable.health_progress,
                    value = "${state.lifeDays} gün",
                    label = "Kazanılan ömür"
                )
            }
            item {
                StatCard(
                    iconRes = com.leosoft.smokefree.R.drawable.badge_smoke_20,
                    value = "${state.notSmokedCount}",
                    label = "İçilmeyen sigara"
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun ProgressScreenPreview() {
    SmokeFreeTheme {
        ProgressContent(
            state = ProgressUiState(
                dayCount = 12,
                streakText = "12 gün",
                progress = 0.5f,
                notSmokedCount = 120,
                savedMoney = 540.0,
                lifeDays = 2
            ),
            modifier = Modifier.padding(AppSpacing.m)
        )
    }
}
