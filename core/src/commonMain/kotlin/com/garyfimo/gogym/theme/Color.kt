package com.garyfimo.gogym.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

val VoltGreen = Color(0xFFCCFF00)
val DarkGrey = Color(0xFF121212)
val CardGrey = Color(0xFF1E1E1E)
val BorderGrey = Color(0xFF2E2E2E)
val LightGrey = Color(0xFFE0E0E0)
val ElectricBlue = Color(0xFF00E5FF)
val ActiveOrange = Color(0xFFFF5D00)
val ErrorRed = Color(0xFFFF3333)

// Dark Theme Color Scheme (Preferred for GoGym)
val DarkColorScheme = darkColorScheme(
    primary = VoltGreen,
    onPrimary = Color.Black,
    secondary = ElectricBlue,
    onSecondary = Color.Black,
    background = DarkGrey,
    onBackground = Color.White,
    surface = CardGrey,
    onSurface = LightGrey,
    surfaceVariant = BorderGrey,
    onSurfaceVariant = Color.White,
    error = ErrorRed,
    onError = Color.White
)

// Light Theme Color Scheme
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF64DD17),
    onPrimary = Color.Black,
    secondary = Color(0xFF00B8D4),
    onSecondary = Color.White,
    background = Color(0xFFFAFAFA),
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFEEEEEE),
    onSurfaceVariant = Color.Black,
    error = Color(0xFFD50000),
    onError = Color.White
)
