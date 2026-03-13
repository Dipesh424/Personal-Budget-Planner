package com.tracker.personalbudgetplanner.ui.budget_category.data.repository

import com.tracker.personalbudgetplanner.core.data.AppDao
import com.tracker.personalbudgetplanner.ui.budget_category.data.mappers.toCategories
import com.tracker.personalbudgetplanner.ui.budget_category.domain.Categories
import com.tracker.personalbudgetplanner.ui.budget_category.domain.repository.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoriesRepositoryImpl(private val appDao: AppDao) : CategoriesRepository {
    override fun getCategories(): Flow<List<Categories>> {
        return appDao.getCategories().map { entities ->
            entities.map { it.toCategories() }
        }
    }
}