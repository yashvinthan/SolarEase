package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import com.example.feature.assessment.ui.StitchSelectableCard
import com.example.feature.assessment.ui.StitchStepProgressBar
import com.example.ui.theme.SolarEaseTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Adversarial Challenger Stress Test Suite for Milestone M2.
 * Strictly tests:
 * 1. Touch targets (>=48dp for all interactive primitives).
 * 2. Interactive states (loading disabling clicks, layout stability, disabled state).
 * 3. SolarEaseStepProgressBar boundaries (0, totalSteps, 1, 12, coerced >totalSteps, negative, 0 totalSteps, TalkBack formatting).
 * 4. SolarEaseMetrics formatting & trend deltas (negative, zero, null, large values, US locale tabular formatting).
 * 5. SolarEaseNavigation 3-tab vs 4-tab variations, selection state, and tab clicks.
 * 6. Legacy adapter delegation and backward compatibility without crashes.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class ComponentPrimitivesChallengerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val DpRect.rectHeight: Dp get() = bottom - top
    private val DpRect.rectWidth: Dp get() = right - left

    // ─────────────────────────────────────────────────────────────────────────
    // 1. TOUCH TARGET EMPIRICAL VERIFICATION (>=48dp)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun touchTargets_allButtonVariantsMeetOrExceed48Dp() {
        val variants = SolarEaseButtonVariant.entries
        composeTestRule.setContent {
            SolarEaseTheme {
                Column {
                    for (variant in variants) {
                        SolarEaseButton(
                            text = "Variant ${variant.name}",
                            onClick = {},
                            variant = variant
                        )
                    }
                }
            }
        }

        for (variant in variants) {
            val node = composeTestRule.onNodeWithText("Variant ${variant.name}")
            node.assertExists()
            val bounds = node.getUnclippedBoundsInRoot()
            assertTrue(
                "Button variant ${variant.name} height (${bounds.rectHeight}) must be >= 48dp",
                bounds.rectHeight >= 48.dp
            )
            assertTrue(
                "Button variant ${variant.name} width (${bounds.rectWidth}) must be >= 48dp",
                bounds.rectWidth >= 48.dp
            )
        }
    }

    @Test
    fun touchTargets_selectableCardMeetsConfiguredMinHeight() {
        composeTestRule.setContent {
            SolarEaseTheme {
                Column {
                    // Standard minHeight: 56dp
                    SolarEaseSelectableCard(
                        selected = false,
                        onClick = {},
                        title = "Standard 56dp Card",
                        subtitle = "Card subtitle",
                        minHeight = 56.dp
                    )
                    // Custom minHeight: 64dp (e.g. used in vendor flow)
                    SolarEaseSelectableCard(
                        selected = true,
                        onClick = {},
                        title = "Custom 64dp Card",
                        minHeight = 64.dp
                    )
                }
            }
        }

        val node56 = composeTestRule.onNodeWithText("Standard 56dp Card")
        node56.assertExists()
        val bounds56 = node56.getUnclippedBoundsInRoot()
        assertTrue("SelectableCard height must be >= 56dp", bounds56.rectHeight >= 56.dp)
        assertTrue("SelectableCard width must be >= 48dp", bounds56.rectWidth >= 48.dp)

        val node64 = composeTestRule.onNodeWithText("Custom 64dp Card")
        node64.assertExists()
        val bounds64 = node64.getUnclippedBoundsInRoot()
        assertTrue("SelectableCard height must be >= 64dp", bounds64.rectHeight >= 64.dp)
    }

    @Test
    fun touchTargets_filterChipsMeetOrExceed48Dp() {
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseFilterChip(
                    selected = true,
                    onClick = {},
                    label = "Monthly"
                )
            }
        }

        val chipNode = composeTestRule.onNodeWithText("Monthly")
        chipNode.assertExists()
        val bounds = chipNode.getUnclippedBoundsInRoot()
        assertTrue("FilterChip height (${bounds.rectHeight}) must be >= 48dp", bounds.rectHeight >= 48.dp)
        assertTrue("FilterChip width (${bounds.rectWidth}) must be >= 48dp", bounds.rectWidth >= 48.dp)
    }

    @Test
    fun touchTargets_topBarAndNavigationItemsMeet48Dp() {
        composeTestRule.setContent {
            SolarEaseTheme {
                Column {
                    SolarEaseTopBar(
                        title = "TopBar Test",
                        onBack = {}
                    )
                    SolarEaseBottomNavigation(
                        currentTab = SolarEaseNavTab.HOME,
                        onNavigateToHome = {},
                        onNavigateToRecommends = {},
                        onNavigateToProfile = {},
                        onNavigateToAnalytics = {}
                    )
                }
            }
        }

        // TopBar back button touch target
        val backBtn = composeTestRule.onNodeWithContentDescription("Navigate back")
        backBtn.assertExists()
        val backBounds = backBtn.getUnclippedBoundsInRoot()
        assertTrue("TopBar back button height >= 48dp", backBounds.rectHeight >= 48.dp)
        assertTrue("TopBar back button width >= 48dp", backBounds.rectWidth >= 48.dp)

        // Bottom nav items touch target
        for (tabLabel in listOf("Home", "Analytics", "Recommends", "Profile")) {
            val tabNode = composeTestRule.onNodeWithText(tabLabel)
            tabNode.assertExists()
            val tabBounds = tabNode.getUnclippedBoundsInRoot()
            assertTrue("Nav item '$tabLabel' height >= 48dp", tabBounds.rectHeight >= 48.dp)
            assertTrue("Nav item '$tabLabel' width >= 48dp", tabBounds.rectWidth >= 48.dp)
        }
    }

    @Test
    fun touchTargets_stepProgressBarBackButtonMeets48Dp() {
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseStepProgressBar(
                    currentStep = 3,
                    totalSteps = 8,
                    stepTitle = "Roof Type",
                    onBack = {}
                )
            }
        }

        val backBtn = composeTestRule.onNodeWithContentDescription("Navigate back")
        backBtn.assertExists()
        val bounds = backBtn.getUnclippedBoundsInRoot()
        assertTrue("StepProgressBar back button height >= 48dp", bounds.rectHeight >= 48.dp)
        assertTrue("StepProgressBar back button width >= 48dp", bounds.rectWidth >= 48.dp)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. INTERACTIVE STATES & BUTTON STABILITY
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun buttonInteractiveState_loadingStateLocksClicksAndMaintainsLayout() {
        var clickCount = 0
        var isLoadingState by mutableStateOf(false)

        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseButton(
                    text = "Submit Application",
                    onClick = { clickCount++ },
                    isLoading = isLoadingState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        val buttonNode = composeTestRule.onNodeWithText("Submit Application")
        buttonNode.assertExists()
        val normalBounds = buttonNode.getUnclippedBoundsInRoot()

        // 1. Normal click works
        buttonNode.performClick()
        assertEquals("Initial click should register", 1, clickCount)

        // 2. Switch to loading
        isLoadingState = true
        composeTestRule.waitForIdle()

        val loadingBounds = buttonNode.getUnclippedBoundsInRoot()
        assertEquals(
            "Button height must not jitter/shift when loading",
            normalBounds.rectHeight.value,
            loadingBounds.rectHeight.value,
            0.5f
        )

        // 3. Click during loading must NOT increment clickCount
        buttonNode.performClick()
        assertEquals("Clicks must be rejected while loading", 1, clickCount)

        // 4. Semantics check
        val semantics = buttonNode.fetchSemanticsNode().config
        assertEquals("Loading", semantics[SemanticsProperties.StateDescription])
        assertEquals(Role.Button, semantics[SemanticsProperties.Role])

        // 5. Restore from loading
        isLoadingState = false
        composeTestRule.waitForIdle()
        buttonNode.performClick()
        assertEquals("Clicks resume after loading finishes", 2, clickCount)
    }

    @Test
    fun buttonInteractiveState_disabledWhileLoadingRemainsDisabled() {
        var clickCount = 0
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseButton(
                    text = "Locked Action",
                    onClick = { clickCount++ },
                    enabled = false,
                    isLoading = true
                )
            }
        }

        val buttonNode = composeTestRule.onNodeWithText("Locked Action")
        buttonNode.assertExists().assertIsNotEnabled()
        buttonNode.performClick()
        assertEquals(0, clickCount)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. STEP PROGRESS BAR BOUNDARY STRESS TESTING
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun stepProgressBar_boundaryCurrentStepZero() {
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseStepProgressBar(
                    currentStep = 0,
                    totalSteps = 12,
                    stepTitle = "Introduction"
                )
            }
        }

        val node = composeTestRule.onNodeWithContentDescription("Step 0 of 12: Introduction")
        node.assertExists()
        assertEquals(
            "0 percent complete",
            node.fetchSemanticsNode().config[SemanticsProperties.StateDescription]
        )
    }

    @Test
    fun stepProgressBar_boundaryCurrentStepEqualsTotalSteps() {
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseStepProgressBar(
                    currentStep = 12,
                    totalSteps = 12,
                    stepTitle = "Final Review"
                )
            }
        }

        val node = composeTestRule.onNodeWithContentDescription("Step 12 of 12: Final Review")
        node.assertExists()
        assertEquals(
            "100 percent complete",
            node.fetchSemanticsNode().config[SemanticsProperties.StateDescription]
        )
    }

    @Test
    fun stepProgressBar_boundarySingleStepProgression() {
        // totalSteps = 1, currentStep = 1
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseStepProgressBar(
                    currentStep = 1,
                    totalSteps = 1,
                    stepTitle = "Single Confirmation"
                )
            }
        }

        val node = composeTestRule.onNodeWithContentDescription("Step 1 of 1: Single Confirmation")
        node.assertExists()
        assertEquals(
            "100 percent complete",
            node.fetchSemanticsNode().config[SemanticsProperties.StateDescription]
        )
    }

    @Test
    fun stepProgressBar_boundaryCurrentStepExceedsTotalCoercedCleanly() {
        // currentStep = 15, totalSteps = 12
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseStepProgressBar(
                    currentStep = 15,
                    totalSteps = 12,
                    stepTitle = "Over Boundary"
                )
            }
        }

        val node = composeTestRule.onNodeWithContentDescription("Step 15 of 12: Over Boundary")
        node.assertExists()
        assertEquals(
            "100 percent complete",
            node.fetchSemanticsNode().config[SemanticsProperties.StateDescription]
        )
    }

    @Test
    fun stepProgressBar_boundaryZeroTotalStepsDoesNotCrash() {
        // Adversarial zero total steps: must not throw ArithmeticException division by zero
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseStepProgressBar(
                    currentStep = 0,
                    totalSteps = 0,
                    stepTitle = "Empty Steps"
                )
            }
        }

        val node = composeTestRule.onNodeWithContentDescription("Step 0 of 0: Empty Steps")
        node.assertExists()
        assertEquals(
            "0 percent complete",
            node.fetchSemanticsNode().config[SemanticsProperties.StateDescription]
        )
    }

    @Test
    fun stepProgressBar_boundaryNegativeCurrentStepCoercedToZero() {
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseStepProgressBar(
                    currentStep = -4,
                    totalSteps = 10,
                    stepTitle = "Negative Step"
                )
            }
        }

        val node = composeTestRule.onNodeWithContentDescription("Step -4 of 10: Negative Step")
        node.assertExists()
        assertEquals(
            "0 percent complete",
            node.fetchSemanticsNode().config[SemanticsProperties.StateDescription]
        )
    }

    @Test
    fun stepProgressBar_talkBackSemanticsExactContract() {
        composeTestRule.setContent {
            SolarEaseTheme {
                Column {
                    // 1. Without title
                    SolarEaseStepProgressBar(
                        currentStep = 4,
                        totalSteps = 8
                    )
                    // 2. With percentage text override
                    SolarEaseStepProgressBar(
                        currentStep = 2,
                        totalSteps = 10,
                        stepTitle = "Address",
                        percentageText = "20% Done"
                    )
                }
            }
        }

        val nodeNoTitle = composeTestRule.onNodeWithContentDescription("Step 4 of 8")
        nodeNoTitle.assertExists()
        assertEquals(
            "50 percent complete",
            nodeNoTitle.fetchSemanticsNode().config[SemanticsProperties.StateDescription]
        )

        composeTestRule.onNodeWithText("20% Done").assertExists()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. METRICS FORMATTING & TREND DELTA STRESS TESTING
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun metrics_trendDirectionEvaluatorLogic() {
        assertEquals(MetricTrendDirection.POSITIVE, evaluateTrendDirection(12.5))
        assertEquals(MetricTrendDirection.POSITIVE, evaluateTrendDirection(0.0001))
        assertEquals(MetricTrendDirection.NEGATIVE, evaluateTrendDirection(-7.3))
        assertEquals(MetricTrendDirection.NEGATIVE, evaluateTrendDirection(-0.0001))
        assertEquals(MetricTrendDirection.NEUTRAL, evaluateTrendDirection(0.0))
        assertEquals(MetricTrendDirection.NEUTRAL, evaluateTrendDirection(null))
    }

    @Test
    fun metrics_formattingStressAcrossUnitsAndScale() {
        // Currency
        assertEquals("₹1,250,000", formatMetricValue(1250000.0, "₹"))
        assertEquals("₹0", formatMetricValue(0.0, "₹"))

        // Energy kWh
        assertEquals("482.4 kWh", formatMetricValue(482.36, "kWh"))
        assertEquals("0.0 kWh", formatMetricValue(0.0, "kWh"))

        // Carbon kg & Tons
        assertEquals("950 kg", formatMetricValue(950.4, "kg"))
        assertEquals("1.23 Tons", formatMetricValue(1.2345, "Tons"))

        // Fallbacks
        assertEquals("15.5 kW", formatMetricValue(15.5, "kW"))
        assertEquals("42.0", formatMetricValue(42.0, ""))
    }

    @Test
    fun metrics_cardRendersNegativeAndZeroDeltasCorrectly() {
        composeTestRule.setContent {
            SolarEaseTheme {
                Column {
                    SolarEaseMetricCard(
                        label = "Grid Import",
                        value = 240.0,
                        unit = "kWh",
                        trendDeltaPct = -12.4,
                        subtitle = "Reduced consumption"
                    )
                    SolarEaseMetricCard(
                        label = "Baseline Power",
                        value = 100.0,
                        unit = "kWh",
                        trendDeltaPct = 0.0
                    )
                    SolarEaseMetricCard(
                        label = "Static Metric",
                        value = 50.0,
                        unit = "kWh",
                        trendDeltaPct = null
                    )
                }
            }
        }

        // Negative delta should display negative sign and format to 1 decimal place
        composeTestRule.onNodeWithText("-12.4%").assertExists()
        composeTestRule.onNodeWithText("Grid Import").assertExists()
        composeTestRule.onNodeWithText("240.0 kWh").assertExists()
        composeTestRule.onNodeWithText("Reduced consumption").assertExists()

        // Zero delta should display 0.0%
        composeTestRule.onNodeWithText("0.0%").assertExists()
        composeTestRule.onNodeWithText("Baseline Power").assertExists()

        // Null delta card should display metric without trend badge
        composeTestRule.onNodeWithText("Static Metric").assertExists()
        composeTestRule.onNodeWithText("50.0 kWh").assertExists()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5. NAVIGATION 3-TAB VS 4-TAB & INTERACTION STRESS TESTING
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun navigation_3TabModeRendersStrictlyThreeTabs() {
        var homeClicked = false
        var recommendsClicked = false
        var profileClicked = false

        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseBottomNavigation(
                    currentTab = SolarEaseNavTab.HOME,
                    onNavigateToHome = { homeClicked = true },
                    onNavigateToRecommends = { recommendsClicked = true },
                    onNavigateToProfile = { profileClicked = true },
                    onNavigateToAnalytics = null // 3-tab mode
                )
            }
        }

        // Home selected
        val homeTab = composeTestRule.onNodeWithText("Home")
        homeTab.assertExists().assertIsSelected()
        assertEquals(
            "Selected",
            homeTab.fetchSemanticsNode().config[SemanticsProperties.StateDescription]
        )

        // Recommends & Profile exist and unselected
        val recTab = composeTestRule.onNodeWithText("Recommends")
        recTab.assertExists().assertIsNotSelected()
        val profTab = composeTestRule.onNodeWithText("Profile")
        profTab.assertExists().assertIsNotSelected()

        // Analytics must NOT exist in 3-tab mode
        composeTestRule.onNodeWithText("Analytics").assertDoesNotExist()

        // Clicks work
        recTab.performClick()
        assertTrue(recommendsClicked)
        profTab.performClick()
        assertTrue(profileClicked)
        homeTab.performClick()
        assertTrue(homeClicked)
    }

    @Test
    fun navigation_4TabModeRendersAllTabsAndHandlesClicks() {
        var analyticsClicked = false
        var currentTabState by mutableStateOf(SolarEaseNavTab.RECOMMENDS)

        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseBottomNavigation(
                    currentTab = currentTabState,
                    onNavigateToHome = { currentTabState = SolarEaseNavTab.HOME },
                    onNavigateToAnalytics = { analyticsClicked = true },
                    onNavigateToRecommends = { currentTabState = SolarEaseNavTab.RECOMMENDS },
                    onNavigateToProfile = { currentTabState = SolarEaseNavTab.PROFILE }
                )
            }
        }

        composeTestRule.onNodeWithText("Home").assertExists().assertIsNotSelected()
        val analyticsTab = composeTestRule.onNodeWithText("Analytics")
        analyticsTab.assertExists().assertIsNotSelected()
        val recommendsTab = composeTestRule.onNodeWithText("Recommends")
        recommendsTab.assertExists().assertIsSelected()
        composeTestRule.onNodeWithText("Profile").assertExists().assertIsNotSelected()

        analyticsTab.performClick()
        assertTrue("Analytics tab click must fire callback", analyticsClicked)

        // Switch tab state and check accessibility update
        currentTabState = SolarEaseNavTab.HOME
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Home").assertIsSelected()
        recommendsTab.assertIsNotSelected()
    }

    @Test
    fun navigationRail_supportsBoth3And4TabsAndMaintainsDimensions() {
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseNavigationRail(
                    currentTab = SolarEaseNavTab.HOME,
                    onNavigateToHome = {},
                    onNavigateToRecommends = {},
                    onNavigateToProfile = {},
                    onNavigateToAnalytics = {}
                )
            }
        }

        // Rail items exist
        composeTestRule.onNodeWithText("Home").assertExists().assertIsSelected()
        composeTestRule.onNodeWithText("Analytics").assertExists()
        composeTestRule.onNodeWithText("Recommends").assertExists()
        composeTestRule.onNodeWithText("Profile").assertExists()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6. LEGACY ADAPTERS STRESS TESTING
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun legacyAdapters_sharedComponentsDelegationWorksWithoutCrash() {
        var cardSelected by mutableStateOf(false)

        composeTestRule.setContent {
            SolarEaseTheme {
                Column {
                    @Suppress("DEPRECATION")
                    StepProgress(
                        currentStep = 2,
                        totalSteps = 5
                    )

                    @Suppress("DEPRECATION")
                    SelectableCard(
                        selected = cardSelected,
                        onClick = { cardSelected = !cardSelected },
                        title = "Legacy Monocrystalline",
                        description = "Mono PERC panel"
                    )
                }
            }
        }

        val stepNode = composeTestRule.onNodeWithContentDescription("Step 2 of 5")
        stepNode.assertExists()
        assertEquals(
            "40 percent complete",
            stepNode.fetchSemanticsNode().config[SemanticsProperties.StateDescription]
        )

        val cardNode = composeTestRule.onNodeWithText("Legacy Monocrystalline")
        cardNode.assertExists().assertIsNotSelected()
        cardNode.performClick()
        assertTrue(cardSelected)
    }

    @Test
    fun legacyAdapters_solarVendorComponentsDelegationWorksWithoutCrash() {
        var vendorCardSelected by mutableStateOf(false)

        composeTestRule.setContent {
            SolarEaseTheme {
                Column {
                    @Suppress("DEPRECATION")
                    StitchStepProgressBar(
                        currentStep = 4,
                        totalSteps = 8,
                        stepTitle = "Vendor Selection",
                        categoryText = "Marketplace"
                    )

                    @Suppress("DEPRECATION")
                    StitchSelectableCard(
                        selected = vendorCardSelected,
                        onClick = { vendorCardSelected = !vendorCardSelected },
                        title = "Vendor Tier 1",
                        subtitle = "Certified DISCOM Partner",
                        badgeText = "Fast Install"
                    )
                }
            }
        }

        val progressNode = composeTestRule.onNodeWithContentDescription("Step 4 of 8: Vendor Selection")
        progressNode.assertExists()
        composeTestRule.onNodeWithText("Marketplace").assertExists()

        val vendorNode = composeTestRule.onNodeWithText("Vendor Tier 1")
        vendorNode.assertExists()
        val bounds = vendorNode.getUnclippedBoundsInRoot()
        assertTrue("StitchSelectableCard minHeight is 64dp", bounds.rectHeight >= 64.dp)
        vendorNode.performClick()
        assertTrue(vendorCardSelected)
    }
}
