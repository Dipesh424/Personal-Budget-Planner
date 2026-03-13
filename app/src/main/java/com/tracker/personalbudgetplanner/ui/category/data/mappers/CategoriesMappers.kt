package com.tracker.personalbudgetplanner.ui.category.data.mappers

import com.tracker.personalbudgetplanner.ui.category.domain.CategoryIcon
import com.tracker.personalbudgetplanner.ui.category.data.local.CategoryEntity
import com.tracker.personalbudgetplanner.ui.category.data.local.CategoryWithIcon
import com.tracker.personalbudgetplanner.ui.category.data.local.IconEntity
import com.tracker.personalbudgetplanner.ui.category.domain.Categories

fun CategoryWithIcon.toCategories() = Categories(
    id = category.id,
    name = category.name,
    iconId = category.iconId,
    iconName = icon.iconName
)

fun Categories.toCategoryEntity() = CategoryEntity(
    id = id,
    name = name,
    iconId = iconId,
)

fun IconEntity.toCategoryIcon() = CategoryIcon(
    id = id,
    iconName = iconName,
)