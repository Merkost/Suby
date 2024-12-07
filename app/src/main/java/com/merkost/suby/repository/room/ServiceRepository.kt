package com.merkost.suby.repository.room

import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.utils.CustomServiceId
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    val services: Flow<List<Service>>
    val customServices: Flow<List<Service>>

    suspend fun deleteCustomService(customServiceId: CustomServiceId)
}