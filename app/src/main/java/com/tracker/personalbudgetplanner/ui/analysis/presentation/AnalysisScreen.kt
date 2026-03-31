package com.tracker.personalbudgetplanner.ui.analysis.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen() {
    var selectedTab by remember { mutableIntStateOf(2) }
    val tabs = listOf("Day", "Week", "Month", "Year")

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
            // Time Filter Tabs
            item {
                SecondaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    divider = {},
                    indicator = {
                        // Fixed the indicator logic for modern Material 3 SecondaryTabRow
                        // Use Modifier.tabIndicatorOffset directly within TabIndicatorScope
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(selectedTab),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
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
                            // Decorative Canvas
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.15f),
                                    radius = size.minDimension * 0.6f,
                                    center = Offset(size.width * 0.95f, size.height * 0.15f)
                                )
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.1f),
                                    radius = size.minDimension * 0.4f,
                                    center = Offset(size.width * 0.05f, size.height * 0.85f)
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
                                        "Rs. 24,500.00",
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
                                            "Monthly Savings Goal",
                                            color = Color.White.copy(alpha = 0.9f),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "49%",
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = { 0.49f },
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

            // AI Smart Insights
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        "Smart Insights",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp
                        ),
                        modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        PremiumInsightCard(
                            icon = Icons.Rounded.AutoAwesome,
                            title = "High Potential Saving",
                            description = "Based on your spending, switching your internet plan could save you Rs. 450 monthly.",
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            accentColor = MaterialTheme.colorScheme.primary
                        )
                        PremiumInsightCard(
                            icon = Icons.Rounded.WarningAmber,
                            title = "Budget Overrun Alert",
                            description = "Your 'Entertainment' category is 15% above your set limit for this month.",
                            containerColor = Color(0xFFFFF3E0),
                            accentColor = Color(0xFFE65100)
                        )
                        PremiumInsightCard(
                            icon = Icons.Rounded.Insights,
                            title = "Spending Habit",
                            description = "Most of your expenses occur on Friday nights. Consider a weekly budget review.",
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f),
                            accentColor = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            // Spending Breakdown (Donut Chart)
            item {
                AnalysisCard(title = "Spending Breakdown") {
                    ModernDonutChart(
                        categories = listOf(
                            CategorySpend("Food", 8500f, Color(0xFF6366F1)),
                            CategorySpend("Transport", 4200f, Color(0xFF10B981)),
                            CategorySpend("Shopping", 12000f, Color(0xFFF43F5E)),
                            CategorySpend("Rent", 15000f, Color(0xFFF59E0B))
                        )
                    )
                }
            }

            // Monthly Trend (Bar Chart)
            item {
                AnalysisCard(title = "Monthly Cash Flow") {
                    ModernBarChart(
                        data = listOf(0.4f, 0.6f, 0.3f, 0.8f, 0.5f, 0.9f, 0.7f),
                        labels = listOf("Sep", "Oct", "Nov", "Dec", "Jan", "Feb", "Mar")
                    )
                }
            }

            // Category Performance
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
                        items(listOf(
                            CategoryItemData("Groceries", "Rs. 4.5k", Icons.Default.ShoppingCart, Color(0xFF4CAF50)),
                            CategoryItemData("Electronics", "Rs. 12k", Icons.Default.Bolt, Color(0xFFFFC107)),
                            CategoryItemData("Travel", "Rs. 2.1k", Icons.Default.Flight, Color(0xFF2196F3)),
                            CategoryItemData("Health", "Rs. 1.5k", Icons.Default.Favorite, Color(0xFFE91E63))
                        )) { category ->
                            ModernCategoryCard(category)
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
fun PremiumInsightCard(
    icon: ImageVector,
    title: String,
    description: String,
    containerColor: Color,
    accentColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
                    val sweepAngle = (category.amount / total) * 360f
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
                Text("Total", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "Rs. ${total.toInt() / 1000}k",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                )
            }
        }
        
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            categories.forEach { category ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(category.color))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ModernBarChart(data: List<Float>, labels: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { index, value ->
            val heightState by animateFloatAsState(
                targetValue = value,
                animationSpec = tween(durationMillis = 1000, delayMillis = index * 100),
                label = "barHeight"
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(16.dp)
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
                    text = labels[index],
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ModernCategoryCard(data: CategoryItemData) {
    Surface(
        modifier = Modifier
            .width(140.dp)
            .height(150.dp),
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
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(data.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(data.icon, contentDescription = null, tint = data.color, modifier = Modifier.size(22.dp))
            }
            Column {
                Text(data.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold))
                Text(data.amount, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

data class CategorySpend(val name: String, val amount: Float, val color: Color)
data class CategoryItemData(val name: String, val amount: String, val icon: ImageVector, val color: Color)

@Preview(showBackground = true)
@Composable
fun AnalysisScreenPreview() {
    AnalysisScreen()
}
