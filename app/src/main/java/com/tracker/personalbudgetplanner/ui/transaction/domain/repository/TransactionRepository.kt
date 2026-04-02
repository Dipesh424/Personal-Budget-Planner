package com.tracker.personalbudgetplanner.ui.transaction.domain.repository

import com.tracker.personalbudgetplanner.core.domain.DataError
import com.tracker.personalbudgetplanner.core.domain.EmptyResult
import com.tracker.personalbudgetplanner.ui.budget.data.local.ExpenseEntity
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun upsertTransaction(transaction: ExpenseEntity): EmptyResult<DataError.Local>
    fun getAllTransactions(): Flow<List<ExpenseEntity>>
}