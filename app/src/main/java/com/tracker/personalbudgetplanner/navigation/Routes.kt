package com.tracker.personalbudgetplanner.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Routes : NavKey {

    @Serializable
    data object Welcome : Routes

    @Serializable
    data object OnBoarding : Routes
}