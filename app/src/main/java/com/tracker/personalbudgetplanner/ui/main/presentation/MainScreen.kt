package com.tracker.personalbudgetplanner.ui.main.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
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
import com.tracker.personalbudgetplanner.navigation.Routes
import com.tracker.personalbudgetplanner.ui.budget.presentation.BudgetScreenRoot
import com.tracker.personalbudgetplanner.ui.budget.presentation.BudgetViewModel
import com.tracker.personalbudgetplanner.ui.category.presentation.CategoriesScreenRoot
import com.tracker.personalbudgetplanner.ui.category.presentation.CategoriesViewModel
import com.tracker.personalbudgetplanner.ui.dashboard.presentation.DashboardScreen
import com.tracker.personalbudgetplanner.ui.dashboard.presentation.RecentActivityScreen
import com.tracker.personalbudgetplanner.ui.settings.presentation.SettingsScreen
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
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                windowInsets = NavigationBarDefaults.windowInsets,
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
                                tint = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        },
                        label = {
                            Text(
                                text = key.label,
                                style = MaterialTheme.typography.labelSmall.copy(
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Expenses",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            backStack = currentBackStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<BottomNavKey.Records> {
                    DashboardScreen(onSeeAllClick = {
                        addToBackStack(Routes.RecentActivity)
                    })
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
                    SettingsScreen()
                }

                entry<Routes.RecentActivity> {
                    RecentActivityScreen(onBack = {
                        onHandleBackPressed()
                    })
                }
            }
        )
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
    val currentStackSize = when (currentBottomKey) {
        BottomNavKey.Records -> recordsBackStackSize
        BottomNavKey.Analysis -> analysisBackStackSize
        BottomNavKey.Budget -> budgetBackStackSize
        BottomNavKey.Categories -> categoriesBackStackSize
        BottomNavKey.Settings -> settingsBackStackSize
    }

    if (currentStackSize > 1) {
        when (currentBottomKey) {
            BottomNavKey.Records -> onPopRecordsBackStack()
            BottomNavKey.Analysis -> onPopAnalysisBackStack()
            BottomNavKey.Budget -> onPopBudgetBackStack()
            BottomNavKey.Categories -> onPopCategoriesBackStack()
            BottomNavKey.Settings -> onPopupSettingsBackStack()
        }
    } else if (currentBottomKey != BottomNavKey.Records) {
        onSetHomeKey()
    } else {
        // Already at home and stack size 1, let system handle exit or whatever
        // Usually we don't do anything here if we want to allow the app to close
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}