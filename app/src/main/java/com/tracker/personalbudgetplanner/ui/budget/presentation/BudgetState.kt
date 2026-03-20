package com.tracker.personalbudgetplanner.ui.budget.presentation

import com.tracker.personalbudgetplanner.ui.budget.domain.BudgetCategories
import com.tracker.personalbudgetplanner.ui.category.domain.Categories
import java.time.LocalDate

data class BudgetState(
    val currentDate: LocalDate = LocalDate.now(),
    val totalBudget: Double = 0.0,
    val totalSpent: Double = 0.0,
    val budgetedCategories: List<BudgetCategories> = emptyList(),
    val unbudgetedCategories: List<Categories> = emptyList(),
    val isLoading: Boolean = false
)