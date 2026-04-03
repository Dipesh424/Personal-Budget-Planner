package com.tracker.personalbudgetplanner.ui.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.personalbudgetplanner.core.data.AppDao
import com.tracker.personalbudgetplanner.utils.constants.DbConstants
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class SettingsState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val activeCategoriesCount: Int = 0
)

class SettingsViewModel(private val appDao: AppDao) : ViewModel() {

    val state = combine(
        appDao.getAllExpenses(),
        appDao.getCategories()
    ) { expenses, categories ->
        // Create a map for quick category type lookup
        val categoryTypeMap = categories.associate { it.category.id to it.category.type }

        val income = expenses.filter { 
            categoryTypeMap[it.categoryId] == DbConstants.category_income 
        }.sumOf { it.amount }

        val expense = expenses.filter { 
            categoryTypeMap[it.categoryId] == DbConstants.category_expense 
        }.sumOf { it.amount }

        // Only count categories that actually have transactions associated with them
        val activeCategories = expenses.map { it.categoryId }.distinct().size

        SettingsState(
            totalIncome = income,
            totalExpense = expense,
            activeCategoriesCount = activeCategories
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = SettingsState()
    )
}