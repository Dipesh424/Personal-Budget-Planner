package com.tracker.personalbudgetplanner.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface BottomNavKey : NavKey {
    val icon: ImageVector
    val label: String

    @Serializable
    data object Home : BottomNavKey {
        override val icon: ImageVector = Icons.Default.Home
        override val label: String = "Home"
    }

    @Serializable
    data object Analysis : BottomNavKey {
        override val icon: ImageVector = Icons.Rounded.BarChart
        override val label: String = "Analysis"
    }

    @Serializable
    data object Budget : BottomNavKey {
        override val icon: ImageVector = Icons.Rounded.AccountBalanceWallet
        override val label: String = "Budget"
    }

    @Serializable
    data object Categories : BottomNavKey {
        override val icon: ImageVector = Icons.Default.Category
        override val label: String = "Categories"
    }

    @Serializable
    data object Settings : BottomNavKey {
        override val icon: ImageVector = Icons.Default.Settings
        override val label: String = "Settings"
    }

    companion object {
        val items = listOf(Home, Analysis, Budget, Categories, Settings)

        val stateSaver = Saver<BottomNavKey, String>(
            save = { it::class.qualifiedName },
            restore = { qualifiedClass ->
                items.firstOrNull { it::class.qualifiedName == qualifiedClass }
                    ?: Home
            }
        )
    }
}