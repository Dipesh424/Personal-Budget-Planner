package com.tracker.personalbudgetplanner.ui.budget_category.data.mappers

import com.tracker.personalbudgetplanner.ui.budget_category.data.local.CategoryWithIcon
import com.tracker.personalbudgetplanner.ui.budget_category.domain.Categories

fun CategoryWithIcon.toCategories() = Categories(
    id = category.id,
    name = category.name,
    iconId = category.iconId,
    iconName = icon.iconName
)