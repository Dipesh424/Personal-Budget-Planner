package com.tracker.personalbudgetplanner.ui.budget.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.personalbudgetplanner.ui.budget.domain.Budget
import com.tracker.personalbudgetplanner.ui.budget.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {

    private val _state = MutableStateFlow(BudgetState())
    val state = _state.onStart {
        getBudgetedAndUnBudgetedCategories(
            month = _state.value.currentDate.monthValue,
            year = _state.value.currentDate.year
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = _state.value
    )

    fun upsertBudget(budget: Budget) {
        viewModelScope.launch {
            repository.upsertBudget(budget)
        }
    }

    fun getBudgetedAndUnBudgetedCategories(month: Int, year: Int) {
        _state.update { it.copy(isLoading = true) }
        combine(
            repository.getBudgetedCategories(month, year),
            repository.getUnBudgetedCategories(month, year)
        ) { budgetedCategories, unbudgetedCategories ->
            _state.update {
                it.copy(
                    isLoading = false,
                    budgetedCategories = budgetedCategories,
                    unbudgetedCategories = unbudgetedCategories
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onMoveMonth(delta: Int) {
        val newDate = _state.value.currentDate.plusMonths(delta.toLong())

        _state.update { it.copy(currentDate = newDate, isLoading = true) }
    }
}