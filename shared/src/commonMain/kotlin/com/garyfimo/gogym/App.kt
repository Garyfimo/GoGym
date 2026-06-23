package com.garyfimo.gogym

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

import gogym.shared.generated.resources.Res
import gogym.shared.generated.resources.compose_multiplatform

import com.garyfimo.gogym.theme.GoGymTheme
import com.garyfimo.gogym.home.HomeDashboardScreen
import com.garyfimo.gogym.exercises.ExerciseListScreen
import com.garyfimo.gogym.exercises.ExerciseDetailScreen
import com.garyfimo.gogym.workout.ActiveWorkoutScreen
import com.garyfimo.gogym.workout.WorkoutSessionViewModel
import com.garyfimo.gogym.api.ExerciseApi
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState

import androidx.savedstate.read

@Composable
@Preview
fun App() {
    KoinContext {
        GoGymTheme {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Show bottom bar only on primary dashboard & exercise list screens
            val showBottomBar = currentRoute == "dashboard" || currentRoute == "exercises"

            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                                label = { Text("Dashboard") },
                                selected = currentRoute == "dashboard",
                                onClick = { 
                                    if (currentRoute != "dashboard") {
                                        navController.navigate("dashboard") {
                                            popUpTo("dashboard") { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Exercises") },
                                label = { Text("Exercises") },
                                selected = currentRoute == "exercises",
                                onClick = { 
                                    if (currentRoute != "exercises") {
                                        navController.navigate("exercises") {
                                            popUpTo("dashboard") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard"
                    ) {
                        composable("dashboard") {
                            HomeDashboardScreen(
                                onStartWorkoutClick = {
                                    navController.navigate("active_workout")
                                },
                                onBrowseExercisesClick = {
                                    navController.navigate("exercises") {
                                        popUpTo("dashboard") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                        composable("exercises") {
                            ExerciseListScreen(
                                onExerciseClick = { exercise ->
                                    navController.navigate("exercise_detail/${exercise.id}")
                                }
                            )
                        }
                        composable("exercise_detail/{exerciseId}") { backStackEntry ->
                            val exerciseId = backStackEntry.arguments?.read { getString("exerciseId") } ?: ""
                            ExerciseDetailScreen(
                                exerciseId = exerciseId,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable("active_workout") {
                            val exerciseApi = koinInject<ExerciseApi>()
                            val workoutViewModel: WorkoutSessionViewModel = viewModel { WorkoutSessionViewModel(exerciseApi) }
                            
                            val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
                            val selectedExerciseId = savedStateHandle?.getStateFlow<String?>("selected_exercise_id", null)?.collectAsState()?.value
                            
                            LaunchedEffect(selectedExerciseId) {
                                selectedExerciseId?.let { id ->
                                    workoutViewModel.addExerciseById(id)
                                    savedStateHandle?.remove<String>("selected_exercise_id")
                                }
                            }
                            
                            ActiveWorkoutScreen(
                                viewModel = workoutViewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onAddExerciseClick = {
                                    navController.navigate("exercises_selection")
                                }
                            )
                        }
                        composable("exercises_selection") {
                            ExerciseListScreen(
                                isSelectionMode = true,
                                onBackClick = { navController.popBackStack() },
                                onExerciseClick = { exercise ->
                                    navController.previousBackStackEntry?.savedStateHandle?.set("selected_exercise_id", exercise.id)
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}