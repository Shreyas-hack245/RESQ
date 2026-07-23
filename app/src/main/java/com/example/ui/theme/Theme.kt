package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ResqDarkColorScheme = darkColorScheme(
    primary = ResqRed,
    onPrimary = Color.White,
    primaryContainer = ResqRedBg,
    onPrimaryContainer = ResqRedDark,
    secondary = ResqBlue,
    onSecondary = Color.White,
    secondaryContainer = ResqBlueBg,
    onSecondaryContainer = ResqBlue,
    tertiary = ResqEmerald,
    onTertiary = Color.White,
    background = ResqLightBg,
    onBackground = ResqTextPrimary,
    surface = ResqCardBg,
    onSurface = ResqTextPrimary,
    surfaceVariant = ResqCardBorder,
    onSurfaceVariant = ResqTextSecondary,
    outline = ResqCardBorder
)

private val ResqLightColorScheme = lightColorScheme(
    primary = ResqRed,
    onPrimary = Color.White,
    primaryContainer = ResqRedBg,
    onPrimaryContainer = ResqRedDark,
    secondary = ResqBlue,
    onSecondary = Color.White,
    secondaryContainer = ResqBlueBg,
    onSecondaryContainer = ResqBlue,
    tertiary = ResqEmerald,
    onTertiary = Color.White,
    background = ResqLightBg,
    onBackground = ResqTextPrimary,
    surface = ResqCardBg,
    onSurface = ResqTextPrimary,
    surfaceVariant = ResqCardBorder,
    onSurfaceVariant = ResqTextSecondary,
    outline = ResqCardBorder
)

@Composable
fun ResqTheme(
    darkTheme: Boolean = false, // Professional Polish theme defaults to light clean slate canvas
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ResqDarkColorScheme else ResqLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

