package com.tracker.personalbudgetplanner.ui.transaction.domain.repository

import com.tracker.personalbudgetplanner.core.domain.DataError
import com.tracker.personalbudgetplanner.core.domain.EmptyResult
import com.tracker.personalbudgetplanner.ui.transaction.domain.Expenses

interface TransactionRepository {

    suspend fun upsertExpense(expense: Expenses): EmptyResult<DataError.Local>
}