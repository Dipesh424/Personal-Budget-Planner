package com.tracker.personalbudgetplanner.ui.budget.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tracker.personalbudgetplanner.R
import com.tracker.personalbudgetplanner.core.presentation.AppAlertDialog
import com.tracker.personalbudgetplanner.ui.budget.domain.Budget
import com.tracker.personalbudgetplanner.ui.budget.domain.BudgetCategories
import com.tracker.personalbudgetplanner.ui.category.domain.Categories
import com.tracker.personalbudgetplanner.ui.category.presentation.getIconVector
import com.tracker.personalbudgetplanner.ui.theme.PersonalBudgetPlannerTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun BudgetScreenRoot(viewModel: BudgetViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.currentDate) {
        viewModel.getBudgetedAndUnBudgetedCategories(
            month = state.currentDate.monthValue, year = state.currentDate.year
        )
    }

    state.budgetToDelete?.let { budget ->
        AppAlertDialog(
            onDismissRequest = { viewModel.onAction(BudgetAction.OnDismissDialogs) },
            onConfirm = { viewModel.onAction(BudgetAction.OnDeleteConfirm) },
            title = stringResource(R.string.remove_budget),
            description = stringResource(R.string.budget_remove_confirmation, budget.category.name),
            confirmText = stringResource(R.string.remove),
            isDestructive = true,
            icon = Icons.Default.DeleteForever
        )
    }

    if (state.categoryForAddBudget != null || state.categoryToEditForBudget != null) {
        SetBudgetBottomSheet(
            category = state.categoryForAddBudget,
            budgetCategories = state.categoryToEditForBudget,
            currentDate = state.currentDate,
            onDismiss = { viewModel.onAction(BudgetAction.OnDismissDialogs) },
            onSave = { budget -> viewModel.onAction(BudgetAction.OnSaveBudget(budget)) }
        )
    }

    BudgetScreen(
        state = state, onAction = {
            viewModel.onAction(it)
        }
    )
}

@Composable
fun BudgetScreen(
    state: BudgetState,
    onAction: (BudgetAction) -> Unit
) {
    val totalBudget = state.budgetedCategories.sumOf { it.budgetAmount }
    val totalSpent = state.budgetedCategories.sumOf { it.spentAmount }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .offset(x = 150.dp, y = (-100).dp)
                .size(400.dp)
                .background(
                    Brush.radialGradient(
                        listOf(MaterialTheme.colorScheme.primary.copy(0.05f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            MonthSelector(
                currentDate = state.currentDate,
                onPreviousMonth = { onAction(BudgetAction.OnPreviousMonth) },
                onNextMonth = { onAction(BudgetAction.OnNextMonth) }
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                BudgetSummaryCard(
                    totalBudget = totalBudget, totalSpent = totalSpent
                )

                BudgetList(
                    budgetedCategories = state.budgetedCategories,
                    unbudgetedCategories = state.unbudgetedCategories,
                    onSetBudget = { onAction(BudgetAction.OnSetBudgetClick(it)) },
                    onEditBudget = { onAction(BudgetAction.OnEditBudgetClick(it)) },
                    onDeleteClick = { onAction(BudgetAction.OnDeleteClick(it)) }
                )
            }
        }
    }
}

@Composable
fun MonthSelector(
    currentDate: LocalDate, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous")
        }

        Text(
            text = "${
                currentDate.month.getDisplayName(
                    TextStyle.FULL, Locale.getDefault()
                )
            } ${currentDate.year}",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Next")
        }
    }
}

@Composable
fun BudgetSummaryCard(totalBudget: Double, totalSpent: Double) {
    val progress = if (totalBudget > 0) (totalSpent / totalBudget).toFloat() else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Total Budget", style = MaterialTheme.typography.labelLarge)
            Text(
                "Rs.${totalBudget}",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = if (progress > 1f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Spent: Rs.${totalSpent}", style = MaterialTheme.typography.bodyMedium)
                Text("${(progress * 100).toInt()}%", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BudgetList(
    budgetedCategories: List<BudgetCategories>,
    unbudgetedCategories: List<Categories>,
    onSetBudget: (Categories) -> Unit,
    onEditBudget: (BudgetCategories) -> Unit,
    onDeleteClick: (BudgetCategories) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { SectionHeader(stringResource(R.string.active_budgets)) }

        if (budgetedCategories.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.currently_no_budget_set_for_this_month),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        } else {
            items(budgetedCategories) { item ->
                BudgetRow(item, onEditClick = onEditBudget, onDeleteClick = onDeleteClick)
            }
        }

        // Section 2: Unbudgeted Categories
        if (unbudgetedCategories.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.add_to_budget)) }

            items(unbudgetedCategories) { category ->
                UnbudgetedRow(category, onSetBudget)
            }
        }
    }
}

@Composable
fun BudgetRow(
    item: BudgetCategories,
    onEditClick: (BudgetCategories) -> Unit,
    onDeleteClick: (BudgetCategories) -> Unit
) {
    val progress = (item.spentAmount / item.budgetAmount).toFloat()
    val isOverspent = item.spentAmount > item.budgetAmount
    val remainingAmount = (item.budgetAmount - item.spentAmount).coerceAtLeast(0.0)

    // State to control the visibility of the dropdown menu
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Icon, Name, and Menu
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isOverspent) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = getIconVector(item.category.iconName),
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp),
                        tint = if (isOverspent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = item.category.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )

                // The 3-Dots Menu Implementation
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(text = { Text("Update Limit") }, leadingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }, onClick = {
                            showMenu = false
                            onEditClick(item)
                        })
                        DropdownMenuItem(text = {
                            Text(
                                "Remove Budget", color = MaterialTheme.colorScheme.error
                            )
                        }, leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }, onClick = {
                            showMenu = false
                            onDeleteClick(item)
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Three-Column Stats Section
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BudgetStatColumn(
                    label = "Limit",
                    value = "Rs.${item.budgetAmount.toInt()}",
                    color = MaterialTheme.colorScheme.onSurface
                )
                BudgetStatColumn(
                    label = "Spent",
                    value = "Rs.${item.spentAmount.toInt()}",
                    color = if (isOverspent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                BudgetStatColumn(
                    label = if (isOverspent) "Over" else "Remaining",
                    value = "Rs.${if (isOverspent) (item.spentAmount - item.budgetAmount).toInt() else remainingAmount.toInt()}",
                    color = if (isOverspent) MaterialTheme.colorScheme.error else Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = if (isOverspent) listOf(
                                    MaterialTheme.colorScheme.error, Color(0xFFFF8A80)
                                )
                                else listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                )
                            )
                        )
                )
            }
        }
    }
}


@Composable
fun BudgetStatColumn(label: String, value: String, color: Color) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            color = color
        )
    }
}

@Composable
fun UnbudgetedRow(category: Categories, onSetBudget: (Categories) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(getIconVector(category.iconName), null, modifier = Modifier.padding(10.dp))
        }

        Text(
            category.name, modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        )

        OutlinedButton(
            onClick = {
                onSetBudget(category)
            }, shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                stringResource(R.string.set_budget), style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = 28.dp, bottom = 12.dp, start = 16.dp, end = 16.dp
            ), // Generous top padding for clear separation
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title.uppercase(), style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp, // Professional "spaced-out" look
                fontSize = 12.sp
            ), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // A very subtle decorative line that fades into the background
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetBudgetBottomSheet(
    category: Categories?,
    budgetCategories: BudgetCategories?,
    currentDate: LocalDate,
    onDismiss: () -> Unit,
    onSave: (Budget) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var amountText by remember { mutableStateOf(budgetCategories?.budgetAmount?.toString() ?: "") }
    val monthName = currentDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val animateAndDismiss = {
        scope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            onDismiss()
        }
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.set_budget),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Category Info Section
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getIconVector(
                            category?.iconName ?: budgetCategories?.category?.iconName
                        ),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = category?.name ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "$monthName, ${currentDate.year}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Numeric TextField
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    if (it.all { char -> char.isDigit() || char == '.' }) amountText = it
                },
                label = { Text(stringResource(R.string.limit)) },
                placeholder = { Text(stringResource(R.string._0_00)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { animateAndDismiss() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.cancel))
                }

                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        if (amount > 0) onSave(
                            Budget(
                                id = budgetCategories?.budgetId,
                                categoryId = category?.id ?: budgetCategories?.category?.id ?: -1,
                                amount = amount,
                                month = currentDate.monthValue,
                                year = currentDate.year
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = amountText.isNotEmpty()
                ) {
                    Text(text = stringResource(R.string.set_budget))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetScreenPreview() {
    PersonalBudgetPlannerTheme() {
        BudgetScreen(
            state = BudgetState(),
            onAction = {})
    }
}

