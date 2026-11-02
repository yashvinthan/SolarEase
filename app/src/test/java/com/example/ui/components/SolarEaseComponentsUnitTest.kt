package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SolarEaseTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class SolarEaseComponentsUnitTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ─────────────────────────────────────────────────────────────────────────
    // SolarEaseButton Tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun button_meets48DpTouchTargetAndExecutesClick() {
        var clicked = false
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseButton(
                    text = "Calculate Solar",
                    onClick = { clicked = true },
                    variant = SolarEaseButtonVariant.PRIMARY
                )
            }
        }

        val node = composeTestRule.onNodeWithText("Calculate Solar")
        node.assertExists()
        val bounds = node.getUnclippedBoundsInRoot()
        assertTrue("Height must be >= 48dp", (bounds.bottom - bounds.top) >= 48.dp)
        assertTrue("Width must be >= 48dp", (bounds.right - bounds.left) >= 48.dp)

        node.performClick()
        assertTrue("Enabled button click should fire callback", clicked)
    }

    @Test
    fun button_whenLoading_disablesClicksAndExposesLoadingState() {
        var clicked = false
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseButton(
                    text = "Saving Data",
                    onClick = { clicked = true },
                    isLoading = true
                )
            }
        }

        val node = composeTestRule.onNodeWithText("Saving Data")
        node.assertExists()
        assertEquals(
            "Loading",
            node.fetchSemanticsNode().config[SemanticsProperties.StateDescription]
        )

        node.performClick()
        assertFalse("Loading button must not execute click", clicked)
    }

    @Test
    fun button_whenDisabled_disablesClick() {
        var clicked = false
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseButton(
                    text = "Disabled Action",
                    onClick = { clicked = true },
                    enabled = false
                )
            }
        }

        val node = composeTestRule.onNodeWithText("Disabled Action")
        node.assertExists().assertIsNotEnabled()
        node.performClick()
        assertFalse("Disabled button must not execute click", clicked)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SolarEaseCard & SolarEaseSelectableCard Tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun card_selectableCard_exposesAccessibilityAndRadioButtonRole() {
        var selected = false
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseSelectableCard(
                    selected = selected,
                    onClick = { selected = !selected },
                    title = "Monocrystalline Panels",
                    subtitle = "High efficiency mono PERC technology",
                    badgeText = "Recommended",
                    icon = Icons.Default.Star
                )
            }
        }

        val node = composeTestRule.onNodeWithText("Monocrystalline Panels")
        node.assertExists().assertIsNotSelected()
        assertEquals(
            Role.RadioButton,
            node.fetchSemanticsNode().config[SemanticsProperties.Role]
        )
        assertEquals(
            "Not selected",
            node.fetchSemanticsNode().config[SemanticsProperties.StateDescription]
        )

        composeTestRule.onNodeWithText("Recommended").assertExists()
        composeTestRule.onNodeWithText("High efficiency mono PERC technology").assertExists()

        node.performClick()
        assertTrue(selected)
    }

    @Test
    fun card_selectableCard_withCheckboxRole_exposesCheckboxSemantics() {
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseSelectableCard(
                    selected = true,
                    onClick = {},
                    title = "Net Metering Addon",
                    selectionRole = Role.Checkbox
                )
            }
        }

        val node = composeTestRule.onNodeWithText("Net Metering Addon")
        node.assertExists().assertIsSelected()
        assertEquals(
            Role.Checkbox,
            node.fetchSemanticsNode().config[SemanticsProperties.Role]
        )
        assertEquals(
            "Selected",
            node.fetchSemanticsNode().config[SemanticsProperties.StateDescription]
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SolarEaseNavigation Tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun topBar_rendersTitleAndHandlesBackClick() {
        var backClicked = false
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseTopBar(
                    title = "Solar Configuration",
                    onBack = { backClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Solar Configuration").assertExists()
        val backBtn = composeTestRule.onNodeWithContentDescription("Navigate back")
        backBtn.assertExists()
        backBtn.performClick()
        assertTrue(backClicked)
    }

    @Test
    fun bottomNavigation_supports4TabsWhenAnalyticsProvided() {
        var analyticsClicked = false
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseBottomNavigation(
                    currentTab = SolarEaseNavTab.ANALYTICS,
                    onNavigateToHome = {},
                    onNavigateToAnalytics = { analyticsClicked = true },
                    onNavigateToRecommends = {},
                    onNavigateToProfile = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Home").assertExists().assertIsNotSelected()
        val analyticsTab = composeTestRule.onNodeWithText("Analytics")
        analyticsTab.assertExists().assertIsSelected()
        composeTestRule.onNodeWithText("Recommends").assertExists().assertIsNotSelected()
        composeTestRule.onNodeWithText("Profile").assertExists().assertIsNotSelected()

        analyticsTab.performClick()
        assertTrue(analyticsClicked)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SolarEaseStepProgressBar Tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun stepProgressBar_rendersUnifiedAccessibilitySemantics() {
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseStepProgressBar(
                    currentStep = 6,
                    totalSteps = 12,
                    stepTitle = "Panel Capacity",
                    category = "Estimation"
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Step 6 of 12: Panel Capacity").assertExists()
        val node = composeTestRule.onNodeWithContentDescription("Step 6 of 12: Panel Capacity")
        assertEquals(
            "50 percent complete",
            node.fetchSemanticsNode().config[SemanticsProperties.StateDescription]
        )
        composeTestRule.onNodeWithText("Estimation").assertExists()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SolarEaseStates Tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun emptyState_rendersTitleDescriptionAndTriggersAction() {
        var actionClicked = false
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseEmptyState(
                    title = "No Quotations Found",
                    description = "Request an estimate from DISCOM-registered vendors.",
                    actionText = "Find Vendors",
                    onAction = { actionClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("No Quotations Found").assertExists()
        composeTestRule.onNodeWithText("Request an estimate from DISCOM-registered vendors.").assertExists()
        val actionBtn = composeTestRule.onNodeWithText("Find Vendors")
        actionBtn.assertExists()
        actionBtn.performClick()
        assertTrue(actionClicked)
    }

    @Test
    fun errorState_formatsErrorCodeAndTriggersRetry() {
        var retried = false
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseErrorState(
                    message = "Failed to load solar data",
                    errorCode = "ERR_503_SERVICE",
                    onRetry = { retried = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Failed to load solar data (ERR_503_SERVICE)").assertExists()
        val retryBtn = composeTestRule.onNodeWithText("Try Again")
        retryBtn.assertExists()
        retryBtn.performClick()
        assertTrue(retried)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SolarEaseMetrics Tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun metricCard_rendersFormattedValueAndTrend() {
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseMetricCard(
                    label = "Monthly Savings",
                    value = 4850.0,
                    unit = "₹",
                    trendDeltaPct = 14.5,
                    subtitle = "vs last month"
                )
            }
        }

        composeTestRule.onNodeWithText("Monthly Savings").assertExists()
        composeTestRule.onNodeWithText("₹4,850").assertExists()
        composeTestRule.onNodeWithText("+14.5%").assertExists()
        composeTestRule.onNodeWithText("vs last month").assertExists()
    }

    @Test
    fun metricStrip_rendersAllMetricItems() {
        val items = listOf(
            SolarEaseMetricItem(label = "Generation", value = 450.0, unit = "kWh"),
            SolarEaseMetricItem(label = "Savings", value = 3150.0, unit = "₹"),
            SolarEaseMetricItem(label = "CO2 Avoided", value = 360.0, unit = "kg")
        )

        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseMetricStrip(metrics = items)
            }
        }

        composeTestRule.onNodeWithText("Generation").assertExists()
        composeTestRule.onNodeWithText("450.0 kWh").assertExists()
        composeTestRule.onNodeWithText("Savings").assertExists()
        composeTestRule.onNodeWithText("₹3,150").assertExists()
        composeTestRule.onNodeWithText("CO2 Avoided").assertExists()
        composeTestRule.onNodeWithText("360 kg").assertExists()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SolarEaseTimeline & FilterChip Tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun timeline_rendersMilestonesCorrectly() {
        val milestones = listOf(
            SolarEaseMilestone("Site Survey", "Completed roof assessment", isCompleted = true, timestamp = "Oct 15"),
            SolarEaseMilestone("Net Metering", "Pending meter installation", isCompleted = false)
        )

        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseTimeline(milestones = milestones)
            }
        }

        composeTestRule.onNodeWithText("Site Survey").assertExists()
        composeTestRule.onNodeWithText("Oct 15").assertExists()
        composeTestRule.onNodeWithText("Net Metering").assertExists()
        assertEquals(0.5f, calculateTimelineProgress(milestones), 0.001f)
    }

    @Test
    fun filterChipGroup_singleSelectionBehavior() {
        var selected = "Monthly"
        composeTestRule.setContent {
            SolarEaseTheme {
                SolarEaseFilterChipGroup(
                    options = listOf("Daily", "Monthly", "Yearly"),
                    selectedOption = selected,
                    onOptionSelected = { selected = it }
                )
            }
        }

        val monthlyNode = composeTestRule.onNodeWithText("Monthly")
        monthlyNode.assertExists().assertIsSelected()

        val yearlyNode = composeTestRule.onNodeWithText("Yearly")
        yearlyNode.assertExists().assertIsNotSelected()
        yearlyNode.performClick()
        assertEquals("Yearly", selected)
    }
}
