package com.leosoft.smokefree.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.leosoft.smokefree.AppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.concurrent.TimeUnit

class HealthViewModel(application: Application) : AndroidViewModel(application) {
    private val healthRepository = AppContainer.healthRepository(application)
    private val statsRepository = AppContainer.statsRepository(application)

    val uiState: StateFlow<HealthUiState> = healthRepository.observeMilestones()
        .combine(statsRepository.statsFlow) { milestones, stats ->
            val elapsedHours = TimeUnit.MILLISECONDS.toHours(stats.elapsedMillis).toInt()
            HealthUiState(
                milestones = milestones.map { milestone ->
                    val progress = if (milestone.triggerDurationHours == 0) {
                        1f
                    } else {
                        (elapsedHours.toFloat() / milestone.triggerDurationHours.toFloat()).coerceIn(0f, 1f)
                    }
                    HealthMilestoneState(
                        id = milestone.id,
                        title = milestone.title,
                        description = milestone.description,
                        iconName = milestone.iconName,
                        progress = progress,
                        isCompleted = elapsedHours >= milestone.triggerDurationHours
                    )
                }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HealthUiState())
}

data class HealthUiState(
    val milestones: List<HealthMilestoneState> = emptyList()
)

data class HealthMilestoneState(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val progress: Float,
    val isCompleted: Boolean
)
