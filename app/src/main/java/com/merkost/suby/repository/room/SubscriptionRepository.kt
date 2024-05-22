package com.merkost.suby.repository.room

import com.merkost.suby.model.room.entity.SubscriptionDb
import com.merkost.suby.model.room.entity.SubscriptionWithDetails
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    val subscriptionsWithServices: Flow<List<SubscriptionWithDetails>>
    val subscriptions: Flow<List<SubscriptionDb>>

    suspend fun addSubscription(newSubscriptionDb: SubscriptionDb)
    suspend fun updateSubscription(newSubscriptionDb: SubscriptionDb)
    suspend fun removeSubscription(subscriptionId: Int)
}