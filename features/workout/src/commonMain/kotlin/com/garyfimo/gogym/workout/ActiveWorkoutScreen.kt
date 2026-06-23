package com.garyfimo.gogym.workout

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.garyfimo.gogym.workout.api.WorkoutApi
import com.garyfimo.gogym.api.ExerciseApi
import com.garyfimo.gogym.model.Exercise
import com.garyfimo.gogym.config.AppConfig
import io.ktor.client.HttpClient
import com.garyfimo.gogym.theme.components.GoGymButton
import com.garyfimo.gogym.theme.components.GoGymOutlineButton
import com.garyfimo.gogym.theme.components.GoGymSearchField
import com.garyfimo.gogym.theme.components.GoGymTextButton
import com.garyfimo.gogym.workout.model.WorkoutExercise
import com.garyfimo.gogym.workout.model.WorkoutSet
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    onBackClick: () -> Unit,
    onAddExerciseClick: () -> Unit,
    modifier: Modifier = Modifier,
    exerciseApi: ExerciseApi = koinInject(),
    httpClient: HttpClient = koinInject(),
    appConfig: AppConfig = koinInject(),
    viewModel: WorkoutSessionViewModel = viewModel {
        WorkoutSessionViewModel(
            exerciseApi = exerciseApi,
            workoutApi = WorkoutApi(
                client = httpClient,
                appConfig = appConfig
            )
        )
    }
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showCancelDialog by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PulsingDot()
                        Text(
                            text = state.sessionName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showCancelDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel Workout",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                border = BoxBorder(MaterialTheme.colorScheme.surfaceVariant, 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GoGymOutlineButton(
                        text = "Cancel",
                        onClick = { showCancelDialog = true },
                        contentColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                    GoGymButton(
                        text = "Finish",
                        onClick = { showFinishDialog = true },
                        modifier = Modifier.weight(1.5f)
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Timer Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Duration",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = formatDuration(state.elapsedSeconds),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = onAddExerciseClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Exercise")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Exercises List
            if (state.exercises.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Your workout is empty.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        GoGymTextButton(
                            text = "Add your first exercise",
                            onClick = onAddExerciseClick
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(state.exercises, key = { it.exercise.id }) { workoutExercise ->
                        WorkoutExerciseCard(
                            workoutExercise = workoutExercise,
                            onUpdateSet = { setId, weight, reps, completed ->
                                viewModel.updateSet(workoutExercise.exercise.id, setId, weight, reps, completed)
                            },
                            onAddSet = {
                                viewModel.addSet(workoutExercise.exercise.id)
                            },
                            onDeleteSet = { setId ->
                                viewModel.removeSet(workoutExercise.exercise.id, setId)
                            },
                            onDeleteExercise = {
                                viewModel.removeExercise(workoutExercise.exercise.id)
                            }
                        )
                    }
                }
            }
        }
    }

    // Cancel Confirmation Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Workout?") },
            text = { Text("Are you sure you want to end this active session? Your progress will not be saved.") },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Yes, Cancel")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep Working Out")
                }
            }
        )
    }

    // Finish Confirmation Dialog
    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Finish Workout?") },
            text = { Text("Great job! Ready to log this session and save your stats?") },
            confirmButton = {
                Button(
                    onClick = {
                        showFinishDialog = false
                        viewModel.finishWorkout {
                            onBackClick()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Log Workout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Resume")
                }
            }
        )
    }


}

@Composable
fun PulsingDot() {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Canvas(modifier = Modifier.size(10.dp)) {
        drawCircle(
            color = Color(0xFF64DD17), // A vibrant bright green
            alpha = alpha
        )
    }
}

@Composable
fun WorkoutExerciseCard(
    workoutExercise: WorkoutExercise,
    onUpdateSet: (setId: String, weight: Double?, reps: Int?, completed: Boolean) -> Unit,
    onAddSet: () -> Unit,
    onDeleteSet: (setId: String) -> Unit,
    onDeleteExercise: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BoxBorder(MaterialTheme.colorScheme.surfaceVariant, 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Exercise Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = workoutExercise.exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = workoutExercise.exercise.muscleGroup,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(onClick = onDeleteExercise) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Exercise",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

            // Sets Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SET",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "KG",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "REPS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DONE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(48.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(36.dp)) // padding match for trash icon
            }

            // Sets List
            workoutExercise.sets.forEach { set ->
                WorkoutSetRow(
                    set = set,
                    onUpdateSet = { weight, reps, completed ->
                        onUpdateSet(set.id, weight, reps, completed)
                    },
                    onDeleteSet = { onDeleteSet(set.id) }
                )
            }

            // Add Set Button
            Button(
                onClick = onAddSet,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(36.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Set", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            }
        }
    }
}

@Composable
fun WorkoutSetRow(
    set: WorkoutSet,
    onUpdateSet: (weight: Double?, reps: Int?, completed: Boolean) -> Unit,
    onDeleteSet: () -> Unit
) {
    var weightText by remember(set.id) { mutableStateOf(set.weight?.toString() ?: "") }
    var repsText by remember(set.id) { mutableStateOf(set.reps?.toString() ?: "") }

    val completedColor = MaterialTheme.colorScheme.primary
    val idleColor = MaterialTheme.colorScheme.surfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set Number Chip
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(36.dp)
                .background(
                    if (set.isCompleted) completedColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = set.setNumber.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (set.isCompleted) completedColor else MaterialTheme.colorScheme.onSurface
                )
            )
        }

        // Weight Input
        Spacer(modifier = Modifier.width(8.dp))
        MiniInputField(
            value = weightText,
            onValueChange = { newValue ->
                weightText = newValue
                onUpdateSet(newValue.toDoubleOrNull(), repsText.toIntOrNull(), set.isCompleted)
            },
            placeholder = "0",
            modifier = Modifier.weight(1f)
        )

        // Reps Input
        Spacer(modifier = Modifier.width(8.dp))
        MiniInputField(
            value = repsText,
            onValueChange = { newValue ->
                repsText = newValue
                onUpdateSet(weightText.toDoubleOrNull(), newValue.toIntOrNull(), set.isCompleted)
            },
            placeholder = "0",
            modifier = Modifier.weight(1f)
        )

        // Done Checkbox / Button
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(36.dp)
                .background(
                    if (set.isCompleted) completedColor else idleColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    onUpdateSet(weightText.toDoubleOrNull(), repsText.toIntOrNull(), !set.isCompleted)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Done",
                tint = if (set.isCompleted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp)
            )
        }

        // Delete Set Button
        IconButton(
            onClick = onDeleteSet,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete Set",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun MiniInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            autoCorrectEnabled = false
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        ),
                        textAlign = TextAlign.Center
                    )
                }
                innerTextField()
            }
        },
        modifier = modifier.height(36.dp)
    )
}

// Utility to draw a simple bottom border for the bar
private fun BoxBorder(color: Color, width: androidx.compose.ui.unit.Dp) = androidx.compose.foundation.BorderStroke(width, color)

fun formatDuration(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) {
        "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
    } else {
        "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
    }
}
