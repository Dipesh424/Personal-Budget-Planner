package com.tracker.personalbudgetplanner.di

import com.tracker.personalbudgetplanner.ui.category.data.repository.CategoriesRepositoryImpl
import com.tracker.personalbudgetplanner.ui.category.domain.repository.CategoriesRepository
import com.tracker.personalbudgetplanner.ui.category.presentation.CategoriesViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::CategoriesRepositoryImpl).bind<CategoriesRepository>()
    viewModelOf(::CategoriesViewModel)
}