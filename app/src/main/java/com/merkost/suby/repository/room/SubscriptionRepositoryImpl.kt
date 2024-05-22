package com.merkost.suby.repository.room

import com.merkost.suby.model.room.dao.SubscriptionDao
import com.merkost.suby.model.room.entity.SubscriptionDb
import com.merkost.suby.model.room.entity.SubscriptionWithDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SubscriptionRepositoryImpl(private val subscriptionDao: SubscriptionDao) :
    SubscriptionRepository {

    override val subscriptionsWithServices: Flow<List<SubscriptionWithDetails>> =
        subscriptionDao.getSubscriptionsWithDetails()
    override val subscriptions: Flow<List<SubscriptionDb>> = subscriptionDao.getSubscriptions()


    override suspend fun addSubscription(newSubscriptionDb: SubscriptionDb) {
        withContext(Dispatchers.IO) {
            subscriptionDao.addSubscription(newSubscriptionDb)
        }
    }

    override suspend fun updateSubscription(newSubscriptionDb: SubscriptionDb) {
        withContext(Dispatchers.IO) {
            subscriptionDao.updateSubscriptionDetails(newSubscriptionDb)
        }
    }

    override suspend fun removeSubscription(subscriptionId: Int) {
        withContext(Dispatchers.IO) {
            subscriptionDao.deleteSubscriptionById(subscriptionId)
        }
    }

}