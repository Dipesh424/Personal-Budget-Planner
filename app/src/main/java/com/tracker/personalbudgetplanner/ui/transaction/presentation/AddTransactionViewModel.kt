package com.tracker.personalbudgetplanner.ui.transaction.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.personalbudgetplanner.ui.category.domain.Categories
import com.tracker.personalbudgetplanner.ui.category.domain.repository.CategoriesRepository
import com.tracker.personalbudgetplanner.ui.transaction.domain.Expenses
import com.tracker.personalbudgetplanner.ui.transaction.domain.repository.TransactionRepository
import com.tracker.personalbudgetplanner.utils.constants.DbConstants
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddTransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoriesRepository
) :
    ViewModel() {

    private var cachedCategories = emptyList<Categories>()
    private var observeCategoryJob: Job? = null
    private val _state = MutableStateFlow(TransactionState())
    val state = _state.onStart {
        if (cachedCategories.isEmpty()) {
            observeCategories()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = _state.value
    )

    fun upsertExpense(expense: Expenses) {
        viewModelScope.launch {
            transactionRepository.upsertExpense(expense)
        }
    }

    fun observeCategories() {
        observeCategoryJob?.cancel()
        observeCategoryJob = categoryRepository.getCategories()
            .onEach { categories ->
                cachedCategories = categories
                val categories = filterCategory(_state.value.isExpense, categories)
                _state.update { it.copy(categories = categories) }
            }.launchIn(viewModelScope)
    }

    fun filterCategory(isExpense: Boolean, categories: List<Categories>): List<Categories> {
        val type = if (isExpense) DbConstants.category_expense else DbConstants.category_income
        return categories.filter { it.type == type }
    }

    fun onAction(action: TransactionAction) {
        when (action) {
            is TransactionAction.CalculatorKey -> {
                handleCalculatorInput(action.key)
            }

            is TransactionAction.UpdateType -> {
                val category = filterCategory(action.isExpense, cachedCategories)
                _state.update {
                    it.copy(
                        isExpense = action.isExpense,
                        selectedCategory = null,
                        categories = category
                    )
                }
            }

            is TransactionAction.ToggleCategorySheet -> {
                _state.update { it.copy(isCategorySheetOpen = action.isOpen) }
            }

            is TransactionAction.SelectCategory -> {
                _state.update { it.copy(selectedCategory = action.category) }
            }

            is TransactionAction.UpdateNote -> {
                _state.update { it.copy(note = action.note) }
            }

            is TransactionAction.UpdateDate -> {
                _state.update { it.copy(date = action.dateTime) }
            }

            TransactionAction.Save -> {
//                saveTransaction()
            }

            TransactionAction.Cancel -> {
                // Handled by Navigation 3 backstack.removeLast()
            }
        }
    }

    private fun handleCalculatorInput(key: String) {
        val current = _state.value.amount

        when (key) {
            "C" -> {
                // Reset to zero
                _state.update { it.copy(amount = "0") }
            }

            "⌫" -> {
                // Remove last character, if only one left, reset to "0"
                _state.update {
                    it.copy(amount = if (current.length > 1) current.dropLast(1) else "0")
                }
            }

            "=" -> {
                // Calculate the result
                val result = evaluateExpression(current)
                _state.update { it.copy(amount = result) }
            }

            "." -> {
                // Logic: Only allow one decimal per number segment
                // Split by operators to check the current active number
                val lastNumberSegment = current.split(Regex("[+−×÷]")).last()
                if (!lastNumberSegment.contains(".")) {
                    _state.update { it.copy(amount = current + ".") }
                }
            }

            "+", "−", "×", "÷" -> {
                val lastChar = current.lastOrNull()?.toString() ?: ""
                val operators = listOf("+", "−", "×", "÷")

                if (lastChar in operators) {
                    // If last char is already an operator, replace it with the new one
                    _state.update { it.copy(amount = current.dropLast(1) + key) }
                } else if (current != "0") {
                    // Otherwise, append the operator
                    _state.update { it.copy(amount = current + key) }
                }
            }

            else -> {
                // Handle numbers (0-9)
                _state.update {
                    it.copy(
                        amount = if (current == "0") key else current + key
                    )
                }
            }
        }
    }

    private fun evaluateExpression(expression: String): String {
        val operators = Regex("[+\\-*/]")
        val parts = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")

        val numbers = parts.split(operators).mapNotNull { it.toDoubleOrNull() }
        val operator = operators.find(parts)?.value

        if (numbers.size < 2 || operator == null) return expression

        val res = when (operator) {
            "+" -> numbers[0] + numbers[1]
            "-" -> numbers[0] - numbers[1]
            "*" -> numbers[0] * numbers[1]
            "/" -> if (numbers[1] != 0.0) numbers[0] / numbers[1] else 0.0
            else -> numbers[0]
        }

        return if (res % 1.0 == 0.0) res.toInt().toString() else "%.2f".format(res)
    }
}