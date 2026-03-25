package com.tracker.personalbudgetplanner.ui.transaction.data.repository

import com.tracker.personalbudgetplanner.core.data.AppDao
import com.tracker.personalbudgetplanner.core.domain.DataError
import com.tracker.personalbudgetplanner.core.domain.EmptyResult
import com.tracker.personalbudgetplanner.core.domain.Result
import com.tracker.personalbudgetplanner.ui.transaction.data.mappers.toExpenseEntity
import com.tracker.personalbudgetplanner.ui.transaction.domain.Expenses
import com.tracker.personalbudgetplanner.ui.transaction.domain.repository.TransactionRepository

class TransactionRepositoryImpl(private val appDao: AppDao) : TransactionRepository {
    override suspend fun upsertExpense(expense: Expenses): EmptyResult<DataError.Local> {
        return try {
            appDao.upsertExpense(expense.toExpenseEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }
}