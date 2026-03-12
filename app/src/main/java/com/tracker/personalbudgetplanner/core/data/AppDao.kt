package com.tracker.personalbudgetplanner.core.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.tracker.personalbudgetplanner.ui.budget_category.data.local.BudgetCategoryEntity
import com.tracker.personalbudgetplanner.ui.budget_category.data.local.IconEntity

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<BudgetCategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIcons(icons: List<IconEntity>)
}