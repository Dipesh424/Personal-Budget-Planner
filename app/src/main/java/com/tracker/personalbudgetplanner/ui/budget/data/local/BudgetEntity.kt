package com.tracker.personalbudgetplanner.ui.budget.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tracker.personalbudgetplanner.ui.category.data.local.CategoryEntity
import com.tracker.personalbudgetplanner.ui.category.data.local.CategoryWithIcon
import com.tracker.personalbudgetplanner.utils.constants.DbConstants

@Entity(
    tableName = DbConstants.table_budgets,
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoryId", "month", "year"], unique = true)]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: Int,
    val amount: Double,
    val month: Int,
    val year: Int
)

data class CategoryWithBudget(
    @Embedded val categoryWithIcon: CategoryWithIcon,
    val budgetId : Int,
    val budgetAmount: Double,
    val spentAmount: Double,
    val month: Int,
    val year: Int
)