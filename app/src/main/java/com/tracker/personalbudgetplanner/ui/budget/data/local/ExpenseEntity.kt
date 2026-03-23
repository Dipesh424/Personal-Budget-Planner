package com.tracker.personalbudgetplanner.ui.budget.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tracker.personalbudgetplanner.ui.category.data.local.CategoryEntity
import com.tracker.personalbudgetplanner.utils.constants.DbConstants

@Entity(
    tableName = DbConstants.table_expenses,
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE // If category is deleted, delete its spending too
        )
    ],
    indices = [Index("categoryId")]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: Int,
    val amount: Double,
    val note: String,
    val timestamp: Long,
    val month: Int,
    val year: Int
)