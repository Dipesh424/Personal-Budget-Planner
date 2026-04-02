package com.tracker.personalbudgetplanner.ui.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.personalbudgetplanner.core.data.AppDao
import com.tracker.personalbudgetplanner.utils.constants.DbConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class RecentActivityState(
    val transactions: List<TransactionUiModel> = emptyList(),
    val searchQuery: String = ""
)

class RecentActivityViewModel(private val appDao: AppDao) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val state = combine(
        _searchQuery,
        appDao.getAllExpenses(),
        appDao.getCategories()
    ) { query, expenses, categories ->
        val categoryMap = categories.associate { it.category.id to it }

        val uiTransactions = expenses.map { expense ->
            val category = categoryMap[expense.categoryId]
            TransactionUiModel(
                categoryName = category?.category?.name ?: "Unknown",
                amount = expense.amount,
                date = Instant.ofEpochMilli(expense.timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                isExpense = category?.category?.type == DbConstants.category_expense,
                timestamp = expense.timestamp
            )
        }.filter { 
            it.categoryName.contains(query, ignoreCase = true) 
        }.sortedByDescending { it.timestamp }

        RecentActivityState(
            transactions = uiTransactions,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = RecentActivityState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}