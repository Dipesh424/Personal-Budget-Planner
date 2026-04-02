package com.tracker.personalbudgetplanner.di

import com.tracker.personalbudgetplanner.ui.budget.data.repository.BudgetRepositoryImpl
import com.tracker.personalbudgetplanner.ui.budget.domain.repository.BudgetRepository
import com.tracker.personalbudgetplanner.ui.budget.presentation.BudgetViewModel
import com.tracker.personalbudgetplanner.ui.category.data.repository.CategoriesRepositoryImpl
import com.tracker.personalbudgetplanner.ui.category.domain.repository.CategoriesRepository
import com.tracker.personalbudgetplanner.ui.category.presentation.CategoriesViewModel
import com.tracker.personalbudgetplanner.ui.dashboard.presentation.DashboardViewModel
import com.tracker.personalbudgetplanner.ui.dashboard.presentation.RecentActivityViewModel
import com.tracker.personalbudgetplanner.ui.analysis.presentation.AnalysisViewModel
import com.tracker.personalbudgetplanner.ui.transaction.data.repository.TransactionRepositoryImpl
import com.tracker.personalbudgetplanner.ui.transaction.domain.repository.TransactionRepository
import com.tracker.personalbudgetplanner.ui.transaction.presentation.TransactionViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::CategoriesRepositoryImpl).bind<CategoriesRepository>()
    singleOf(::BudgetRepositoryImpl).bind<BudgetRepository>()
    singleOf(::TransactionRepositoryImpl).bind<TransactionRepository>()
    viewModelOf(::CategoriesViewModel)
    viewModelOf(::BudgetViewModel)
    viewModelOf(::TransactionViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::RecentActivityViewModel)
    viewModelOf(::AnalysisViewModel)
}