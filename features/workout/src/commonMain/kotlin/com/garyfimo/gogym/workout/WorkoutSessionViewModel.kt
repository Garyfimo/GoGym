package com.garyfimo.gogym.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garyfimo.gogym.api.ExerciseApi
import com.garyfimo.gogym.model.Exercise
import com.garyfimo.gogym.workout.model.WorkoutExercise
import com.garyfimo.gogym.workout.model.WorkoutSet
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

data class WorkoutSessionState(
    val sessionName: String = "Active Workout",
    val elapsedSeconds: Long = 0,
    val exercises: List<WorkoutExercise> = emptyList(),
    val availableExercises: List<Exercise> = emptyList(),
    val isAddExerciseSheetOpen: Boolean = false,
    val isLoadingExercises: Boolean = false,
    val exerciseSearchQuery: String = "",
    val error: String? = null
)

class WorkoutSessionViewModel(
    private val exerciseApi: ExerciseApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutSessionState())
    val uiState: StateFlow<WorkoutSessionState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startTimer()
        loadAvailableExercises()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    private fun loadAvailableExercises() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingExercises = true, error = null) }
            try {
                val exercises = exerciseApi.getExercises()
                _uiState.update { it.copy(availableExercises = exercises, isLoadingExercises = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to load exercises", isLoadingExercises = false) }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(exerciseSearchQuery = query) }
    }

    fun openAddExerciseSheet() {
        _uiState.update { it.copy(isAddExerciseSheetOpen = true) }
    }

    fun closeAddExerciseSheet() {
        _uiState.update { it.copy(isAddExerciseSheetOpen = false, exerciseSearchQuery = "") }
    }

    fun addExercise(exercise: Exercise) {
        _uiState.update { state ->
            if (state.exercises.any { it.exercise.id == exercise.id }) {
                state.copy(isAddExerciseSheetOpen = false, exerciseSearchQuery = "")
            } else {
                val newWorkoutExercise = WorkoutExercise(
                    exercise = exercise,
                    sets = listOf(
                        WorkoutSet(
                            id = generateId(),
                            setNumber = 1,
                            weight = null,
                            reps = null,
                            isCompleted = false
                        )
                    )
                )
                state.copy(
                    exercises = state.exercises + newWorkoutExercise,
                    isAddExerciseSheetOpen = false,
                    exerciseSearchQuery = ""
                )
            }
        }
    }

    fun removeExercise(exerciseId: String) {
        _uiState.update { state ->
            state.copy(exercises = state.exercises.filter { it.exercise.id != exerciseId })
        }
    }

    fun addSet(exerciseId: String) {
        _uiState.update { state ->
            val updatedExercises = state.exercises.map { workoutExercise ->
                if (workoutExercise.exercise.id == exerciseId) {
                    val nextSetNumber = workoutExercise.sets.size + 1
                    val lastSet = workoutExercise.sets.lastOrNull()
                    val newSet = WorkoutSet(
                        id = generateId(),
                        setNumber = nextSetNumber,
                        weight = lastSet?.weight,
                        reps = lastSet?.reps,
                        isCompleted = false
                    )
                    workoutExercise.copy(sets = workoutExercise.sets + newSet)
                } else {
                    workoutExercise
                }
            }
            state.copy(exercises = updatedExercises)
        }
    }

    fun removeSet(exerciseId: String, setId: String) {
        _uiState.update { state ->
            val updatedExercises = state.exercises.map { workoutExercise ->
                if (workoutExercise.exercise.id == exerciseId) {
                    val remainingSets = workoutExercise.sets.filter { it.id != setId }
                    val renumberedSets = remainingSets.mapIndexed { index, workoutSet ->
                        workoutSet.copy(setNumber = index + 1)
                    }
                    workoutExercise.copy(sets = renumberedSets)
                } else {
                    workoutExercise
                }
            }
            state.copy(exercises = updatedExercises)
        }
    }

    fun updateSet(exerciseId: String, setId: String, weight: Double?, reps: Int?, isCompleted: Boolean) {
        _uiState.update { state ->
            val updatedExercises = state.exercises.map { workoutExercise ->
                if (workoutExercise.exercise.id == exerciseId) {
                    val updatedSets = workoutExercise.sets.map { set ->
                        if (set.id == setId) {
                            set.copy(weight = weight, reps = reps, isCompleted = isCompleted)
                        } else {
                            set
                        }
                    }
                    workoutExercise.copy(sets = updatedSets)
                } else {
                    workoutExercise
                }
            }
            state.copy(exercises = updatedExercises)
        }
    }

    private fun generateId(): String {
        return Random.nextInt(1000000, 9999999).toString()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
