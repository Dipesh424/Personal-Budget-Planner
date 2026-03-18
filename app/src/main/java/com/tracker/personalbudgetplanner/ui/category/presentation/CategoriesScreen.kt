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
import com.tracker.personalbudgetplanner.utils.constants.DbConstants
import com.tracker.personalbudgetplanner.utils.constants.IconConstants
import org.koin.androidx.compose.koinViewModel

@Composable
fun CategoriesScreenRoot(
    viewModel: CategoriesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Categories?>(null) }

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
    if (showSheet) {
        AddCategorySheetRoot(viewModel, onDismiss = {
            showSheet = false
        }, onSave = { name, iconId, isExpense ->
            viewModel.addNewCategory(
                Categories(
                    name = name,
                    iconId = iconId,
                    type = if (isExpense) DbConstants.category_expense else DbConstants.category_expense
                )
            )
            showSheet = false
        })
    }
    CategoriesScreen(
        categoryState = state,
        onAddCategory = {
            showSheet = true
        },
        onEditCategory = {},
        onDeleteCategory = { category ->
            showDeleteDialog = category
        })
}

@Composable
fun CategoriesScreen(
    categoryState: CategoryState,
    onAddCategory: () -> Unit,
    onEditCategory: (Categories) -> Unit,
    onDeleteCategory: (Categories) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Aesthetic Background Glow
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
                    text = "Categories",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1.5).sp
                    )
                )
                Text(
                    text = "Manage how you organize your money",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp) // Space for floating buttons
            ) {
                items(categoryState.categories) { item ->
                    CategoryManagementRow(
                        category = item,
                        onEdit = { onEditCategory(item) },
                        onDelete = { onDeleteCategory(item) }
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { onAddCategory() },
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

//@Composable
//fun BudgetCategoryRow(
//    category: Categories,
//    onAmountChange: (String) -> Unit
//) {
//    Surface(
//        shape = RoundedCornerShape(24.dp),
//        color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
//        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                // Category Icon
//                Surface(
//                    shape = CircleShape,
//                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
//                    modifier = Modifier.size(44.dp)
//                ) {
//                    Icon(
//                        imageVector = getIconVector(category.iconName),
//                        contentDescription = null,
//                        modifier = Modifier.padding(10.dp),
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(16.dp))
//
//                Text(
//                    text = category.name,
//                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
//                    modifier = Modifier.weight(1f)
//                )
//
//                // The Input Field
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Text(
//                        "Rs.",
//                        color = MaterialTheme.colorScheme.primary,
//                        fontWeight = FontWeight.Bold
//                    )
//                    BasicTextField(
//                        value = category.budgetLimit.toString(),
//                        onValueChange = { if (it.all { c -> c.isDigit() }) onAmountChange(it) },
//                        modifier = Modifier
//                            .width(IntrinsicSize.Min)
//                            .widthIn(min = 40.dp),
//                        textStyle = MaterialTheme.typography.titleLarge.copy(
//                            textAlign = TextAlign.End,
//                            fontWeight = FontWeight.Bold
//                        ),
//                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                        decorationBox = { inner ->
//                            if (category.budgetLimit.toString() == "0") Text(
//                                "0",
//                                style = MaterialTheme.typography.titleLarge,
//                                color = Color.LightGray,
//                                modifier = Modifier.fillMaxWidth(),
//                                textAlign = TextAlign.End
//                            )
//                            inner()
//                        }
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Way 2: Quick Budget Selectors (Presets)
//            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                listOf("500", "1000", "5000").forEach { preset ->
//                    AssistChip(
//                        onClick = { onAmountChange(preset) },
//                        label = { Text("Rs. $preset") },
//                        shape = CircleShape,
//                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surface)
//                    )
//                }
//            }
//        }
//    }
//}

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
            onAddCategory = {},
            onEditCategory = {},
            onDeleteCategory = {})
    }
}