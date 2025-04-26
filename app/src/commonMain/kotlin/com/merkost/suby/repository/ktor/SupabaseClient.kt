package com.merkost.suby.repository.ktor

import com.merkost.suby.shared.EnvironmentManager
import com.merkost.suby.utils.AndroidConstants.SUPABASE_ENDPOINT
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.PropertyConversionMethod
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json

val supaClient = createSupabaseClient(
    supabaseUrl = SUPABASE_ENDPOINT,
    supabaseKey = EnvironmentManager.supabaseApiKey
) {

    defaultSerializer = KotlinXSerializer(
        json = Json { ignoreUnknownKeys = true }
    )
    install(Postgrest) {

        propertyConversionMethod = PropertyConversionMethod.SERIAL_NAME
    }
    install(Storage) {}
}