package com.tracker.personalbudgetplanner.ui.category.domain.repository

import com.tracker.personalbudgetplanner.core.domain.DataError
import com.tracker.personalbudgetplanner.core.domain.EmptyResult
import com.tracker.personalbudgetplanner.ui.category.domain.Categories
import com.tracker.personalbudgetplanner.ui.category.domain.CategoryIcon
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {
    fun getCategories(): Flow<List<Categories>>
    suspend fun insertCategory(category: Categories): EmptyResult<DataError.Local>
    fun getCategoryIcons(): Flow<List<CategoryIcon>>
}