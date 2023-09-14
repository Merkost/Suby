package com.merkost.suby.repository.room

import com.merkost.suby.model.room.entity.Subscription
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    val subscriptions: Flow<List<Subscription>>

    suspend fun addSubscription(newSubscription: Subscription)
    suspend fun updateSubscription(newSubscription: Subscription)
    suspend fun removeSubscription(subscriptionId: Int)
}