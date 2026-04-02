package com.tracker.personalbudgetplanner.ui.transaction.domain

data class Expenses(
    val id : Int,
    val categoryId: Int,
    val amount: Double,
    val note: String,
    val timestamp: Long,
    val month: Int,
    val year: Int
)
