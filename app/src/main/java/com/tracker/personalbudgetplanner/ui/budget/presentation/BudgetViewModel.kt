package com.tracker.personalbudgetplanner.ui.budget.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.personalbudgetplanner.ui.budget.domain.Budget
import com.tracker.personalbudgetplanner.ui.budget.domain.repository.BudgetRepository
import kotlinx.coroutines.Job
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

    private var getBudgetedAndUnBudgetedCategoriesJob: Job? = null

    fun upsertBudget(budget: Budget) {
        viewModelScope.launch {
            repository.upsertBudget(budget)
        }
    }

    fun getBudgetedAndUnBudgetedCategories(month: Int, year: Int) {
        getBudgetedAndUnBudgetedCategoriesJob?.cancel()
        _state.update { it.copy(isLoading = true) }
        getBudgetedAndUnBudgetedCategoriesJob = combine(
            repository.getBudgetedCategories(month, year),
            repository.getUnBudgetedCategories(month, year)
        ) { budgetedCategories, unbudgetedCategories ->
            _state.update {
                it.copy(
                    budgetedCategories = budgetedCategories,
                    unbudgetedCategories = unbudgetedCategories,
                    isLoading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onMoveMonth(delta: Int) {
        val newDate = _state.value.currentDate.plusMonths(delta.toLong())

        _state.update { it.copy(currentDate = newDate) }
    }

    fun deleteBudgetById(id: Int) {
        viewModelScope.launch {
            repository.deleteBudgetById(id)
        }
    }

    fun onAction(action: BudgetAction) {
        when (action) {
            is BudgetAction.OnNextMonth -> onMoveMonth(1)
            is BudgetAction.OnPreviousMonth -> onMoveMonth(-1)
            is BudgetAction.OnSetBudgetClick -> {
                _state.update { it.copy(categoryForAddBudget = action.category) }
            }
            is BudgetAction.OnEditBudgetClick -> {
                _state.update { it.copy(categoryToEditForBudget = action.budget) }
            }
            is BudgetAction.OnDeleteClick -> {
                _state.update { it.copy(budgetToDelete = action.budget) }
            }
            is BudgetAction.OnDismissDialogs -> {
                _state.update { it.copy(
                    categoryForAddBudget = null,
                    categoryToEditForBudget = null,
                    budgetToDelete = null
                )}
            }
            is BudgetAction.OnDeleteConfirm -> {
                val id = _state.value.budgetToDelete?.budgetId ?: return
                deleteBudgetById(id)
                onAction(BudgetAction.OnDismissDialogs)
            }
            is BudgetAction.OnSaveBudget -> {
                upsertBudget(action.budget)
                onAction(BudgetAction.OnDismissDialogs)
            }
        }
    }
}