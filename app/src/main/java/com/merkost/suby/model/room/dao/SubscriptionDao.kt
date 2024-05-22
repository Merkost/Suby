package com.merkost.suby.model.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.merkost.suby.model.room.entity.SubscriptionDb
import com.merkost.suby.model.room.entity.SubscriptionWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSubscription(subscriptionDb: SubscriptionDb): Long

    @Query("SELECT * FROM subscriptions WHERE id = :subId")
    suspend fun findSubscriptionById(subId: String): SubscriptionDb

    @Transaction
    @Query("""
        SELECT * FROM subscriptions
        LEFT JOIN service ON subscriptions.serviceId = service.serviceId
        LEFT JOIN category ON service.categoryId = category.categoryId
    """)
    fun getSubscriptionsWithDetails(): Flow<List<SubscriptionWithDetails>>
    @Query("SELECT * FROM subscriptions")
    fun getSubscriptions(): Flow<List<SubscriptionDb>>

    @Update
    suspend fun updateSubscriptionDetails(subscriptionDb: SubscriptionDb): Int

    @Delete
    suspend fun deleteSubscription(subscriptionDb: SubscriptionDb): Int

    @Query("DELETE FROM subscriptions WHERE id = :subscriptionId")
    suspend fun deleteSubscriptionById(subscriptionId: Int): Int
}