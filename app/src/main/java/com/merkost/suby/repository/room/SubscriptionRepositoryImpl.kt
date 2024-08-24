package com.merkost.suby.repository.room

import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.model.entity.full.toSubscription
import com.merkost.suby.model.room.dao.SubscriptionDao
import com.merkost.suby.model.room.entity.PartialSubscriptionDb
import com.merkost.suby.model.room.entity.SubscriptionDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SubscriptionRepositoryImpl(private val subscriptionDao: SubscriptionDao) :
    SubscriptionRepository {

    override val subscriptions: Flow<List<Subscription>> = combine(
        subscriptionDao.getSubscriptionsWithService(),
        subscriptionDao.getSubscriptionsWithCustomService()
    ) { sub1, sub2 ->
        sub1.map { it.toSubscription() } + sub2.map { it.toSubscription() }
    }

    override suspend fun getSubscriptionById(subscriptionId: Int): Flow<Subscription> {
        val subscriptionDb = subscriptionDao.getSubscriptionById(subscriptionId)
        return if (subscriptionDb.isCustomService) {
            subscriptionDao.getSubscriptionWithCustomService(subscriptionId)
                .map { it.toSubscription() }
        } else {
            subscriptionDao.getSubscriptionWithService(subscriptionId)
                .map { it.toSubscription() }
        }

    }

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

}