package com.tracker.personalbudgetplanner.ui.dashboard.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentActivityScreen(
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recent Activity", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Filter */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search transactions...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Mock data for now
                val transactions = List(20) { index ->
                    TransactionData(
                        category = if (index % 3 == 0) "Food" else if (index % 3 == 1) "Rent" else "Salary",
                        date = "24 Feb 2026",
                        amount = if (index % 3 == 2) "+ Rs. 20,000" else "- Rs. ${100 * (index + 1)}",
                        isExpense = index % 3 != 2
                    )
                }
                
                items(transactions.filter { it.category.contains(searchQuery, ignoreCase = true) }) { item ->
                    TransactionItem(
                        category = item.category,
                        date = item.date,
                        amount = item.amount,
                        isExpense = item.isExpense
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecentActivityScreenPreview() {
    RecentActivityScreen(onBack = {})
}


data class TransactionData(
    val category: String,
    val date: String,
    val amount: String,
    val isExpense: Boolean
)
