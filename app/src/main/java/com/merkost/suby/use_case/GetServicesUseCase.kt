package com.merkost.suby.use_case

import com.merkost.suby.model.entity.dto.CategoryDto
import com.merkost.suby.model.entity.dto.ServiceDto
import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.model.room.DbMapper
import com.merkost.suby.model.room.dao.CategoryDao
import com.merkost.suby.model.room.dao.ServiceDao
import com.merkost.suby.repository.ktor.api.SupabaseApi
import com.merkost.suby.repository.room.ServiceRepository
import com.merkost.suby.utils.Constants
import com.merkost.suby.utils.Environment
import com.merkost.suby.utils.now
import com.merkost.suby.utils.toEpochMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDateTime
import timber.log.Timber

class GetServicesUseCase(
    private val supabaseApi: SupabaseApi,
    private val serviceDao: ServiceDao,
    private val categoryDao: CategoryDao,
    private val serviceRepository: ServiceRepository
) {
    operator fun invoke(): Flow<Result<List<Service>>> = flow {
        val currentTime = System.currentTimeMillis()
        val updateThreshold = Constants.SUBY_UPDATE_THRESHOLD.inWholeMilliseconds

        val lastServiceUpdate = serviceDao.getLastServiceUpdate()?.toEpochMillis() ?: 0
        val lastCategoryUpdate = categoryDao.getLastCategoryUpdate()?.toEpochMillis() ?: 0

        val needToUpdateServices = currentTime - lastServiceUpdate > updateThreshold
        val needToUpdateCategories = currentTime - lastCategoryUpdate > updateThreshold

        if (needToUpdateServices || needToUpdateCategories || Environment.DEBUG) {
            val categories = loadCategories()
            val services = loadServices()

            if (categories.isEmpty() || services.isEmpty()) {
                emit(Result.failure(Throwable("Failed to fetch data from API")))
                return@flow
            }
        }

        val services = serviceRepository.services.first()
        emit(Result.success(services))
    }

    private suspend fun loadServices(): List<ServiceDto> {
        return supabaseApi.getServices().first().onSuccess { result ->
            serviceDao.upsertServices(
                result.map {
                    DbMapper.mapService(it).copy(lastUpdated = LocalDateTime.now())
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
