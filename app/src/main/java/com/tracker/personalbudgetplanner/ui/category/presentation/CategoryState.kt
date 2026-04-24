package com.tracker.personalbudgetplanner.ui.category.presentation

import androidx.compose.runtime.Immutable
import com.tracker.personalbudgetplanner.ui.category.domain.Categories
import com.tracker.personalbudgetplanner.ui.category.domain.CategoryIcon

@Immutable
data class CategoryState(
    val isLoading: Boolean = true,
    val categories: List<Categories> = emptyList(),
    val icons: List<CategoryIcon> = emptyList(),
    val isAddingNew: Boolean = false,
    val categoryToEdit: Categories? = null,
    val categoryToDelete: Categories? = null
)