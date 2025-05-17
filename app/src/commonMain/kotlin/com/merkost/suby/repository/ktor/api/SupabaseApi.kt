package com.merkost.suby.repository.ktor.api

import com.merkost.suby.domain.ServiceRequest
import com.merkost.suby.model.entity.dto.CategoryDto
import com.merkost.suby.model.entity.dto.ServiceDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


class SupabaseApi(private val client: SupabaseClient) {

    fun getCategories() = flow<Result<List<CategoryDto>>> {
        val list = client.postgrest.from(SupaTables.CATEGORY).select().decodeList<CategoryDto>()

        emit(Result.success(list))
    }.catch {
        if (it !is CancellationException) {
//            Timber.w(it, "Failed to get categories")
            // TODO: Logs
        }
        emit(Result.failure<List<CategoryDto>>(it))
    }

    fun getServices() = flow<Result<List<ServiceDto>>> {
        val list = client.postgrest[SupaTables.SERVICE].select(Columns.raw("*"))
            .decodeList<ServiceDto>()

        emit(Result.success(list))
    }.catch {
        if (it !is CancellationException) {
//            Timber.w(it, "Failed to get services")
        }
        emit(Result.failure<List<ServiceDto>>(it))
    }

    @Serializable
    data class FeedbackRequest(
        @SerialName("service_name")
        val serviceName: String,
        @SerialName("description")
        val description: String?,
        @SerialName("website")
        val website: String?,
        @SerialName("created_at")
        val createdAt: String
    )

    fun submitServiceRequest(serviceRequestDetails: ServiceRequest) = flow<Result<Unit>> {
        val feedbackRequest = FeedbackRequest(
            serviceName = serviceRequestDetails.serviceName,
            description = serviceRequestDetails.description,
            website = serviceRequestDetails.website,
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()
        )

        client.postgrest[SupaTables.SERVICE_REQUESTS]
            .insert(feedbackRequest)

        emit(Result.success(Unit))
    }.catch {
//        Timber.d(it, "Failed to submit service request")
        emit(Result.failure<Unit>(it))
    }
}