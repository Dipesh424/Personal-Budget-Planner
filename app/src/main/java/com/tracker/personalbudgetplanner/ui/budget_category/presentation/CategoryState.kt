package com.tracker.personalbudgetplanner.ui.budget_category.presentation

import com.tracker.personalbudgetplanner.ui.budget_category.domain.Categories

data class CategoryState(
    val categories: List<Categories> = emptyList()
)