package com.leosoft.smokefree.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.SmokeFree
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leosoft.smokefree.ui.viewmodel.ProgressViewModel
import com.leosoft.smokefree.ui.viewmodel.ProgressUiState
import com.leosoft.smokefree.ui.viewmodel.MotivationViewModel
import com.leosoft.smokefree.R
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.math.floor
import kotlin.math.roundToLong

@Composable
fun ProgressScreen() {
    val viewModel: ProgressViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    val motivationViewModel: MotivationViewModel = viewModel()
    val motivationState by motivationViewModel.uiState.collectAsState()
    val showSettingsDialog = remember { mutableStateOf(false) }
    val cigarettesPerDayInput = remember { mutableStateOf("") }
    val packPriceInput = remember { mutableStateOf("") }
    val packSizeInput = remember { mutableStateOf("") }
    val smokeFreeDaysInput = remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        motivationViewModel.refreshQuote()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.title_progress),
                onSettingsClick = {
                    cigarettesPerDayInput.value = state.cigarettesPerDay.toString()
                    packPriceInput.value = state.packPrice.toString()
                    packSizeInput.value = state.packSize.toString()
                    smokeFreeDaysInput.value = state.smokeFreeDays.toString()
                    showSettingsDialog.value = true
                }
            )
        }
    ) { padding ->
        ProgressContent(
            state = state,
            dailyQuote = motivationState.quote,
            modifier = Modifier
                .padding(padding)
                .padding(AppSpacing.m)
        )
    }

    if (showSettingsDialog.value) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog.value = false },
            title = { Text(text = "Ayarlar") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.s)) {
                    TextField(
                        value = cigarettesPerDayInput.value,
                        onValueChange = { cigarettesPerDayInput.value = it },
                        label = { Text("Günde kaç sigara") }
                    )
                    TextField(
                        value = packPriceInput.value,
                        onValueChange = { packPriceInput.value = it },
                        label = { Text("Paket fiyatı (₺)") }
                    )
                    TextField(
                        value = packSizeInput.value,
                        onValueChange = { packSizeInput.value = it },
                        label = { Text("Paket adedi") }
                    )
                    TextField(
                        value = smokeFreeDaysInput.value,
                        onValueChange = { smokeFreeDaysInput.value = it },
                        label = { Text("Sigarasız geçen gün") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val cigarettesPerDay = cigarettesPerDayInput.value.toIntOrNull()
                            ?.coerceAtLeast(0) ?: state.cigarettesPerDay
                        val packPrice = packPriceInput.value.toIntOrNull()
                            ?.coerceAtLeast(0) ?: state.packPrice
                        val packSize = packSizeInput.value.toIntOrNull()
                            ?.coerceAtLeast(1) ?: state.packSize
                        val smokeFreeDays = smokeFreeDaysInput.value.toLongOrNull()
                            ?.coerceAtLeast(0L) ?: state.smokeFreeDays.toLong()
                        val startTimestamp = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(smokeFreeDays)
                        viewModel.updateUserStats(startTimestamp, cigarettesPerDay, packPrice, packSize)
                        showSettingsDialog.value = false
                    }
                ) {
                    Text("Kaydet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog.value = false }) {
                    Text("Vazgeç")
                }
            }
        )
    }
}

@Composable
fun ProgressContent(state: ProgressUiState, dailyQuote: String, modifier: Modifier = Modifier) {
    val nowMillis = remember { mutableStateOf(System.currentTimeMillis()) }
    val selectedGoal = remember { mutableStateOf(GoalOption.Day1) }
    val showGoalDialog = remember { mutableStateOf(false) }
    LaunchedEffect(state.startTimestamp) {
        while (true) {
            nowMillis.value = System.currentTimeMillis()
            delay(1000)
        }
    }

    val elapsedMillis = if (state.startTimestamp == 0L) 0L else (nowMillis.value - state.startTimestamp).coerceAtLeast(0L)
    val dayMillis = TimeUnit.DAYS.toMillis(1)
    val elapsedDays = elapsedMillis.toDouble() / dayMillis.toDouble()
    val effectiveGoal = if (selectedGoal.value.isDayBased) {
        val nextDay = (kotlin.math.floor(elapsedDays).toInt() + 1).coerceIn(1, 6)
        GoalOption.fromDay(nextDay)
    } else {
        selectedGoal.value
    }
    val targetDays = effectiveGoal.days.coerceAtLeast(1)
    val progress = if (elapsedMillis == 0L) 0f else (elapsedDays / targetDays.toDouble()).toFloat().coerceIn(0f, 1f)
    val progressTarget = remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        progressTarget.value = progress
    }
    LaunchedEffect(progress) {
        progressTarget.value = progress
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget.value,
        animationSpec = tween(durationMillis = 1400),
        label = "progressIndicator"
    )

    val savedMoney = state.savedMoney
    val cigarettesPerDay = state.cigarettesPerDay.coerceAtLeast(0)
    val notSmokedCount = if (elapsedMillis > 0L && cigarettesPerDay > 0) {
        val millisPerCigarette = dayMillis.toDouble() / cigarettesPerDay.toDouble()
        (elapsedMillis.toDouble() / millisPerCigarette).coerceAtLeast(0.0)
    } else {
        state.notSmokedCount.toDouble()
    }
    val displayedNotSmokedCount = floor(notSmokedCount).toLong()
    val lifeGainedMillis = (notSmokedCount * TimeUnit.MINUTES.toMillis(11)).roundToLong()

    val smokeFreeDuration = formatDuration(elapsedMillis)
    val lifeGainedDuration = formatDuration(lifeGainedMillis)

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.l)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = effectiveGoal.label,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { showGoalDialog.value = true }
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.size(190.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier.size(190.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 14.dp
                )
                Text(
                    text = "%${formatPercent((animatedProgress * 100).toDouble())}",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (dailyQuote.isNotBlank()) {
            Text(
                text = dailyQuote,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.m)) {
            StatCard(
                icon = Icons.Filled.Timer,
                value = smokeFreeDuration,
                label = "Sigarasız süre",
                modifier = Modifier.fillMaxWidth()
            )
            StatCard(
                icon = Icons.Filled.MonetizationOn,
                value = "${formatCurrency(savedMoney)}₺",
                label = "Tasarruf",
                modifier = Modifier.fillMaxWidth()
            )
            StatCard(
                icon = Icons.Filled.Favorite,
                value = lifeGainedDuration,
                label = "Kazanılan ömür",
                modifier = Modifier.fillMaxWidth()
            )
            StatCard(
                icon = Icons.Filled.SmokeFree,
                value = displayedNotSmokedCount.toString(),
                label = "İçilmeyen sigara",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showGoalDialog.value) {
        AlertDialog(
            onDismissRequest = { showGoalDialog.value = false },
            title = { Text(text = "Hedef seç") },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(GoalOption.entries) { option ->
                        TextButton(
                            onClick = {
                                selectedGoal.value = option
                                showGoalDialog.value = false
                            }
                        ) {
                            Text(text = option.label)
                        }
                    }
                }
            },
            confirmButton = {}
        )
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

private fun formatCurrency(value: Double): String {
    return "%.2f".format(value)
}

private fun formatPercent(value: Double): String {
    val rounded = floor(value * 10) / 10.0
    return if (rounded % 1.0 == 0.0) {
        rounded.toInt().toString()
    } else {
        "%.1f".format(rounded)
    }
}

private enum class GoalOption(val label: String, val days: Int, val isDayBased: Boolean = false) {
    Day1("Gün 1", 1, true),
    Day2("Gün 2", 2, true),
    Day3("Gün 3", 3, true),
    Day4("Gün 4", 4, true),
    Day5("Gün 5", 5, true),
    Day6("Gün 6", 6, true),
    Week1("1 hafta", 7),
    Week2("2 hafta", 14),
    Week3("3 hafta", 21),
    Month1("1 ay", 30),
    Year1("1 yıl", 365),
    Year5("5 yıl", 1825),
    Year10("10 yıl", 3650);

    companion object {
        fun fromDay(day: Int): GoalOption {
            return when (day) {
                1 -> Day1
                2 -> Day2
                3 -> Day3
                4 -> Day4
                5 -> Day5
                else -> Day6
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
                startTimestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(20),
                elapsedMillis = TimeUnit.HOURS.toMillis(20),
                smokeFreeDays = 0,
                notSmokedCount = 6,
                savedMoney = 30.0,
                lifeDays = 0,
                cigarettesPerDay = 6,
                packPrice = 60,
                packSize = 20
            ),
            dailyQuote = "Bugün de sigarasız kalmayı seçtin.",
            modifier = Modifier.padding(AppSpacing.m)
        )
    }
}
