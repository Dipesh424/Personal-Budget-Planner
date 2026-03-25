package com.tracker.personalbudgetplanner.ui.category.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tracker.personalbudgetplanner.R
import com.tracker.personalbudgetplanner.core.presentation.AppAlertDialog
import com.tracker.personalbudgetplanner.ui.category.domain.Categories
import com.tracker.personalbudgetplanner.ui.theme.PersonalBudgetPlannerTheme
import com.tracker.personalbudgetplanner.utils.constants.IconConstants
import org.koin.androidx.compose.koinViewModel

@Composable
fun CategoriesScreenRoot(
    viewModel: CategoriesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    state.categoryToDelete?.let { category ->
        AppAlertDialog(
            onDismissRequest = { viewModel.onAction(CategoryAction.OnDismissDialogs) },
            onConfirm = { viewModel.onAction(CategoryAction.OnDeleteConfirm) },
            title = stringResource(R.string.delete_category),
            description = stringResource(R.string.confirm_category_delete, category.name),
            confirmText = stringResource(R.string.delete),
            isDestructive = true,
            icon = Icons.Default.DeleteForever
        )
    }

    if (state.isAddingNew || state.categoryToEdit != null) {
        AddCategorySheetRoot(
            viewModel = viewModel,
            editingCategory = state.categoryToEdit,
            onDismiss = { viewModel.onAction(CategoryAction.OnDismissDialogs) },
            onSave = { category -> viewModel.onAction(CategoryAction.OnSaveCategory(category)) }
        )
    }

    CategoriesScreen(
        categoryState = state,
        onAction = {
            viewModel.onAction(it)
        })
}

@Composable
fun CategoriesScreen(
    categoryState: CategoryState,
    onAction: (CategoryAction) -> Unit
) {
    val scrollState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .offset(x = (-80).dp, y = (-80).dp)
                .size(300.dp)
                .background(
                    Brush.radialGradient(
                        listOf(MaterialTheme.colorScheme.primary.copy(0.08f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .statusBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header - High Contrast Minimalist
            Column {
                Text(
                    text = stringResource(R.string.categories),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1.5).sp
                    )
                )
                Text(
                    text = stringResource(R.string.manage_how_you_organize_your_money),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (categoryState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                }
            } else {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp) // Space for floating buttons
                ) {
                    items(categoryState.categories) { item ->
                        CategoryManagementRow(
                            category = item,
                            onEdit = { onAction(CategoryAction.OnEditCategoryClick(item)) },
                            onDelete = { onAction(CategoryAction.OnDeleteCategoryClick(item)) }
                        )
                    }

                    item {
                        OutlinedButton(
                            onClick = { onAction(CategoryAction.OnAddCategoryClick) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                stringResource(R.string.add_new_category),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryManagementRow(
    category: Categories,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = getIconVector(category.iconName), // Using your icon mapper
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Name
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f)
            )

            // 3 Dots Menu
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            showMenu = false
                            onEdit()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

fun getIconVector(iconName: String?): ImageVector {
    return when (iconName) {
        IconConstants.grocery -> Icons.Default.LocalGroceryStore
        IconConstants.home -> Icons.Default.Home
        IconConstants.utilities -> Icons.Default.Lightbulb
        IconConstants.transport -> Icons.Default.DirectionsCar
        IconConstants.baby -> Icons.Default.ChildCare
        IconConstants.clothing -> Icons.Default.Checkroom
        IconConstants.health -> Icons.Default.MedicalServices
        IconConstants.personal -> Icons.Default.SelfImprovement
        IconConstants.dinner -> Icons.Default.LocalCafe
        IconConstants.shopping -> Icons.Default.ShoppingBag
        IconConstants.entertainment -> Icons.Default.ConfirmationNumber
        IconConstants.investments -> Icons.AutoMirrored.Filled.TrendingUp
        IconConstants.education -> Icons.Default.School
        IconConstants.insurance -> Icons.Default.Shield
        IconConstants.gifts -> Icons.Default.Favorite
        IconConstants.misc -> Icons.Default.MoreHoriz
        else -> Icons.Default.MoreHoriz
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    PersonalBudgetPlannerTheme {
        CategoriesScreen(
            categoryState = CategoryState(),
            onAction = {})
    }
}