package com.tracker.personalbudgetplanner.ui.budget_category.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tracker.personalbudgetplanner.ui.budget_category.domain.Categories
import com.tracker.personalbudgetplanner.ui.theme.PersonalBudgetPlannerTheme
import com.tracker.personalbudgetplanner.utils.constants.IconConstants
import org.koin.androidx.compose.koinViewModel

@Composable
fun CategoriesScreenRoot(
    viewModel: CategoriesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CategoriesScreen(categoryState = state, onFinish = {}) { }
}

@Composable
fun CategoriesScreen(
    categoryState: CategoryState,
    onFinish: (Map<String, Double>) -> Unit,
    onSkip: () -> Unit
) {

    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible.value = true }

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
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .statusBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Column {
                Text(
                    text = "Plan your spend",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1.5).sp
                    )
                )
                Text(
                    text = "How much do you want to limit yourself?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Categories List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                itemsIndexed(categoryState.categories) { index, item ->
                    BudgetCategoryRow(
                        category = item,
                        onAmountChange = { newAmount ->
//                            categories[index] = item.copy(amount = newAmount)
                        }
                    )
                }
            }

            // Action Footer
            Column(
                modifier = Modifier.padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = CircleShape
                ) {
                    Text(
                        "Finish Setup",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                TextButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "I'll do this later",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
fun BudgetCategoryRow(
    category: Categories,
    onAmountChange: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Category Icon
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = getIconVector(category.iconName),
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )

                // The Input Field
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Rs.",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    BasicTextField(
                        value = category.budgetLimit.toString(),
                        onValueChange = { if (it.all { c -> c.isDigit() }) onAmountChange(it) },
                        modifier = Modifier
                            .width(IntrinsicSize.Min)
                            .widthIn(min = 40.dp),
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.Bold
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        decorationBox = { inner ->
                            if (category.budgetLimit.toString() == "0") Text(
                                "0",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.LightGray,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                            inner()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Way 2: Quick Budget Selectors (Presets)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("500", "1000", "5000").forEach { preset ->
                    AssistChip(
                        onClick = { onAmountChange(preset) },
                        label = { Text("Rs. $preset") },
                        shape = CircleShape,
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surface)
                    )
                }
            }
        }
    }
}

private fun getIconVector(iconName: String): ImageVector {
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
            onFinish = {},
            onSkip = {})
    }
}