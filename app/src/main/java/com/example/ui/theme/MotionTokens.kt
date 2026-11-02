package com.example.ui.theme

import android.content.Context
import android.provider.Settings
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * CompositionLocal providing whether reduced motion is active.
 * Defaults to false, injected by [SolarEaseTheme].
 */
val LocalIsReducedMotionEnabled = compositionLocalOf { false }

/**
 * Centralized motion tokens and accessibility-aware motion utilities for SolarEase.
 * Adheres to WCAG 2.2 Criterion 2.3.3 (Animation from Interactions).
 */
object SolarEaseMotion {
    const val durationInstant: Int = 0
    const val durationShort: Int = 150
    const val durationMedium: Int = 300
    const val durationLong: Int = 500

    val easingStandard: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val easingDecelerate: Easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
    val easingAccelerate: Easing = CubicBezierEasing(0.3f, 0.0f, 1.0f, 1.0f)
    val easingLinear: Easing = LinearEasing

    fun <T> springBouncy(): SpringSpec<T> =
        spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )

    fun <T> springResponsive(): SpringSpec<T> =
        spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )

    fun <T> springGentle(): SpringSpec<T> =
        spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        )

    /**
     * Checks whether the user has requested reduced motion / disabled animations
     * via Android system accessibility settings (`ANIMATOR_DURATION_SCALE == 0f`).
     */
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

    /**
     * Composable helper checking active reduced motion state.
     */
    @Composable
    @ReadOnlyComposable
    fun isReducedMotion(): Boolean = LocalIsReducedMotionEnabled.current
}

/**
 * Checks whether reduced motion is enabled on the current device context.
 * Preserved at package level for unit test backward-compatibility.
 */
fun isReducedMotionEnabled(context: Context): Boolean =
    SolarEaseMotion.isReducedMotionEnabled(context)

/**
 * Remember reduced motion setting across recompositions.
 */
@Composable
fun rememberIsReducedMotionEnabled(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        isReducedMotionEnabled(context)
    }
}

/**
 * Motion-aware tween helper: automatically collapses to snap() when reduced motion is enabled.
 */
@Composable
fun <T> motionTween(
    durationMillis: Int = SolarEaseMotion.durationMedium,
    delayMillis: Int = 0,
    easing: Easing = SolarEaseMotion.easingStandard
): AnimationSpec<T> {
    return if (LocalIsReducedMotionEnabled.current) {
        snap(delayMillis = delayMillis)
    } else {
        tween(durationMillis = durationMillis, delayMillis = delayMillis, easing = easing)
    }
}

/**
 * Motion-aware spring helper: automatically collapses to snap() when reduced motion is enabled.
 */
@Composable
fun <T> motionSpring(
    dampingRatio: Float = Spring.DampingRatioNoBouncy,
    stiffness: Float = Spring.StiffnessMedium,
    visibilityThreshold: T? = null
): AnimationSpec<T> {
    return if (LocalIsReducedMotionEnabled.current) {
        snap()
    } else {
        spring(
            dampingRatio = dampingRatio,
            stiffness = stiffness,
            visibilityThreshold = visibilityThreshold
        )
    }
}

/**
 * Resolves effective duration in milliseconds, returning 0 if reduced motion is enabled.
 */
@Composable
fun motionDuration(durationMillis: Int): Int {
    return if (LocalIsReducedMotionEnabled.current) 0 else durationMillis
}
