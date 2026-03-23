package com.tracker.personalbudgetplanner.ui.category.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tracker.personalbudgetplanner.R
import com.tracker.personalbudgetplanner.ui.category.domain.Categories
import com.tracker.personalbudgetplanner.ui.theme.PersonalBudgetPlannerTheme
import com.tracker.personalbudgetplanner.utils.constants.DbConstants
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddCategorySheetRoot(
    viewModel: CategoriesViewModel = koinViewModel(),
    editingCategory: Categories? = null,
    onDismiss: () -> Unit,
    onSave: (category: Categories) -> Unit,

    ) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AddCategorySheet(
        categoryState = state,
        existingCategory = editingCategory,
        onDismiss = onDismiss,
        onSave = onSave
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategorySheet(
    categoryState: CategoryState,
    existingCategory: Categories? = null,
    onDismiss: () -> Unit,
    onSave: (category: Categories) -> Unit
) {
    var categoryName by remember { mutableStateOf(existingCategory?.name ?: "") }
    var isExpense by remember {
        mutableStateOf(existingCategory?.type?.let { it == DbConstants.category_expense } ?: true)
    }
    var selectedIconId by remember { mutableIntStateOf(existingCategory?.iconId ?: 0) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
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
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.outlineVariant) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Text(
                text = stringResource(R.string.new_category),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
            )

            // 1. Type Selector (Income vs Expense)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                        CircleShape
                    )
                    .padding(4.dp)
            ) {
                listOf(
                    true to stringResource(R.string.expense),
                    false to stringResource(R.string.income)
                ).forEach { (type, label) ->
                    val isSelected = isExpense == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { isExpense = type },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 2. Name TextField
            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Category Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // 3. Icon Picker Grid
            Column {
                Text(
                    text = stringResource(R.string.select_icon),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyVerticalGrid(
                    state = scrollState,
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.height(200.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categoryState.icons) { icon ->
                        val isSelected = selectedIconId == icon.id
                        Surface(
                            onClick = { selectedIconId = icon.id },
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceColorAtElevation(
                                2.dp
                            ),
                            border = if (isSelected) BorderStroke(
                                2.dp,
                                MaterialTheme.colorScheme.primary
                            ) else null,
                            modifier = Modifier.size(50.dp)
                        ) {
                            Icon(
                                imageVector = getIconVector(icon.iconName),
                                contentDescription = null,
                                modifier = Modifier.padding(12.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 4. Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {animateAndDismiss()},
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Button(
                    onClick = {
                        if (categoryName.isNotBlank()) onSave(
                            Categories(
                                id = existingCategory?.id,
                                name = categoryName,
                                iconId = selectedIconId,
                                type = if (isExpense) DbConstants.category_expense else DbConstants.category_income
                            )
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = categoryName.isNotBlank()
                ) {
                    Text(stringResource(R.string.save_category), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable()
fun AddCategorySheetPreview() {
    PersonalBudgetPlannerTheme {
        AddCategorySheet(CategoryState(), onDismiss = {}, onSave = { _ -> })
    }
}
