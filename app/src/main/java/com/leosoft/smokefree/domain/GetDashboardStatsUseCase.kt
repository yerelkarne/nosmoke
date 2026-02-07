package com.leosoft.smokefree.domain

import com.leosoft.smokefree.data.repository.DashboardStats
import com.leosoft.smokefree.data.repository.StatsRepository
import kotlinx.coroutines.flow.Flow

class GetDashboardStatsUseCase(private val repository: StatsRepository) {
    operator fun invoke(): Flow<DashboardStats> = repository.statsFlow
}
