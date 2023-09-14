package com.merkost.suby.repository.room

import com.merkost.suby.model.room.entity.Subscription
import com.merkost.suby.model.room.dao.SubscriptionDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SubscriptionRepositoryImpl(private val subscriptionDao: SubscriptionDao) :
    SubscriptionRepository {

    override val subscriptions: Flow<List<Subscription>> = subscriptionDao.getAllSubscriptions()

    override suspend fun addSubscription(newSubscription: Subscription) {
        withContext(Dispatchers.IO) {
            subscriptionDao.addSubscription(newSubscription)
        }
    }

    override suspend fun updateSubscription(newSubscription: Subscription) {
        withContext(Dispatchers.IO) {
            subscriptionDao.updateSubscriptionDetails(newSubscription)
        }
    }

    override suspend fun removeSubscription(subscriptionId: Int) {
        withContext(Dispatchers.IO) {
            subscriptionDao.deleteSubscriptionById(subscriptionId)
        }
    }

}