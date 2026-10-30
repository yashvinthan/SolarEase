package com.example.ui.theme

import android.content.Context
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Centralized spacing tokens for the SolarEase design system.
 * Follows a consistent 4dp-based rhythm: 4, 8, 12, 16, 24, 32 dp.
 */
object SolarEaseSpacing {
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val screenHorizontal: Dp = 20.dp
    val xl: Dp = 24.dp
    val xxl: Dp = 32.dp
}

/**
 * Centralized shape tokens for consistent corner radii across cards, inputs, and badges.
 */
object SolarEaseShapes {
    val xs = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
    val sm = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
    val md = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    val lg = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    val xl = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
    val pill = androidx.compose.foundation.shape.RoundedCornerShape(999.dp)
}

/**
 * Semantic status & contrast-verified tokens for SolarEase.
 * All foreground colors on their corresponding containers meet WCAG 2.2 AA (>= 4.5:1 for normal text).
 */
object SolarEaseSemanticColors {
    val SuccessText = Color(0xFF1B5E20)
    val SuccessContainer = Color(0xFFE5F2D9)
    val WarningText = Color(0xFF9A3412)
    val WarningContainer = Color(0xFFFFF7ED)
    val WarningBorder = Color(0xFFFED7AA)
    val ErrorText = Color(0xFF93000A)
    val ErrorContainer = Color(0xFFFFDAD6)
    val DangerText = ErrorText
    val DangerContainer = ErrorContainer
    val InfoText = Color(0xFF18324B)
    val InfoContainer = Color(0xFFEAF3FF)
}

/**
 * Window width size classes based on Android Adaptive Layout guidance:
 * - COMPACT: width < 600dp (phones in portrait)
 * - MEDIUM: 600dp <= width < 840dp (large phones in landscape, foldables, small tablets)
 * - EXPANDED: width >= 840dp (tablets, desktop/DeX, large foldables)
 */
enum class SolarEaseWindowWidthSizeClass {
    COMPACT,
    MEDIUM,
    EXPANDED
}

data class SolarEaseWindowLayoutInfo(
    val widthSizeClass: SolarEaseWindowWidthSizeClass,
    val screenWidthDp: Int,
    val screenHeightDp: Int,
    val isLandscape: Boolean,
    val maxContentWidth: Dp,
    val horizontalPadding: Dp,
    val useTwoColumnGrid: Boolean,
    val useNavigationRail: Boolean
)

@Composable
fun rememberSolarEaseWindowLayoutInfo(): SolarEaseWindowLayoutInfo {
    val configuration = LocalConfiguration.current
    val widthDp = configuration.screenWidthDp
    val heightDp = configuration.screenHeightDp
    return remember(widthDp, heightDp) {
        val sizeClass = when {
            widthDp < 600 -> SolarEaseWindowWidthSizeClass.COMPACT
            widthDp < 840 -> SolarEaseWindowWidthSizeClass.MEDIUM
            else -> SolarEaseWindowWidthSizeClass.EXPANDED
        }
        val maxContentWidth = when (sizeClass) {
            SolarEaseWindowWidthSizeClass.COMPACT -> Dp.Unspecified
            SolarEaseWindowWidthSizeClass.MEDIUM -> 680.dp
            SolarEaseWindowWidthSizeClass.EXPANDED -> 840.dp
        }
        val horizontalPadding = when (sizeClass) {
            SolarEaseWindowWidthSizeClass.COMPACT -> 20.dp
            SolarEaseWindowWidthSizeClass.MEDIUM -> 28.dp
            SolarEaseWindowWidthSizeClass.EXPANDED -> 36.dp
        }
        SolarEaseWindowLayoutInfo(
            widthSizeClass = sizeClass,
            screenWidthDp = widthDp,
            screenHeightDp = heightDp,
            isLandscape = widthDp > heightDp,
            maxContentWidth = maxContentWidth,
            horizontalPadding = horizontalPadding,
            useTwoColumnGrid = sizeClass != SolarEaseWindowWidthSizeClass.COMPACT,
            useNavigationRail = sizeClass != SolarEaseWindowWidthSizeClass.COMPACT
        )
    }
}

/**
 * Checks whether the user has requested reduced motion / disabled animations
 * via Android system accessibility settings (`ANIMATOR_DURATION_SCALE == 0f`).
 */
@Composable
fun rememberIsReducedMotionEnabled(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        isReducedMotionEnabled(context)
    }
}

fun isReducedMotionEnabled(context: Context): Boolean {
    return try {
        val scale = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        )
        scale == 0f
    } catch (_: Exception) {
        false
    }
}
