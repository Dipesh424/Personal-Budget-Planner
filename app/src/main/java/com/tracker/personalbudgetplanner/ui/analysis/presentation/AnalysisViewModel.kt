package com.tracker.personalbudgetplanner.ui.analysis.presentation

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.personalbudgetplanner.core.data.AppDao
import com.tracker.personalbudgetplanner.ui.budget.data.local.ExpenseEntity
import com.tracker.personalbudgetplanner.ui.category.data.local.CategoryWithIcon
import com.tracker.personalbudgetplanner.utils.constants.DbConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

data class AnalysisState(
    val totalSavings: Double = 0.0,
    val savingsGoalProgress: Float = 0.0f,
    val spendingBreakdown: List<CategorySpend> = emptyList(),
    val cashFlowData: List<CashFlowUiModel> = emptyList(),
    val categoryPerformance: List<CategoryPerformanceUiModel> = emptyList(),
    val selectedFilter: String = "Month",
    val selectedDate: LocalDate = LocalDate.now(),
    val isLoading: Boolean = false
)

data class CashFlowUiModel(
    val label: String,
    val value: Float,
    val amount: String
)

data class CategoryPerformanceUiModel(
    val name: String,
    val amount: String,
    val color: Color
)

class AnalysisViewModel(private val appDao: AppDao) : ViewModel() {

    private val _selectedFilter = MutableStateFlow("Month")
    private val _selectedDate = MutableStateFlow(LocalDate.now())

    val state = combine(
        _selectedFilter,
        _selectedDate,
        appDao.getAllExpenses(),
        appDao.getCategories()
    ) { filter, date, expenses, categories ->
        val categoryMap = categories.associateBy { it.category.id }
        
        val filteredExpenses = expenses.filter { expense ->
            val expenseDate = Instant.ofEpochMilli(expense.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
            when (filter) {
                "Day" -> expenseDate == date
                "Week" -> {
                    val startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    val endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                    !expenseDate.isBefore(startOfWeek) && !expenseDate.isAfter(endOfWeek)
                }
                "Month" -> expenseDate.month == date.month && expenseDate.year == date.year
                "Year" -> expenseDate.year == date.year
                else -> true
            }
        }

        val totalIncome = filteredExpenses
            .filter { categoryMap[it.categoryId]?.category?.type == DbConstants.category_income }
            .sumOf { it.amount }
            
        val totalExpense = filteredExpenses
            .filter { categoryMap[it.categoryId]?.category?.type == DbConstants.category_expense }
            .sumOf { it.amount }

        // Spending Breakdown with unique colors
        val expenseByCategory = filteredExpenses
            .filter { categoryMap[it.categoryId]?.category?.type == DbConstants.category_expense }
            .groupBy { it.categoryId }
            .entries.mapIndexed { index, entry ->
                val category = categoryMap[entry.key]
                CategorySpend(
                    name = category?.category?.name ?: "Unknown",
                    amount = entry.value.sumOf { it.amount }.toFloat(),
                    color = getUniqueColor(index)
                )
            }

        // Cash Flow (Last 6 labels based on filter)
        val cashFlow = when (filter) {
            "Month", "Year" -> {
                val lastMonths = (0..5).reversed().map { date.minusMonths(it.toLong()) }
                lastMonths.map { monthDate ->
                    val monthExpenses = expenses.filter {
                        val d = Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
                        d.month == monthDate.month && d.year == monthDate.year
                    }
                    val income = monthExpenses
                        .filter { categoryMap[it.categoryId]?.category?.type == DbConstants.category_income }
                        .sumOf { it.amount }
                    val expense = monthExpenses
                        .filter { categoryMap[it.categoryId]?.category?.type == DbConstants.category_expense }
                        .sumOf { it.amount }
                    
                    CashFlowUiModel(
                        label = monthDate.month.name.take(3),
                        value = if (income > 0) (expense / income).toFloat().coerceIn(0.01f, 1f) else 0.01f,
                        amount = "Rs. ${expense.toInt()}"
                    )
                }
            }
            "Week" -> {
                val last7Days = (0..6).reversed().map { date.minusDays(it.toLong()) }
                last7Days.map { dayDate ->
                    val dayExpenses = expenses.filter {
                        val d = Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
                        d == dayDate
                    }
                    val expense = dayExpenses
                        .filter { categoryMap[it.categoryId]?.category?.type == DbConstants.category_expense }
                        .sumOf { it.amount }
                    
                    CashFlowUiModel(
                        label = dayDate.dayOfWeek.name.take(3),
                        value = 0.5f, // Just a placeholder height for weekly
                        amount = "Rs. ${expense.toInt()}"
                    )
                }
            }
            else -> emptyList()
        }

        AnalysisState(
            totalSavings = totalIncome - totalExpense,
            savingsGoalProgress = if (totalIncome > 0) ( (totalIncome - totalExpense) / totalIncome ).toFloat().coerceIn(0f, 1f) else 0f,
            spendingBreakdown = expenseByCategory,
            cashFlowData = cashFlow,
            categoryPerformance = expenseByCategory.map { 
                CategoryPerformanceUiModel(it.name, "Rs. ${it.amount.toInt()}", it.color)
            },
            selectedFilter = filter,
            selectedDate = date
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = AnalysisState()
    )

    fun onFilterChange(filter: String) {
        _selectedFilter.value = filter
    }

    fun onDateChange(newDate: LocalDate) {
        _selectedDate.value = newDate
    }

    private fun getUniqueColor(index: Int): Color {
        val colors = listOf(
            Color(0xFF6366F1), Color(0xFF10B981), Color(0xFFF43F5E), 
            Color(0xFFF59E0B), Color(0xFF8B5CF6), Color(0xFFEC4899),
            Color(0xFF06B6D4), Color(0xFF84CC16), Color(0xFFEAB308)
        )
        return colors[index % colors.size]
    }
}