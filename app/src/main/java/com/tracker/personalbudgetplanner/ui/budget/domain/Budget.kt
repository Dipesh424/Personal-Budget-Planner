package com.tracker.personalbudgetplanner.ui.budget.domain

import com.tracker.personalbudgetplanner.ui.category.domain.Categories

data class Budget(
    val id: Int? = null,
    val categoryId: Int,
    val amount: Double,
    val month: Int,
    val year: Int
)

data class BudgetCategories(
    val budgetId : Int?,
    val category: Categories,
    val budgetAmount: Double,
    val spentAmount: Double = 0.0,
    val month: Int,
    val year: Int
)
