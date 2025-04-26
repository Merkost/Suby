package com.merkost.suby.model.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.merkost.suby.model.room.entity.PartialSubscriptionDb
import com.merkost.suby.model.room.entity.SubscriptionDb
import com.merkost.suby.model.room.entity.related.SubscriptionWithDetails
import com.merkost.suby.utils.ServiceId
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun addSubscription(subscriptionDb: SubscriptionDb): Long

    @Transaction
    @Query(
        """
        SELECT * FROM subscription
    """
    )
    fun getSubscriptionsWithService(): Flow<List<SubscriptionWithDetails>>

    @Transaction
    @Query(
        """
        SELECT * FROM subscription
        WHERE subscription.id = :subscriptionId
    """
    )
    fun getSubscriptionWithService(subscriptionId: Int): Flow<SubscriptionWithDetails>

    @Query("SELECT * FROM subscription")
    fun getAllSubscriptions(): Flow<List<SubscriptionDb>>

    @Query("SELECT * FROM subscription WHERE id = :subscriptionId")
    suspend fun getSubscriptionById(subscriptionId: Int): SubscriptionDb

    @Update(entity = SubscriptionDb::class)
    suspend fun updateSubscriptionDetails(partialSubscriptionDb: PartialSubscriptionDb): Int

    @Delete
    suspend fun deleteSubscription(subscriptionDb: SubscriptionDb): Int

    @Transaction
    @Query("DELETE FROM subscription WHERE serviceId = :serviceId")
    suspend fun deleteSubscriptionsByService(serviceId: ServiceId)

    @Query("DELETE FROM subscription WHERE id = :subscriptionId")
    suspend fun deleteSubscriptionById(subscriptionId: Int): Int

    @Query("SELECT EXISTS(SELECT 1 FROM subscription)")
    fun hasAnySubscriptions(): Flow<Boolean>
}