package com.tracker.personalbudgetplanner.ui.transaction.presentation

import com.tracker.personalbudgetplanner.ui.category.domain.Categories
import java.time.LocalDateTime

data class TransactionState(
    val amount: String = "0",
    val isExpense: Boolean = true,
    val selectedCategory: Categories? = null,
    val note: String = "",
    val date: LocalDateTime = LocalDateTime.now(),
    val isCategorySheetOpen: Boolean = false,
    val categories: List<Categories> = emptyList()
)