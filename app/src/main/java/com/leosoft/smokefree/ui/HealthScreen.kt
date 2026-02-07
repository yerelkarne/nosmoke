package com.leosoft.smokefree.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leosoft.smokefree.ui.viewmodel.HealthViewModel
import com.leosoft.smokefree.ui.viewmodel.HealthMilestoneState
import com.leosoft.smokefree.R

@Composable
fun HealthScreen() {
    val viewModel: HealthViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.title_health)) }
    ) { padding ->
        HealthContent(
            milestones = state.milestones,
            modifier = Modifier
                .padding(padding)
                .padding(AppSpacing.m)
        )
    }
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
                    painter = painterResource(com.leosoft.smokefree.R.drawable.health_heart),
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
                iconRes = drawableByHealthIcon(milestone.iconName),
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
            modifier = Modifier.padding(AppSpacing.m)
        )
    }
}

private fun drawableByHealthIcon(name: String): Int {
    return when (name) {
        "health_heart" -> com.leosoft.smokefree.R.drawable.health_heart
        "health_lungs" -> com.leosoft.smokefree.R.drawable.health_lungs
        "health_timer" -> com.leosoft.smokefree.R.drawable.health_timer
        "health_progress" -> com.leosoft.smokefree.R.drawable.health_progress
        else -> com.leosoft.smokefree.R.drawable.health_heart
    }
}
