package com.tracker.personalbudgetplanner.di

import androidx.room.Room
import com.tracker.personalbudgetplanner.core.data.AppDao
import com.tracker.personalbudgetplanner.core.data.AppDatabase
import com.tracker.personalbudgetplanner.core.data.DatabaseInitializer
import com.tracker.personalbudgetplanner.ui.dashboard.presentation.DashboardViewModel
import com.tracker.personalbudgetplanner.ui.settings.presentation.SettingsViewModel
import com.tracker.personalbudgetplanner.utils.constants.DbConstants
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java, DbConstants.dbName
        ).addCallback(DatabaseInitializer { get<AppDao>() })
            .build()
    }

    single { get<AppDatabase>().appDao() }

    viewModel { DashboardViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
}