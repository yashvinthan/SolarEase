package com.example.ui.theme

import android.content.Context
import android.provider.Settings
import androidx.compose.animation.core.SnapSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.feature.assessment.ui.StitchSelectableCard
import com.example.feature.assessment.ui.StitchStepProgressBar
import com.example.ui.components.SolarEaseBottomNavigation
import com.example.ui.components.SolarEaseNavTab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Adversarial Challenger Test Suite for Milestone M1.
 * Focuses on:
 * 1. Motion behavior and reduced-motion preference collapsing to 0ms / SnapSpec.
 * 2. Caller compatibility across all 33 legacy Stitch* composable getters.
 * 3. Light and Dark theme adaptation and contrast invariants.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class MotionAndBackwardCompatibilityChallengerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    private fun channelToLinear(channel: Float): Double {
        return if (channel <= 0.04045f) {
            channel / 12.92
        } else {
            ((channel + 0.055) / 1.055).pow(2.4)
        }
    }

    private fun calculateLuminance(color: Color): Double {
        val r = channelToLinear(color.red)
        val g = channelToLinear(color.green)
        val b = channelToLinear(color.blue)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun calculateContrastRatio(c1: Color, c2: Color): Double {
        val l1 = calculateLuminance(c1)
        val l2 = calculateLuminance(c2)
        val lighter = max(l1, l2)
        val darker = min(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    // =========================================================================
    // SECTION 1: Adversarial MotionTokens & Reduced-Motion Tests
    // =========================================================================

    @Test
    fun challengeMotionDuration_whenReducedMotionTrue_collapsesAllDurationsToZero() {
        var durationShortResult = -1
        var durationMediumResult = -1
        var durationLongResult = -1
        var durationZeroResult = -1
        var durationArbitraryResult = -1
        var durationNegativeResult = -1

        composeTestRule.setContent {
            CompositionLocalProvider(LocalIsReducedMotionEnabled provides true) {
                durationShortResult = motionDuration(SolarEaseMotion.durationShort)
                durationMediumResult = motionDuration(SolarEaseMotion.durationMedium)
                durationLongResult = motionDuration(SolarEaseMotion.durationLong)
                durationZeroResult = motionDuration(0)
                durationArbitraryResult = motionDuration(10_000)
                durationNegativeResult = motionDuration(-250)
            }
        }
        composeTestRule.waitForIdle()

        assertEquals("Short duration must collapse to 0ms", 0, durationShortResult)
        assertEquals("Medium duration must collapse to 0ms", 0, durationMediumResult)
        assertEquals("Long duration must collapse to 0ms", 0, durationLongResult)
        assertEquals("Zero duration must remain 0ms", 0, durationZeroResult)
        assertEquals("Large arbitrary duration must collapse to 0ms", 0, durationArbitraryResult)
        assertEquals("Negative duration must collapse to 0ms", 0, durationNegativeResult)
    }

    @Test
    fun challengeMotionDuration_whenReducedMotionFalse_preservesRequestedDurations() {
        var durationShortResult = -1
        var durationMediumResult = -1
        var durationLongResult = -1
        var durationZeroResult = -1
        var durationArbitraryResult = -1

        composeTestRule.setContent {
            CompositionLocalProvider(LocalIsReducedMotionEnabled provides false) {
                durationShortResult = motionDuration(SolarEaseMotion.durationShort)
                durationMediumResult = motionDuration(SolarEaseMotion.durationMedium)
                durationLongResult = motionDuration(SolarEaseMotion.durationLong)
                durationZeroResult = motionDuration(0)
                durationArbitraryResult = motionDuration(1_250)
            }
        }
        composeTestRule.waitForIdle()

        assertEquals(150, durationShortResult)
        assertEquals(300, durationMediumResult)
        assertEquals(500, durationLongResult)
        assertEquals(0, durationZeroResult)
        assertEquals(1_250, durationArbitraryResult)
    }

    @Test
    fun challengeMotionTween_whenReducedMotionTrue_returnsSnapSpecWithPreservedDelay() {
        var specWithZeroDelay: Any? = null
        var specWithNonZeroDelay: Any? = null

        composeTestRule.setContent {
            CompositionLocalProvider(LocalIsReducedMotionEnabled provides true) {
                specWithZeroDelay = motionTween<Float>(durationMillis = 300, delayMillis = 0)
                specWithNonZeroDelay = motionTween<Float>(durationMillis = 500, delayMillis = 120)
            }
        }
        composeTestRule.waitForIdle()

        assertTrue("Spec must be an instance of SnapSpec", specWithZeroDelay is SnapSpec<*>)
        assertTrue("Spec with delay must be an instance of SnapSpec", specWithNonZeroDelay is SnapSpec<*>)

        val snapZero = specWithZeroDelay as SnapSpec<Float>
        val snapDelayed = specWithNonZeroDelay as SnapSpec<Float>

        assertEquals(0, snapZero.delay)
        assertEquals(120, snapDelayed.delay)
    }

    @Test
    fun challengeMotionTween_whenReducedMotionFalse_returnsTweenSpecWithExactParams() {
        var spec: Any? = null

        composeTestRule.setContent {
            CompositionLocalProvider(LocalIsReducedMotionEnabled provides false) {
                spec = motionTween<Float>(
                    durationMillis = SolarEaseMotion.durationLong,
                    delayMillis = 75,
                    easing = SolarEaseMotion.easingDecelerate
                )
            }
        }
        composeTestRule.waitForIdle()

        assertTrue("Spec must be an instance of TweenSpec", spec is TweenSpec<*>)
        val tween = spec as TweenSpec<Float>
        assertEquals(500, tween.durationMillis)
        assertEquals(75, tween.delay)
        assertEquals(SolarEaseMotion.easingDecelerate, tween.easing)
    }

    @Test
    fun challengeMotionSpring_whenReducedMotionTrue_collapsesToSnapSpec() {
        var springSpec: Any? = null

        composeTestRule.setContent {
            CompositionLocalProvider(LocalIsReducedMotionEnabled provides true) {
                springSpec = motionSpring<Float>(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = Spring.StiffnessVeryLow
                )
            }
        }
        composeTestRule.waitForIdle()

        assertTrue("Reduced motion spring must collapse to SnapSpec", springSpec is SnapSpec<*>)
        val snap = springSpec as SnapSpec<Float>
        assertEquals(0, snap.delay)
    }

    @Test
    fun challengeMotionSpring_whenReducedMotionFalse_returnsConfiguredSpringSpec() {
        var springSpec: Any? = null

        composeTestRule.setContent {
            CompositionLocalProvider(LocalIsReducedMotionEnabled provides false) {
                springSpec = motionSpring<Float>(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            }
        }
        composeTestRule.waitForIdle()

        assertTrue("Spec must be an instance of SpringSpec", springSpec is SpringSpec<*>)
        val spring = springSpec as SpringSpec<Float>
        assertEquals(Spring.DampingRatioMediumBouncy, spring.dampingRatio, 0.001f)
        assertEquals(Spring.StiffnessLow, spring.stiffness, 0.001f)
    }

    @Test
    fun challengeSolarEaseMotion_isReducedMotion_evaluatesCorrectlyInComposition() {
        var isReducedMotionTrue = false
        var isReducedMotionFalse = true

        composeTestRule.setContent {
            CompositionLocalProvider(LocalIsReducedMotionEnabled provides true) {
                isReducedMotionTrue = SolarEaseMotion.isReducedMotion()
            }
            CompositionLocalProvider(LocalIsReducedMotionEnabled provides false) {
                isReducedMotionFalse = SolarEaseMotion.isReducedMotion()
            }
        }
        composeTestRule.waitForIdle()

        assertTrue("SolarEaseMotion.isReducedMotion() must be true when local is true", isReducedMotionTrue)
        assertFalse("SolarEaseMotion.isReducedMotion() must be false when local is false", isReducedMotionFalse)
    }

    @Test
    fun challengeSolarEaseTheme_reducedMotionTrue_whenSystemScaleZero() {
        var activeReducedMotion = false
        try {
            Settings.Global.putFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0.0f)
            composeTestRule.setContent {
                SolarEaseTheme {
                    activeReducedMotion = SolarEaseMotion.isReducedMotion()
                }
            }
            composeTestRule.waitForIdle()
            assertTrue(
                "When system animator scale is 0.0f, SolarEaseTheme must provide reduced motion = true",
                activeReducedMotion
            )
        } finally {
            Settings.Global.putFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f)
        }
    }

    @Test
    fun challengeSolarEaseTheme_reducedMotionFalse_whenSystemScaleOne() {
        var activeReducedMotion = true
        try {
            Settings.Global.putFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f)
            composeTestRule.setContent {
                SolarEaseTheme {
                    activeReducedMotion = SolarEaseMotion.isReducedMotion()
                }
            }
            composeTestRule.waitForIdle()
            assertFalse(
                "When system animator scale is 1.0f, SolarEaseTheme must provide reduced motion = false",
                activeReducedMotion
            )
        } finally {
            Settings.Global.putFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f)
        }
    }

    // =========================================================================
    // SECTION 2: Stress-Testing All 33 Legacy Stitch* Composable Getters
    // =========================================================================

    private data class StitchGetterResult(
        val name: String,
        val color: Color
    )

    @Composable
    private fun collectAll33StitchGetters(): List<StitchGetterResult> {
        return listOf(
            StitchGetterResult("StitchPrimary", StitchPrimary),
            StitchGetterResult("StitchPrimaryContainer", StitchPrimaryContainer),
            StitchGetterResult("StitchPrimaryFixed", StitchPrimaryFixed),
            StitchGetterResult("StitchPrimaryFixedDim", StitchPrimaryFixedDim),
            StitchGetterResult("StitchOnPrimary", StitchOnPrimary),
            StitchGetterResult("StitchOnPrimaryContainer", StitchOnPrimaryContainer),
            StitchGetterResult("StitchOnPrimaryFixed", StitchOnPrimaryFixed),
            StitchGetterResult("StitchOnPrimaryFixedVariant", StitchOnPrimaryFixedVariant),
            StitchGetterResult("StitchSurface", StitchSurface),
            StitchGetterResult("StitchBackground", StitchBackground),
            StitchGetterResult("StitchSurfaceDim", StitchSurfaceDim),
            StitchGetterResult("StitchSurfaceBright", StitchSurfaceBright),
            StitchGetterResult("StitchSurfaceContainerLowest", StitchSurfaceContainerLowest),
            StitchGetterResult("StitchSurfaceLowest", StitchSurfaceLowest),
            StitchGetterResult("StitchSurfaceContainerLow", StitchSurfaceContainerLow),
            StitchGetterResult("StitchSurfaceLow", StitchSurfaceLow),
            StitchGetterResult("StitchSurfaceContainer", StitchSurfaceContainer),
            StitchGetterResult("StitchSurfaceContainerHigh", StitchSurfaceContainerHigh),
            StitchGetterResult("StitchSurfaceHigh", StitchSurfaceHigh),
            StitchGetterResult("StitchSurfaceContainerHighest", StitchSurfaceContainerHighest),
            StitchGetterResult("StitchOnSurface", StitchOnSurface),
            StitchGetterResult("StitchOnSurfaceVariant", StitchOnSurfaceVariant),
            StitchGetterResult("StitchInverseSurface", StitchInverseSurface),
            StitchGetterResult("StitchInverseOnSurface", StitchInverseOnSurface),
            StitchGetterResult("StitchTertiary", StitchTertiary),
            StitchGetterResult("StitchTertiaryContainer", StitchTertiaryContainer),
            StitchGetterResult("StitchSecondary", StitchSecondary),
            StitchGetterResult("StitchSecondaryContainer", StitchSecondaryContainer),
            StitchGetterResult("StitchOutline", StitchOutline),
            StitchGetterResult("StitchOutlineVariant", StitchOutlineVariant),
            StitchGetterResult("StitchError", StitchError),
            StitchGetterResult("StitchErrorContainer", StitchErrorContainer),
            StitchGetterResult("StitchOnErrorContainer", StitchOnErrorContainer)
        )
    }

    @Test
    fun challengeStitchGetters_countIsExactly33() {
        var count = 0
        composeTestRule.setContent {
            SolarEaseTheme {
                count = collectAll33StitchGetters().size
            }
        }
        composeTestRule.waitForIdle()
        assertEquals("There must be exactly 33 legacy Stitch* composable getters", 33, count)
    }

    @Test
    fun challengeStitchGetters_inLightTheme_returnsValidColorsMatchingLightTokens() {
        var lightResults: List<StitchGetterResult> = emptyList()

        composeTestRule.setContent {
            SolarEaseTheme(themeMode = ThemeMode.LIGHT) {
                lightResults = collectAll33StitchGetters()
            }
        }
        composeTestRule.waitForIdle()

        assertEquals(33, lightResults.size)
        val map = lightResults.associate { it.name to it.color }

        // Verify non-null, specified, and fully opaque
        for (item in lightResults) {
            assertNotNull("Getter ${item.name} returned null", item.color)
            assertTrue("Getter ${item.name} must be specified", item.color.isSpecified)
            assertTrue("Getter ${item.name} must be opaque (alpha >= 0.99)", item.color.alpha >= 0.99f)
        }

        // Verify exact matches with StitchLightTokens
        assertEquals(StitchLightTokens.Primary, map["StitchPrimary"])
        assertEquals(StitchLightTokens.PrimaryContainer, map["StitchPrimaryContainer"])
        assertEquals(StitchLightTokens.PrimaryFixed, map["StitchPrimaryFixed"])
        assertEquals(StitchLightTokens.PrimaryFixedDim, map["StitchPrimaryFixedDim"])
        assertEquals(StitchLightTokens.OnPrimary, map["StitchOnPrimary"])
        assertEquals(StitchLightTokens.OnPrimaryContainer, map["StitchOnPrimaryContainer"])
        assertEquals(StitchLightTokens.OnPrimaryFixed, map["StitchOnPrimaryFixed"])
        assertEquals(StitchLightTokens.OnPrimaryFixedVariant, map["StitchOnPrimaryFixedVariant"])
        assertEquals(StitchLightTokens.Surface, map["StitchSurface"])
        assertEquals(StitchLightTokens.Background, map["StitchBackground"])
        assertEquals(StitchLightTokens.SurfaceDim, map["StitchSurfaceDim"])
        assertEquals(StitchLightTokens.SurfaceBright, map["StitchSurfaceBright"])
        assertEquals(StitchLightTokens.SurfaceContainerLowest, map["StitchSurfaceContainerLowest"])
        assertEquals(StitchLightTokens.SurfaceContainerLowest, map["StitchSurfaceLowest"])
        assertEquals(StitchLightTokens.SurfaceContainerLow, map["StitchSurfaceContainerLow"])
        assertEquals(StitchLightTokens.SurfaceContainerLow, map["StitchSurfaceLow"])
        assertEquals(StitchLightTokens.SurfaceContainer, map["StitchSurfaceContainer"])
        assertEquals(StitchLightTokens.SurfaceContainerHigh, map["StitchSurfaceContainerHigh"])
        assertEquals(StitchLightTokens.SurfaceContainerHigh, map["StitchSurfaceHigh"])
        assertEquals(StitchLightTokens.SurfaceContainerHighest, map["StitchSurfaceContainerHighest"])
        assertEquals(StitchLightTokens.OnSurface, map["StitchOnSurface"])
        assertEquals(StitchLightTokens.OnSurfaceVariant, map["StitchOnSurfaceVariant"])
        assertEquals(StitchLightTokens.InverseSurface, map["StitchInverseSurface"])
        assertEquals(StitchLightTokens.InverseOnSurface, map["StitchInverseOnSurface"])
        assertEquals(StitchLightTokens.Tertiary, map["StitchTertiary"])
        assertEquals(StitchLightTokens.TertiaryContainer, map["StitchTertiaryContainer"])
        assertEquals(StitchLightTokens.Secondary, map["StitchSecondary"])
        assertEquals(StitchLightTokens.SecondaryContainer, map["StitchSecondaryContainer"])
        assertEquals(StitchLightTokens.Outline, map["StitchOutline"])
        assertEquals(StitchLightTokens.OutlineVariant, map["StitchOutlineVariant"])
        assertEquals(StitchLightTokens.Error, map["StitchError"])
        assertEquals(StitchLightTokens.ErrorContainer, map["StitchErrorContainer"])
        assertEquals(StitchLightTokens.OnErrorContainer, map["StitchOnErrorContainer"])
    }

    @Test
    fun challengeStitchGetters_inDarkTheme_returnsValidColorsMatchingDarkTokens() {
        var darkResults: List<StitchGetterResult> = emptyList()

        composeTestRule.setContent {
            SolarEaseTheme(themeMode = ThemeMode.DARK) {
                darkResults = collectAll33StitchGetters()
            }
        }
        composeTestRule.waitForIdle()

        assertEquals(33, darkResults.size)
        val map = darkResults.associate { it.name to it.color }

        // Verify non-null, specified, and fully opaque
        for (item in darkResults) {
            assertNotNull("Getter ${item.name} returned null in Dark Mode", item.color)
            assertTrue("Getter ${item.name} must be specified in Dark Mode", item.color.isSpecified)
            assertTrue("Getter ${item.name} must be opaque in Dark Mode", item.color.alpha >= 0.99f)
        }

        // Verify exact matches with StitchDarkTokens
        assertEquals(StitchDarkTokens.Primary, map["StitchPrimary"])
        assertEquals(StitchDarkTokens.PrimaryContainer, map["StitchPrimaryContainer"])
        assertEquals(StitchDarkTokens.PrimaryFixed, map["StitchPrimaryFixed"])
        assertEquals(StitchDarkTokens.PrimaryFixedDim, map["StitchPrimaryFixedDim"])
        assertEquals(StitchDarkTokens.OnPrimary, map["StitchOnPrimary"])
        assertEquals(StitchDarkTokens.OnPrimaryContainer, map["StitchOnPrimaryContainer"])
        assertEquals(StitchDarkTokens.OnPrimaryFixed, map["StitchOnPrimaryFixed"])
        assertEquals(StitchDarkTokens.OnPrimaryFixedVariant, map["StitchOnPrimaryFixedVariant"])
        assertEquals(StitchDarkTokens.Surface, map["StitchSurface"])
        assertEquals(StitchDarkTokens.Background, map["StitchBackground"])
        assertEquals(StitchDarkTokens.SurfaceDim, map["StitchSurfaceDim"])
        assertEquals(StitchDarkTokens.SurfaceBright, map["StitchSurfaceBright"])
        assertEquals(StitchDarkTokens.SurfaceContainerLowest, map["StitchSurfaceContainerLowest"])
        assertEquals(StitchDarkTokens.SurfaceContainerLowest, map["StitchSurfaceLowest"])
        assertEquals(StitchDarkTokens.SurfaceContainerLow, map["StitchSurfaceContainerLow"])
        assertEquals(StitchDarkTokens.SurfaceContainerLow, map["StitchSurfaceLow"])
        assertEquals(StitchDarkTokens.SurfaceContainer, map["StitchSurfaceContainer"])
        assertEquals(StitchDarkTokens.SurfaceContainerHigh, map["StitchSurfaceContainerHigh"])
        assertEquals(StitchDarkTokens.SurfaceContainerHigh, map["StitchSurfaceHigh"])
        assertEquals(StitchDarkTokens.SurfaceContainerHighest, map["StitchSurfaceContainerHighest"])
        assertEquals(StitchDarkTokens.OnSurface, map["StitchOnSurface"])
        assertEquals(StitchDarkTokens.OnSurfaceVariant, map["StitchOnSurfaceVariant"])
        assertEquals(StitchDarkTokens.InverseSurface, map["StitchInverseSurface"])
        assertEquals(StitchDarkTokens.InverseOnSurface, map["StitchInverseOnSurface"])
        assertEquals(StitchDarkTokens.Tertiary, map["StitchTertiary"])
        assertEquals(StitchDarkTokens.TertiaryContainer, map["StitchTertiaryContainer"])
        assertEquals(StitchDarkTokens.Secondary, map["StitchSecondary"])
        assertEquals(StitchDarkTokens.SecondaryContainer, map["StitchSecondaryContainer"])
        assertEquals(StitchDarkTokens.Outline, map["StitchOutline"])
        assertEquals(StitchDarkTokens.OutlineVariant, map["StitchOutlineVariant"])
        assertEquals(StitchDarkTokens.Error, map["StitchError"])
        assertEquals(StitchDarkTokens.ErrorContainer, map["StitchErrorContainer"])
        assertEquals(StitchDarkTokens.OnErrorContainer, map["StitchOnErrorContainer"])
    }

    @Test
    fun challengeStitchGetters_contrastRemediationDarkPrimaryContainerEliminates1_3To1Bug() {
        var darkPrimaryContainer: Color = Color.Unspecified
        var darkOnPrimaryContainer: Color = Color.Unspecified

        composeTestRule.setContent {
            SolarEaseTheme(themeMode = ThemeMode.DARK) {
                darkPrimaryContainer = StitchPrimaryContainer
                darkOnPrimaryContainer = StitchOnPrimaryContainer
            }
        }
        composeTestRule.waitForIdle()

        // Verify dark primary container is deep emerald #1E4E30, NOT #B0D49A
        assertEquals(Color(0xFF1E4E30), darkPrimaryContainer)
        assertEquals(Color(0xFFA6EDB4), darkOnPrimaryContainer)

        val contrastRatio = calculateContrastRatio(darkOnPrimaryContainer, darkPrimaryContainer)
        assertTrue(
            "Dark OnPrimaryContainer on PrimaryContainer must exceed 7.0:1 (actual: $contrastRatio:1)",
            contrastRatio >= 7.0
        )
    }

    @Test
    fun challengeStitchGetters_dynamicThemeSwitchingRecomposesAccurately() {
        var currentThemeMode by mutableStateOf(ThemeMode.LIGHT)
        var sampledPrimary: Color = Color.Unspecified
        var sampledSurface: Color = Color.Unspecified

        composeTestRule.setContent {
            SolarEaseTheme(themeMode = currentThemeMode) {
                sampledPrimary = StitchPrimary
                sampledSurface = StitchSurface
            }
        }
        composeTestRule.waitForIdle()

        // Verify initial Light values
        assertEquals(StitchLightTokens.Primary, sampledPrimary)
        assertEquals(StitchLightTokens.Surface, sampledSurface)

        // Switch to Dark
        currentThemeMode = ThemeMode.DARK
        composeTestRule.waitForIdle()

        // Verify updated Dark values
        assertEquals(StitchDarkTokens.Primary, sampledPrimary)
        assertEquals(StitchDarkTokens.Surface, sampledSurface)
        assertFalse("Primary color must adapt across theme switch", StitchLightTokens.Primary == sampledPrimary)
        assertFalse("Surface color must adapt across theme switch", StitchLightTokens.Surface == sampledSurface)
    }

    @Test
    fun challengeStitchGetters_calledOutsideSolarEaseTheme_doesNotCrash() {
        var primary: Color = Color.Unspecified
        var surfaceDim: Color = Color.Unspecified

        composeTestRule.setContent {
            // Calling Stitch* without explicit SolarEaseTheme wrapper uses defaults
            primary = StitchPrimary
            surfaceDim = StitchSurfaceDim
        }
        composeTestRule.waitForIdle()

        assertTrue("StitchPrimary should resolve without crashing", primary.isSpecified)
        assertTrue("StitchSurfaceDim should resolve without crashing", surfaceDim.isSpecified)
    }

    @Test
    fun challengeFeatureScreenCallerCompatibility_commonComposablesExecuteWithoutError() {
        var themeMode by mutableStateOf(ThemeMode.LIGHT)
        composeTestRule.setContent {
            SolarEaseTheme(themeMode = themeMode) {
                androidx.compose.foundation.layout.Column {
                    StitchStepProgressBar(
                        currentStep = 2,
                        totalSteps = 6,
                        stepTitle = "Solar Sizing"
                    )
                    StitchSelectableCard(
                        selected = true,
                        title = "Tier 1 Panel",
                        subtitle = "540W Mono PERC",
                        onClick = {}
                    )
                    SolarEaseBottomNavigation(
                        currentTab = SolarEaseNavTab.HOME,
                        onNavigateToHome = {},
                        onNavigateToRecommends = {},
                        onNavigateToProfile = {}
                    )
                }
            }
        }
        composeTestRule.waitForIdle()

        // Switch theme mode dynamically to DARK
        themeMode = ThemeMode.DARK
        composeTestRule.waitForIdle()
    }
}
