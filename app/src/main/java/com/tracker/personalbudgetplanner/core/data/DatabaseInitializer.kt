package com.tracker.personalbudgetplanner.core.data

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tracker.personalbudgetplanner.ui.budget_category.data.local.CategoryEntity
import com.tracker.personalbudgetplanner.ui.budget_category.data.local.IconEntity
import com.tracker.personalbudgetplanner.utils.constants.IconConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseInitializer(private val provideDao: () -> AppDao) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            val iconList = listOf(
                IconEntity(id = 1, iconName = IconConstants.grocery),
                IconEntity(id = 2, iconName = IconConstants.home),
                IconEntity(id = 3, iconName = IconConstants.utilities),
                IconEntity(id = 4, iconName = IconConstants.transport),
                IconEntity(id = 5, iconName = IconConstants.baby),
                IconEntity(id = 6, iconName = IconConstants.clothing),
                IconEntity(id = 7, iconName = IconConstants.health),
                IconEntity(id = 8, iconName = IconConstants.personal),
                IconEntity(id = 9, iconName = IconConstants.dinner),
                IconEntity(id = 10, iconName = IconConstants.shopping),
                IconEntity(id = 11, iconName = IconConstants.entertainment),
                IconEntity(id = 12, iconName = IconConstants.investments),
                IconEntity(id = 13, iconName = IconConstants.education),
                IconEntity(id = 14, iconName = IconConstants.insurance),
                IconEntity(id = 15, iconName = IconConstants.gifts),
                IconEntity(id = 16, iconName = IconConstants.misc),
            )
            provideDao().insertIcons(iconList)

            val initialCategories = listOf(
                CategoryEntity(name = "Groceries", iconId = 1, budgetLimit = 0.0),
                CategoryEntity(name = "Rent & Housing", iconId = 2, budgetLimit = 0.0),
                CategoryEntity(name = "Utilities", iconId = 3, budgetLimit = 0.0),
                CategoryEntity(name = "Transport", iconId = 4, budgetLimit = 0.0),

                CategoryEntity(name = "Baby Care", iconId = 5, budgetLimit = 0.0),
                CategoryEntity(name = "Clothing", iconId = 6, budgetLimit = 0.0),
                CategoryEntity(name = "Health", iconId = 7, budgetLimit = 0.0),
                CategoryEntity(name = "Personal Care", iconId = 8, budgetLimit = 0.0),

                CategoryEntity(name = "Dining Out", iconId = 9, budgetLimit = 0.0),
                CategoryEntity(name = "Shopping", iconId = 10, budgetLimit = 0.0),
                CategoryEntity(name = "Entertainment", iconId = 11, budgetLimit = 0.0),

                CategoryEntity(name = "Investments", iconId = 12, budgetLimit = 0.0),
                CategoryEntity(name = "Education", iconId = 13, budgetLimit = 0.0),
                CategoryEntity(name = "Insurance", iconId = 14, budgetLimit = 0.0),

                CategoryEntity(name = "Gifts & Charity", iconId = 15, budgetLimit = 0.0),
                CategoryEntity(name = "Miscellaneous", iconId = 16, budgetLimit = 0.0)
            )
            provideDao().insertCategories(initialCategories)
        }
    }
}