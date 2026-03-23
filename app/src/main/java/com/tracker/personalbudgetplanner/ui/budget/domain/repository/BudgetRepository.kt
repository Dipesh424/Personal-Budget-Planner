package com.tracker.personalbudgetplanner.ui.budget.domain.repository

import com.tracker.personalbudgetplanner.core.domain.DataError
import com.tracker.personalbudgetplanner.core.domain.EmptyResult
import com.tracker.personalbudgetplanner.ui.budget.domain.Budget
import com.tracker.personalbudgetplanner.ui.budget.domain.BudgetCategories
import com.tracker.personalbudgetplanner.ui.category.domain.Categories
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    suspend fun upsertBudget(budget: Budget): EmptyResult<DataError.Local>

    fun getBudgetedCategories(month: Int, year: Int): Flow<List<BudgetCategories>>

    fun getUnBudgetedCategories(month: Int, year: Int): Flow<List<Categories>>

    suspend fun deleteBudgetById (id : Int)
}