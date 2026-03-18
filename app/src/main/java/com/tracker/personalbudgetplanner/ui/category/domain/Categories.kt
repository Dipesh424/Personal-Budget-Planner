package com.tracker.personalbudgetplanner.ui.category.domain

data class Categories(
    val id: Int? = null,
    val name: String,
    val iconId: Int,
    val iconName: String? = null,
    val type: String
)
