package com.leosoft.smokefree.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.leosoft.smokefree.AppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class BadgesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppContainer.achievementRepository(application)

    val uiState: StateFlow<BadgesUiState> = repository.observeDefinitions()
        .combine(repository.observeProgress()) { definitions, progress ->
            val progressMap = progress.associateBy { it.achievementId }
            val badges = definitions
                .filterNot { definition -> shouldHideBadge(definition.id, definition.title) }
                .map { definition ->
                    BadgeItemState(
                        id = definition.id,
                        title = definition.title,
                        category = definition.category,
                        iconName = definition.iconName,
                        targetValue = definition.targetValue,
                        progressValue = progressMap[definition.id]?.progressValue ?: 0L,
                        isUnlocked = progressMap[definition.id]?.isUnlocked == true
                    )
                }
            BadgesUiState(badges)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BadgesUiState())

    private fun shouldHideBadge(id: String, title: String): Boolean {
        val normalizedId = id.lowercase()
        val normalizedTitle = title.lowercase()
        return normalizedId.contains("premium") ||
            normalizedId.contains("support") ||
            normalizedTitle.contains("premium") ||
            normalizedTitle.contains("destekçi") ||
            normalizedTitle.contains("destekci")
    }
}

data class BadgesUiState(
    val badges: List<BadgeItemState> = emptyList()
)

data class BadgeItemState(
    val id: String,
    val title: String,
    val category: String,
    val iconName: String,
    val targetValue: Long,
    val progressValue: Long,
    val isUnlocked: Boolean
)
