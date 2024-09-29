package com.merkost.suby.model.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.merkost.suby.model.room.entity.CustomServiceDb
import com.merkost.suby.model.room.entity.related.CustomServiceWithCategory
import com.merkost.suby.utils.CustomServiceId
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCustomService(customService: CustomServiceDb): Long

    @Update
    suspend fun updateCustomService(customService: CustomServiceDb)

    @Delete
    suspend fun deleteCustomService(customService: CustomServiceDb)

    @Query("DELETE FROM custom_service WHERE id = :customServiceId")
    suspend fun deleteCustomService(customServiceId: CustomServiceId)

    @Query("SELECT * FROM custom_service WHERE id = :id")
    suspend fun getCustomServiceById(id: Int): CustomServiceDb?

    @Transaction
    @Query("SELECT * FROM custom_service")
    fun getCustomServicesWithCategory(): Flow<List<CustomServiceWithCategory>>

    @Query("SELECT * FROM custom_service ORDER BY createdAt DESC")
    fun getAllCustomServices(): Flow<List<CustomServiceDb>>


}
