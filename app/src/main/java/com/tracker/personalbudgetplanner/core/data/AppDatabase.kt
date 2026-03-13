package com.tracker.personalbudgetplanner.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tracker.personalbudgetplanner.ui.budget_category.data.local.CategoryEntity
import com.tracker.personalbudgetplanner.ui.budget_category.data.local.IconEntity

@Database(
    entities = [
        CategoryEntity::class,
        IconEntity::class
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}
