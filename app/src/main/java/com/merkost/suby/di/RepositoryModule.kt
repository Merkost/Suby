package com.merkost.suby.di

import com.merkost.suby.repository.ktor.api.RatesApi
import com.merkost.suby.repository.ktor.api.SupabaseApi
import com.merkost.suby.repository.ktor.ktorHttpClient
import com.merkost.suby.repository.ktor.supaClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient {
        return ktorHttpClient
    }

    @Provides
    @Singleton
    fun provideCurrencyApi(httpClient: HttpClient): RatesApi {
        return RatesApi(httpClient)
    }

    @Singleton
    @Provides
    fun provideSupaClient(): SupabaseClient {
        return supaClient
    }

    @Singleton
    @Provides
    fun provideServicesApi(supabaseClient: SupabaseClient): SupabaseApi {
        return SupabaseApi(supabaseClient)
    }

}