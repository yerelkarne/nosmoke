package com.leosoft.smokefree.data.repository

import com.leosoft.smokefree.data.db.dao.RewardDao
import com.leosoft.smokefree.data.db.entities.RewardItem
import kotlinx.coroutines.flow.Flow

class RewardRepository(private val dao: RewardDao) {
    fun observeRewards(): Flow<List<RewardItem>> = dao.observeRewards()

    suspend fun addReward(item: RewardItem) {
        dao.insertReward(item)
    }
}
