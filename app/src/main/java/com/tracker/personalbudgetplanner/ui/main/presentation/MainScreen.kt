package com.tracker.personalbudgetplanner.ui.main.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.tracker.personalbudgetplanner.navigation.BottomNavKey
import com.tracker.personalbudgetplanner.ui.budget.presentation.BudgetScreenRoot
import com.tracker.personalbudgetplanner.ui.budget.presentation.BudgetViewModel
import com.tracker.personalbudgetplanner.ui.category.presentation.CategoriesScreenRoot
import com.tracker.personalbudgetplanner.ui.category.presentation.CategoriesViewModel
import com.tracker.personalbudgetplanner.ui.dashboard.presentation.DashboardScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen() {
    val recordBackStack = rememberNavBackStack(BottomNavKey.Records)
    val analysisBackStack = rememberNavBackStack(BottomNavKey.Analysis)
    val budgetBackStack = rememberNavBackStack(BottomNavKey.Budget)
    val categoriesBackStack = rememberNavBackStack(BottomNavKey.Categories)
    val settingsBackStack = rememberNavBackStack(BottomNavKey.Settings)

    var currentKey by rememberSaveable(stateSaver = BottomNavKey.stateSaver) {
        mutableStateOf(BottomNavKey.Records)
    }
    val currentBackStack = when (currentKey) {
        BottomNavKey.Records -> recordBackStack
        BottomNavKey.Analysis -> analysisBackStack
        BottomNavKey.Budget -> budgetBackStack
        BottomNavKey.Categories -> categoriesBackStack
        BottomNavKey.Settings -> settingsBackStack
    }

    val addToBackStack: (NavKey) -> Unit = {
        when (currentKey) {
            BottomNavKey.Records -> recordBackStack.add(it)
            BottomNavKey.Analysis -> analysisBackStack.add(it)
            BottomNavKey.Budget -> budgetBackStack.add(it)
            BottomNavKey.Categories -> categoriesBackStack.add(it)
            BottomNavKey.Settings -> settingsBackStack.add(it)
        }
    }
    val resetBackStack: (NavBackStack<NavKey>) -> Unit = { backStack ->
        if (backStack.size > 1)
            backStack.subList(1, backStack.size).clear()
    }

    val onHandleBackPressed: () -> Unit = {
        onBackPressed(
            currentBottomKey = currentKey,
            recordsBackStackSize = recordBackStack.size,
            analysisBackStackSize = analysisBackStack.size,
            budgetBackStackSize = budgetBackStack.size,
            categoriesBackStackSize = categoriesBackStack.size,
            settingsBackStackSize = settingsBackStack.size,
            onSetHomeKey = {
                currentKey = BottomNavKey.Records
            },
            onPopRecordsBackStack = {
                recordBackStack.removeLastOrNull()
            },
            onPopAnalysisBackStack = {
                analysisBackStack.removeLastOrNull()
            },
            onPopBudgetBackStack = {
                budgetBackStack.removeLastOrNull()
            },
            onPopCategoriesBackStack = {
                categoriesBackStack.removeLastOrNull()
            },
            onPopupSettingsBackStack = {
                settingsBackStack.removeLastOrNull()
            }
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                modifier = Modifier.graphicsLayer {
                    shadowElevation = 8f
                }
            ) {
                BottomNavKey.items.forEach { key ->
                    val isSelected = key == currentKey

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentKey != key) {
                                currentKey = key
                            } else {
                                when (key) {
                                    BottomNavKey.Records -> resetBackStack(recordBackStack)
                                    BottomNavKey.Analysis -> resetBackStack(analysisBackStack)
                                    BottomNavKey.Budget -> resetBackStack(budgetBackStack)
                                    BottomNavKey.Categories -> resetBackStack(categoriesBackStack)
                                    BottomNavKey.Settings -> resetBackStack(settingsBackStack)
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = key.icon,
                                contentDescription = key.label,
                                // Animate the icon color for a smooth transition
                                tint = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        },
                        label = {
                            Text(
                                text = key.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    // Use a bolder weight for the selected item
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    letterSpacing = if (isSelected) 0.sp else 0.5.sp
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = 0.7f
                            )
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            backStack = currentBackStack, entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<BottomNavKey.Records> {
                    DashboardScreen { }
                }
                entry<BottomNavKey.Analysis> {

                }
                entry<BottomNavKey.Budget> {
                    val viewModel = koinViewModel<BudgetViewModel>()
                    BudgetScreenRoot(viewModel)
                }

                entry<BottomNavKey.Categories> {
                    val viewModel = koinViewModel<CategoriesViewModel>()
                    CategoriesScreenRoot(viewModel)
                }

                entry<BottomNavKey.Settings> {
                }
            })
    }

    BackHandler(enabled = true) {
        onHandleBackPressed()
    }
}

private fun onBackPressed(
    currentBottomKey: BottomNavKey,
    recordsBackStackSize: Int,
    analysisBackStackSize: Int,
    budgetBackStackSize: Int,
    categoriesBackStackSize: Int,
    settingsBackStackSize: Int,
    onSetHomeKey: () -> Unit,
    onPopRecordsBackStack: () -> Unit,
    onPopAnalysisBackStack: () -> Unit,
    onPopBudgetBackStack: () -> Unit,
    onPopCategoriesBackStack: () -> Unit,
    onPopupSettingsBackStack: () -> Unit,
) {
    when (currentBottomKey) {
        BottomNavKey.Records -> {
            if (recordsBackStackSize > 1) {
                onPopRecordsBackStack()
            } else {
                onSetHomeKey()
            }
        }

        BottomNavKey.Analysis -> {
            if (analysisBackStackSize > 1) {
                onPopAnalysisBackStack()
            } else {
                onSetHomeKey()
            }
        }

        BottomNavKey.Budget -> {
            if (budgetBackStackSize > 1) {
                onPopBudgetBackStack()
            } else {
                onSetHomeKey()
            }
        }

        BottomNavKey.Categories -> {
            if (categoriesBackStackSize > 1) {
                onPopCategoriesBackStack()
            } else {
                onSetHomeKey()
            }
        }

        BottomNavKey.Settings -> {
            if (settingsBackStackSize > 1) {
                onPopupSettingsBackStack()
            } else {
                onSetHomeKey()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}