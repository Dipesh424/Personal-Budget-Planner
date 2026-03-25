package com.tracker.personalbudgetplanner.ui.transaction.presentation

import com.tracker.personalbudgetplanner.ui.category.domain.Categories
import java.time.LocalDateTime

sealed interface TransactionAction {
    // Calculator Actions
    data class CalculatorKey(val key: String) : TransactionAction

    // Toggle Actions
    data class UpdateType(val isExpense: Boolean) : TransactionAction

    // Selection Actions
    data class ToggleCategorySheet(val isOpen: Boolean) : TransactionAction
    data class SelectCategory(val category: Categories) : TransactionAction

    // Input Actions
    data class UpdateNote(val note: String) : TransactionAction
    data class UpdateDate(val dateTime: LocalDateTime) : TransactionAction

    // Screen Actions
    data object Save : TransactionAction
    data object Cancel : TransactionAction
}