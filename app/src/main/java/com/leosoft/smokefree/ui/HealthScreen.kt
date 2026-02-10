package com.leosoft.smokefree.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leosoft.smokefree.ui.viewmodel.HealthViewModel
import com.leosoft.smokefree.ui.viewmodel.HealthMilestoneState
import com.leosoft.smokefree.ui.viewmodel.ProgressViewModel
import com.leosoft.smokefree.R

@Composable
fun HealthScreen() {
    val viewModel: HealthViewModel = viewModel()
    val progressViewModel: ProgressViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    val progressState by progressViewModel.uiState.collectAsState()
    val showSettingsDialog = remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            AppTopBar(
                title = stringResource(R.string.title_health),
                onSettingsClick = { showSettingsDialog.value = true }
            )
        }
    ) { padding ->
        HealthContent(
            milestones = state.milestones,
            modifier = Modifier
                .padding(padding)
                .padding(start = AppSpacing.m, top = AppSpacing.m, end = AppSpacing.m)
        )
    }

    SettingsDialog(
        show = showSettingsDialog.value,
        cigarettesPerDay = progressState.cigarettesPerDay,
        packPrice = progressState.packPrice,
        packSize = progressState.packSize,
        smokeFreeDays = progressState.smokeFreeDays,
        onDismiss = { showSettingsDialog.value = false },
        onSave = { startTimestamp, cigarettes, price, size ->
            progressViewModel.updateUserStats(startTimestamp, cigarettes, price, size)
            showSettingsDialog.value = false
        }
    )
}

@Composable
private fun HealthContent(milestones: List<HealthMilestoneState>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.m)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.s), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Bilgilendirme amaçlıdır.",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        items(milestones) { milestone ->
            MilestoneCard(
                title = milestone.title,
                description = milestone.description,
                progress = milestone.progress,
                statusText = if (milestone.isCompleted) "Tamamlandı" else "Yaklaşıyor",
                icon = iconByHealthName(milestone.iconName),
                isCompleted = milestone.isCompleted,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HealthPreview() {
    SmokeFreeTheme {
        HealthContent(
            milestones = listOf(
                HealthMilestoneState("m1", "24 saat", "Kalp krizi riski azalır.", "health_heart", 1f, true),
                HealthMilestoneState("m2", "48 saat", "Tat ve koku geri gelir.", "health_progress", 0.4f, false)
            ),
            modifier = Modifier.padding(start = AppSpacing.m, top = AppSpacing.m, end = AppSpacing.m)
        )
    }
}

private fun iconByHealthName(name: String): ImageVector {
    return when (name) {
        "health_heart" -> Icons.Filled.Favorite
        "health_lungs" -> Icons.Filled.Air
        "health_timer" -> Icons.Filled.Timer
        "health_progress" -> Icons.Filled.TrendingUp
        else -> Icons.Filled.Favorite
    }
}
