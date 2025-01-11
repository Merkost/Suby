package com.merkost.suby.repository.room

import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.model.room.entity.PartialSubscriptionDb
import com.merkost.suby.model.room.entity.SubscriptionDb
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    val subscriptions: Flow<List<Subscription>>
    val hasAnySubscriptions: Flow<Boolean>

    suspend fun addSubscription(newSubscriptionDb: SubscriptionDb)
    suspend fun updateSubscription(update: PartialSubscriptionDb)
    suspend fun removeSubscription(subscriptionId: Int)
    suspend fun getSubscriptionById(subscriptionId: Int): Flow<Subscription?>
}