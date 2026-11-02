package com.example.e2e

import com.example.feature.assessment.util.AssessmentValidator
import com.example.feature.assessment.util.EstimateCalculator
import com.example.feature.assessment.util.SubsidyCalculator
import com.example.feature.home.ui.modules.MeterCycleBaseline
import com.example.feature.home.ui.modules.MeterReadingRecord
import com.example.feature.home.ui.modules.TNEBBillCalculator
import com.example.feature.lens.ui.Room
import com.example.feature.lens.ui.RoomStatus
import com.example.feature.lens.utils.ApplianceMaster
import com.example.feature.lens.utils.AuditReportExporter
import com.example.feature.meterreading.model.MeterOcrStatus
import com.example.feature.meterreading.parser.ReadingPlausibilityStatus
import com.example.feature.meterreading.parser.ReadingPlausibilityValidator
import com.example.feature.meterreading.parser.TnebReadingParser
import com.example.feature.recommends.model.RewardConfig
import com.example.feature.recommends.model.StreakState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class Tier2BoundaryAndCornerTest {

    // =========================================================================
    // 1. TNEB SLAB BILLING BOUNDARY TESTS
    // =========================================================================

    @Test
    fun boundary_testTnebZeroAndSingleUnit() {
        val resZero = TNEBBillCalculator.calculateTNEBBill(0.0)
        assertEquals(200.0, resZero.freeUnits, 0.001)
        assertEquals(0.0, resZero.chargeableUnits, 0.001)
        assertEquals(0.0, resZero.totalEnergyCharge, 0.001)

        val resOne = TNEBBillCalculator.calculateTNEBBill(1.0)
        assertEquals(200.0, resOne.freeUnits, 0.001)
        assertEquals(0.0, resOne.chargeableUnits, 0.001)
        assertEquals(0.0, resOne.totalEnergyCharge, 0.001)
    }

    @Test
    fun boundary_testTnebExact200UnitsFreeThreshold() {
        val res200 = TNEBBillCalculator.calculateTNEBBill(200.0)
        assertEquals(200.0, res200.freeUnits, 0.001)
        assertEquals(0.0, res200.chargeableUnits, 0.001)
        assertEquals(0.0, res200.totalEnergyCharge, 0.001)

        // 201 units -> 1 chargeable unit @ 4.70 = 4.70
        val res201 = TNEBBillCalculator.calculateTNEBBill(201.0)
        assertEquals(200.0, res201.freeUnits, 0.001)
        assertEquals(1.0, res201.chargeableUnits, 0.001)
        assertEquals(4.70, res201.totalEnergyCharge, 0.001)
    }

    @Test
    fun boundary_testTnebExact400UnitsThreshold() {
        // 400 units: 200 free, 200 @ 4.70 = 940.0
        val res400 = TNEBBillCalculator.calculateTNEBBill(400.0)
        assertEquals(200.0, res400.freeUnits, 0.001)
        assertEquals(200.0, res400.chargeableUnits, 0.001)
        assertEquals(940.0, res400.totalEnergyCharge, 0.001)

        // 401 units: 200 free, 200 @ 4.70 + 1 @ 6.30 = 946.30
        val res401 = TNEBBillCalculator.calculateTNEBBill(401.0)
        assertEquals(946.30, res401.totalEnergyCharge, 0.001)
    }

    @Test
    fun boundary_testTnebMajor500UnitsTransition() {
        // At exactly 500 units: <=500 slab, 200 free units!
        // Chargeable: 300 units (200 @ 4.70 = 940 + 100 @ 6.30 = 630) = 1570.0
        val res500 = TNEBBillCalculator.calculateTNEBBill(500.0)
        assertEquals(200.0, res500.freeUnits, 0.001)
        assertEquals(300.0, res500.chargeableUnits, 0.001)
        assertEquals(1570.0, res500.totalEnergyCharge, 0.001)

        // At 501 units: >500 slab, free units drop to 100!
        // Chargeable: 401 units:
        // 300 @ 4.70 = 1410
        // 100 @ 6.30 = 630
        // 1 @ 8.40 = 8.40
        // Total = 2048.40
        val res501 = TNEBBillCalculator.calculateTNEBBill(501.0)
        assertEquals(100.0, res501.freeUnits, 0.001)
        assertEquals(401.0, res501.chargeableUnits, 0.001)
        assertEquals(2048.40, res501.totalEnergyCharge, 0.001)
    }

    @Test
    fun boundary_testTnebHighSlabs800And1000Units() {
        // 800 units: >500 slab, 100 free, 700 chargeable:
        // 300 @ 4.70 (1410) + 100 @ 6.30 (630) + 100 @ 8.40 (840) + 200 @ 9.45 (1890) = 4770.0
        val res800 = TNEBBillCalculator.calculateTNEBBill(800.0)
        assertEquals(100.0, res800.freeUnits, 0.001)
        assertEquals(700.0, res800.chargeableUnits, 0.001)
        assertEquals(4770.0, res800.totalEnergyCharge, 0.001)

        // 1000 units: 700 units (4770) + 200 @ 10.50 (2100) = 6870.0
        val res1000 = TNEBBillCalculator.calculateTNEBBill(1000.0)
        assertEquals(900.0, res1000.chargeableUnits, 0.001)
        assertEquals(6870.0, res1000.totalEnergyCharge, 0.001)

        // 1001 units: 6870 + 1 @ 11.55 = 6881.55
        val res1001 = TNEBBillCalculator.calculateTNEBBill(1001.0)
        assertEquals(6881.55, res1001.totalEnergyCharge, 0.001)
    }

    // =========================================================================
    // 2. PM SURYA GHAR SUBSIDY BOUNDARY TESTS
    // =========================================================================

    @Test
    fun boundary_testSubsidyZeroCapacity() {
        val sub = SubsidyCalculator.calculate("Residential", "On-Grid", "DCR", 0.0)
        assertEquals(0.0, sub.amount, 0.001)
        assertTrue(sub.eligible)
    }

    @Test
    fun boundary_testSubsidyExactly1kW() {
        val sub = SubsidyCalculator.calculate("Residential", "On-Grid", "DCR", 1.0)
        assertEquals(30000.0, sub.amount, 0.001)
        assertTrue(sub.eligible)
    }

    @Test
    fun boundary_testSubsidyExactly2kW() {
        val sub = SubsidyCalculator.calculate("Residential", "On-Grid", "DCR", 2.0)
        assertEquals(60000.0, sub.amount, 0.001)
        assertTrue(sub.eligible)
    }

    @Test
    fun boundary_testSubsidyMidTier2Point5kW() {
        // 60,000 + (2.5 - 2.0) * 18,000 = 60,000 + 9,000 = 69,000
        val sub = SubsidyCalculator.calculate("Residential", "On-Grid", "DCR", 2.5)
        assertEquals(69000.0, sub.amount, 0.001)
        assertTrue(sub.eligible)
    }

    @Test
    fun boundary_testSubsidyExactly3kWAndAbove() {
        // At 3.0 kW: 60,000 + 1.0 * 18,000 = 78,000
        val sub3 = SubsidyCalculator.calculate("Residential", "On-Grid", "DCR", 3.0)
        assertEquals(78000.0, sub3.amount, 0.001)

        // Above 3.0 kW: capped at ₹78,000
        val sub3Point1 = SubsidyCalculator.calculate("Residential", "On-Grid", "DCR", 3.1)
        assertEquals(78000.0, sub3Point1.amount, 0.001)

        val sub10 = SubsidyCalculator.calculate("Residential", "On-Grid", "DCR", 10.0)
        assertEquals(78000.0, sub10.amount, 0.001)

        val sub100 = SubsidyCalculator.calculate("Residential", "On-Grid", "DCR", 100.0)
        assertEquals(78000.0, sub100.amount, 0.001)
    }

    @Test
    fun boundary_testSubsidyDisqualifications() {
        // Non-DCR panels
        val nonDcr = SubsidyCalculator.calculate("Residential", "On-Grid", "Non-DCR", 3.0)
        assertFalse(nonDcr.eligible)
        assertEquals(0.0, nonDcr.amount, 0.001)

        // Off-Grid system
        val offGrid = SubsidyCalculator.calculate("Residential", "Off-Grid", "DCR", 3.0)
        assertFalse(offGrid.eligible)
        assertEquals(0.0, offGrid.amount, 0.001)

        // Hybrid system
        val hybrid = SubsidyCalculator.calculate("Residential", "Hybrid", "DCR", 3.0)
        assertFalse(hybrid.eligible)
        assertEquals(0.0, hybrid.amount, 0.001)
    }

    // =========================================================================
    // 3. METER READING OCR & PLAUSIBILITY BOUNDARIES
    // =========================================================================

    @Test
    fun boundary_testOcrEmptyAndWhitespaceInputs() {
        val candidateEmpty = TnebReadingParser.parse("")
        assertEquals(MeterOcrStatus.NO_READING_FOUND, candidateEmpty.status)
        assertEquals(0.0, candidateEmpty.numericValue, 0.001)

        val candidateWhitespace = TnebReadingParser.parse("   \n\t   \n  ")
        assertEquals(MeterOcrStatus.NO_READING_FOUND, candidateWhitespace.status)
    }

    @Test
    fun boundary_testOcrCorruptedLettersOnly() {
        val candidate = TnebReadingParser.parse("NO_DIGITS_HERE_ONLY_LETTERS")
        assertEquals(MeterOcrStatus.NO_READING_FOUND, candidate.status)
    }

    @Test
    fun boundary_testOcrDroppedDecimalRecoveryWithBaseline() {
        // Digital LCD: 165977 extracted as 6 digits without decimal
        // Baseline is 16500.0 kWh (incremental delta = 97.7 kWh)
        val candidate = TnebReadingParser.parse(
            fullRawText = "165977 kWh",
            baselineKwh = 16500.0
        )
        // Dropped decimal recovery should resolve 16597.7
        assertEquals(16597.7, candidate.numericValue, 0.1)
    }

    @Test
    fun boundary_testPlausibilityValidatorExactSameReading() {
        val latest = MeterReadingRecord(readingValue = 15400.0, readingDate = "2026-09-15")
        val status = ReadingPlausibilityValidator.validate(15400.0, null, latest)
        // Exact same reading (0 change) is mathematically valid
        assertEquals(ReadingPlausibilityStatus.Valid, status)
    }

    @Test
    fun boundary_testPlausibilityValidatorBoundaryJump15000() {
        val latest = MeterReadingRecord(readingValue = 10000.0, readingDate = "2026-09-15")

        // Jump of 14,999 is valid
        val statusValid = ReadingPlausibilityValidator.validate(24999.0, null, latest)
        assertEquals(ReadingPlausibilityStatus.Valid, statusValid)

        // Jump of 15,001 is suspicious -> Warning
        val statusWarning = ReadingPlausibilityValidator.validate(25001.0, null, latest)
        assertTrue(statusWarning is ReadingPlausibilityStatus.Warning)
    }

    @Test
    fun boundary_testPlausibilityValidatorSmallDrop() {
        val latest = MeterReadingRecord(readingValue = 12000.0, readingDate = "2026-09-15")
        val status = ReadingPlausibilityValidator.validate(11999.0, null, latest)
        assertTrue(status is ReadingPlausibilityStatus.LowerThanPrevious)
    }

    // =========================================================================
    // 4. SOLAR ASSESSMENT & AREA BOUNDARIES
    // =========================================================================

    @Test
    fun boundary_testAssessmentInputValidationEdgeValues() {
        assertFalse(AssessmentValidator.isValidConsumption("0.0"))
        assertFalse(AssessmentValidator.isValidConsumption("-1"))
        assertTrue(AssessmentValidator.isValidConsumption("1"))
        assertTrue(AssessmentValidator.isValidConsumption("99999")) // 5 chars
        assertFalse(AssessmentValidator.isValidConsumption("100000")) // 6 chars

        assertFalse(AssessmentValidator.isValidArea("0.0"))
        assertTrue(AssessmentValidator.isValidArea("0.1"))
        assertTrue(AssessmentValidator.isValidArea("100000.0")) // Exact max
        assertFalse(AssessmentValidator.isValidArea("100000.1")) // Exceeds max
    }

    @Test
    fun boundary_testAreaSuitabilityExactBoundary() {
        // 360 kWh / 120 = 3.0 kW -> Required Area = 30.0 sq m
        val capacity = EstimateCalculator.calculateRecommendedSystemSize(360.0)
        val reqArea = EstimateCalculator.calculateRequiredArea(capacity)
        assertEquals(30.0, reqArea, 0.001)

        // Available area exactly equal to required area -> Suitable
        val resExact = EstimateCalculator.calculateCostEstimate(360.0, 30.0, "Rooftop", "On-Grid", "DCR", "Residential")
        assertEquals("Suitable", resExact.areaStatus)

        // Available area 29.9 sq m -> Insufficient Area
        val resUnder = EstimateCalculator.calculateCostEstimate(360.0, 29.9, "Rooftop", "On-Grid", "DCR", "Residential")
        assertEquals("Insufficient Area", resUnder.areaStatus)
    }

    // =========================================================================
    // 5. SOLAR LENS & APPLIANCE AUDIT BOUNDARIES
    // =========================================================================

    @Test
    fun boundary_testApplianceZeroWattageAndZeroHours() {
        val kwhZeroWatts = ApplianceMaster.calculateMonthlyKwh(0f, 6f, 30, 1)
        // Power <= 0 clamped to 1f fallback: (1 * 6 * 30 * 1) / 1000 = 0.18 kWh
        assertTrue(kwhZeroWatts >= 0f)

        val kwhZeroHours = ApplianceMaster.calculateMonthlyKwh(100f, 0f, 30, 1)
        assertEquals(0f, kwhZeroHours, 0.001f)

        val kwhZeroDays = ApplianceMaster.calculateMonthlyKwh(100f, 6f, 0, 1)
        assertEquals(0f, kwhZeroDays, 0.001f)
    }

    @Test
    fun boundary_testAuditReportEmptyRoomsList() {
        val md = AuditReportExporter.generateMarkdownContent(emptyList(), Date())
        assertTrue(md.contains("Insufficient Audit Data"))
        assertTrue(md.contains("0.0 kW"))
        assertTrue(md.contains("₹0.00"))
    }

    @Test
    fun boundary_testAuditReportSingleRoomZeroLoad() {
        val room = Room(
            id = "R1",
            name = "Store Room",
            status = RoomStatus.COMPLETED,
            appliances = emptyList()
        )
        val md = AuditReportExporter.generateMarkdownContent(listOf(room), Date())
        assertTrue(md.contains("No electrical appliances recorded in this zone."))
        assertTrue(md.contains("Insufficient Audit Data"))
    }

    @Test
    fun boundary_testAuditReportExtremeSpecialCharacterSanitization() {
        val adversarial = "Kitchen ||| `rm -rf` \n\r\n # Header <script>alert(1)</script>"
        val clean = AuditReportExporter.sanitizeMarkdownText(adversarial)
        assertFalse(clean.contains("\n"))
        assertFalse(clean.contains("\r"))
        assertFalse(clean.contains("`"))
    }

    // =========================================================================
    // 6. STREAK STATE & REWARD BONUS BOUNDARIES
    // =========================================================================

    @Test
    fun boundary_testStreakCycleTransitions() {
        val s0 = StreakState(currentStreak = 0)
        assertEquals(0, s0.daysInCurrentCycle)
        assertFalse(s0.isMilestonePending)

        val s7 = StreakState(currentStreak = 7)
        assertEquals(7, s7.daysInCurrentCycle)
        assertTrue(s7.isMilestonePending)

        val s8 = StreakState(currentStreak = 8)
        assertEquals(1, s8.daysInCurrentCycle) // 8 % 7 = 1
        assertFalse(s8.isMilestonePending)

        val s14 = StreakState(currentStreak = 14)
        assertEquals(7, s14.daysInCurrentCycle) // 14 % 7 == 0 -> 7
        assertTrue(s14.isMilestonePending)
    }

    @Test
    fun boundary_testRewardReductionBonusExactPercentages() {
        // Boundary at 5.0%
        assertEquals(0, RewardConfig.getReductionBonus(4.999))
        assertEquals(15, RewardConfig.getReductionBonus(5.0))

        // Boundary at 10.0%
        assertEquals(15, RewardConfig.getReductionBonus(9.999))
        assertEquals(30, RewardConfig.getReductionBonus(10.0))

        // Boundary at 20.0%
        assertEquals(30, RewardConfig.getReductionBonus(19.999))
        assertEquals(50, RewardConfig.getReductionBonus(20.0))

        // Negative reduction (consumption increased)
        assertEquals(0, RewardConfig.getReductionBonus(-15.0))
    }
}
