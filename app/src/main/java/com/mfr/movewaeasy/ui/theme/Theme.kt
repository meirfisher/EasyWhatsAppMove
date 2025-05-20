package com.mfr.movewaeasy.ui.theme // או שם החבילה האמיתי שלך

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
// import androidx.compose.material3.Shapes // בטל הערה אם תיצור קובץ Shapes.kt
// import androidx.compose.material3.Typography // בטל הערה אם תיצור קובץ Typography.kt
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// הגדרת ערכת הצבעים הבהירה שלך באמצעות שמות צבעים מ-Color.kt
private val AppLightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,

    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,

    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,

    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,

    background = LightBackground,
    onBackground = LightOnBackground,

    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,

    outline = LightOutline
)

// הגדרת ערכת הצבעים הכהה שלך באמצעות שמות צבעים מ-Color.kt
private val AppDarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,

    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,

    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,

    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,

    background = DarkBackground,
    onBackground = DarkOnBackground,

    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,

    outline = DarkOutline
)

@Composable
fun MoveWAEasyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // צבע דינמי זמין באנדרואיד 12+
    // הגדר ל-'false' כדי להשתמש בקפדנות ב-AppLightColorScheme/AppDarkColorScheme שלך.
    // הגדר ל-'true' אם ברצונך לאפשר למשתמשים עם אנדרואיד 12+ להשתמש בעיצוב מבוסס טפט.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> AppDarkColorScheme
        else -> AppLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // הגדרת צבע שורת המצב כך שיתאים לצבע הרקע של ערכת הנושא
            window.statusBarColor = colorScheme.background.toArgb()
            // הגדרת אייקוני שורת המצב להיות בהירים או כהים בהתאם לערכת הנושא
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        // typography = Typography, // תוכל להוסיף Typography.kt מאוחר יותר עבור פונטים מותאמים אישית ולבטל הערה זו
        // shapes = Shapes,       // תוכל להוסיף Shapes.kt מאוחר יותר עבור צורות רכיבים מותאמות אישית ולבטל הערה זו
        content = content
    )
}
