package com.tracker.personalbudgetplanner.ui.budget_category.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tracker.personalbudgetplanner.utils.constants.DbConstants

@Entity(tableName = DbConstants.table_category_icons)
data class IconEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val iconName: String,
)