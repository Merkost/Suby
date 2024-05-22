package com.merkost.suby.model.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.merkost.suby.model.room.entity.CategoryDb
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(service: CategoryDb): Long

    @Update
    suspend fun updateCategory(service: CategoryDb)

    @Transaction
    suspend fun upsertServices(categories: List<CategoryDb>) {
        categories.forEach { category ->
            val id = insertCategory(category)
            if (id == -1L) {
                updateCategory(category)
            }
        }
    }

    @Query("SELECT * FROM category ORDER BY name")
    fun getCategories(): Flow<List<CategoryDb>>

    @Delete
    suspend fun deleteCategory(categoryDb: CategoryDb): Int

}