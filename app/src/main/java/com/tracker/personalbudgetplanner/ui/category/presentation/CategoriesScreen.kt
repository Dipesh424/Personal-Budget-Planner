package com.tracker.personalbudgetplanner.ui.category.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tracker.personalbudgetplanner.R
import com.tracker.personalbudgetplanner.core.presentation.AppAlertDialog
import com.tracker.personalbudgetplanner.ui.category.domain.Categories
import com.tracker.personalbudgetplanner.ui.theme.PersonalBudgetPlannerTheme
import com.tracker.personalbudgetplanner.utils.constants.DbConstants
import com.tracker.personalbudgetplanner.utils.constants.IconConstants
import org.koin.androidx.compose.koinViewModel

@Composable
fun CategoriesScreenRoot(
    viewModel: CategoriesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var isAddingNew by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Categories?>(null) }
    var categoryToEdit by remember { mutableStateOf<Categories?>(null) }

    showDeleteDialog?.let { category ->
        AppAlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            onConfirm = {
                viewModel.deleteCategory(category)
                showDeleteDialog = null
            },
            title = stringResource(R.string.delete_category),
            description = stringResource(
                R.string.confirm_category_delete,
                category.name
            ),
            confirmText = stringResource(R.string.delete),
            isDestructive = true,
            icon = Icons.Default.DeleteForever
        )
    }
    if (isAddingNew || categoryToEdit != null) {
        AddCategorySheetRoot(viewModel, editingCategory = categoryToEdit, onDismiss = {
            isAddingNew = false
            categoryToEdit = null
        }, onSave = { category ->
            viewModel.upsertCategory(category)
            isAddingNew = false
            categoryToEdit = null
        })
    }
    CategoriesScreen(
        categoryState = state,
        onAddCategory = {
            isAddingNew = true
        },
        onEditCategory = { categoryToEdit = it },
        onDeleteCategory = { category ->
            showDeleteDialog = category
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    categoryState: CategoryState,
    onAddCategory: () -> Unit,
    onEditCategory: (Categories) -> Unit,
    onDeleteCategory: (Categories) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(DbConstants.category_expense, DbConstants.category_income)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFF6366F1).copy(alpha = 0.05f),
                radius = size.minDimension * 0.4f,
                center = Offset(size.width * 0.9f, size.height * 0.1f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp
                        )
                    )
                    Text(
                        text = "Manage your labels",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                Surface(
                    onClick = onAddCategory,
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 8.dp
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Create",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                SecondaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    divider = {},
                    indicator = {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(selectedTabIndex),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, type ->
                        val isSelected = selectedTabIndex == index
                        Tab(
                            selected = isSelected,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = type.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary 
                                                else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (categoryState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val filteredCategories = categoryState.categories
                    .filter { it.type == tabs[selectedTabIndex] }
                    .sortedByDescending { it.name == "Salary" }

                AnimatedContent(
                    targetState = filteredCategories,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    },
                    label = "categoryContent"
                ) { list ->
                    if (list.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "No categories found",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(list, key = { it.id ?: 0 }) { category ->
                                PremiumCategoryRow(
                                    category = category,
                                    onEdit = { onEditCategory(category) },
                                    onDelete = { onDeleteCategory(category) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(20.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumCategoryRow(
    category: Categories,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val isIncome = category.type == DbConstants.category_income
    val accentColor = if (isIncome) Color(0xFF10B981) else Color(0xFFF43F5E)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconVector(category.iconName),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.2.sp
                    )
                )
                Text(
                    text = category.type.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }

            IconButton(
                onClick = { showMenu = true },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.MoreHoriz,
                    contentDescription = "Options",
                    modifier = Modifier.size(20.dp)
                )
                
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
                        leadingIcon = { Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp)) }
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
                                null, 
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.error
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
        IconConstants.salary -> Icons.Default.Payments
        IconConstants.freelance -> Icons.Default.Work
        else -> Icons.Default.MoreHoriz
    }
}

@Preview(showBackground = true)
@Composable
fun CategoriesScreenPreview() {
    PersonalBudgetPlannerTheme {
        CategoriesScreen(
            categoryState = CategoryState(),
            onAddCategory = {},
            onEditCategory = {},
            onDeleteCategory = {})
    }
}