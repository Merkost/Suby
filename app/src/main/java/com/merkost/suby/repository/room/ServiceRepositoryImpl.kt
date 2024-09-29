package com.merkost.suby.repository.room

import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.model.entity.full.toService
import com.merkost.suby.model.room.dao.CustomServiceDao
import com.merkost.suby.model.room.dao.ServiceDao
import com.merkost.suby.model.room.dao.SubscriptionDao
import com.merkost.suby.utils.CustomServiceId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ServiceRepositoryImpl(
    private val serviceDao: ServiceDao,
    private val customServiceDao: CustomServiceDao,
    private val subscriptionDao: SubscriptionDao,
) : ServiceRepository {

    override val services: Flow<List<Service>> =
        serviceDao.getServicesWithCategories()
            .map { dbservices ->
                dbservices.map { it.toService() }
            }

    override val customServices: Flow<List<Service>> =
        customServiceDao.getCustomServicesWithCategory()
            .map { dbservices ->
                dbservices.map { it.toService() }
            }

    override suspend fun deleteCustomService(customServiceId: CustomServiceId) {
        subscriptionDao.deleteSubscriptionsByCustomService(customServiceId)
        customServiceDao.deleteCustomService(customServiceId)
    }

}