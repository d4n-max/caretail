package com.caretail.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val CareTailLightColorScheme = lightColorScheme(
    primary = CareTailPrimary,
    onPrimary = CareTailTextPrimary,
    secondary = CareTailAccent,
    onSecondary = CareTailCard,
    background = CareTailBackground,
    onBackground = CareTailTextPrimary,
    surface = CareTailCard,
    onSurface = CareTailTextPrimary,
    surfaceVariant = CareTailWarmSurface,
    onSurfaceVariant = CareTailTextSecondary,
    outline = CareTailDivider,
)

private val CareTailDarkColorScheme = darkColorScheme(
    primary = CareTailPrimary,
    onPrimary = CareTailTextPrimary,
    secondary = CareTailAccent,
    onSecondary = CareTailCard,
    background = CareTailDarkBackground,
    onBackground = CareTailCard,
    surface = CareTailDarkCard,
    onSurface = CareTailCard,
    surfaceVariant = CareTailDarkSurface,
    onSurfaceVariant = CareTailDivider,
    outline = CareTailTextSecondary,
)

val CareTailShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun CareTailTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) CareTailDarkColorScheme else CareTailLightColorScheme,
        typography = CareTailTypography,
        shapes = CareTailShapes,
        content = content,
    )
}
