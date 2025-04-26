package com.merkost.suby.repository.room

import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.model.entity.full.toSubscription
import com.merkost.suby.model.room.dao.SubscriptionDao
import com.merkost.suby.model.room.entity.PartialSubscriptionDb
import com.merkost.suby.model.room.entity.SubscriptionDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class SubscriptionRepositoryImpl(
    private val subscriptionDao: SubscriptionDao,
    private val ioScope: CoroutineScope,
) : SubscriptionRepository {

    override val subscriptionsState: StateFlow<List<Subscription>> =
        subscriptionDao.getSubscriptionsWithService().map { it.map { it.toSubscription() } }
            .stateIn(ioScope, SharingStarted.Eagerly, emptyList())

    override val subscriptions: Flow<List<Subscription>> =
        subscriptionDao.getSubscriptionsWithService().map { it.map { it.toSubscription() } }


    override suspend fun getSubscriptionById(subscriptionId: Int): Flow<Subscription> =
        subscriptionDao.getSubscriptionWithService(subscriptionId)
            .map { it.toSubscription() }

    override suspend fun addSubscription(newSubscriptionDb: SubscriptionDb) {
        withContext(Dispatchers.IO) {
            subscriptionDao.addSubscription(newSubscriptionDb)
        }
    }

    override suspend fun updateSubscription(update: PartialSubscriptionDb) {
        withContext(Dispatchers.IO) {
            subscriptionDao.updateSubscriptionDetails(update)
        }
    }

    override suspend fun removeSubscription(subscriptionId: Int) {
        withContext(Dispatchers.IO) {
            subscriptionDao.deleteSubscriptionById(subscriptionId)
        }
    }

    override val hasAnySubscriptions: Flow<Boolean> = subscriptionDao.hasAnySubscriptions()

}