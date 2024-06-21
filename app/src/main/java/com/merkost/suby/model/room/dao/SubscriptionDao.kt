package com.merkost.suby.model.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.merkost.suby.model.room.entity.SubscriptionDb
import com.merkost.suby.model.room.entity.related.SubscriptionWithCustomDetails
import com.merkost.suby.model.room.entity.related.SubscriptionWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun addSubscription(subscriptionDb: SubscriptionDb): Long

    @Transaction
    @Query("""
        SELECT * FROM subscription
        WHERE id = :subId AND isCustomService = 0
    """)
    suspend fun findSubscriptionById(subId: String): SubscriptionDb

    @Transaction
    @Query("""
        SELECT * FROM subscription
        WHERE subscription.isCustomService = 0
    """)
    fun getSubscriptionsWithService(): Flow<List<SubscriptionWithDetails>>

    @Transaction
    @Query("""
        SELECT * FROM subscription
        WHERE subscription.isCustomService = 1
    """)
    fun getSubscriptionsWithCustomService(): Flow<List<SubscriptionWithCustomDetails>>

    @Transaction
    @Query("""
        SELECT * FROM subscription
        WHERE subscription.isCustomService = 0 and subscription.id = :subscriptionId
    """)
    fun getSubscriptionWithService(subscriptionId: Int): Flow<SubscriptionWithDetails>

    @Transaction
    @Query("""
        SELECT * FROM subscription
        WHERE subscription.isCustomService = 1 and subscription.id = :subscriptionId
    """)
    fun getSubscriptionWithCustomService(subscriptionId: Int): Flow<SubscriptionWithCustomDetails>

    @Query("SELECT * FROM subscription")
    fun getAllSubscriptions(): Flow<List<SubscriptionDb>>

    @Query("SELECT * FROM subscription WHERE id = :subscriptionId")
    suspend fun getSubscriptionById(subscriptionId: Int): SubscriptionDb

    @Update
    suspend fun updateSubscriptionDetails(subscriptionDb: SubscriptionDb): Int

    @Delete
    suspend fun deleteSubscription(subscriptionDb: SubscriptionDb): Int

    @Query("DELETE FROM subscription WHERE id = :subscriptionId")
    suspend fun deleteSubscriptionById(subscriptionId: Int): Int
}