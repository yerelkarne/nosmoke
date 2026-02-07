package com.leosoft.smokefree.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.leosoft.smokefree.AppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.concurrent.TimeUnit

class ProgressViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppContainer.statsRepository(application)

    val uiState: StateFlow<ProgressUiState> = repository.statsFlow.map { stats ->
        val elapsedHours = TimeUnit.MILLISECONDS.toHours(stats.elapsedMillis)
        val progress = (elapsedHours / 24f).coerceIn(0f, 1f)
        ProgressUiState(
            dayCount = stats.smokeFreeDays,
            streakText = "${stats.smokeFreeDays} gün",
            progress = progress,
            notSmokedCount = stats.notSmokedCount,
            savedMoney = stats.savedMoney,
            lifeDays = stats.lifeDays
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProgressUiState())
}

data class ProgressUiState(
    val dayCount: Int = 0,
    val streakText: String = "0 gün",
    val progress: Float = 0f,
    val notSmokedCount: Long = 0,
    val savedMoney: Double = 0.0,
    val lifeDays: Int = 0
)
