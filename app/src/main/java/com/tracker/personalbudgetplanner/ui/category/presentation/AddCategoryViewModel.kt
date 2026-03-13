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

class AddCategoryViewModel(private val categoriesRepository: CategoriesRepository) : ViewModel() {
    private var getCategoryIconsJob: Job? = null
    private val _state = MutableStateFlow(IconState())
    val state = _state.onStart {
        getCategoryIcons()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = _state.value
    )

    fun getCategoryIcons() {
        getCategoryIconsJob?.cancel()
        getCategoryIconsJob = categoriesRepository.getCategoryIcons()
            .onEach { icons ->
                _state.update { it.copy(icons = icons) }
            }.launchIn(viewModelScope)
    }
}