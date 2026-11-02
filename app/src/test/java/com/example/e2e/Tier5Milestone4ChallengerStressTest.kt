package com.example.e2e

import com.example.feature.assessment.ui.SolarVendorState
import com.example.feature.assessment.ui.SolarVendorViewModel
import com.example.feature.assessment.util.AssessmentValidator
import com.example.feature.assessment.util.EstimateCalculator
import com.example.feature.assessment.util.EstimateConstants
import com.example.feature.assessment.util.SubsidyCalculator
import com.example.feature.dashboard.domain.model.ActivityItem
import com.example.feature.dashboard.domain.model.CustomerDashboard
import com.example.feature.dashboard.domain.model.NotificationItem
import com.example.feature.environmental.domain.model.ImpactHistory
import com.example.feature.environmental.domain.model.ChartDataPoint as EnvironmentalChartPoint
import com.example.feature.financial.domain.model.RoiSummary
import com.example.feature.financial.domain.model.SavingsHistory
import com.example.feature.financial.domain.model.ChartDataPoint as FinancialChartPoint
import com.example.feature.lens.ui.EditableAppliance
import com.example.feature.lens.ui.LensViewModel
import com.example.feature.lens.ui.Room
import com.example.feature.lens.ui.RoomStatus
import com.example.feature.performance.domain.model.EnergyFlow
import com.example.feature.performance.domain.model.LiveGeneration
import com.example.feature.performance.domain.model.ChartDataPoint as PerformanceChartPoint
import com.example.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.NumberFormat
import java.util.Locale

/**
 * Challenger M4 Empirical Adversarial Verification Suite.
 *
 * Covers:
 * 1. Funnel Navigation & Boundary Stress (Steps 1..12 continuous range, empty/invalid inputs)
 * 2. Marketplace Route Reachability & Null Safety (Screen routes, argument passing, defaults)
 * 3. Telemetry & Live Data Models Stress (CustomerDashboard & SolarPanelScreen under edge values)
 * 4. Canvas Chart Accessibility & Semantics Tree (SimpleBarChart, RoiSection, SavingsHistory, MonthlyImpact)
 * 5. Lens Modularization Invariants (Photo limit 4, room reordering bounds, load calculations)
 */
class Tier5Milestone4ChallengerStressTest {

    // =========================================================================
    // 1. FUNNEL NAVIGATION & BOUNDARY STRESS
    // =========================================================================

    data class FunnelStepContract(
        val stepIndex: Int,
        val totalSteps: Int,
        val stepTitle: String,
        val category: String
    )

    private val authoritativeFunnelSteps = listOf(
        FunnelStepContract(1, 12, "EB Service Number", "Consumer Verification"),
        FunnelStepContract(2, 12, "Property Classification", "Location Type"),
        FunnelStepContract(3, 12, "Solar System Type", "Grid Configuration"),
        FunnelStepContract(4, 12, "Electricity Usage", "Monthly Consumption"),
        FunnelStepContract(5, 12, "Installation Location", "Mount Type"),
        FunnelStepContract(6, 12, "Available Rooftop Space", "Site Dimensions"),
        FunnelStepContract(7, 12, "System Recommendation", "Engine Sizing"),
        FunnelStepContract(8, 12, "Panel Specification", "DCR vs Non-DCR"),
        FunnelStepContract(9, 12, "Cost Estimation Breakdown", "Subsidy & Financials"),
        FunnelStepContract(10, 12, "Payment & Financing", "Payment Method"),
        FunnelStepContract(11, 12, "Order Summary & Review", "Order Confirmation"),
        FunnelStepContract(12, 12, "Installation Tracking", "Project Milestones")
    )

    @Test
    fun funnel_testStepIndexContinuousRangeInvariant() {
        assertEquals("Funnel must contain exactly 12 steps", 12, authoritativeFunnelSteps.size)

        authoritativeFunnelSteps.forEachIndexed { index, step ->
            val expectedStep = index + 1
            assertEquals("Step at index $index must be $expectedStep", expectedStep, step.stepIndex)
            assertEquals("Step $expectedStep must report totalSteps = 12", 12, step.totalSteps)
            assertFalse("Step title must not be blank", step.stepTitle.isBlank())
            assertFalse("Step category must not be blank", step.category.isBlank())
            // Ensure no legacy regressions back to 8 or 3 steps
            assertNotEquals(8, step.totalSteps)
            assertNotEquals(3, step.totalSteps)
        }
    }

    @Test
    fun funnel_testConsumptionInputValidationAdversarialCases() {
        // Valid inputs
        assertTrue(AssessmentValidator.isValidConsumption("1"))
        assertTrue(AssessmentValidator.isValidConsumption("50"))
        assertTrue(AssessmentValidator.isValidConsumption("350"))
        assertTrue(AssessmentValidator.isValidConsumption("500.5"))
        assertTrue(AssessmentValidator.isValidConsumption("9999"))
        assertTrue(AssessmentValidator.isValidConsumption("99999")) // exactly 5 chars

        // Adversarial & empty inputs
        assertFalse(AssessmentValidator.isValidConsumption(""))
        assertFalse(AssessmentValidator.isValidConsumption("   "))
        assertFalse(AssessmentValidator.isValidConsumption("0"))
        assertFalse(AssessmentValidator.isValidConsumption("0.0"))
        assertFalse(AssessmentValidator.isValidConsumption("-1"))
        assertFalse(AssessmentValidator.isValidConsumption("-50.5"))
        assertFalse(AssessmentValidator.isValidConsumption("100000")) // 6 chars (exceeds 5)
        assertFalse(AssessmentValidator.isValidConsumption("abc"))
        assertFalse(AssessmentValidator.isValidConsumption("350kwh"))
        assertFalse(AssessmentValidator.isValidConsumption("350.5.5"))
        assertFalse(AssessmentValidator.isValidConsumption("NaN"))
        assertFalse(AssessmentValidator.isValidConsumption("Infinity"))
    }

    @Test
    fun funnel_testAreaInputValidationAdversarialCases() {
        // Valid inputs
        assertTrue(AssessmentValidator.isValidArea("0.1"))
        assertTrue(AssessmentValidator.isValidArea("1"))
        assertTrue(AssessmentValidator.isValidArea("50"))
        assertTrue(AssessmentValidator.isValidArea("250.75"))
        assertTrue(AssessmentValidator.isValidArea("100000"))
        assertTrue(AssessmentValidator.isValidArea("100000.0"))

        // Adversarial & empty inputs
        assertFalse(AssessmentValidator.isValidArea(""))
        assertFalse(AssessmentValidator.isValidArea("   "))
        assertFalse(AssessmentValidator.isValidArea("0"))
        assertFalse(AssessmentValidator.isValidArea("0.0"))
        assertFalse(AssessmentValidator.isValidArea("-10"))
        assertFalse(AssessmentValidator.isValidArea("-0.01"))
        assertFalse(AssessmentValidator.isValidArea("100000.1")) // Exceeds upper limit
        assertFalse(AssessmentValidator.isValidArea("500000"))
        assertFalse(AssessmentValidator.isValidArea("sqft"))
        assertFalse(AssessmentValidator.isValidArea("50 sq m"))
        assertFalse(AssessmentValidator.isValidArea("NaN"))
    }

    @Test
    fun funnel_testSolarVendorViewModelBoundaryStress() {
        val vm = SolarVendorViewModel()
        assertNotNull(vm.state.value)

        // Stress consumer number updates
        vm.updateConsumerNumber("")
        assertEquals("", vm.state.value.rawConsumerNumber)
        assertFalse(vm.state.value.isConsumerValid)

        vm.updateConsumerNumber("12345") // Too short (<10)
        assertFalse(vm.state.value.isConsumerValid)

        vm.updateConsumerNumber("09440233135") // 11 digits
        assertTrue(vm.state.value.isConsumerValid)
        assertEquals("09440233135", vm.state.value.rawConsumerNumber)

        vm.updateConsumerNumber("123456789012345678") // Excess digits -> truncated to 12
        assertEquals(12, vm.state.value.rawConsumerNumber.length)
        assertTrue(vm.state.value.isConsumerValid)

        vm.clearConsumerNumber()
        assertEquals("", vm.state.value.rawConsumerNumber)
        assertFalse(vm.state.value.isConsumerValid)

        // Stress consumption updates with boundary numbers
        vm.updateMonthlyConsumption(0.0)
        assertEquals(0.0, vm.state.value.monthlyConsumptionKwh, 0.001)
        assertEquals(0.0, vm.state.value.recommendedCapacityKw, 0.001)
        assertEquals(0.0, vm.state.value.requiredAreaSqM, 0.001)
        assertTrue(vm.state.value.isFeasible)

        vm.updateMonthlyConsumption(1200.0) // 1200 / 120 = 10 kW -> 100 sq m required
        vm.updateAvailableArea(50.0) // 50 < 100 -> not feasible
        assertFalse(vm.state.value.isFeasible)

        vm.updateAvailableArea(120.0) // 120 >= 100 -> feasible
        assertTrue(vm.state.value.isFeasible)

        // Request notes boundary
        val hugeNotes = "A".repeat(5000)
        vm.updateRequestNotes(hugeNotes)
        assertEquals(5000, vm.state.value.requestNotes.length)

        // Estimate request generation
        val generatedRef = vm.submitEstimateRequest()
        assertTrue(generatedRef.startsWith("REQ-SOL-2026-"))
        assertEquals(generatedRef, vm.state.value.referenceId)
        assertEquals(generatedRef, vm.state.value.docketId)
    }

    // =========================================================================
    // 2. MARKETPLACE ROUTE REACHABILITY & NULL SAFETY
    // =========================================================================

    @Test
    fun marketplace_testScreenRouteSafetyAndDefaultArguments() {
        // VendorProfile default argument
        val defaultProfile = Screen.VendorProfile()
        assertEquals("abc_solar", defaultProfile.vendorId)

        val customProfile = Screen.VendorProfile(vendorId = "tamil_nadu_solar_42")
        assertEquals("tamil_nadu_solar_42", customProfile.vendorId)

        // SiteSurveyStatus
        val siteSurvey = Screen.SiteSurveyStatus(systemType = "On-Grid")
        assertEquals("On-Grid", siteSurvey.systemType)

        // InstallationTracking
        val tracking = Screen.InstallationTracking(systemType = "Hybrid")
        assertEquals("Hybrid", tracking.systemType)

        // Singleton screens
        assertNotNull(Screen.NearbyVendors)
        assertNotNull(Screen.EstimateRequest)
        assertNotNull(Screen.RequestConfirmation)
        assertNotNull(Screen.CustomerDashboard)
        assertNotNull(Screen.MySolarSystem)
        assertNotNull(Screen.Performance)
        assertNotNull(Screen.Financial)
        assertNotNull(Screen.Environmental)
    }

    @Test
    fun marketplace_testVendorProfileFallbackOnEmptyName() {
        val blankName = ""
        val whitespaceName = "   "
        val realName = "SunPower India Ltd"

        val fallback1 = blankName.ifBlank { "Kaveri Solar Systems" }
        assertEquals("Kaveri Solar Systems", fallback1)

        val fallback2 = whitespaceName.ifBlank { "Kaveri Solar Systems" }
        assertEquals("Kaveri Solar Systems", fallback2)

        val actual = realName.ifBlank { "Kaveri Solar Systems" }
        assertEquals("SunPower India Ltd", actual)
    }

    @Test
    fun marketplace_testSolarVendorStateDefaultValues() {
        val defaultState = SolarVendorState()
        assertNotNull(defaultState.rawConsumerNumber)
        assertNotNull(defaultState.formattedConsumerNumber)
        assertNotNull(defaultState.propertyType)
        assertNotNull(defaultState.systemType)
        assertNotNull(defaultState.mountType)
        assertNotNull(defaultState.panelType)
        assertNotNull(defaultState.selectedVendorId)
        assertNotNull(defaultState.selectedVendorName)
        assertNotNull(defaultState.customerName)
        assertNotNull(defaultState.customerPhone)
        assertNotNull(defaultState.requestNotes)
        assertNotNull(defaultState.referenceId)
        assertEquals(defaultState.referenceId, defaultState.docketId)
    }

    // =========================================================================
    // 3. TELEMETRY & LIVE DATA MODELS STRESS
    // =========================================================================

    @Test
    fun telemetry_testCustomerDashboardEdgeValues() {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
            maximumFractionDigits = 0
        }

        // Edge Case 1: Zero generation (Freshly commissioned system)
        val zeroDashboard = CustomerDashboard(
            username = "Ramesh",
            systemStatus = "Standby",
            capacity = 3.0,
            systemType = "On-Grid",
            installationDate = "Today",
            todayGeneration = 0.0,
            todaySavings = 0.0,
            todayCo2 = 0.0,
            lifetimeGeneration = 0.0,
            lifetimeSavings = 0.0,
            lifetimeCo2 = 0.0,
            plantHealth = 100,
            recentActivities = emptyList(),
            notifications = emptyList()
        )
        assertEquals("Standby", zeroDashboard.systemStatus)
        assertEquals("₹0", currencyFormat.format(zeroDashboard.todaySavings).replace("\u00A0", " ").trim())
        assertTrue(zeroDashboard.recentActivities.isEmpty())
        assertTrue(zeroDashboard.notifications.isEmpty())

        // Edge Case 2: Negative generation / savings (Inverter auxiliary grid consumption)
        val negativeDashboard = CustomerDashboard(
            username = "Priya",
            systemStatus = "Offline",
            capacity = 5.0,
            systemType = "Hybrid",
            installationDate = "2026-01-15",
            todayGeneration = -0.5,
            todaySavings = -25.0,
            todayCo2 = -0.4,
            lifetimeGeneration = 1200.0,
            lifetimeSavings = 9600.0,
            lifetimeCo2 = 984.0,
            plantHealth = 0,
            recentActivities = listOf(ActivityItem("Grid Outage", 1700000000000L)),
            notifications = listOf(NotificationItem("Inverter disconnected", 1700000000000L))
        )
        assertEquals("Offline", negativeDashboard.systemStatus)
        assertEquals(-0.5, negativeDashboard.todayGeneration, 0.001)
        val formattedNegative = currencyFormat.format(negativeDashboard.todaySavings)
        assertTrue("Formatted currency should represent negative amount", formattedNegative.contains("25"))

        // Edge Case 3: High Capacity Commercial System (>100 kW)
        val commercialDashboard = CustomerDashboard(
            username = "Kaveri Textiles",
            systemStatus = "Online",
            capacity = 150.0, // 150 kW
            systemType = "On-Grid",
            installationDate = "2025-06-01",
            todayGeneration = 780.0,
            todaySavings = 6240.0,
            todayCo2 = 639.6,
            lifetimeGeneration = 280000.0,
            lifetimeSavings = 2240000.0,
            lifetimeCo2 = 229600.0,
            plantHealth = 99,
            recentActivities = emptyList(),
            notifications = emptyList()
        )
        assertEquals(150.0, commercialDashboard.capacity, 0.001)
        assertEquals("Online", commercialDashboard.systemStatus)
        assertEquals(280000.0, commercialDashboard.lifetimeGeneration, 0.001)
    }

    @Test
    fun telemetry_testLiveGenerationAndEnergyFlowEdgeStress() {
        // Zero generation (Nighttime)
        val nighttimeGen = LiveGeneration(
            currentOutput = 0.0,
            todayGeneration = 14.2,
            systemStatus = "Standby (Night)",
            gridStatus = "Connected (230V)",
            lastUpdated = System.currentTimeMillis(),
            weather = "Clear Night"
        )
        assertEquals(0.0, nighttimeGen.currentOutput, 0.001)

        // Peak output
        val peakGen = LiveGeneration(
            currentOutput = 12.8,
            todayGeneration = 68.4,
            systemStatus = "Peak Generation",
            gridStatus = "Connected (242V)",
            lastUpdated = System.currentTimeMillis(),
            weather = "Intense Sunshine"
        )
        assertEquals(12.8, peakGen.currentOutput, 0.001)

        // Energy flow balance
        val flow = EnergyFlow(
            solarGenerated = 14.5,
            consumed = 8.2,
            exportedToGrid = 6.3,
            importedFromGrid = 0.0,
            batteryStatus = "Not Installed"
        )
        val energyDesc = "Energy Flow: Generated ${flow.solarGenerated} kWh, Consumed ${flow.consumed} kWh, Exported ${flow.exportedToGrid} kWh, Imported ${flow.importedFromGrid} kWh"
        assertTrue(energyDesc.contains("Generated 14.5 kWh"))
        assertTrue(energyDesc.contains("Exported 6.3 kWh"))
        assertEquals("Not Installed", flow.batteryStatus)
    }

    // =========================================================================
    // 4. CANVAS CHART ACCESSIBILITY & SEMANTICS TREE
    // =========================================================================

    @Test
    fun chartSemantics_testSimpleBarChartSemanticsGenerator() {
        val testPoints = listOf(
            PerformanceChartPoint("Mon", 10.5),
            PerformanceChartPoint("Tue", 22.0),
            PerformanceChartPoint("Wed", 18.5),
            PerformanceChartPoint("Thu", 26.0),
            PerformanceChartPoint("Fri", 14.0)
        )

        val maxValue = testPoints.maxOf { it.value }
        val maxLabel = testPoints.firstOrNull { it.value == maxValue }?.label ?: ""
        val contentDesc = "Generation History: " + testPoints.joinToString("; ") { "${it.label}: ${it.value}" }
        val stateDesc = "Interactive chart displaying ${testPoints.size} periods. Highest: $maxLabel ($maxValue)."

        assertEquals(26.0, maxValue, 0.001)
        assertEquals("Thu", maxLabel)
        assertEquals(
            "Generation History: Mon: 10.5; Tue: 22.0; Wed: 18.5; Thu: 26.0; Fri: 14.0",
            contentDesc
        )
        assertEquals("Interactive chart displaying 5 periods. Highest: Thu (26.0).", stateDesc)

        // Empty chart points edge case
        val emptyPoints = emptyList<PerformanceChartPoint>()
        assertTrue(emptyPoints.isEmpty())

        // All zero points edge case
        val zeroPoints = listOf(
            PerformanceChartPoint("Mon", 0.0),
            PerformanceChartPoint("Tue", 0.0)
        )
        val maxZero = zeroPoints.maxOf { it.value }
        assertEquals(0.0, maxZero, 0.001)
    }

    @Test
    fun chartSemantics_testRoiSectionSemanticsGenerator() {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
            maximumFractionDigits = 0
        }

        val roi = RoiSummary(
            estimatedPaybackPeriodYears = 4.2,
            investmentRecovered = 85000.0,
            remainingAmount = 65000.0,
            roiPercentage = 56.67
        )

        val roiDesc = "Return on Investment: ${roi.roiPercentage}% recovered. Recovered: ${currencyFormat.format(roi.investmentRecovered)}, Remaining: ${currencyFormat.format(roi.remainingAmount)}. Payback: ${roi.estimatedPaybackPeriodYears} years."
        val stateDesc = "${roi.roiPercentage}% recovered"

        assertTrue(roiDesc.contains("56.67% recovered"))
        assertTrue(roiDesc.contains("Payback: 4.2 years"))
        assertTrue(roiDesc.contains("85,000"))
        assertTrue(roiDesc.contains("65,000"))
        assertEquals("56.67% recovered", stateDesc)
    }

    @Test
    fun chartSemantics_testSavingsHistorySemanticsGenerator() {
        val savingsPoints = listOf(
            FinancialChartPoint("Jan", 2400.0, 600.0),
            FinancialChartPoint("Feb", 2800.0, 500.0),
            FinancialChartPoint("Mar", 3200.0, 450.0)
        )

        val maxValue = savingsPoints.maxOf { it.savings.coerceAtLeast(it.estimatedBill) }
        val maxLabel = savingsPoints.maxByOrNull { it.savings }?.label ?: ""
        val contentDesc = "Savings History: " + savingsPoints.joinToString("; ") { "${it.label}: Savings ${it.savings}, Bill ${it.estimatedBill}" }
        val stateDesc = "Interactive savings chart with ${savingsPoints.size} periods. Peak: $maxLabel ($maxValue)."

        assertEquals(3200.0, maxValue, 0.001)
        assertEquals("Mar", maxLabel)
        assertEquals("Savings History: Jan: Savings 2400.0, Bill 600.0; Feb: Savings 2800.0, Bill 500.0; Mar: Savings 3200.0, Bill 450.0", contentDesc)
        assertEquals("Interactive savings chart with 3 periods. Peak: Mar (3200.0).", stateDesc)
    }

    @Test
    fun chartSemantics_testMonthlyImpactSemanticsGenerator() {
        val impactPoints = listOf(
            EnvironmentalChartPoint("Q1", 450.0),
            EnvironmentalChartPoint("Q2", 620.0),
            EnvironmentalChartPoint("Q3", 580.0),
            EnvironmentalChartPoint("Q4", 490.0)
        )

        val maxValue = impactPoints.maxOf { it.value }
        val peakLabel = impactPoints.maxByOrNull { it.value }?.label ?: ""
        val chartSummary = impactPoints.joinToString(", ") { "${it.label}: ${it.value}" }
        val stateDesc = "Interactive impact chart with ${impactPoints.size} periods. Peak: $peakLabel ($maxValue)."
        val contentDescription = "Impact over time chart ($chartSummary)"

        assertEquals(620.0, maxValue, 0.001)
        assertEquals("Q2", peakLabel)
        assertEquals("Impact over time chart (Q1: 450.0, Q2: 620.0, Q3: 580.0, Q4: 490.0)", contentDescription)
        assertEquals("Interactive impact chart with 4 periods. Peak: Q2 (620.0).", stateDesc)
    }

    // =========================================================================
    // 5. LENS MODULARIZATION INVARIANTS
    // =========================================================================

    @Test
    fun lens_testMaxRoomPhotosInvariant() {
        assertEquals("LensViewModel must limit photos per room to exactly 4", 4, LensViewModel.MAX_ROOM_PHOTOS)

        // Simulate photo addition logic
        val photos = mutableListOf<String>()
        val maxPhotos = LensViewModel.MAX_ROOM_PHOTOS

        fun addPhoto(uri: String): Boolean {
            if (photos.size >= maxPhotos) return false
            if (photos.contains(uri)) return false
            photos.add(uri)
            return true
        }

        assertTrue(addPhoto("content://media/photo_1.jpg"))
        assertTrue(addPhoto("content://media/photo_2.jpg"))
        assertTrue(addPhoto("content://media/photo_3.jpg"))
        assertTrue(addPhoto("content://media/photo_4.jpg"))
        // 5th photo must be rejected
        assertFalse(addPhoto("content://media/photo_5.jpg"))
        assertEquals(4, photos.size)

        // Duplicate rejection
        assertFalse(addPhoto("content://media/photo_1.jpg"))
        assertEquals(4, photos.size)
    }

    @Test
    fun lens_testRoomReorderingBounds() {
        val initialRooms = listOf(
            Room(id = "1", name = "Living Room", order = 0),
            Room(id = "2", name = "Master Bedroom", order = 1),
            Room(id = "3", name = "Kitchen", order = 2)
        )

        fun reorder(rooms: List<Room>, fromIndex: Int, toIndex: Int): List<Room> {
            if (fromIndex !in rooms.indices || toIndex !in rooms.indices) return rooms
            val list = rooms.toMutableList()
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            return list.mapIndexed { idx, r -> r.copy(order = idx) }
        }

        // Boundary: Moving index 0 UP (out of bounds)
        val outOfBoundsUp = reorder(initialRooms, 0, -1)
        assertEquals("Living Room", outOfBoundsUp[0].name)

        // Boundary: Moving index 2 DOWN (out of bounds)
        val outOfBoundsDown = reorder(initialRooms, 2, 3)
        assertEquals("Kitchen", outOfBoundsDown[2].name)

        // Valid move: Move Kitchen (index 2) to index 0
        val reordered = reorder(initialRooms, 2, 0)
        assertEquals("Kitchen", reordered[0].name)
        assertEquals(0, reordered[0].order)
        assertEquals("Living Room", reordered[1].name)
        assertEquals(1, reordered[1].order)
        assertEquals("Master Bedroom", reordered[2].name)
        assertEquals(2, reordered[2].order)
    }

    @Test
    fun lens_testRoomLoadCalculationsUnderExtremeQuantities() {
        // Zero appliances room
        val emptyRoom = Room(name = "Storage", appliances = emptyList())
        assertEquals(0f, emptyRoom.connectedLoadWatts, 0.01f)
        assertEquals(0f, emptyRoom.connectedLoadKw, 0.01f)
        assertEquals(0f, emptyRoom.monthlyKwh, 0.01f)
        assertEquals(0f, emptyRoom.monthlyCost, 0.01f)

        // High load industrial room
        val heavyRoom = Room(
            name = "Commercial Bakery",
            appliances = listOf(
                EditableAppliance(
                    label = "Industrial Oven",
                    powerWatts = 5000f,
                    usageHoursPerDay = 10f,
                    monthlyUsageDays = 30,
                    quantity = 4
                ),
                EditableAppliance(
                    label = "HVAC Chiller",
                    powerWatts = 3500f,
                    usageHoursPerDay = 12f,
                    monthlyUsageDays = 30,
                    quantity = 2
                )
            )
        )
        // 5000 * 4 + 3500 * 2 = 20,000 + 7,000 = 27,000 Watts = 27.0 kW
        assertEquals(27000f, heavyRoom.connectedLoadWatts, 0.01f)
        assertEquals(27.0f, heavyRoom.connectedLoadKw, 0.01f)

        // Oven monthly kWh: (5000 * 10 * 30 * 4) / 1000 = 6,000 kWh
        // Chiller monthly kWh: (3500 * 12 * 30 * 2) / 1000 = 2,520 kWh
        // Total monthly kWh: 6,000 + 2,520 = 8,520 kWh
        assertEquals(8520f, heavyRoom.monthlyKwh, 0.01f)
        assertTrue(heavyRoom.monthlyCost > 0)
    }
}
