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

private val DarkColorScheme =
  darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = Color(0xFF454B40),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    surfaceContainer = Color(0xFF20251D),
    surfaceContainerLow = Color(0xFF1C2119),
    surfaceContainerLowest = Color(0xFF171B14),
    surfaceContainerHigh = Color(0xFF2B3127),
    surfaceContainerHighest = Color(0xFF353C31)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PrimaryEnergy,
    onPrimary = OnPrimaryEnergy,
    primaryContainer = PrimaryContainerEnergy,
    onPrimaryContainer = OnPrimaryContainerEnergy,
    secondary = SecondaryBlue,
    onSecondary = OnSecondaryBlue,
    secondaryContainer = SecondaryContainerBlue,
    onSecondaryContainer = OnSecondaryContainerBlue,
    tertiary = TertiaryPurple,
    onTertiary = OnTertiaryPurple,
    tertiaryContainer = TertiaryContainerYellow,
    onTertiaryContainer = OnTertiaryContainerYellow,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = Color(0xFFC5CFC3),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    surfaceContainer = Color(0xFFEEF2E9),
    surfaceContainerLow = Color(0xFFF4F6F0),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFE6ECE1),
    surfaceContainerHighest = Color(0xFFDDE5D9)
  )

@Composable
fun SolarEaseTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false, // Force custom theme
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    shapes = Shapes,
    content = content
  )
}
