package com.tracker.personalbudgetplanner.ui.category.presentation

import com.tracker.personalbudgetplanner.ui.category.domain.Categories

sealed interface CategoryAction {
    data object OnAddCategoryClick : CategoryAction
    data class OnEditCategoryClick(val category: Categories) : CategoryAction
    data class OnDeleteCategoryClick(val category: Categories) : CategoryAction
    data object OnDeleteConfirm : CategoryAction
    data object OnDismissDialogs : CategoryAction
    data class OnSaveCategory(val category: Categories) : CategoryAction
}