package com.tracker.personalbudgetplanner.ui.budget_category.domain.repository

import com.tracker.personalbudgetplanner.ui.budget_category.domain.Categories
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {
    fun getCategories(): Flow<List<Categories>>
}