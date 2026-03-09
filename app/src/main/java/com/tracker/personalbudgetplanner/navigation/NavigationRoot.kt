package com.tracker.personalbudgetplanner.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.tracker.personalbudgetplanner.features.income.presentation.IncomeSetupScreen
import com.tracker.personalbudgetplanner.features.welcome.presentation.WelcomeScreen

@Composable
fun NavigationRoot() {
    val backStack = rememberNavBackStack(Routes.Welcome)

    Scaffold() { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()

            ), backStack = backStack,
            entryProvider = entryProvider {
                entry<Routes.Welcome> {
                    WelcomeScreen {
                        backStack.add(Routes.OnBoarding)
                    }
                }
                entry<Routes.OnBoarding> {
                    IncomeSetupScreen() { _, _ -> }
                }
            }
        )
    }
}