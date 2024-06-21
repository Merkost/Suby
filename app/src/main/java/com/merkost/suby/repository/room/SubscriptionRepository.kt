package com.merkost.suby.repository.room

import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.model.room.entity.SubscriptionDb
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    val subscriptions: Flow<List<Subscription>>

    suspend fun addSubscription(newSubscriptionDb: SubscriptionDb)
    suspend fun updateSubscription(newSubscriptionDb: SubscriptionDb)
    suspend fun removeSubscription(subscriptionId: Int)
    suspend fun getSubscriptionById(subscriptionId: Int): Flow<Subscription?>
}