package com.tracker.personalbudgetplanner.ui.transaction.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.personalbudgetplanner.core.domain.Result
import com.tracker.personalbudgetplanner.ui.budget.data.local.ExpenseEntity
import com.tracker.personalbudgetplanner.ui.category.domain.Categories
import com.tracker.personalbudgetplanner.ui.category.domain.repository.CategoriesRepository
import com.tracker.personalbudgetplanner.ui.transaction.domain.repository.TransactionRepository
import com.tracker.personalbudgetplanner.utils.constants.DbConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionState())
    val state = combine(
        _state,
        categoriesRepository.getCategories()
    ) { state, categories ->
        state.copy(
            categories = categories.filter { it.type == state.transactionType }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = TransactionState()
    )

    fun onAmountChange(amount: String) {
        _state.update { it.copy(amount = amount) }
    }

    fun onNoteChange(note: String) {
        _state.update { it.copy(note = note) }
    }

    fun onCategorySelect(category: Categories) {
        _state.update { it.copy(selectedCategory = category) }
    }

    fun onTransactionTypeChange(type: String) {
        _state.update { 
            it.copy(
                transactionType = type,
                selectedCategory = null // Reset category when type changes
            ) 
        }
    }

    fun saveTransaction() {
        val currentState = state.value
        
        // Validation: For both types, amount and category are required.
        // For income, the user selects an "Income Source" which is just a category of type 'income'.
        if (currentState.amount.isBlank() || currentState.selectedCategory == null) {
            _state.update { it.copy(error = "Please fill all required fields") }
            return
        }

        val amountValue = currentState.amount.toDoubleOrNull() ?: run {
            _state.update { it.copy(error = "Invalid amount") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val now = LocalDate.now()
            
            // Both income and expenses are stored in the same table.
            // Income will have a categoryId pointing to a category where type is 'income'.
            val transaction = ExpenseEntity(
                categoryId = currentState.selectedCategory.id ?: 0,
                amount = amountValue,
                note = currentState.note,
                timestamp = System.currentTimeMillis(),
                month = now.monthValue,
                year = now.year
            )

            when (val result = transactionRepository.upsertTransaction(transaction)) {
                is Result.Success -> {
                    _state.update { it.copy(isSuccess = true, isLoading = false) }
                }
                is Result.Error -> {
                    _state.update { it.copy(error = "Failed to save transaction", isLoading = false) }
                }
            }
        }
    }

    fun resetSuccess() {
        _state.update { it.copy(isSuccess = false) }
    }
}