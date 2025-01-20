package com.merkost.suby.model.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.merkost.suby.model.room.entity.Service
import com.merkost.suby.model.room.entity.related.ServiceWithCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

@Dao
interface ServiceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertService(service: Service): Long

    @Update
    suspend fun updateService(service: Service)

    @Transaction
    suspend fun upsertServices(services: List<Service>) {
        services.forEach { service ->
            val id = insertService(service)
            if (id == -1L) {
                updateService(service)
            }
        }
    }

    @Query("SELECT * FROM service ORDER BY name")
    fun getServices(): Flow<List<Service>>

    @Query("SELECT * FROM service WHERE id = :serviceId")
    suspend fun getServiceById(serviceId: Int): Service?

    @Transaction
    @Query("SELECT * FROM service ORDER BY name")
    fun getServicesWithCategories(): Flow<List<ServiceWithCategory>>

    @Delete
    suspend fun deleteServices(serviceDb: Service): Int

    @Query("SELECT MAX(lastUpdated) FROM service")
    suspend fun getLastServiceUpdate(): LocalDateTime?

}