package com.tracker.personalbudgetplanner.ui.category.presentation

import com.tracker.personalbudgetplanner.ui.category.domain.Categories

data class CategoryState(
    val categories: List<Categories> = emptyList()
)