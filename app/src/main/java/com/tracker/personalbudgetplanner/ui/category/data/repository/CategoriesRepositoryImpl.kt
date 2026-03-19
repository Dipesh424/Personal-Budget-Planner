package com.tracker.personalbudgetplanner.ui.category.data.repository

import androidx.sqlite.SQLiteException
import com.tracker.personalbudgetplanner.core.data.AppDao
import com.tracker.personalbudgetplanner.core.domain.DataError
import com.tracker.personalbudgetplanner.core.domain.EmptyResult
import com.tracker.personalbudgetplanner.core.domain.Result
import com.tracker.personalbudgetplanner.ui.category.data.mappers.toCategories
import com.tracker.personalbudgetplanner.ui.category.data.mappers.toCategoryEntity
import com.tracker.personalbudgetplanner.ui.category.data.mappers.toCategoryIcon
import com.tracker.personalbudgetplanner.ui.category.domain.Categories
import com.tracker.personalbudgetplanner.ui.category.domain.CategoryIcon
import com.tracker.personalbudgetplanner.ui.category.domain.repository.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoriesRepositoryImpl(private val appDao: AppDao) : CategoriesRepository {
    override fun getCategories(): Flow<List<Categories>> {
        return appDao.getCategories().map { entities ->
            entities.map { it.toCategories() }
        }
    }

    override suspend fun upsertCategory(category: Categories): EmptyResult<DataError.Local> {
        return try {
            appDao.upsertCategory(category.toCategoryEntity())
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override fun getCategoryIcons(): Flow<List<CategoryIcon>> {
        return appDao.getCategoryIcons().map { entities ->
            entities.map { it.toCategoryIcon() }
        }
    }

    override suspend fun deleteCategory(category: Categories): EmptyResult<DataError.Local> {
        return try {
            appDao.deleteCategory(category.toCategoryEntity())
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }
}