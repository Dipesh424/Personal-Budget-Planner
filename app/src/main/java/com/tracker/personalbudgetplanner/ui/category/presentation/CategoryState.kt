package com.tracker.personalbudgetplanner.ui.category.presentation

import com.tracker.personalbudgetplanner.ui.category.domain.Categories
import com.tracker.personalbudgetplanner.ui.category.domain.CategoryIcon

data class CategoryState(
    val isLoading: Boolean = true,
    val categories: List<Categories> = emptyList(),
    val icons: List<CategoryIcon> = emptyList()
)