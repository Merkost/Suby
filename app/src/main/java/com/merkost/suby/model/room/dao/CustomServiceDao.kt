package com.merkost.suby.model.room.dao

import androidx.room.*
import com.merkost.suby.model.room.entity.CustomServiceDb
import com.merkost.suby.model.room.entity.related.CustomServiceWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCustomService(customService: CustomServiceDb): Long

    @Update
    suspend fun updateCustomService(customService: CustomServiceDb)

    @Delete
    suspend fun deleteCustomService(customService: CustomServiceDb)

    @Query("SELECT * FROM custom_service WHERE id = :id")
    suspend fun getCustomServiceById(id: Int): CustomServiceDb?

    @Transaction
    @Query("SELECT * FROM custom_service")
    fun getCustomServicesWithCategory(): Flow<List<CustomServiceWithCategory>>

    @Query("SELECT * FROM custom_service ORDER BY createdAt DESC")
    fun getAllCustomServices(): Flow<List<CustomServiceDb>>


}
