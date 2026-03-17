package com.tracker.personalbudgetplanner.ui.budget_category.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "icons")
data class IconEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val iconName: String,
)