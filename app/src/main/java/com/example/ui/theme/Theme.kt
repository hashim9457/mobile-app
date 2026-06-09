package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = OnBackground,
    surface = PrimaryContainer,
    onPrimary = PrimaryColor
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    tertiary = GoldAccent,
    background = BackgroundColor,
    surface = SurfaceContainerLow,
    onPrimary = Color.White,
    onSecondary = OnBackground,
    onBackground = OnBackground,
    onSurface = OnBackground,
    onSurfaceVariant = OnSurfaceVariant
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to strictly enforce our premium glassmorphic educational branding!
    content: @Composable () -> Unit,
) {
    // Force LightColorScheme under both dark and light mode settings because the app's visual identity,
    // layout containers, and text definitions are custom designed with high-contrast light emerald white/mints.
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
