package com.merkost.suby.repository.room

import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.model.entity.full.toService
import com.merkost.suby.model.room.dao.CustomServiceDao
import com.merkost.suby.model.room.dao.ServiceDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ServiceRepositoryImpl(
    private val serviceDao: ServiceDao,
    private val customServiceDao: CustomServiceDao
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


}