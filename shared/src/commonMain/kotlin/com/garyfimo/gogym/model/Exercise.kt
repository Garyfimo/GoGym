package com.garyfimo.gogym.model

import kotlinx.serialization.Serializable

@Serializable
data class Exercise(
    val id: String,
    val name: String,
    val category: String,
    val primaryMuscles: List<String>
)
