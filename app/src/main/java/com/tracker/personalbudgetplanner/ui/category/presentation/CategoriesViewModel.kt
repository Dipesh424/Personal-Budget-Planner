package com.tracker.personalbudgetplanner.ui.category.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.personalbudgetplanner.ui.category.domain.repository.CategoriesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class CategoriesViewModel(private val categoriesRepository: CategoriesRepository) : ViewModel() {
    private val _state = MutableStateFlow(CategoryState())
    val state = _state.onStart {
        observeCategories()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = _state.value
    )

    private var observeCategoriesJob: Job? = null

    fun observeCategories() {
        observeCategoriesJob?.cancel()
        observeCategoriesJob = categoriesRepository.getCategories()
            .onEach { categories ->
                _state.update { it.copy(categories = categories) }
            }.launchIn(viewModelScope)
    }
}