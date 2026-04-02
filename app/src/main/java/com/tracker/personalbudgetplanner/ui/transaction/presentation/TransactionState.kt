package com.tracker.personalbudgetplanner.ui.transaction.presentation

import com.tracker.personalbudgetplanner.ui.category.domain.Categories
import com.tracker.personalbudgetplanner.utils.constants.DbConstants

data class TransactionState(
    val amount: String = "",
    val note: String = "",
    val selectedCategory: Categories? = null,
    val transactionType: String = DbConstants.category_expense,
    val categories: List<Categories> = emptyList(),
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)