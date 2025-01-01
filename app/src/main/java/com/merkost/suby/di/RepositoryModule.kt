package com.merkost.suby.di

import com.merkost.suby.repository.ktor.api.RatesApi
import com.merkost.suby.repository.ktor.api.SupabaseApi
import com.merkost.suby.repository.ktor.ktorHttpClient
import com.merkost.suby.repository.ktor.supaClient
import io.github.jan.supabase.SupabaseClient
import io.ktor.client.HttpClient
import org.koin.dsl.module

val repositoryModule = module {
    single<HttpClient> { ktorHttpClient }
    single<SupabaseClient> { supaClient }
    single<RatesApi> { RatesApi(client = get()) }
    single<SupabaseApi> { SupabaseApi(client = get()) }
}