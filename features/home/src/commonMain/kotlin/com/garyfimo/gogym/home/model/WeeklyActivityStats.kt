package com.garyfimo.gogym.home.model

import kotlinx.serialization.Serializable

@Serializable
data class WeeklyActivityStats(
    val workoutsCompleted: Int,
    val streakDays: Int,
    val activeTimeMinutes: Int
)
