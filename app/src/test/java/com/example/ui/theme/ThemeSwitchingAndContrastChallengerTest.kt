package com.example.ui.theme

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.test.core.app.ApplicationProvider
import com.example.core.session.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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

/**
 * Empirical Adversarial Challenger Test Suite for Milestone M1.
 * Focuses on:
 * 1. Empirically calculating contrast ratios for all Light & Dark pairings.
 * 2. Verifying dark mode container contrast is >= 7.0:1 and eliminates the 1.3:1 flaw.
 * 3. Verifying zero instances of #B0D49A as primary container.
 * 4. Stress testing rapid theme switching (SYSTEM -> LIGHT -> DARK -> SYSTEM) sequentially and concurrently.
 * 5. Robustness against corrupted preference states.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ThemeSwitchingAndContrastChallengerTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        // Reset theme to SYSTEM
        UserSession.setThemeMode(context, ThemeMode.SYSTEM)
    }

    // =========================================================================
    // WCAG 2.2 Relative Luminance and Contrast Ratio Helpers
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
    // 1. EMPIRICAL CONTRAST CHALLENGE & CONTAINER VERIFICATION
    // =========================================================================

    @Test
    fun challengeDarkModePrimaryContainer_exceeds7To1AndEliminates1_3To1Defect() {
        val container = StitchDarkTokens.PrimaryContainer
        val onContainer = StitchDarkTokens.OnPrimaryContainer

        // Verify container is NOT the legacy PrimaryDark (#B0D49A)
        assertNotEquals(
            "PrimaryContainer in dark mode must not be hardcoded to PrimaryDark (#B0D49A)",
            Color(0xFFB0D49A),
            container
        )
        assertEquals("PrimaryContainerDark must match #1E4E30", Color(0xFF1E4E30), container)
        assertEquals("OnPrimaryContainerDark must match #A6EDB4", Color(0xFFA6EDB4), onContainer)

        val ratio = calculateContrastRatio(container, onContainer)
        assertTrue(
            "PrimaryContainerDark contrast ($ratio:1) MUST exceed 7.0:1 (AAA standard, eliminating 1.3:1 bug)",
            ratio >= 7.0
        )
    }

    @Test
    fun challengeDarkModeSecondaryAndTertiaryContainers_exceedWcagAA() {
        val secRatio = calculateContrastRatio(SecondaryContainerDark, OnSecondaryContainerDark)
        assertTrue(
            "SecondaryContainerDark contrast ($secRatio:1) must be >= 6.5:1",
            secRatio >= 6.5
        )

        val tertRatio = calculateContrastRatio(TertiaryContainerDark, OnTertiaryContainerDark)
        assertTrue(
            "TertiaryContainerDark contrast ($tertRatio:1) must be >= 6.5:1",
            tertRatio >= 6.5
        )

        val errRatio = calculateContrastRatio(ErrorContainerDark, OnErrorContainerDark)
        assertTrue(
            "ErrorContainerDark contrast ($errRatio:1) must be >= 4.5:1",
            errRatio >= 4.5
        )
    }

    @Test
    fun challengeLightModeContainers_exceed7To1() {
        val priRatio = calculateContrastRatio(PrimaryContainerLight, OnPrimaryContainerLight)
        assertTrue("PrimaryContainerLight ($priRatio:1) must exceed 7.0:1", priRatio >= 7.0)

        val secRatio = calculateContrastRatio(SecondaryContainerLight, OnSecondaryContainerLight)
        assertTrue("SecondaryContainerLight ($secRatio:1) must exceed 7.0:1", secRatio >= 7.0)

        val tertRatio = calculateContrastRatio(TertiaryContainerLight, OnTertiaryContainerLight)
        assertTrue("TertiaryContainerLight ($tertRatio:1) must exceed 7.0:1", tertRatio >= 7.0)

        val errRatio = calculateContrastRatio(ErrorContainerLight, OnErrorContainerLight)
        assertTrue("ErrorContainerLight ($errRatio:1) must exceed 7.0:1", errRatio >= 7.0)
    }

    @Test
    fun challengeOutlinesOnSurfaces_satisfiesWcagNonTextBoundaryContrast() {
        // Light mode outlines
        val outlineLightOnSurface = calculateContrastRatio(OutlineLight, SurfaceLight)
        val outlineLightOnHighest = calculateContrastRatio(OutlineLight, SurfaceContainerHighestLight)
        assertTrue("OutlineLight on SurfaceLight ($outlineLightOnSurface:1) must be >= 3.0:1", outlineLightOnSurface >= 3.0)
        assertTrue("OutlineLight on SurfaceContainerHighestLight ($outlineLightOnHighest:1) must be >= 3.0:1", outlineLightOnHighest >= 3.0)

        // Dark mode outlines
        val outlineDarkOnSurface = calculateContrastRatio(OutlineDark, SurfaceDark)
        val outlineDarkOnHighest = calculateContrastRatio(OutlineDark, SurfaceContainerHighestDark)
        assertTrue("OutlineDark on SurfaceDark ($outlineDarkOnSurface:1) must be >= 3.0:1", outlineDarkOnSurface >= 3.0)
        assertTrue("OutlineDark on SurfaceContainerHighestDark ($outlineDarkOnHighest:1) must be >= 3.0:1", outlineDarkOnHighest >= 3.0)
    }

    // =========================================================================
    // 2. ADVERSARIAL RAPID THEME SWITCHING STRESS TESTS
    // =========================================================================

    @Test
    fun challengeSequentialRapidThemeSwitching_1000Cycles() {
        val modes = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)

        for (i in 1..1000) {
            val targetMode = modes[i % modes.size]
            UserSession.setThemeMode(context, targetMode)

            // Verify memory state matches immediately
            assertEquals("Cycle $i memory state mismatch", targetMode, UserSession.themeMode)
            assertEquals("Cycle $i flow state mismatch", targetMode, UserSession.themeModeFlow.value)
        }

        // Verify final persistent state
        val finalMode = modes[1000 % modes.size]
        assertEquals(finalMode, UserSession.getSavedThemeMode(context))
    }

    @Test
    fun challengeConcurrentRapidThemeSwitching_multiThreadedStress() = runBlocking {
        val coroutinesCount = 10
        val switchesPerCoroutine = 100
        val modes = listOf(ThemeMode.LIGHT, ThemeMode.DARK, ThemeMode.SYSTEM)

        val deferreds = (0 until coroutinesCount).map { coroutineId ->
            async(Dispatchers.Default) {
                for (step in 0 until switchesPerCoroutine) {
                    val mode = modes[(coroutineId + step) % modes.size]
                    UserSession.setThemeMode(context, mode)
                }
            }
        }

        deferreds.awaitAll()

        // Verify the system didn't deadlock and state is a valid ThemeMode
        val finalState = UserSession.themeMode
        assertTrue("Final state must be valid enum value", finalState in modes)
        val flowState = UserSession.themeModeFlow.first()
        assertEquals("Flow state must equal property state", finalState, flowState)
    }

    @Test
    fun challengeThemeModeFromString_handlesAllCorruptedInputsGracefully() {
        // Valid inputs (case-insensitive)
        assertEquals(ThemeMode.LIGHT, ThemeMode.fromString("LIGHT"))
        assertEquals(ThemeMode.LIGHT, ThemeMode.fromString("light"))
        assertEquals(ThemeMode.LIGHT, ThemeMode.fromString("Light"))
        assertEquals(ThemeMode.DARK, ThemeMode.fromString("DARK"))
        assertEquals(ThemeMode.DARK, ThemeMode.fromString("dark"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromString("SYSTEM"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromString("system"))

        // Malformed / corrupted inputs
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromString(null))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromString(""))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromString("   "))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromString("UNKNOWN_VALUE"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromString("AUTO"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromString("12345"))
    }

    @Test
    fun challengePreferenceCorruptionRecovery() {
        val appPrefs = context.getSharedPreferences("SolarEaseAppPrefs", Context.MODE_PRIVATE)
        val themePrefs = context.getSharedPreferences("SolarEaseThemePrefs", Context.MODE_PRIVATE)

        // Corrupt both preference stores
        appPrefs.edit().putString("theme_mode", "CORRUPTED_GARBAGE").apply()
        themePrefs.edit().putString("app_theme_mode", "ANOTHER_CORRUPTED_VALUE").apply()

        // Should recover gracefully without exception to SYSTEM
        val recovered = UserSession.getSavedThemeMode(context)
        assertEquals("Corrupted prefs must recover to ThemeMode.SYSTEM", ThemeMode.SYSTEM, recovered)
    }

    @Test
    fun challengeSharedPreferencesDualWriteSynchronization() {
        UserSession.setThemeMode(context, ThemeMode.DARK)

        val appPrefs = context.getSharedPreferences("SolarEaseAppPrefs", Context.MODE_PRIVATE)
        val themePrefs = context.getSharedPreferences("SolarEaseThemePrefs", Context.MODE_PRIVATE)

        assertEquals("DARK", appPrefs.getString("theme_mode", null))
        assertEquals("DARK", themePrefs.getString("app_theme_mode", null))

        UserSession.setThemeMode(context, ThemeMode.LIGHT)
        assertEquals("LIGHT", appPrefs.getString("theme_mode", null))
        assertEquals("LIGHT", themePrefs.getString("app_theme_mode", null))
    }
}
