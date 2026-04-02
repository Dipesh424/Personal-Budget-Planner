package com.tracker.personalbudgetplanner.ui.transaction.data.mappers

import com.tracker.personalbudgetplanner.ui.transaction.data.local.ExpenseEntity
import com.tracker.personalbudgetplanner.ui.transaction.domain.Expenses

fun Expenses.toExpenseEntity() = ExpenseEntity(
    id = id,
    categoryId = categoryId,
    amount = amount,
    note = note,
    timestamp = timestamp,
    year = year,
    month = month
)