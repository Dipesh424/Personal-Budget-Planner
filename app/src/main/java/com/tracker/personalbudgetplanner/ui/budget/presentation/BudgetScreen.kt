package com.tracker.personalbudgetplanner.ui.budget.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.tracker.personalbudgetplanner.ui.theme.PersonalBudgetPlannerTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun BudgetScreenRoot(viewModel: BudgetViewModel = koinViewModel()) {
    BudgetScreen()
}

@Composable
fun BudgetScreen() {

}

@Preview(showBackground = true)
@Composable
fun BudgetScreenPreview() {
    PersonalBudgetPlannerTheme() {
        BudgetScreen()
    }
}

