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
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

import gogym.shared.generated.resources.Res
import gogym.shared.generated.resources.compose_multiplatform

import com.garyfimo.gogym.theme.GoGymTheme
import com.garyfimo.gogym.ui.HomeDashboardScreen

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
                            onClick = { currentScreen = Screen.Dashboard }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.List, contentDescription = "Exercises") },
                            label = { Text("Exercises") },
                            selected = currentScreen == Screen.Exercises,
                            onClick = { currentScreen = Screen.Exercises }
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
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Exercises List",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Task TR-003 will implement this screen.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Platform: $platformGreeting",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
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