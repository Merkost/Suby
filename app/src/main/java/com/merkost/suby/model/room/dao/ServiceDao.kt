package com.merkost.suby.model.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.merkost.suby.model.room.entity.ServiceDb
import com.merkost.suby.model.room.entity.ServiceWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertService(service: ServiceDb): Long

    @Update
    suspend fun updateService(service: ServiceDb)

    @Transaction
    suspend fun upsertServices(services: List<ServiceDb>) {
        services.forEach { service ->
            val id = insertService(service)
            if (id == -1L) {
                updateService(service)
            }
        }
    }

    @Query("SELECT * FROM service ORDER BY name")
    fun getServices(): Flow<List<ServiceDb>>

    @Transaction
    @Query("SELECT * FROM service ORDER BY name")
    fun getServicesWithCategories(): Flow<List<ServiceWithCategory>>

    @Delete
    suspend fun deleteServices(serviceDb: ServiceDb): Int

    @Query("SELECT MAX(lastUpdated) FROM service")
    suspend fun getLastServiceUpdate(): Long?

}