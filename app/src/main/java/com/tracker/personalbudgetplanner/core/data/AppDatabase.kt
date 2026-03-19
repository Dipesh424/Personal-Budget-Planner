package com.tracker.personalbudgetplanner.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tracker.personalbudgetplanner.ui.budget.data.local.BudgetEntity
import com.tracker.personalbudgetplanner.ui.category.data.local.CategoryEntity
import com.tracker.personalbudgetplanner.ui.category.data.local.IconEntity

@Database(
    entities = [
        CategoryEntity::class,
        IconEntity::class,
        BudgetEntity::class
    ], version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}
