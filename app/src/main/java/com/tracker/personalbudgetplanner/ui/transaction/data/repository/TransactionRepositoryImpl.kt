package com.tracker.personalbudgetplanner.ui.transaction.data.repository

import com.tracker.personalbudgetplanner.core.data.AppDao
import com.tracker.personalbudgetplanner.core.domain.DataError
import com.tracker.personalbudgetplanner.core.domain.EmptyResult
import com.tracker.personalbudgetplanner.core.domain.Result
import com.tracker.personalbudgetplanner.ui.budget.data.local.ExpenseEntity
import com.tracker.personalbudgetplanner.ui.transaction.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class TransactionRepositoryImpl(private val appDao: AppDao) : TransactionRepository {
    override suspend fun upsertTransaction(transaction: ExpenseEntity): EmptyResult<DataError.Local> {
        return try {
            appDao.upsertExpense(transaction)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override fun getAllTransactions(): Flow<List<ExpenseEntity>> {
        return appDao.getAllExpenses()
    }
}