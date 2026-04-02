package com.tracker.personalbudgetplanner.core.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.tracker.personalbudgetplanner.ui.budget.data.local.BudgetEntity
import com.tracker.personalbudgetplanner.ui.budget.data.local.CategoryWithBudget
import com.tracker.personalbudgetplanner.ui.budget.data.local.ExpenseEntity
import com.tracker.personalbudgetplanner.ui.category.data.local.CategoryEntity
import com.tracker.personalbudgetplanner.ui.category.data.local.CategoryWithIcon
import com.tracker.personalbudgetplanner.ui.category.data.local.IconEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIcons(icons: List<IconEntity>)

    @Transaction
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getCategories(): Flow<List<CategoryWithIcon>>

    @Transaction
    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name ASC")
    fun getCategoriesByType(type: String): Flow<List<CategoryWithIcon>>

    @Upsert
    suspend fun upsertCategory(category: CategoryEntity)

    @Query("SELECT * FROM icons ORDER BY iconName ASC")
    fun getCategoryIcons(): Flow<List<IconEntity>>

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Upsert
    suspend fun upsertBudget(budget: BudgetEntity)

    @Upsert
    suspend fun upsertExpense(expense: ExpenseEntity)

    @Transaction
    @Query(
        """
    SELECT 
        c.*, 
        b.amount as budgetAmount,
        b.id as budgetId,
        b.month,
        b.year,
        (SELECT IFNULL(SUM(amount), 0.0) FROM expenses 
         WHERE categoryId = c.id AND month = :month AND year = :year) as spentAmount
    FROM categories c
    INNER JOIN budgets b ON c.id = b.categoryId
    WHERE b.month = :month AND b.year = :year
    ORDER BY c.name ASC
    """
    )
    fun getBudgetedCategories(month: Int, year: Int): Flow<List<CategoryWithBudget>>

    @Transaction
    @Query(
        """
    SELECT * FROM categories 
    WHERE id NOT IN (
        SELECT categoryId FROM budgets 
        WHERE month = :month AND year = :year
    )
    ORDER BY name ASC
"""
    )
    fun getUnbudgetedCategories(month: Int, year: Int): Flow<List<CategoryWithIcon>>

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteBudgetById(id: Int)

    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>
}