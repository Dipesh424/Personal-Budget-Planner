package com.tracker.personalbudgetplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tracker.personalbudgetplanner.features.income.presentation.IncomeSetupScreen
import com.tracker.personalbudgetplanner.ui.theme.PersonalBudgetPlannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PersonalBudgetPlannerTheme {
//                WelcomeScreen(onTimeout = {})
                IncomeSetupScreen(){_, _ -> }
            }
        }
    }
}