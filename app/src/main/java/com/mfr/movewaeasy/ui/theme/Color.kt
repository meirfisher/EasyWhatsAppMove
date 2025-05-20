package com.mfr.movewaeasy.ui.theme // Or your actual package name

import androidx.compose.ui.graphics.Color

// --- Core App Palette ---
val AppPrimaryGreen = Color(0xFF4CAF50) // Vibrant Green for primary actions
val AppOnPrimary = Color.White // Text/icon color on AppPrimaryGreen

val AppTextPrimaryDark = Color.Black // For main text on light backgrounds
val AppTextSecondaryDark = Color.Gray // For subtitles on light backgrounds
val AppTextPrimaryLight = Color.White // For main text on dark backgrounds
val AppTextSecondaryLight = Color(0xFFB0B0B0) // For subtitles on dark backgrounds

// --- Light Theme Semantic Colors ---
val LightPrimary = AppPrimaryGreen
val LightOnPrimary = AppOnPrimary
val LightPrimaryContainer = Color(0xFFC8E6C9)
val LightOnPrimaryContainer = Color(0xFF1B5E20)

// Using green as secondary as well for this theme, adjust if you have a distinct secondary color
val LightSecondary = AppPrimaryGreen
val LightOnSecondary = AppOnPrimary
val LightSecondaryContainer = Color(0xFFDCEDC8)
val LightOnSecondaryContainer = Color(0xFF33691E)

// You can define a specific tertiary color if needed, or use an existing one
val LightTertiary = Color(0xFF03DAC5) // Example: OriginalSecondary from your previous file
val LightOnTertiary = Color.Black
val LightTertiaryContainer = Color(0xFFB2DFDB)
val LightOnTertiaryContainer = Color(0xFF004D40)

val LightError = Color(0xFFB00020)
val LightOnError = Color.White
val LightErrorContainer = Color(0xFFFDECEA)
val LightOnErrorContainer = Color(0xFF690005)

val LightBackground = Color(0xFFE7F1EB) // Light gray screen background
val LightOnBackground = AppTextPrimaryDark

val LightSurface = Color.White // Card backgrounds
val LightOnSurface = AppTextPrimaryDark // Text/icon color on AppSurface (cards)
val LightSurfaceVariant = Color(0xFFE0E0E0) // For elements like outlined text fields, dividers
val LightOnSurfaceVariant = Color(0xFF424242)
val LightOutline = Color(0xFFBDBDBD) // Borders, dividers

// --- Dark Theme Semantic Colors ---
val DarkPrimary = Color(0xFF66BB6A) // A slightly lighter green for dark theme
val DarkOnPrimary = Color.Black
val DarkPrimaryContainer = Color(0xFF003300) // Darker shade for containers in dark theme
val DarkOnPrimaryContainer = Color(0xFFC8E6C9)

// Using a corresponding green as secondary for dark theme
val DarkSecondary = Color(0xFF66BB6A)
val DarkOnSecondary = Color.Black
val DarkSecondaryContainer = Color(0xFF2E7D32)
val DarkOnSecondaryContainer = Color(0xFFDCEDC8)

// You can define a specific tertiary color for dark theme
val DarkTertiary = Color(0xFF03DAC5) // Example: OriginalSecondary, adjust for dark theme contrast if needed
val DarkOnTertiary = Color.Black // Or Color.White depending on the DarkTertiary color
val DarkTertiaryContainer = Color(0xFF004D40)
val DarkOnTertiaryContainer = Color(0xFFB2DFDB)

val DarkError = Color(0xFFCF6679)
val DarkOnError = Color.Black
val DarkErrorContainer = Color(0xFFB00020)
val DarkOnErrorContainer = Color(0xFFFDECEA)

val DarkBackground = Color(0xFF121212) // Standard dark background
val DarkOnBackground = AppTextPrimaryLight

val DarkSurface = Color(0xFF1E1E1E) // Slightly lighter than background for cards
val DarkOnSurface = AppTextPrimaryLight // Text/icon color on AppDarkSurface
val DarkSurfaceVariant = Color(0xFF424242)
val DarkOnSurfaceVariant = Color(0xFFBDBDBD)
val DarkOutline = Color(0xFF757575)
