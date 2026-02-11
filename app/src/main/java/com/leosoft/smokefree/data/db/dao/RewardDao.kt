package com.leosoft.smokefree.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.leosoft.smokefree.data.db.entities.RewardItem
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {
    @Query("SELECT * FROM reward_items ORDER BY createdAt DESC")
    fun observeRewards(): Flow<List<RewardItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(item: RewardItem)
}
