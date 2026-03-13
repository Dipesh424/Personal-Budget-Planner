package com.tracker.personalbudgetplanner.di

import com.tracker.personalbudgetplanner.ui.budget_category.data.repository.CategoriesRepositoryImpl
import com.tracker.personalbudgetplanner.ui.budget_category.domain.repository.CategoriesRepository
import com.tracker.personalbudgetplanner.ui.budget_category.presentation.CategoriesViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::CategoriesRepositoryImpl).bind<CategoriesRepository>()
    viewModelOf(::CategoriesViewModel)
}