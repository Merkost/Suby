package com.merkost.suby.model.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.merkost.suby.model.room.entity.Subscription
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSubscription(subscription: Subscription): Long

    @Query("SELECT * FROM subscriptions WHERE id = :subId")
    suspend fun findSubscriptionById(subId: String): Subscription

    @Query("SELECT * FROM subscriptions")
    fun getAllSubscriptions(): Flow<List<Subscription>>

    @Update
    suspend fun updateSubscriptionDetails(subscription: Subscription): Int

    @Delete
    suspend fun deleteSubscription(subscription: Subscription): Int

    @Query("DELETE FROM subscriptions WHERE id = :subscriptionId")
    suspend fun deleteSubscriptionById(subscriptionId: Int): Int
}