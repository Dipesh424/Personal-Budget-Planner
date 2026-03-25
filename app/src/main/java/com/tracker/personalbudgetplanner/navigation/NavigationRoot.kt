package com.tracker.personalbudgetplanner.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.tracker.personalbudgetplanner.ui.category.presentation.CategoriesScreenRoot
import com.tracker.personalbudgetplanner.ui.category.presentation.CategoriesViewModel
import com.tracker.personalbudgetplanner.ui.income.presentation.IncomeSetupScreen
import com.tracker.personalbudgetplanner.ui.main.presentation.MainScreen
import com.tracker.personalbudgetplanner.ui.transaction.presentation.AddTransactionScreenRoot
import com.tracker.personalbudgetplanner.ui.transaction.presentation.AddTransactionViewModel
import com.tracker.personalbudgetplanner.ui.welcome.presentation.WelcomeScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavigationRoot() {
    val backStack = rememberNavBackStack(Routes.Welcome)

    // Removed Scaffold here to allow individual screens to handle their own insets
    // This fixes the "gap" below the bottom navigation bar in MainScreen
    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backStack,
        entryProvider = entryProvider {
            entry<Routes.Welcome> {
                WelcomeScreen {
                    backStack.remove(Routes.Welcome)
                    backStack.add(Routes.Main)
                }
            }
            entry<Routes.OnBoarding> {
                IncomeSetupScreen(onContinue = { _, _ ->
                    backStack.add(Routes.SetCategoryBudget)
                })
            }
            entry<Routes.SetCategoryBudget> {
                val viewModel = koinViewModel<CategoriesViewModel>()
                CategoriesScreenRoot(viewModel)
            }
            entry<Routes.Main> {
                MainScreen(onFabClick = {
                    backStack.add(Routes.Transaction)
                })
            }
            entry<Routes.Transaction> {
                val viewModel = koinViewModel<AddTransactionViewModel>()
                AddTransactionScreenRoot(viewModel)
            }
        }
    )
}