package com.tracker.personalbudgetplanner

import android.app.Application
import com.tracker.personalbudgetplanner.di.appModule
import com.tracker.personalbudgetplanner.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BudgetPlannerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BudgetPlannerApplication)
            modules(appModule, repositoryModule)
        }
    }
}