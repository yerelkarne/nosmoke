package com.leosoft.smokefree.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.leosoft.smokefree.AppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProgressViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppContainer.statsRepository(application)

    val uiState: StateFlow<ProgressUiState> = repository.statsFlow.map { stats ->
        ProgressUiState(
            startTimestamp = stats.stats.startTimestamp,
            elapsedMillis = stats.elapsedMillis,
            smokeFreeDays = stats.smokeFreeDays,
            cigarettesPerDay = stats.stats.cigarettesPerDay,
            packPrice = stats.stats.packPrice,
            packSize = stats.stats.packSize
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProgressUiState())
}

data class ProgressUiState(
    val startTimestamp: Long = 0L,
    val elapsedMillis: Long = 0L,
    val smokeFreeDays: Int = 0,
    val cigarettesPerDay: Int = 0,
    val packPrice: Int = 0,
    val packSize: Int = 20
)
