package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Supported application theme modes.
 */
enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        fun fromString(value: String?): ThemeMode {
            return when (value?.uppercase(java.util.Locale.ROOT)) {
                "LIGHT" -> LIGHT
                "DARK" -> DARK
                else -> SYSTEM
            }
        }
    }
}

/**
 * Stable theme controller contract for inspection and runtime mutation.
 */
@Stable
interface ThemeController {
    val currentThemeMode: ThemeMode
    val isDarkTheme: Boolean
    fun setThemeMode(mode: ThemeMode)
    fun toggleTheme() {
        setThemeMode(if (isDarkTheme) ThemeMode.LIGHT else ThemeMode.DARK)
    }

    // Aliases for convenience / backward compatibility
    val currentMode: ThemeMode get() = currentThemeMode
    fun setMode(mode: ThemeMode) = setThemeMode(mode)
}

/**
 * Root CompositionLocal providing ThemeController to all descendant composables.
 */
val LocalThemeController = staticCompositionLocalOf<ThemeController> {
    object : ThemeController {
        override val currentThemeMode: ThemeMode = ThemeMode.SYSTEM
        override val isDarkTheme: Boolean = false
        override fun setThemeMode(mode: ThemeMode) {}
    }
}

/**
 * CompositionLocal exposing whether the active theme is dark, respecting ThemeMode overrides.
 */
val LocalIsDarkTheme = compositionLocalOf { false }

/**
 * Accessor returning true if the app is currently rendered in dark mode.
 */
val isAppInDarkTheme: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalIsDarkTheme.current

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
        outlineVariant = OutlineVariantDark,
        error = ErrorDark,
        onError = OnErrorDark,
        errorContainer = ErrorContainerDark,
        onErrorContainer = OnErrorContainerDark,
        surfaceDim = SurfaceDimDark,
        surfaceBright = SurfaceBrightDark,
        surfaceContainer = SurfaceContainerMediumDark,
        surfaceContainerLow = SurfaceContainerLowDark,
        surfaceContainerLowest = SurfaceContainerLowestDark,
        surfaceContainerHigh = SurfaceContainerHighDark,
        surfaceContainerHighest = SurfaceContainerHighestDark,
        inverseSurface = InverseSurfaceDark,
        inverseOnSurface = InverseOnSurfaceDark,
        inversePrimary = PrimaryLight
    )

private val LightColorScheme =
    lightColorScheme(
        primary = PrimaryLight,
        onPrimary = OnPrimaryLight,
        primaryContainer = PrimaryContainerLight,
        onPrimaryContainer = OnPrimaryContainerLight,
        secondary = SecondaryLight,
        onSecondary = OnSecondaryLight,
        secondaryContainer = SecondaryContainerLight,
        onSecondaryContainer = OnSecondaryContainerLight,
        tertiary = TertiaryLight,
        onTertiary = OnTertiaryLight,
        tertiaryContainer = TertiaryContainerLight,
        onTertiaryContainer = OnTertiaryContainerLight,
        background = BackgroundLight,
        onBackground = OnBackgroundLight,
        surface = SurfaceLight,
        onSurface = OnSurfaceLight,
        surfaceVariant = SurfaceVariantLight,
        onSurfaceVariant = OnSurfaceVariantLight,
        outline = OutlineLight,
        outlineVariant = OutlineVariantLight,
        error = ErrorLight,
        onError = OnErrorLight,
        errorContainer = ErrorContainerLight,
        onErrorContainer = OnErrorContainerLight,
        surfaceDim = SurfaceDimLight,
        surfaceBright = SurfaceBrightLight,
        surfaceContainer = SurfaceContainerMediumLight,
        surfaceContainerLow = SurfaceContainerLowLight,
        surfaceContainerLowest = SurfaceContainerLowestLight,
        surfaceContainerHigh = SurfaceContainerHighLight,
        surfaceContainerHighest = SurfaceContainerHighestLight,
        inverseSurface = InverseSurfaceLight,
        inverseOnSurface = InverseOnSurfaceLight,
        inversePrimary = PrimaryDark
    )

/**
 * SolarEaseTheme entry point.
 * Supports explicit [themeMode] (SYSTEM, LIGHT, DARK), boolean [darkTheme] fallback,
 * dynamic colors on Android 12+, and synchronizes system bar insets contrast.
 */
@Composable
fun SolarEaseTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    darkTheme: Boolean = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    },
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDark = when {
        themeMode == ThemeMode.DARK -> true
        themeMode == ThemeMode.LIGHT -> false
        else -> darkTheme
    }

    val context = LocalContext.current
    val view = LocalView.current

    // Synchronize system bar icon contrast with theme state
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !isDark
                insetsController.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    val isReducedMotion = rememberIsReducedMotionEnabled()

    val themeController = remember(themeMode, isDark) {
        object : ThemeController {
            override val currentThemeMode: ThemeMode = themeMode
            override val isDarkTheme: Boolean = isDark
            override fun setThemeMode(mode: ThemeMode) {
                com.example.core.session.UserSession.setThemeMode(context, mode)
            }
        }
    }

    CompositionLocalProvider(
        LocalIsDarkTheme provides isDark,
        LocalIsReducedMotionEnabled provides isReducedMotion,
        LocalThemeController provides themeController
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

/**
 * Accessor object providing centralized design tokens per Interface Contract 1.
 */
object SolarEaseTheme {
    val colors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes

    val motion: SolarEaseMotion
        get() = SolarEaseMotion
}
