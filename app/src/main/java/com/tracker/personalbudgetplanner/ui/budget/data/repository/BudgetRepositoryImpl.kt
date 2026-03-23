package com.tracker.personalbudgetplanner.ui.budget.data.repository

import com.tracker.personalbudgetplanner.core.data.AppDao
import com.tracker.personalbudgetplanner.core.domain.DataError
import com.tracker.personalbudgetplanner.core.domain.EmptyResult
import com.tracker.personalbudgetplanner.core.domain.Result
import com.tracker.personalbudgetplanner.ui.budget.data.mappers.toBudgetCategories
import com.tracker.personalbudgetplanner.ui.budget.data.mappers.toBudgetEntity
import com.tracker.personalbudgetplanner.ui.budget.domain.Budget
import com.tracker.personalbudgetplanner.ui.budget.domain.BudgetCategories
import com.tracker.personalbudgetplanner.ui.budget.domain.repository.BudgetRepository
import com.tracker.personalbudgetplanner.ui.category.data.mappers.toCategories
import com.tracker.personalbudgetplanner.ui.category.domain.Categories
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetRepositoryImpl(private val appDao: AppDao) : BudgetRepository {
    override suspend fun upsertBudget(budget: Budget): EmptyResult<DataError.Local> {
        return try {
            appDao.upsertBudget(budget.toBudgetEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override fun getBudgetedCategories(
        month: Int,
        year: Int
    ): Flow<List<BudgetCategories>> {
        return appDao.getBudgetedCategories(month, year).map { categories ->
            categories.map { it.toBudgetCategories() }
        }
    }

    override fun getUnBudgetedCategories(
        month: Int,
        year: Int
    ): Flow<List<Categories>> {
        return appDao.getUnbudgetedCategories(month, year).map { categories ->
            categories.map { it.toCategories() }
        }
    }

    override suspend fun deleteBudgetById(id: Int) {
        appDao.deleteBudgetById(id)
    }
}