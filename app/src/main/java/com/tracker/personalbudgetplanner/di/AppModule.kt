package com.tracker.personalbudgetplanner.di

import androidx.room.Room
import com.tracker.personalbudgetplanner.core.data.AppDao
import com.tracker.personalbudgetplanner.core.data.AppDatabase
import com.tracker.personalbudgetplanner.core.data.DatabaseInitializer
import com.tracker.personalbudgetplanner.utils.constants.DbConstants
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
}