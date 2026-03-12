package com.tracker.personalbudgetplanner.ui.budget_category.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "budget_categories", foreignKeys = [
        ForeignKey(
            entity = IconEntity::class,
            parentColumns = ["id"],
            childColumns = ["iconId"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class BudgetCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val iconId: Int,
    val budgetLimit: Double = 0.0
)