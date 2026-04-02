package com.tracker.personalbudgetplanner.ui.budget.data.mappers

import com.tracker.personalbudgetplanner.ui.budget.data.local.BudgetEntity
import com.tracker.personalbudgetplanner.ui.budget.data.local.CategoryWithBudget
import com.tracker.personalbudgetplanner.ui.budget.domain.Budget
import com.tracker.personalbudgetplanner.ui.budget.domain.BudgetCategories
import com.tracker.personalbudgetplanner.ui.category.data.mappers.toCategories

fun BudgetEntity.toBudget() = Budget(
    id = id,
    categoryId = categoryId,
    amount = amount,
    month = month,
    year = year
)

fun Budget.toBudgetEntity() = BudgetEntity(
    id = id ?: 0,
    categoryId = categoryId,
    amount = amount,
    month = month,
    year = year
)

fun CategoryWithBudget.toBudgetCategories() = BudgetCategories(
    budgetId = budgetId,
    category = categoryWithIcon.toCategories(),
    budgetAmount = budgetAmount,
    spentAmount = spentAmount,
    month = month,
    year = year
)