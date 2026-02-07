package com.leosoft.smokefree.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leosoft.smokefree.ui.viewmodel.ProgressViewModel
import com.leosoft.smokefree.ui.viewmodel.ProgressUiState
import com.leosoft.smokefree.R
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.math.floor

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
    val nowMillis = remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(state.startTimestamp) {
        while (true) {
            nowMillis.value = System.currentTimeMillis()
            delay(1000)
        }
    }

    val elapsedMillis = if (state.startTimestamp == 0L) 0L else (nowMillis.value - state.startTimestamp).coerceAtLeast(0L)
    val dayMillis = TimeUnit.DAYS.toMillis(1)
    val progress = if (elapsedMillis == 0L) 0f else ((elapsedMillis % dayMillis).toFloat() / dayMillis.toFloat()).coerceIn(0f, 1f)
    val progressPercent = (progress * 100).toInt()
    val smokeFreeDays = TimeUnit.MILLISECONDS.toDays(elapsedMillis).toInt()

    val cigarettesPerDay = state.cigarettesPerDay.coerceAtLeast(0)
    val millisPerCigarette = if (cigarettesPerDay == 0) 0.0 else dayMillis.toDouble() / cigarettesPerDay.toDouble()
    val notSmokedCount = if (millisPerCigarette == 0.0) 0.0 else (elapsedMillis / millisPerCigarette)
    val pricePerCigarette = if (state.packSize == 0) 0.0 else state.packPrice.toDouble() / state.packSize.toDouble()
    val savedMoney = notSmokedCount * pricePerCigarette
    val lifeGainedMillis = (notSmokedCount * TimeUnit.MINUTES.toMillis(11)).toLong()

    val smokeFreeDuration = formatDuration(elapsedMillis)
    val lifeGainedDuration = formatDuration(lifeGainedMillis)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.l)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.size(190.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(190.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    strokeWidth = 10.dp
                )
                Text(
                    text = "%$progressPercent",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Gün $smokeFreeDays",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                )
            }
        }

        LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(AppSpacing.m), horizontalArrangement = Arrangement.spacedBy(AppSpacing.m)) {
            item {
                StatCard(
                    iconRes = com.leosoft.smokefree.R.drawable.health_timer,
                    value = smokeFreeDuration,
                    label = "Sigarasız süre"
                )
            }
            item {
                StatCard(
                    iconRes = com.leosoft.smokefree.R.drawable.reward_gift,
                    value = "${formatDecimal(savedMoney)}₺",
                    label = "Tasarruf"
                )
            }
            item {
                StatCard(
                    iconRes = com.leosoft.smokefree.R.drawable.health_progress,
                    value = lifeGainedDuration,
                    label = "Kazanılan ömür"
                )
            }
            item {
                StatCard(
                    iconRes = com.leosoft.smokefree.R.drawable.badge_smoke_20,
                    value = formatDecimal(notSmokedCount),
                    label = "İçilmeyen sigara"
                )
            }
        }
    }
}

private fun formatDuration(durationMillis: Long): String {
    if (durationMillis <= 0L) return "00:00:00"
    val days = TimeUnit.MILLISECONDS.toDays(durationMillis)
    val hours = TimeUnit.MILLISECONDS.toHours(durationMillis) % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60
    return if (days > 0) {
        "%d gün %02d:%02d:%02d".format(days, hours, minutes, seconds)
    } else {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    }
}

private fun formatDecimal(value: Double): String {
    val rounded = floor(value * 10) / 10.0
    return if (rounded % 1.0 == 0.0) {
        rounded.toInt().toString()
    } else {
        "%.1f".format(rounded)
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun ProgressScreenPreview() {
    SmokeFreeTheme {
        ProgressContent(
            state = ProgressUiState(
                startTimestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(20),
                elapsedMillis = TimeUnit.HOURS.toMillis(20),
                smokeFreeDays = 0,
                cigarettesPerDay = 6,
                packPrice = 60,
                packSize = 20
            ),
            modifier = Modifier.padding(AppSpacing.m)
        )
    }
}
