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

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import androidx.savedstate.read

enum class Screen {
    Dashboard,
    Exercises
}

@Composable
@Preview
fun App() {
    KoinContext {
        GoGymTheme {
            var currentScreen by remember { mutableStateOf(Screen.Dashboard) }
            val greetingService = koinInject<Greeting>()
            val platformGreeting = remember { greetingService.greet() }

            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                            label = { Text("Dashboard") },
                            selected = currentScreen == Screen.Dashboard,
                            onClick = { 
                                currentScreen = Screen.Dashboard 
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Exercises") },
                            label = { Text("Exercises") },
                            selected = currentScreen == Screen.Exercises,
                            onClick = { 
                                currentScreen = Screen.Exercises
                            }
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        Screen.Dashboard -> {
                            HomeDashboardScreen(
                                onStartWorkoutClick = {
                                    // Quick action start workout handler
                                },
                                onBrowseExercisesClick = {
                                    currentScreen = Screen.Exercises
                                }
                            )
                        }
                        Screen.Exercises -> {
                            val navController = rememberNavController()
                            NavHost(navController = navController, startDestination = "list") {
                                composable("list") {
                                    ExerciseListScreen(
                                        onExerciseClick = { exercise ->
                                            navController.navigate("detail/${exercise.id}")
                                        }
                                    )
                                }
                                composable("detail/{exerciseId}") { backStackEntry ->
                                    val exerciseId = backStackEntry.arguments?.read { getString("exerciseId") } ?: ""
                                    ExerciseDetailScreen(
                                        exerciseId = exerciseId,
                                        onBackClick = { navController.popBackStack() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}