package com.leosoft.smokefree.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.leosoft.smokefree.AppContainer
import com.leosoft.smokefree.data.db.entities.RewardItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class RewardsViewModel(application: Application) : AndroidViewModel(application) {
    private val rewardsRepository = AppContainer.rewardRepository(application)
    private val statsRepository = AppContainer.statsRepository(application)

    val uiState: StateFlow<RewardsUiState> = rewardsRepository.observeRewards()
        .combine(statsRepository.statsFlow) { rewards, stats ->
            RewardsUiState(
                savedMoney = stats.savedMoney,
                items = rewards.map { reward ->
                    RewardItemState(
                        id = reward.id,
                        title = reward.title,
                        price = reward.price,
                        iconName = reward.iconName,
                        progress = (stats.savedMoney / reward.price).toFloat().coerceIn(0f, 1f)
                    )
                }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RewardsUiState())

    suspend fun addReward(item: RewardItem) {
        rewardsRepository.addReward(item)
    }
}

data class RewardsUiState(
    val savedMoney: Double = 0.0,
    val items: List<RewardItemState> = emptyList()
)

data class RewardItemState(
    val id: String,
    val title: String,
    val price: Double,
    val iconName: String,
    val progress: Float
)
