package com.tracker.personalbudgetplanner.ui.category.domain

data class Categories(
    val id: Int,
    val name: String,
    val iconId: Int,
    val budgetLimit: Double = 0.0,
    val iconName : String
)
