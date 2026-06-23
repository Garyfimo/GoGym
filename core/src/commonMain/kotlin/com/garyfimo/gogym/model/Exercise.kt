package com.garyfimo.gogym.model

import kotlinx.serialization.Serializable

@Serializable
data class Exercise(
    val id: String,
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val instructions: String
)
