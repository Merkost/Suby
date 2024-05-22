package com.merkost.suby.use_case

import com.merkost.suby.model.entity.dto.CategoryDto
import com.merkost.suby.model.entity.dto.ServiceDto
import com.merkost.suby.model.room.DbMapper
import com.merkost.suby.model.room.dao.CategoryDao
import com.merkost.suby.model.room.dao.ServiceDao
import com.merkost.suby.model.room.entity.ServiceWithCategory
import com.merkost.suby.repository.ktor.api.SupabaseApi
import com.merkost.suby.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import timber.log.Timber

class GetServicesUseCase(
    private val supabaseApi: SupabaseApi,
    private val serviceDao: ServiceDao,
    private val categoryDao: CategoryDao,
) {
    suspend operator fun invoke(): Flow<GetServicesResult> = flow {
        emit(GetServicesResult.Loading)

        val currentTime = System.currentTimeMillis()
        val updateThreshold = Constants.SUBY_UPDATE_THRESHOLD.inWholeMilliseconds

        val lastServiceUpdate = serviceDao.getLastServiceUpdate() ?: 0
        val lastCategoryUpdate = categoryDao.getLastCategoryUpdate() ?: 0

        val needToUpdateServices = currentTime - lastServiceUpdate > updateThreshold
        val needToUpdateCategories = currentTime - lastCategoryUpdate > updateThreshold

        if (needToUpdateServices || needToUpdateCategories) {
            val categories = loadCategories()
            val services = loadServices()

            if (categories.isEmpty() || services.isEmpty()) {
                emit(GetServicesResult.Failure(Throwable("Failed to fetch data from API")))
                return@flow
            }
        }

        val dbCategories = categoryDao.getCategories().first()
        val dbServices = serviceDao.getServices().first()

        if (dbCategories.isEmpty() || dbServices.isEmpty()) {
            emit(GetServicesResult.Failure(Throwable("No categories or services available")))
        } else {
            val servicesWithCategory = dbServices.mapNotNull { service ->
                val category = dbCategories.find { it.id == service.categoryId }
                category?.let { ServiceWithCategory(service, category) }
            }
            emit(GetServicesResult.Success(servicesWithCategory))
        }
    }

    private suspend fun loadServices(): List<ServiceDto> {
        return supabaseApi.getServices().first().onSuccess { result ->
            serviceDao.upsertServices(
                result.map {
                    DbMapper.mapService(it).copy(lastUpdated = System.currentTimeMillis())
                }
            )
        }.onFailure {
            Timber.tag("GetServicesUseCase").w(it, "Failed to get services from API")
        }.getOrDefault(listOf())
    }

    private suspend fun loadCategories(): List<CategoryDto> {
        return supabaseApi.getCategories().first().onSuccess { result ->
            categoryDao.upsertCategories(
                result.map {
                    DbMapper.mapCategory(it)
                }
            )
        }.onFailure {
            Timber.tag("GetServicesUseCase").w(it, "Failed to get categories from API")
        }.getOrDefault(listOf())
    }
}
