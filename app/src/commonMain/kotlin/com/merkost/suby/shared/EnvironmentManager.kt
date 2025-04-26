package com.merkost.suby.shared

expect object EnvironmentManager {
    val isDebug: Boolean
    val supabaseId: String
    val supabaseApiKey: String
}