package com.tracker.personalbudgetplanner.ui.budget.presentation

import com.tracker.personalbudgetplanner.ui.budget.domain.Budget
import com.tracker.personalbudgetplanner.ui.budget.domain.BudgetCategories
import com.tracker.personalbudgetplanner.ui.category.domain.Categories

sealed interface BudgetAction {
    data object OnNextMonth : BudgetAction
    data object OnPreviousMonth : BudgetAction
    data class OnSetBudgetClick(val category: Categories) : BudgetAction
    data class OnEditBudgetClick(val budget: BudgetCategories) : BudgetAction
    data class OnDeleteClick(val budget: BudgetCategories) : BudgetAction
    data object OnDeleteConfirm : BudgetAction
    data object OnDismissDialogs : BudgetAction
    data class OnSaveBudget(val budget: Budget) : BudgetAction
}