package com.merkost.suby.repository.room

import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.model.entity.full.toService
import com.merkost.suby.model.room.dao.ServiceDao
import com.merkost.suby.model.room.dao.SubscriptionDao
import com.merkost.suby.utils.ImageFileManager
import com.merkost.suby.utils.ServiceId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ServiceRepositoryImpl(
    private val serviceDao: ServiceDao,
    private val subscriptionDao: SubscriptionDao,
    private val imageFileManager: ImageFileManager,
) : ServiceRepository {

    override val services: Flow<List<Service>> =
        serviceDao.getServicesWithCategories()
            .map { dbservices ->
                dbservices.map { it.toService() }
            }

    override val customServices: Flow<List<Service>> =
        serviceDao.getServicesWithCategories()
            .map { dbservices ->
                dbservices.filter { it.service.backendId == null }.map { it.toService() }
            }

    override suspend fun deleteService(serviceId: ServiceId) {
        subscriptionDao.deleteSubscriptionsByService(serviceId)
        val service = serviceDao.getServiceById(serviceId)
        service?.let {
            it.customImageUri?.let { imageFileManager.deleteCustomServiceImageFromInternalStorage(it) }
            serviceDao.deleteServices(service)
        }
    }

}