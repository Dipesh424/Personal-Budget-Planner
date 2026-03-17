package com.tracker.personalbudgetplanner.core.data

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tracker.personalbudgetplanner.ui.category.data.local.CategoryEntity
import com.tracker.personalbudgetplanner.ui.category.data.local.IconEntity
import com.tracker.personalbudgetplanner.utils.constants.DbConstants
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
                CategoryEntity(name = "Groceries", iconId = 1, type = DbConstants.category_expense),
                CategoryEntity(
                    name = "Rent & Housing",
                    iconId = 2,
                    type = DbConstants.category_expense
                ),
                CategoryEntity(name = "Utilities", iconId = 3, type = DbConstants.category_expense),
                CategoryEntity(name = "Transport", iconId = 4, type = DbConstants.category_expense),

                CategoryEntity(name = "Baby Care", iconId = 5, type = DbConstants.category_expense),
                CategoryEntity(name = "Clothing", iconId = 6, type = DbConstants.category_expense),
                CategoryEntity(name = "Health", iconId = 7, type = DbConstants.category_expense),
                CategoryEntity(
                    name = "Personal Care",
                    iconId = 8,
                    type = DbConstants.category_expense
                ),

                CategoryEntity(
                    name = "Dining Out",
                    iconId = 9,
                    type = DbConstants.category_expense
                ),
                CategoryEntity(name = "Shopping", iconId = 10, type = DbConstants.category_expense),
                CategoryEntity(
                    name = "Entertainment",
                    iconId = 11,
                    type = DbConstants.category_expense
                ),

                CategoryEntity(
                    name = "Investments",
                    iconId = 12,
                    type = DbConstants.category_expense
                ),
                CategoryEntity(
                    name = "Education",
                    iconId = 13,
                    type = DbConstants.category_expense
                ),
                CategoryEntity(
                    name = "Insurance",
                    iconId = 14,
                    type = DbConstants.category_expense
                ),

                CategoryEntity(
                    name = "Gifts & Charity",
                    iconId = 15,
                    type = DbConstants.category_expense
                ),
                CategoryEntity(
                    name = "Miscellaneous",
                    iconId = 16,
                    type = DbConstants.category_expense
                )
            )
            provideDao().insertCategories(initialCategories)
        }
    }
}