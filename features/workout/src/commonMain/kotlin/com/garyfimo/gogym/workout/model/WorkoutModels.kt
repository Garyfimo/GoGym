package com.garyfimo.gogym.workout.model

import com.garyfimo.gogym.model.Exercise
import kotlinx.serialization.Serializable

@Serializable
data class WorkoutSet(
    val id: String,
    val setNumber: Int,
    val weight: Double? = null,
    val reps: Int? = null,
    val isCompleted: Boolean = false
)

@Serializable
data class WorkoutExercise(
    val exercise: Exercise,
    val sets: List<WorkoutSet> = emptyList()
)

@Serializable
data class WorkoutSession(
    val id: String,
    val name: String,
    val startTime: Long, // epoch millis
    val exercises: List<WorkoutExercise> = emptyList()
)
