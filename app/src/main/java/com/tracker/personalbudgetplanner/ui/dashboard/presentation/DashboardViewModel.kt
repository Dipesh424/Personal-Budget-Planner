package com.tracker.personalbudgetplanner.ui.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.personalbudgetplanner.core.data.AppDao
import com.tracker.personalbudgetplanner.ui.budget.data.local.CategoryWithBudget
import com.tracker.personalbudgetplanner.ui.category.data.local.CategoryWithIcon
import com.tracker.personalbudgetplanner.utils.constants.DbConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class DashboardState(
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val recentTransactions: List<TransactionUiModel> = emptyList(),
    val monthlyTargets: List<CategoryWithBudget> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now()
)

data class TransactionUiModel(
    val categoryName: String,
    val amount: Double,
    val date: String,
    val isExpense: Boolean,
    val timestamp: Long,
    val note: String = ""
)

class DashboardViewModel(private val appDao: AppDao) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())

    val state = _selectedDate.flatMapLatest { date ->
        combine(
            appDao.getAllExpenses(),
            appDao.getCategories(),
            appDao.getBudgetedCategories(date.monthValue, date.year)
        ) { expenses, categories, budgets ->
            val filteredExpenses = expenses.filter {
                val expenseDate = Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
                expenseDate.month == date.month && expenseDate.year == date.year
            }

            val categoryMap = categories.associate { it.category.id to it }

            val uiTransactions = filteredExpenses.map { expense ->
                val category = categoryMap[expense.categoryId]
                TransactionUiModel(
                    categoryName = category?.category?.name ?: "Unknown",
                    amount = expense.amount,
                    date = Instant.ofEpochMilli(expense.timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                    isExpense = category?.category?.type == DbConstants.category_expense,
                    timestamp = expense.timestamp,
                    note = expense.note
                )
            }.sortedByDescending { it.timestamp }

            val income = uiTransactions.filter { !it.isExpense }.sumOf { it.amount }
            val expense = uiTransactions.filter { it.isExpense }.sumOf { it.amount }

            DashboardState(
                totalBalance = income - expense,
                totalIncome = income,
                totalExpense = expense,
                recentTransactions = uiTransactions.take(10),
                monthlyTargets = budgets,
                selectedDate = date
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = DashboardState()
    )

    fun onDateChange(newDate: LocalDate) {
        _selectedDate.value = newDate
    }
}