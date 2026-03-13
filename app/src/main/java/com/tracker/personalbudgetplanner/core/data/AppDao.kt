package com.tracker.personalbudgetplanner.core.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tracker.personalbudgetplanner.ui.budget_category.data.local.CategoryEntity
import com.tracker.personalbudgetplanner.ui.budget_category.data.local.CategoryWithIcon
import com.tracker.personalbudgetplanner.ui.budget_category.data.local.IconEntity
import com.tracker.personalbudgetplanner.utils.constants.DbConstants
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIcons(icons: List<IconEntity>)

    @Transaction
    @Query("SELECT * FROM ${DbConstants.table_categories}")
    fun getCategories(): Flow<List<CategoryWithIcon>>
}