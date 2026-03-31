package com.tracker.personalbudgetplanner.ui.dashboard.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracker.personalbudgetplanner.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onSeeAllClick: () -> Unit = {}
) {
    // State for Date Navigation
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())

    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay()
                .toEpochSecond(ZoneOffset.UTC) * 1000
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
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
                        stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Month Selector
            item {
                MonthSelectorModern(
                    currentMonthLabel = selectedDate.format(monthYearFormatter),
                    onPrevious = { selectedDate = selectedDate.minusMonths(1) },
                    onNext = { selectedDate = selectedDate.plusMonths(1) },
                    onDateClick = { showDatePicker = true }
                )
            }

            // Main Balance Card
            item {
                ModernBalanceCard(
                    totalBalance = "Rs. 25,000",
                    income = "50,000",
                    expense = "25,000"
                )
            }

            // Recent Transactions Header
            item {
                SectionHeader(
                    title = "Recent Activity",
                    onActionClick = onSeeAllClick,
                    actionLabel = "See All"
                )
            }

            // Transactions
            items(6) { index ->
                TransactionItem(
                    category = if (index % 2 == 0) "Grocery" else "Salary",
                    date = "24 Feb 2026",
                    amount = if (index % 2 == 0) "- Rs. 500" else "+ Rs. 15,000",
                    isExpense = index % 2 == 0
                )
            }
        }
    }
}

@Composable
fun AddOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun MonthSelectorModern(
    currentMonthLabel: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onDateClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPrevious) {
                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                    contentDescription = "Back",
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = currentMonthLabel,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onDateClick() }
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
            IconButton(onClick = onNext) {
                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = "Next",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun ModernBalanceCard(totalBalance: String, income: String, expense: String) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            primaryColor,
                            primaryColor.copy(alpha = 0.85f),
                            primaryColor.copy(alpha = 0.7f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
        ) {
            // Decorative Background Circles
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = onPrimaryColor.copy(alpha = 0.05f),
                    radius = size.minDimension * 0.4f,
                    center = Offset(size.width * 0.9f, size.height * 0.1f)
                )
                drawCircle(
                    color = onPrimaryColor.copy(alpha = 0.08f),
                    radius = size.minDimension * 0.6f,
                    center = Offset(size.width * 0.1f, size.height * 0.9f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Total Balance",
                            style = MaterialTheme.typography.labelLarge,
                            color = onPrimaryColor.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            imageVector = Icons.Rounded.AccountBalanceWallet,
                            contentDescription = null,
                            tint = onPrimaryColor.copy(alpha = 0.4f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        totalBalance,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 36.sp,
                            letterSpacing = (-1).sp
                        ),
                        color = onPrimaryColor
                    )
                }

                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 0.5.dp,
                        color = onPrimaryColor.copy(alpha = 0.2f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BalanceSummaryItem(
                            label = "Income",
                            amount = "Rs. $income",
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            color = onPrimaryColor
                        )
                        BalanceSummaryItem(
                            label = "Expenses",
                            amount = "Rs. $expense",
                            icon = Icons.AutoMirrored.Filled.TrendingDown,
                            color = onPrimaryColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BalanceSummaryItem(
    label: String,
    amount: String,
    icon: ImageVector,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.7f)
            )
            Text(
                text = amount,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = color
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    onActionClick: () -> Unit,
    actionLabel: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        TextButton(onClick = onActionClick) {
            Text(actionLabel, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun TransactionItem(category: String, date: String, amount: String, isExpense: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.take(1),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    category,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    date,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = amount,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = if (isExpense) Color(0xFFE53935) else Color(0xFF43A047)
            )
        }
    }
}