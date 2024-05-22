package com.merkost.suby.use_case

import com.merkost.suby.model.room.entity.ServiceWithCategory

sealed class GetServicesResult {
    data object Loading : GetServicesResult()
    data class Success(val servicesWithCategory: List<ServiceWithCategory>) :
        GetServicesResult()

    data class Failure(val error: Throwable) : GetServicesResult()
}