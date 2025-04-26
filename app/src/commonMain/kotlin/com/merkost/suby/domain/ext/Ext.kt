package com.merkost.suby.domain.ext

import com.merkost.suby.model.room.entity.Service
import com.merkost.suby.repository.ktor.supaClient
import io.github.jan.supabase.storage.storage


/**
 * Computed property to retrieve the appropriate image link.
 * - For backend services, constructs the URL using `logoName`.
 * - For custom services, uses the provided `customImageUri`.
 */
val Service.imageLink: String?
    get() = if (backendId != null && logoName != null) {
        runCatching { supaClient.storage["service_logo"].publicUrl(logoName.orEmpty()) }
            .getOrElse {
                // FIXME: Logger is not initialized yet
//                Timber.w(it, "Failed to get logo link for service $name")
                null
            }
    } else {
        customImageUri
    }