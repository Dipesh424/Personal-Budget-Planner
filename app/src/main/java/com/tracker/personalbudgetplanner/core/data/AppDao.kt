package com.tracker.personalbudgetplanner.core.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.tracker.personalbudgetplanner.ui.category.data.local.CategoryEntity
import com.tracker.personalbudgetplanner.ui.category.data.local.CategoryWithIcon
import com.tracker.personalbudgetplanner.ui.category.data.local.IconEntity
import com.tracker.personalbudgetplanner.utils.constants.DbConstants
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIcons(icons: List<IconEntity>)

    @Transaction
    @Query("SELECT * FROM ${DbConstants.table_categories} ORDER BY name ASC")
    fun getCategories(): Flow<List<CategoryWithIcon>>

    @Upsert
    suspend fun insertCategory(category: CategoryEntity)

    @Query("SELECT * FROM ${DbConstants.table_category_icons} ORDER BY iconName ASC")
    fun getCategoryIcons(): Flow<List<IconEntity>>

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)
}