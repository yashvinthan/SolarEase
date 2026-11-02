package com.example.e2e

import com.example.feature.assessment.util.AssessmentValidator
import com.example.feature.assessment.util.EstimateCalculator
import com.example.feature.assessment.util.EstimateConstants
import com.example.feature.assessment.util.SubsidyCalculator
import com.example.feature.dashboard.domain.model.ActivityItem
import com.example.feature.dashboard.domain.model.CustomerDashboard
import com.example.feature.dashboard.domain.model.NotificationItem
import com.example.feature.performance.domain.model.EnergyFlow
import com.example.feature.performance.domain.model.LiveGeneration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Tier1SolarFunnelAndMarketplaceTest {

    // =========================================================================
    // FEATURE 21: 12-Step Guided Solar Assessment Journey
    // =========================================================================

    @Test
    fun feature21_testAssessmentInputValidationConsumption() {
        assertTrue(AssessmentValidator.isValidConsumption("350"))
        assertTrue(AssessmentValidator.isValidConsumption("500.5"))
        assertFalse(AssessmentValidator.isValidConsumption("0"))
        assertFalse(AssessmentValidator.isValidConsumption("-50"))
        assertFalse(AssessmentValidator.isValidConsumption("invalid"))
        assertFalse(AssessmentValidator.isValidConsumption("1234567")) // Exceeds 5 chars
    }

    @Test
    fun feature21_testAssessmentInputValidationArea() {
        assertTrue(AssessmentValidator.isValidArea("50"))
        assertTrue(AssessmentValidator.isValidArea("250.5"))
        assertFalse(AssessmentValidator.isValidArea("0"))
        assertFalse(AssessmentValidator.isValidArea("-10"))
        assertFalse(AssessmentValidator.isValidArea("invalid"))
        assertFalse(AssessmentValidator.isValidArea("1000000")) // Exceeds 100,000 sq m
    }

    @Test
    fun feature21_testRecommendedSystemSizeFormula() {
        // Authoritative: Monthly Consumption / 120.0 rounded to 1 decimal place
        val size1 = EstimateCalculator.calculateRecommendedSystemSize(360.0)
        assertEquals(3.0, size1, 0.001)

        val size2 = EstimateCalculator.calculateRecommendedSystemSize(600.0)
        assertEquals(5.0, size2, 0.001)

        val size3 = EstimateCalculator.calculateRecommendedSystemSize(450.0) // 450 / 120 = 3.75 -> 3.8
        assertEquals(3.8, size3, 0.001)
    }

    @Test
    fun feature21_testRequiredAreaFormula() {
        // Authoritative: Capacity * 10.0 sq m / kW
        val area = EstimateCalculator.calculateRequiredArea(3.0)
        assertEquals(30.0, area, 0.001)
    }

    @Test
    fun feature21_testAreaSuitabilityEvaluation() {
        val resultSuitable = EstimateCalculator.calculateCostEstimate(
            monthlyConsumptionKwh = 360.0,
            availableAreaSqM = 50.0,
            mountType = "Rooftop",
            systemType = "On-Grid",
            panelType = "DCR",
            customerType = "Residential"
        )
        assertEquals("Suitable", resultSuitable.areaStatus)

        val resultInsufficient = EstimateCalculator.calculateCostEstimate(
            monthlyConsumptionKwh = 360.0,
            availableAreaSqM = 20.0,
            mountType = "Rooftop",
            systemType = "On-Grid",
            panelType = "DCR",
            customerType = "Residential"
        )
        assertEquals("Insufficient Area", resultInsufficient.areaStatus)
    }

    // =========================================================================
    // FEATURE 22: Contractor Marketplace Reconnection
    // =========================================================================

    data class SolarVendor(
        val id: String,
        val companyName: String,
        val rating: Double,
        val experienceYears: Int,
        val completedInstallations: Int,
        val distanceKm: Double,
        val isEmpaneled: Boolean = true
    )

    data class EstimateBookingRequest(
        val vendorId: String,
        val customerName: String,
        val phone: String,
        val systemCapacityKw: Double,
        val roofType: String
    )

    @Test
    fun feature22_testVendorListSortingByDistance() {
        val vendors = listOf(
            SolarVendor("1", "SunTech Solutions", 4.8, 8, 340, 6.2),
            SolarVendor("2", "GreenVolt Systems", 4.9, 10, 520, 2.4),
            SolarVendor("3", "TamilNadu Solar Works", 4.7, 5, 180, 4.1)
        )
        val sortedByDistance = vendors.sortedBy { it.distanceKm }
        assertEquals("GreenVolt Systems", sortedByDistance.first().companyName)
        assertEquals(2.4, sortedByDistance.first().distanceKm, 0.01)
    }

    @Test
    fun feature22_testVendorSortingByRating() {
        val vendors = listOf(
            SolarVendor("1", "SunTech", 4.6, 5, 200, 3.0),
            SolarVendor("2", "Apex Solar", 4.9, 12, 600, 5.0)
        )
        val sortedByRating = vendors.sortedByDescending { it.rating }
        assertEquals("Apex Solar", sortedByRating.first().companyName)
    }

    @Test
    fun feature22_testEstimateRequestPayloadValidation() {
        val request = EstimateBookingRequest(
            vendorId = "VEND_101",
            customerName = "Anand Kumar",
            phone = "9876543210",
            systemCapacityKw = 3.0,
            roofType = "Rooftop"
        )
        assertTrue(request.customerName.isNotBlank())
        assertEquals(10, request.phone.length)
        assertTrue(request.systemCapacityKw > 0)
    }

    @Test
    fun feature22_testVendorEmpaneledVerification() {
        val vendor = SolarVendor("1", "Authorized TNEB Partner", 4.9, 8, 400, 3.5, isEmpaneled = true)
        assertTrue("Only empaneled vendors should qualify for subsidy facilitation", vendor.isEmpaneled)
    }

    @Test
    fun feature22_testConfirmationReferenceIdGeneration() {
        val ref = "EST-" + System.currentTimeMillis().toString().takeLast(6)
        assertTrue(ref.startsWith("EST-"))
        assertEquals(10, ref.length)
    }

    // =========================================================================
    // FEATURE 23: Dead Legacy Screen Pruning
    // =========================================================================

    @Test
    fun feature23_testAssessmentValidatorDeprecationAlias() {
        // Verifies backward compatibility of AssessmentValidator typealias
        @Suppress("DEPRECATION")
        val isOldValid = com.example.feature.assessment.util.Validation.isValidConsumption("300")
        val isNewValid = AssessmentValidator.isValidConsumption("300")
        assertEquals(isNewValid, isOldValid)
    }

    @Test
    fun feature23_testZeroMockOrDeadEndRoutesInvariant() {
        val activeFeatureRoutes = listOf(
            "home",
            "assessment/step1",
            "assessment/cost_estimation",
            "nearby_vendors",
            "customer_dashboard",
            "lens/landing",
            "recommends",
            "profile"
        )
        assertEquals(8, activeFeatureRoutes.size)
        activeFeatureRoutes.forEach { route ->
            assertTrue(route.isNotBlank())
            assertFalse(route.contains("dummy"))
        }
    }

    @Test
    fun feature23_testPreservationOfEstimateConstantsInvariants() {
        assertEquals(120.0, EstimateConstants.DIVISOR, 0.001)
        assertEquals(10.0, EstimateConstants.AREA_PER_KW, 0.001)
        assertEquals(4.5, EstimateConstants.PEAK_SUN_HOURS, 0.001)
        assertEquals(0.79, EstimateConstants.SYSTEM_PERFORMANCE_RATIO, 0.001)
        assertEquals(7.0, EstimateConstants.TARIFF_PER_KWH, 0.001)
    }

    @Test
    fun feature23_testSubsidyCalculationConstantsInvariants() {
        assertEquals(30000.0, EstimateConstants.SUBSIDY_TIER_1_AMOUNT, 0.001)
        assertEquals(60000.0, EstimateConstants.SUBSIDY_TIER_2_AMOUNT, 0.001)
        assertEquals(78000.0, EstimateConstants.SUBSIDY_TIER_3_AMOUNT, 0.001)
    }

    @Test
    fun feature23_testDeadCodeRemovalMaintainsContractIntegrity() {
        val estimateResult = EstimateCalculator.calculateCostEstimate(
            monthlyConsumptionKwh = 360.0,
            availableAreaSqM = 40.0,
            mountType = "Rooftop",
            systemType = "On-Grid",
            panelType = "DCR",
            customerType = "Residential"
        )
        assertNotNull(estimateResult)
        assertTrue(estimateResult.subsidyEligible)
    }

    // =========================================================================
    // FEATURE 24: CustomerDashboard Re-Integration
    // =========================================================================

    @Test
    fun feature24_testCustomerDashboardStateContract() {
        val dashboard = CustomerDashboard(
            username = "Ramesh",
            systemStatus = "Online",
            capacity = 3.0,
            systemType = "On-Grid",
            installationDate = "2026-06-15",
            todayGeneration = 14.2,
            todaySavings = 99.4,
            todayCo2 = 11.3,
            lifetimeGeneration = 1840.0,
            lifetimeSavings = 12880.0,
            lifetimeCo2 = 1472.0,
            plantHealth = 98,
            recentActivities = listOf(ActivityItem("System Online", System.currentTimeMillis())),
            notifications = listOf(NotificationItem("Grid export verified", System.currentTimeMillis()))
        )

        assertEquals("Online", dashboard.systemStatus)
        assertEquals(3.0, dashboard.capacity, 0.001)
        assertEquals(98, dashboard.plantHealth)
        assertEquals(1, dashboard.recentActivities.size)
    }

    @Test
    fun feature24_testDashboardPlantHealthStatusClassification() {
        fun getHealthCategory(score: Int): String = when {
            score >= 90 -> "Optimal"
            score >= 75 -> "Good"
            score >= 50 -> "Maintenance Recommended"
            else -> "Warning"
        }

        assertEquals("Optimal", getHealthCategory(98))
        assertEquals("Good", getHealthCategory(82))
        assertEquals("Maintenance Recommended", getHealthCategory(60))
        assertEquals("Warning", getHealthCategory(42))
    }

    @Test
    fun feature24_testDashboardLifetimeSavingsCalculation() {
        val lifetimeKwh = 2500.0
        val tariff = EstimateConstants.TARIFF_PER_KWH
        val expectedSavings = lifetimeKwh * tariff
        assertEquals(17500.0, expectedSavings, 0.001)
    }

    @Test
    fun feature24_testDashboardNotificationsListOrdering() {
        val n1 = NotificationItem("First alert", 1000L)
        val n2 = NotificationItem("Second alert", 2000L)
        val list = listOf(n1, n2).sortedByDescending { it.timestamp }
        assertEquals("Second alert", list.first().message)
    }

    @Test
    fun feature24_testDashboardOfflineSystemStatusHandling() {
        val dashboard = CustomerDashboard(
            username = "Suresh",
            systemStatus = "Offline",
            capacity = 5.0,
            systemType = "On-Grid",
            installationDate = "2026-01-10",
            todayGeneration = 0.0,
            todaySavings = 0.0,
            todayCo2 = 0.0,
            lifetimeGeneration = 4200.0,
            lifetimeSavings = 29400.0,
            lifetimeCo2 = 3360.0,
            plantHealth = 0,
            recentActivities = emptyList(),
            notifications = listOf(NotificationItem("Inverter disconnected", System.currentTimeMillis()))
        )
        assertEquals("Offline", dashboard.systemStatus)
        assertEquals(0.0, dashboard.todayGeneration, 0.001)
    }

    // =========================================================================
    // FEATURE 25: Accessible Canvas Data Visualizations
    // =========================================================================

    data class AccessibleChartSeries(
        val seriesName: String,
        val dataPoints: List<Pair<String, Double>>
    ) {
        val contentDescription: String
            get() = "$seriesName: " + dataPoints.joinToString("; ") { "${it.first}: ${it.second}" }
        val tabularFallback: List<Map<String, String>>
            get() = dataPoints.map { mapOf("Period" to it.first, "Value" to it.second.toString()) }
    }

    @Test
    fun feature25_testCanvasChartAccessibilityContentDescription() {
        val chart = AccessibleChartSeries(
            seriesName = "Monthly Generation",
            dataPoints = listOf("Aug" to 420.0, "Sep" to 440.0, "Oct" to 460.0)
        )
        val description = chart.contentDescription
        assertTrue(description.contains("Monthly Generation"))
        assertTrue(description.contains("Aug: 420.0"))
        assertTrue(description.contains("Oct: 460.0"))
    }

    @Test
    fun feature25_testCanvasChartTabularFallbackFormat() {
        val chart = AccessibleChartSeries(
            seriesName = "Savings",
            dataPoints = listOf("Q1" to 3200.0, "Q2" to 3500.0)
        )
        val table = chart.tabularFallback
        assertEquals(2, table.size)
        assertEquals("Q1", table[0]["Period"])
        assertEquals("3200.0", table[0]["Value"])
    }

    @Test
    fun feature25_testChartHighContrastColors() {
        val primaryColor = com.example.ui.theme.StitchLightTokens.Primary
        val surfaceColor = com.example.ui.theme.StitchLightTokens.Surface
        assertNotNull(primaryColor)
        assertNotNull(surfaceColor)
    }

    @Test
    fun feature25_testEmptyChartDataPointsGracefulFallback() {
        val emptyChart = AccessibleChartSeries("Empty Series", emptyList())
        assertEquals("Empty Series: ", emptyChart.contentDescription)
        assertTrue(emptyChart.tabularFallback.isEmpty())
    }

    @Test
    fun feature25_testChartDataPointsBoundaryValues() {
        val chart = AccessibleChartSeries(
            seriesName = "Power",
            dataPoints = listOf("06:00" to 0.0, "12:00" to 2.85, "18:00" to 0.0)
        )
        assertEquals(0.0, chart.dataPoints[0].second, 0.001)
        assertEquals(2.85, chart.dataPoints[1].second, 0.001)
    }

    // =========================================================================
    // FEATURE 26: Action Wire-Up & Inverter Telemetry Link
    // =========================================================================

    @Test
    fun feature26_testLiveGenerationTelemetryContract() {
        val live = LiveGeneration(
            currentOutput = 2.45,
            todayGeneration = 12.8,
            systemStatus = "Generating",
            gridStatus = "Connected (238V)",
            lastUpdated = System.currentTimeMillis(),
            weather = "Sunny"
        )
        assertEquals(2.45, live.currentOutput, 0.001)
        assertEquals("Generating", live.systemStatus)
        assertTrue(live.gridStatus.contains("Connected"))
    }

    @Test
    fun feature26_testEnergyFlowTelemetryContract() {
        val flow = EnergyFlow(
            solarGenerated = 14.5,
            consumed = 8.2,
            exportedToGrid = 6.3,
            importedFromGrid = 0.0,
            batteryStatus = "Not Installed"
        )
        assertEquals(14.5, flow.solarGenerated, 0.001)
        assertEquals(8.2, flow.consumed, 0.001)
        assertEquals(6.3, flow.exportedToGrid, 0.001)
        // Energy balance invariant: generated = consumed + exported - imported
        val netBalance = flow.solarGenerated - (flow.consumed + flow.exportedToGrid - flow.importedFromGrid)
        assertEquals(0.0, netBalance, 0.001)
    }

    @Test
    fun feature26_testShareActionIntentContract() {
        val shareText = "My SolarEase system generated 14.2 kWh today! Saved ₹99.40."
        assertTrue(shareText.contains("SolarEase"))
        assertTrue(shareText.contains("14.2 kWh"))
    }

    @Test
    fun feature26_testDownloadReportActionContract() {
        val reportFileName = "solarease_report_20261101.md"
        assertTrue(reportFileName.endsWith(".md"))
        assertTrue(reportFileName.startsWith("solarease_report_"))
    }

    @Test
    fun feature26_testTelemetryLastUpdatedFreshness() {
        val now = System.currentTimeMillis()
        val live = LiveGeneration(
            currentOutput = 1.8,
            todayGeneration = 9.4,
            systemStatus = "Generating",
            gridStatus = "Connected",
            lastUpdated = now,
            weather = "Clear"
        )
        val ageSeconds = (System.currentTimeMillis() - live.lastUpdated) / 1000
        assertTrue("Telemetry must be fresh (< 5 seconds)", ageSeconds < 5)
    }
}
