package com.tracker.personalbudgetplanner.ui.analysis.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracker.personalbudgetplanner.ui.dashboard.presentation.MonthSelectorModern
import org.koin.compose.viewmodel.koinViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AnalysisScreenRoot(
    viewModel: AnalysisViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    AnalysisScreen(
        state = state,
        onFilterChange = viewModel::onFilterChange,
        onDateChange = viewModel::onDateChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    state: AnalysisState,
    onFilterChange: (String) -> Unit,
    onDateChange: (LocalDate) -> Unit
) {
    val tabs = listOf("Day", "Week", "Month", "Year")
    val selectedTabIndex = tabs.indexOf(state.selectedFilter).coerceAtLeast(0)
    val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
    val yearFormatter = DateTimeFormatter.ofPattern("yyyy", Locale.getDefault())
    val dayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())

    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.selectedDate.atStartOfDay()
                .toEpochSecond(ZoneOffset.UTC) * 1000
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val newDate = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateChange(newDate)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Financial Insights",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Date Selector
            item {
                val label = when(state.selectedFilter) {
                    "Year" -> state.selectedDate.format(yearFormatter)
                    "Day" -> state.selectedDate.format(dayFormatter)
                    else -> state.selectedDate.format(monthYearFormatter)
                }
                
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    MonthSelectorModern(
                        currentMonthLabel = label,
                        onPrevious = { 
                            val newDate = when(state.selectedFilter) {
                                "Year" -> state.selectedDate.minusYears(1)
                                "Day" -> state.selectedDate.minusDays(1)
                                "Week" -> state.selectedDate.minusWeeks(1)
                                else -> state.selectedDate.minusMonths(1)
                            }
                            onDateChange(newDate)
                        },
                        onNext = { 
                            val newDate = when(state.selectedFilter) {
                                "Year" -> state.selectedDate.plusYears(1)
                                "Day" -> state.selectedDate.plusDays(1)
                                "Week" -> state.selectedDate.plusWeeks(1)
                                else -> state.selectedDate.plusMonths(1)
                            }
                            onDateChange(newDate)
                        },
                        onDateClick = { showDatePicker = true }
                    )
                }
            }

            // Time Filter Tabs
            item {
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
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { onFilterChange(title) },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                            }
                        )
                    }
                }
            }

            // Summary Balance Card (Total Savings)
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .shadow(24.dp, RoundedCornerShape(32.dp)),
                        shape = RoundedCornerShape(32.dp),
                        color = Color.Transparent
                    ) {
                        val primary = MaterialTheme.colorScheme.primary
                        val tertiary = MaterialTheme.colorScheme.tertiary
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(primary, tertiary),
                                        start = Offset(0f, 0f),
                                        end = Offset(1000f, 1000f)
                                    )
                                )
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.15f),
                                    radius = size.minDimension * 0.6f,
                                    center = Offset(size.width * 0.95f, size.height * 0.15f)
                                )
                            }

                            Column(
                                modifier = Modifier.padding(28.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Available Savings",
                                            color = Color.White.copy(alpha = 0.8f),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Icon(
                                            imageVector = Icons.Rounded.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.6f),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    Text(
                                        "Rs. ${state.totalSavings}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.displayMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = (-1).sp
                                        )
                                    )
                                }
                                
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Text(
                                            "Savings Goal Progress",
                                            color = Color.White.copy(alpha = 0.9f),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "${(state.savingsGoalProgress * 100).toInt()}%",
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = { state.savingsGoalProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(10.dp)
                                            .clip(CircleShape),
                                        color = Color.White,
                                        trackColor = Color.White.copy(alpha = 0.25f),
                                        strokeCap = StrokeCap.Round
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Spending Breakdown (Donut Chart)
            if (state.spendingBreakdown.isNotEmpty()) {
                item {
                    AnalysisCard(title = "Spending Breakdown") {
                        ModernDonutChart(categories = state.spendingBreakdown)
                    }
                }
            }

            // Trend (Bar Chart)
            if (state.cashFlowData.isNotEmpty()) {
                item {
                    AnalysisCard(title = "Expense Trend") {
                        ModernBarChart(data = state.cashFlowData)
                    }
                }
            }

            // Category Performance
            if (state.categoryPerformance.isNotEmpty()) {
                item {
                    Column {
                        Text(
                            "Category Performance",
                            modifier = Modifier.padding(horizontal = 24.dp),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.categoryPerformance) { category ->
                                ModernCategoryCard(category)
                            }
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun AnalysisCard(
    title: String,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = containerColor,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
fun ModernDonutChart(categories: List<CategorySpend>) {
    val total = categories.sumOf { it.amount.toDouble() }.toFloat()
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                categories.forEach { category ->
                    val sweepAngle = if (total > 0) (category.amount / total) * 360f else 0f
                    drawArc(
                        color = category.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 48f, cap = StrokeCap.Round)
                    )
                    startAngle += sweepAngle
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total Spent", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "Rs. ${total.toInt()}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                )
            }
        }
        
        Column(modifier = Modifier.weight(1f).padding(start = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { category ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(category.color))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                        Text(
                            text = "Rs. ${category.amount.toInt()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernBarChart(data: List<CashFlowUiModel>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { index, item ->
            val heightState by animateFloatAsState(
                targetValue = item.value,
                animationSpec = tween(durationMillis = 1000, delayMillis = index * 100),
                label = "barHeight"
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text(
                    text = item.amount,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .fillMaxHeight(heightState)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                )
                            )
                        )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ModernCategoryCard(data: CategoryPerformanceUiModel) {
    Surface(
        modifier = Modifier
            .width(140.dp)
            .height(120.dp),
        shape = RoundedCornerShape(28.dp),
        color = data.color.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, data.color.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(data.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(data.name.take(1), color = data.color, fontWeight = FontWeight.Bold)
            }
            Column {
                Text(data.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold), maxLines = 1)
                Text(data.amount, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

data class CategorySpend(val name: String, val amount: Float, val color: Color)
