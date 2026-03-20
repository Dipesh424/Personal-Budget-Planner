package com.tracker.personalbudgetplanner.ui.category.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.personalbudgetplanner.ui.category.domain.Categories
import com.tracker.personalbudgetplanner.ui.category.domain.repository.CategoriesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriesViewModel(private val categoriesRepository: CategoriesRepository) : ViewModel() {
    private val _state = MutableStateFlow(CategoryState())
    val state = _state.onStart {
        observeCategoryAndIcons()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = _state.value
    )

    fun observeCategoryAndIcons() {
        _state.update { it.copy(isLoading = true) }
        combine(
            categoriesRepository.getCategories(),
            categoriesRepository.getCategoryIcons()
        ) { categories, icons ->
            _state.update {
                it.copy(
                    categories = categories,
                    icons = icons,
                    isLoading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    fun upsertCategory(category: Categories) {
        viewModelScope.launch {
            categoriesRepository.upsertCategory(category)
        }
    }

    fun deleteCategory(category: Categories) {
        viewModelScope.launch {
            categoriesRepository.deleteCategory(category)
        }
    }
}