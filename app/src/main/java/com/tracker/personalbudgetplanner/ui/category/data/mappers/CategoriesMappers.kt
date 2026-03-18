package com.tracker.personalbudgetplanner.ui.category.data.mappers

import com.tracker.personalbudgetplanner.ui.category.data.local.CategoryEntity
import com.tracker.personalbudgetplanner.ui.category.data.local.CategoryWithIcon
import com.tracker.personalbudgetplanner.ui.category.data.local.IconEntity
import com.tracker.personalbudgetplanner.ui.category.domain.Categories
import com.tracker.personalbudgetplanner.ui.category.domain.CategoryIcon

fun CategoryWithIcon.toCategories() = Categories(
    id = category.id,
    name = category.name,
    iconId = category.iconId,
    iconName = icon.iconName,
    type = category.type
)

fun Categories.toCategoryEntity() = CategoryEntity(
    id = id ?: 0,
    name = name,
    iconId = iconId,
    type = type
)

fun IconEntity.toCategoryIcon() = CategoryIcon(
    id = id,
    iconName = iconName,
)