package com.merkost.suby.repository.ktor

import com.merkost.suby.BuildConfig
import com.merkost.suby.SUPABASE_ENDPOINT
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.PropertyConversionMethod
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json

val supaClient = createSupabaseClient(
    supabaseUrl = SUPABASE_ENDPOINT,
    supabaseKey = BuildConfig.SUPABASE_API_KEY
) {

    defaultSerializer = KotlinXSerializer(
        json = Json { ignoreUnknownKeys = true }
    )
    install(Postgrest) {

        propertyConversionMethod = PropertyConversionMethod.SERIAL_NAME
    }
    install(Storage) {}
//    install(GoTrue)
//    install(ComposeAuth) {
//        nativeGoogleLogin("WEB_GOOGLE_CLIENT_ID") //Use the Web Client ID, not the Android one!
//    }

}