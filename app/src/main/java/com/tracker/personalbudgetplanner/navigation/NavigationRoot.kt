package com.tracker.personalbudgetplanner.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tracker.personalbudgetplanner.features.budget.presentation.BudgetCategoryScreen
import com.tracker.personalbudgetplanner.features.dashboard.presentation.DashboardScreen
import com.tracker.personalbudgetplanner.features.dashboard.presentation.RecentActivityScreen
import com.tracker.personalbudgetplanner.features.income.presentation.IncomeSetupScreen
import com.tracker.personalbudgetplanner.features.welcome.presentation.WelcomeScreen

@Composable
fun NavigationRoot(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(
                onTimeout = { navController.navigate("income_setup") }
            )
        }
        composable("income_setup") {
            IncomeSetupScreen(
                onContinue = { _, _ -> navController.navigate("budget_category_setup") }
            )
        }
        composable("budget_category_setup") {
            BudgetCategoryScreen(
                onFinish = { navController.navigate("dashboard") },
                onSkip = { navController.navigate("dashboard") }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                onSeeAllClick = { navController.navigate("recent_activity") }
            )
        }
        composable("recent_activity") {
            RecentActivityScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}