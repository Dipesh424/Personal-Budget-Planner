package com.tracker.personalbudgetplanner.ui.main.presentation

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.tracker.personalbudgetplanner.ui.analysis.presentation.AnalysisScreen
import com.tracker.personalbudgetplanner.ui.budget.presentation.BudgetScreenRoot
import com.tracker.personalbudgetplanner.ui.budget.presentation.BudgetViewModel
import com.tracker.personalbudgetplanner.ui.category.presentation.CategoriesScreenRoot
import com.tracker.personalbudgetplanner.ui.category.presentation.CategoriesViewModel
import com.tracker.personalbudgetplanner.ui.dashboard.presentation.AddOptionItem
import com.tracker.personalbudgetplanner.ui.dashboard.presentation.DashboardScreen
import com.tracker.personalbudgetplanner.ui.dashboard.presentation.RecentActivityScreen
import com.tracker.personalbudgetplanner.ui.settings.presentation.ImportStatementDialog
import com.tracker.personalbudgetplanner.ui.settings.presentation.SettingsScreen
import com.tracker.personalbudgetplanner.ui.settings.presentation.SuccessImportDialog
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val homeBackStack = rememberNavBackStack(BottomNavKey.Home)
    val analysisBackStack = rememberNavBackStack(BottomNavKey.Analysis)
    val budgetBackStack = rememberNavBackStack(BottomNavKey.Budget)
    val categoriesBackStack = rememberNavBackStack(BottomNavKey.Categories)
    val settingsBackStack = rememberNavBackStack(BottomNavKey.Settings)

    var currentKey by rememberSaveable(stateSaver = BottomNavKey.stateSaver) {
        mutableStateOf(BottomNavKey.Home)
    }
    val currentBackStack = when (currentKey) {
        BottomNavKey.Home -> homeBackStack
        BottomNavKey.Analysis -> analysisBackStack
        BottomNavKey.Budget -> budgetBackStack
        BottomNavKey.Categories -> categoriesBackStack
        BottomNavKey.Settings -> settingsBackStack
    }

    // State for FAB Options
    var showAddOptionsSheet by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }

    val addToBackStack: (NavKey) -> Unit = {
        when (currentKey) {
            BottomNavKey.Home -> homeBackStack.add(it)
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
            homeBackStackSize = homeBackStack.size,
            analysisBackStackSize = analysisBackStack.size,
            budgetBackStackSize = budgetBackStack.size,
            categoriesBackStackSize = categoriesBackStack.size,
            settingsBackStackSize = settingsBackStack.size,
            onSetHomeKey = {
                currentKey = BottomNavKey.Home
            },
            onPopHomeBackStack = {
                homeBackStack.removeLastOrNull()
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
                                    BottomNavKey.Home -> resetBackStack(homeBackStack)
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
                onClick = { showAddOptionsSheet = true },
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
                entry<BottomNavKey.Home> {
                    DashboardScreen(onSeeAllClick = {
                        addToBackStack(Routes.RecentActivity)
                    })
                }
                entry<BottomNavKey.Analysis> {
                    AnalysisScreen()
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

        if (showAddOptionsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddOptionsSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, top = 8.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Add Transaction",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                    )

                    AddOptionItem(
                        icon = Icons.Default.Edit,
                        title = "Add manually",
                        subtitle = "Enter transaction details manually",
                        onClick = {
                            showAddOptionsSheet = false
                            // TODO: Navigate to manual add screen
                        }
                    )

                    AddOptionItem(
                        icon = Icons.Default.UploadFile,
                        title = "Upload your Statement",
                        subtitle = "Import from Excel file",
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showAddOptionsSheet = false
                                    showImportDialog = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    if (showImportDialog) {
        ImportStatementDialog(
            selectedFileUri = selectedFileUri,
            onDismiss = {
                showImportDialog = false
                selectedFileUri = null
            },
            onPickFile = {
                filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            },
            onUpload = {
                if (selectedFileUri != null) {
                    showImportDialog = false
                    showSuccessDialog = true
                    selectedFileUri = null
                }
            }
        )
    }

    if (showSuccessDialog) {
        SuccessImportDialog(
            onDismiss = { showSuccessDialog = false }
        )
    }

    BackHandler(enabled = true) {
        onHandleBackPressed()
    }
}

private fun onBackPressed(
    currentBottomKey: BottomNavKey,
    homeBackStackSize: Int,
    analysisBackStackSize: Int,
    budgetBackStackSize: Int,
    categoriesBackStackSize: Int,
    settingsBackStackSize: Int,
    onSetHomeKey: () -> Unit,
    onPopHomeBackStack: () -> Unit,
    onPopAnalysisBackStack: () -> Unit,
    onPopBudgetBackStack: () -> Unit,
    onPopCategoriesBackStack: () -> Unit,
    onPopupSettingsBackStack: () -> Unit,
) {
    val currentStackSize = when (currentBottomKey) {
        BottomNavKey.Home -> homeBackStackSize
        BottomNavKey.Analysis -> analysisBackStackSize
        BottomNavKey.Budget -> budgetBackStackSize
        BottomNavKey.Categories -> categoriesBackStackSize
        BottomNavKey.Settings -> settingsBackStackSize
    }

    if (currentStackSize > 1) {
        when (currentBottomKey) {
            BottomNavKey.Home -> onPopHomeBackStack()
            BottomNavKey.Analysis -> onPopAnalysisBackStack()
            BottomNavKey.Budget -> onPopBudgetBackStack()
            BottomNavKey.Categories -> onPopCategoriesBackStack()
            BottomNavKey.Settings -> onPopupSettingsBackStack()
        }
    } else if (currentBottomKey != BottomNavKey.Home) {
        onSetHomeKey()
    } else {
        // Already at home and stack size 1
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}