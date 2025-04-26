package com.merkost.suby.shared

import com.merkost.suby.BuildConfig

actual object EnvironmentManager {
    actual val isDebug: Boolean
        get() = BuildConfig.DEBUG

    actual val supabaseId: String
        get() = BuildConfig.SUPABASE_ID

    actual val supabaseApiKey: String
        get() = BuildConfig.SUPABASE_API_KEY
}