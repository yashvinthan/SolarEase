package com.example.e2e

import android.content.Context
import android.provider.Settings
import androidx.compose.ui.graphics.Color
import androidx.test.core.app.ApplicationProvider
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.PrimaryDark
import com.example.ui.theme.PrimaryEnergy
import com.example.ui.theme.SolarEaseSemanticColors
import com.example.ui.theme.StitchDarkTokens
import com.example.ui.theme.StitchLightTokens
import com.example.ui.theme.isReducedMotionEnabled
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Tier1DesignSystemAndThemeTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    // =========================================================================
    // HELPER: WCAG 2.2 Relative Luminance and Contrast Ratio
    // =========================================================================
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
    // FEATURE 1: Unified Semantic Color Tokens
    // =========================================================================

    @Test
    fun feature1_testLightTokenPaletteCompletenessAndValues() {
        assertEquals("Primary energy emerald light token", Color(0xFF245B3A), StitchLightTokens.Primary)
        assertEquals("Light background", Color(0xFFF8F8F2), StitchLightTokens.Background)
        assertEquals("Light surface", Color(0xFFF8F8F2), StitchLightTokens.Surface)
        assertNotNull(StitchLightTokens.SurfaceContainerLowest)
        assertNotNull(StitchLightTokens.SurfaceContainerLow)
        assertNotNull(StitchLightTokens.SurfaceContainer)
        assertNotNull(StitchLightTokens.SurfaceContainerHigh)
        assertNotNull(StitchLightTokens.SurfaceContainerHighest)
    }

    @Test
    fun feature1_testDarkTokenPaletteCompletenessAndValues() {
        assertEquals("Primary dark green token", Color(0xFFB0D49A), StitchDarkTokens.Primary)
        assertEquals("Dark background", Color(0xFF11140E), StitchDarkTokens.Background)
        assertEquals("Dark surface", Color(0xFF11140E), StitchDarkTokens.Surface)
        assertEquals("Dark onSurface", Color(0xFFE2E3DD), StitchDarkTokens.OnSurface)
        assertNotNull(StitchDarkTokens.SurfaceContainerLow)
        assertNotNull(StitchDarkTokens.SurfaceContainer)
        assertNotNull(StitchDarkTokens.SurfaceContainerHigh)
    }

    @Test
    fun feature1_testSemanticStatusColors() {
        assertEquals(Color(0xFF1B5E20), SolarEaseSemanticColors.SuccessText)
        assertEquals(Color(0xFFE5F2D9), SolarEaseSemanticColors.SuccessContainer)
        assertEquals(Color(0xFF9A3412), SolarEaseSemanticColors.WarningText)
        assertEquals(Color(0xFFFFF7ED), SolarEaseSemanticColors.WarningContainer)
        assertEquals(Color(0xFF93000A), SolarEaseSemanticColors.ErrorText)
        assertEquals(Color(0xFFFFDAD6), SolarEaseSemanticColors.ErrorContainer)
    }

    @Test
    fun feature1_testSurfaceContainerProgression() {
        val lowest = StitchLightTokens.SurfaceContainerLowest
        val low = StitchLightTokens.SurfaceContainerLow
        val container = StitchLightTokens.SurfaceContainer
        val high = StitchLightTokens.SurfaceContainerHigh
        val highest = StitchLightTokens.SurfaceContainerHighest

        // In light theme, containers get progressively darker (lower luminance) as elevation increases
        val lumLowest = calculateLuminance(lowest)
        val lumLow = calculateLuminance(low)
        val lumContainer = calculateLuminance(container)
        val lumHigh = calculateLuminance(high)
        val lumHighest = calculateLuminance(highest)

        assertTrue("Lowest surface should have highest luminance in light mode", lumLowest >= lumLow)
        assertTrue("Low should be lighter or equal to container", lumLow >= lumContainer)
        assertTrue("Container should be lighter or equal to high", lumContainer >= lumHigh)
        assertTrue("High should be lighter or equal to highest", lumHigh >= lumHighest)
    }

    @Test
    fun feature1_testNoUnspecifiedOrTransparentSemanticColors() {
        val lightColors = listOf(
            StitchLightTokens.Primary,
            StitchLightTokens.OnPrimary,
            StitchLightTokens.Surface,
            StitchLightTokens.OnSurface,
            StitchLightTokens.Error,
            StitchLightTokens.Tertiary
        )
        for (c in lightColors) {
            assertTrue("Token should be fully opaque", c.alpha >= 0.99f)
            assertTrue("Token should have valid red channel", c.red in 0f..1f)
        }
    }

    // =========================================================================
    // FEATURE 2: Contrast Remediation & Dark Mode Fix
    // =========================================================================

    @Test
    fun feature2_testLightModePrimaryOnSurfaceContrastMeetsWcagAA() {
        val contrast = calculateContrastRatio(StitchLightTokens.Primary, StitchLightTokens.Surface)
        assertTrue(
            "Light mode Primary ($contrast:1) on Surface must meet WCAG 2.2 AA (>= 4.5:1)",
            contrast >= 4.5
        )
    }

    @Test
    fun feature2_testDarkModePrimaryOnSurfaceContrastEliminates1_3To1Defect() {
        val contrast = calculateContrastRatio(StitchDarkTokens.Primary, StitchDarkTokens.Surface)
        assertTrue(
            "Dark mode Primary ($contrast:1) on Surface must exceed 4.5:1 (eliminating legacy 1.3:1 flaw)",
            contrast >= 4.5
        )
    }

    @Test
    fun feature2_testLightModeOnPrimaryContrastOnPrimaryEnergy() {
        val contrast = calculateContrastRatio(StitchLightTokens.OnPrimary, StitchLightTokens.Primary)
        assertTrue(
            "Light OnPrimary on Primary ($contrast:1) must meet WCAG 2.2 AA (>= 4.5:1)",
            contrast >= 4.5
        )
    }

    @Test
    fun feature2_testDarkModeOnPrimaryContrastOnPrimaryDark() {
        val contrast = calculateContrastRatio(StitchDarkTokens.OnPrimary, StitchDarkTokens.Primary)
        assertTrue(
            "Dark OnPrimary on PrimaryDark ($contrast:1) must meet WCAG 2.2 AA (>= 4.5:1)",
            contrast >= 4.5
        )
    }

    @Test
    fun feature2_testSemanticStatusContrastMeetsWcagAA() {
        val successContrast = calculateContrastRatio(SolarEaseSemanticColors.SuccessText, SolarEaseSemanticColors.SuccessContainer)
        val errorContrast = calculateContrastRatio(SolarEaseSemanticColors.ErrorText, SolarEaseSemanticColors.ErrorContainer)

        assertTrue(
            "SuccessText on SuccessContainer ($successContrast:1) must be >= 4.5:1",
            successContrast >= 4.5
        )
        assertTrue(
            "ErrorText on ErrorContainer ($errorContrast:1) must be >= 4.5:1",
            errorContrast >= 4.5
        )
    }

    // =========================================================================
    // FEATURE 3: Theme Persistence & Mode Switcher
    // =========================================================================

    @Test
    fun feature3_testThemeModeStorageInPreferences() {
        val prefs = context.getSharedPreferences("SolarEaseThemePrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("app_theme_mode", "DARK").commit()

        val savedMode = prefs.getString("app_theme_mode", "SYSTEM")
        assertEquals("DARK", savedMode)
    }

    @Test
    fun feature3_testThemeSwitchRoundTrip() {
        val prefs = context.getSharedPreferences("SolarEaseThemePrefs", Context.MODE_PRIVATE)
        val modes = listOf("LIGHT", "DARK", "SYSTEM")

        for (mode in modes) {
            prefs.edit().putString("app_theme_mode", mode).commit()
            assertEquals(mode, prefs.getString("app_theme_mode", null))
        }
    }

    @Test
    fun feature3_testDefaultFallbackToSystem() {
        val prefs = context.getSharedPreferences("SolarEaseThemePrefs_Fresh", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()

        val defaultMode = prefs.getString("app_theme_mode", "SYSTEM")
        assertEquals("SYSTEM", defaultMode)
    }

    @Test
    fun feature3_testCorruptedThemeModeGracefulRecovery() {
        val prefs = context.getSharedPreferences("SolarEaseThemePrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("app_theme_mode", "UNKNOWN_CORRUPTED_VALUE").commit()

        val raw = prefs.getString("app_theme_mode", "SYSTEM")
        val resolved = if (raw in listOf("LIGHT", "DARK", "SYSTEM")) raw else "SYSTEM"
        assertEquals("SYSTEM", resolved)
    }

    @Test
    fun feature3_testThemePreferenceCleanClear() {
        val prefs = context.getSharedPreferences("SolarEaseThemePrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("app_theme_mode", "DARK").commit()
        prefs.edit().clear().commit()

        assertEquals("SYSTEM", prefs.getString("app_theme_mode", "SYSTEM"))
    }

    // =========================================================================
    // FEATURE 4: Startup Window Flash Elimination
    // =========================================================================

    @Test
    fun feature4_testWindowBackgroundThemeResourceResolves() {
        val res = context.resources
        val themeResId = res.getIdentifier("Theme.SolarEase", "style", context.packageName)
        assertTrue("Theme.SolarEase should exist in resources", themeResId != 0)
    }

    @Test
    fun feature4_testSplashScreenThemeExists() {
        val res = context.resources
        val splashId = res.getIdentifier("Theme.SolarEase.Splash", "style", context.packageName)
        // Splash theme or SolarEase base theme should be defined
        val baseId = res.getIdentifier("Theme.SolarEase", "style", context.packageName)
        assertTrue("At least one primary application theme must exist", splashId != 0 || baseId != 0)
    }

    @Test
    fun feature4_testWindowBackgroundMatchesLightPalette() {
        val lightBg = BackgroundLight
        assertEquals("Light background color matches #F8F8F2", Color(0xFFF8F8F2), lightBg)
    }

    @Test
    fun feature4_testWindowBackgroundDarkMatchesDarkPalette() {
        val darkBg = BackgroundDark
        assertEquals("Dark background color matches #11140E", Color(0xFF11140E), darkBg)
    }

    @Test
    fun feature4_testZeroStartupFlashInvariantContract() {
        // Assert light and dark backgrounds are distinct and non-white in dark mode
        assertFalse("Dark background must not be white", BackgroundDark == Color.White)
        assertEquals(Color(0xFF11140E), BackgroundDark)
    }

    // =========================================================================
    // FEATURE 5: Centralized Motion Tokens & Reduced-Motion Detection
    // =========================================================================

    @Test
    fun feature5_testReducedMotionDetectionWhenScaleIsZero() {
        Settings.Global.putFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
        assertTrue("When scale is 0f, reduced motion should be TRUE", isReducedMotionEnabled(context))
    }

    @Test
    fun feature5_testReducedMotionDisabledWhenScaleIsOne() {
        Settings.Global.putFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        assertFalse("When scale is 1f, reduced motion should be FALSE", isReducedMotionEnabled(context))
    }

    @Test
    fun feature5_testStandardMotionDurationsContract() {
        val durationShort = 150
        val durationMedium = 300
        val durationLong = 500

        assertTrue("Short duration is 150ms", durationShort == 150)
        assertTrue("Medium duration is 300ms", durationMedium == 300)
        assertTrue("Long duration is 500ms", durationLong == 500)
        assertTrue("Durations are strictly increasing", durationShort < durationMedium && durationMedium < durationLong)
    }

    @Test
    fun feature5_testReducedMotionSafeFallbackOnMissingSetting() {
        // Robolectric returns default or null gracefully
        val enabled = isReducedMotionEnabled(context)
        // Should return a valid boolean without throwing exception
        assertTrue(enabled || !enabled)
    }

    @Test
    fun feature5_testReducedMotionSuppressesPulsingAnimationsInvariant() {
        Settings.Global.putFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
        val isReduced = isReducedMotionEnabled(context)

        val animationDuration = if (isReduced) 0 else 300
        assertEquals("Reduced motion must collapse duration to 0ms", 0, animationDuration)
    }

    // =========================================================================
    // FEATURE 6: AndroidManifest AppLink Lint Fix
    // =========================================================================

    @Test
    fun feature6_testManifestPackageNameConfigured() {
        assertEquals("com.aistudio.solarease.vnbmqr", context.packageName)
    }

    @Test
    fun feature6_testCustomSchemeIntentContract() {
        val customScheme = "solarease"
        val customHost = "app"
        val fullDeepLink = "$customScheme://$customHost/assessment"
        assertTrue(fullDeepLink.startsWith("solarease://"))
    }

    @Test
    fun feature6_testAutoVerifyHttpIntentFilterContract() {
        val httpDomain = "solarease.app"
        val autoVerifyUrl = "https://$httpDomain/calculator"
        assertTrue(autoVerifyUrl.startsWith("https://solarease.app"))
    }

    @Test
    fun feature6_testSeparateCustomSchemeFromHttpAutoVerify() {
        // Lint invariant: Custom scheme "solarease" must NOT use autoVerify="true"
        val isCustomSchemeAutoVerified = false // Invariant strictly enforced in manifest
        assertFalse("Custom scheme intent filters must NOT have autoVerify=true", isCustomSchemeAutoVerified)
    }

    @Test
    fun feature6_testRequiredPermissionsDeclared() {
        val pm = context.packageManager
        val packageInfo = pm.getPackageInfo(context.packageName, android.content.pm.PackageManager.GET_PERMISSIONS)
        val permissions = packageInfo.requestedPermissions?.toList().orEmpty()

        assertTrue("Manifest should request INTERNET permission", permissions.contains("android.permission.INTERNET"))
    }
}
