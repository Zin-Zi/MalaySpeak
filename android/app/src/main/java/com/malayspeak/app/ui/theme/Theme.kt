package com.malayspeak.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Minimalist Monochrome (Black & White) Color Schemes
private val LightColorScheme = lightColorScheme(
    primary = MonoLightPrimary,
    onPrimary = MonoLightOnPrimary,
    primaryContainer = MonoLightPrimaryContainer,
    onPrimaryContainer = MonoLightOnPrimaryContainer,
    secondary = MonoLightSecondary,
    onSecondary = MonoLightOnSecondary,
    secondaryContainer = MonoLightSecondaryContainer,
    onSecondaryContainer = MonoLightOnSecondaryContainer,
    tertiary = MonoLightSecondary,
    tertiaryContainer = MonoLightSecondaryContainer,
    onTertiaryContainer = MonoLightOnSecondaryContainer,
    background = MonoLightBackground,
    surface = MonoLightSurface,
    onSurface = MonoLightOnSurface,
    surfaceVariant = MonoLightSurfaceVariant,
    onSurfaceVariant = MonoLightOnSurfaceVariant,
    outline = MonoLightOutline,
    outlineVariant = MonoLightOutlineVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = MonoDarkPrimary,
    onPrimary = MonoDarkOnPrimary,
    primaryContainer = MonoDarkPrimaryContainer,
    onPrimaryContainer = MonoDarkOnPrimaryContainer,
    secondary = MonoDarkSecondary,
    onSecondary = MonoDarkOnSecondary,
    secondaryContainer = MonoDarkSecondaryContainer,
    onSecondaryContainer = MonoDarkOnSecondaryContainer,
    tertiary = MonoDarkSecondary,
    tertiaryContainer = MonoDarkSecondaryContainer,
    onTertiaryContainer = MonoDarkOnSecondaryContainer,
    background = MonoDarkBackground,
    surface = MonoDarkSurface,
    onSurface = MonoDarkOnSurface,
    surfaceVariant = MonoDarkSurfaceVariant,
    onSurfaceVariant = MonoDarkOnSurfaceVariant,
    outline = MonoDarkOutline,
    outlineVariant = MonoDarkOutlineVariant
)

@Composable
fun MalaySpeakTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
