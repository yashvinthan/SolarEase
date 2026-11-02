package com.example.e2e

import com.example.feature.assessment.util.AssessmentValidator
import com.example.feature.assessment.util.EstimateCalculator
import com.example.feature.assessment.util.EstimateConstants
import com.example.feature.assessment.util.SubsidyCalculator
import com.example.feature.dashboard.domain.model.CustomerDashboard
import com.example.feature.home.ui.modules.MeterCycleBaseline
import com.example.feature.home.ui.modules.MeterReadingRecord
import com.example.feature.home.ui.modules.TNEBBillCalculator
import com.example.feature.lens.ui.EditableAppliance
import com.example.feature.lens.ui.Room
import com.example.feature.lens.ui.RoomStatus
import com.example.feature.lens.utils.AuditReportExporter
import com.example.feature.meterreading.model.MeterOcrStatus
import com.example.feature.meterreading.parser.ReadingPlausibilityStatus
import com.example.feature.meterreading.parser.ReadingPlausibilityValidator
import com.example.feature.meterreading.parser.TnebReadingParser
import com.example.feature.recommends.model.CoinEventType
import com.example.feature.recommends.model.CoinTransaction
import com.example.feature.recommends.model.RewardConfig
import com.example.feature.recommends.model.StreakState
import com.example.feature.recommends.model.UserRewardData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class Tier4RealWorldScenariosTest {

    // =========================================================================
    // SCENARIO 1: Urban Apartment Retrofit (Chennai 2BHK)
    // =========================================================================

    @Test
    fun scenario1_chennaiUrbanApartmentRetrofit() {
        // Consumer: Chennai resident in a 2BHK apartment with limited rooftop space (20 sq m)
        val biMonthlyUnits = 360.0
        val monthlyUnits = biMonthlyUnits / 2.0 // 180 kWh/month
        val availableRoofArea = 20.0

        // Step 1: Pre-solar electricity cost under TNEB LT-1A
        // 360 units bi-monthly: 200 free units, remaining 160 units @ 4.70 = ₹752.0 per bi-month
        val billPreSolar = TNEBBillCalculator.calculateTNEBBill(biMonthlyUnits)
        assertEquals(200.0, billPreSolar.freeUnits, 0.001)
        assertEquals(160.0, billPreSolar.chargeableUnits, 0.001)
        assertEquals(752.0, billPreSolar.totalEnergyCharge, 0.001)

        // Annual electricity cost without solar: 752.0 * 6 = ₹4512.0
        val annualCostBefore = billPreSolar.totalEnergyCharge * 6.0
        assertEquals(4512.0, annualCostBefore, 0.001)

        // Step 2: Solar Assessment
        assertTrue(AssessmentValidator.isValidConsumption(monthlyUnits.toString()))
        assertTrue(AssessmentValidator.isValidArea(availableRoofArea.toString()))

        val capacity = EstimateCalculator.calculateRecommendedSystemSize(monthlyUnits) // 180 / 120 = 1.5 kW
        assertEquals(1.5, capacity, 0.001)

        val requiredArea = EstimateCalculator.calculateRequiredArea(capacity) // 1.5 * 10 = 15.0 sq m
        assertEquals(15.0, requiredArea, 0.001)
        assertTrue("Available roof area (20 m²) comfortably accommodates 15 m²", requiredArea <= availableRoofArea)

        // Step 3: Financial Feasibility
        val estimate = EstimateCalculator.calculateCostEstimate(
            monthlyConsumptionKwh = monthlyUnits,
            availableAreaSqM = availableRoofArea,
            mountType = "Rooftop",
            systemType = "On-Grid",
            panelType = "DCR",
            customerType = "Residential"
        )
        assertEquals("Suitable", estimate.areaStatus)
        assertEquals(82500.0, estimate.grossTotal, 0.001) // 1.5 * 55,000 = 82,500
        assertTrue(estimate.subsidyEligible)
        assertEquals(45000.0, estimate.subsidyAmount, 0.001) // 1.5 * 30,000 = 45,000
        assertEquals(37500.0, estimate.netPayable, 0.001) // 82,500 - 45,000 = 37,500

        // Step 4: Payback Period & Annual Savings
        // Annual savings: 180 kWh * 12 months * ₹7.0 = ₹15,120/year
        assertEquals(15120.0, estimate.annualSavings, 0.001)
        // Payback: 37,500 / 15,120 = 2.48 -> 2.5 years
        assertEquals(2.5, estimate.estimatedPaybackYears, 0.01)

        // Step 5: Post-solar TNEB grid electricity offset:
        // Solar system generates ~180 kWh/month -> 360 kWh bi-monthly.
        // Net grid consumption = 0 kWh. Net bill = ₹0.00.
        val billPostSolar = TNEBBillCalculator.calculateTNEBBill(0.0)
        assertEquals(0.0, billPostSolar.totalEnergyCharge, 0.001)
        val billReduction = billPreSolar.totalEnergyCharge - billPostSolar.totalEnergyCharge
        assertEquals(752.0, billReduction, 0.001)
    }

    // =========================================================================
    // SCENARIO 2: High-Consumption Suburban Villa (Coimbatore Independent Home)
    // =========================================================================

    @Test
    fun scenario2_coimbatoreSuburbanVillaHighConsumption() {
        // Consumer: Coimbatore independent house with central AC, 980 kWh bi-monthly consumption
        val biMonthlyUnits = 980.0
        val monthlyUnits = biMonthlyUnits / 2.0 // 490 kWh/month
        val availableRoofArea = 60.0 // 60 sq m concrete roof

        // Step 1: Pre-solar bill in high slab (> 500 kWh)
        val billPre = TNEBBillCalculator.calculateTNEBBill(biMonthlyUnits)
        assertEquals(100.0, billPre.freeUnits, 0.001) // Drops to 100 free units in high slab
        assertEquals(880.0, billPre.chargeableUnits, 0.001)
        assertEquals(6660.0, billPre.totalEnergyCharge, 0.001) // Huge bi-monthly bill!

        // Annual bill without solar: 6660.0 * 6 = ₹39,960.0
        val annualPreSolarCost = billPre.totalEnergyCharge * 6.0
        assertEquals(39960.0, annualPreSolarCost, 0.001)

        // Step 2: Sizing: 490 / 120 = 4.08 -> 4.1 kW system
        val capacity = EstimateCalculator.calculateRecommendedSystemSize(monthlyUnits)
        assertEquals(4.1, capacity, 0.001)
        val requiredArea = EstimateCalculator.calculateRequiredArea(capacity) // 41.0 sq m
        assertTrue(requiredArea <= availableRoofArea)

        // Step 3: Full PM Surya Ghar maximum subsidy cap applied
        val estimate = EstimateCalculator.calculateCostEstimate(
            monthlyConsumptionKwh = monthlyUnits,
            availableAreaSqM = availableRoofArea,
            mountType = "Rooftop",
            systemType = "On-Grid",
            panelType = "DCR",
            customerType = "Residential"
        )
        assertEquals("Suitable", estimate.areaStatus)
        assertEquals(78000.0, estimate.subsidyAmount, 0.001) // Max subsidy cap ₹78,000!
        assertEquals(225500.0, estimate.grossTotal, 0.001) // 4.1 * 55,000 = 225,500
        assertEquals(147500.0, estimate.netPayable, 0.001) // 225,500 - 78,000 = 147,500

        // Step 4: Payback: Annual savings = 490 * 12 * 7.0 = ₹41,160.
        // Payback = 147,500 / 41,160 = 3.58 -> 3.6 years
        assertEquals(41160.0, estimate.annualSavings, 0.001)
        assertEquals(3.6, estimate.estimatedPaybackYears, 0.01)

        // Step 5: System commissioning into CustomerDashboard
        val dashboard = CustomerDashboard(
            username = "Sundar",
            systemStatus = "Online",
            capacity = capacity,
            systemType = "On-Grid",
            installationDate = "2026-11-01",
            todayGeneration = 19.5,
            todaySavings = 136.5,
            todayCo2 = 15.6,
            lifetimeGeneration = 19.5,
            lifetimeSavings = 136.5,
            lifetimeCo2 = 15.6,
            plantHealth = 100,
            recentActivities = emptyList(),
            notifications = emptyList()
        )
        assertEquals("Online", dashboard.systemStatus)
        assertEquals(100, dashboard.plantHealth)
    }

    // =========================================================================
    // SCENARIO 3: Rural Agricultural / Off-Grid Outpost (Madurai Farmhouse)
    // =========================================================================

    @Test
    fun scenario3_maduraiRuralOffGridFarmhouse() {
        // Step 1: Solar Lens whole-audit of farmhouse
        val pump = EditableAppliance("A1", "Water Pump (0.5–1 HP)", 500f, 4f, 20, 1)
        val lights = EditableAppliance("A2", "LED Tube Light", 36f, 8f, 30, 3)
        val fan = EditableAppliance("A3", "Ceiling Fan", 75f, 10f, 30, 2)

        val room = Room(
            id = "R_FARM",
            name = "Farmhouse Main",
            status = RoomStatus.COMPLETED,
            appliances = listOf(pump, lights, fan)
        )

        // Step 2: Export Markdown audit report
        val md = AuditReportExporter.generateMarkdownContent(listOf(room), Date())
        assertTrue(md.contains("Farmhouse Main"))
        assertTrue(md.contains("Water Pump (0.5–1 HP)"))

        // Step 3: Off-Grid System assessment
        val monthlyUnits = room.monthlyKwh.toDouble()
        val capacity = EstimateCalculator.calculateRecommendedSystemSize(monthlyUnits) // 110.9 / 120 = 0.9 kW
        assertEquals(0.9, capacity, 0.001)

        val estimate = EstimateCalculator.calculateCostEstimate(
            monthlyConsumptionKwh = monthlyUnits,
            availableAreaSqM = 50.0,
            mountType = "Ground",
            systemType = "Off-Grid",
            panelType = "DCR",
            customerType = "Residential"
        )

        // Off-Grid is not eligible for PM Surya Ghar subsidy
        assertFalse("Off-Grid systems are ineligible for PM Surya Ghar subsidy", estimate.subsidyEligible)
        assertEquals(0.0, estimate.subsidyAmount, 0.001)
        assertEquals(estimate.grossTotal, estimate.netPayable, 0.001)
    }

    // =========================================================================
    // SCENARIO 4: Professional Clinic / Daytime Commercial (Tiruchirappalli)
    // =========================================================================

    @Test
    fun scenario4_tiruchirappalliCommercialClinicHybrid() {
        // Daytime medical clinic with high reliable power requirement
        val monthlyUnits = 600.0 // 5.0 kW system
        val capacity = EstimateCalculator.calculateRecommendedSystemSize(monthlyUnits)
        assertEquals(5.0, capacity, 0.001)

        val requiredArea = EstimateCalculator.calculateRequiredArea(capacity)
        assertEquals(50.0, requiredArea, 0.001)

        // Hybrid system with battery storage
        val estimate = EstimateCalculator.calculateCostEstimate(
            monthlyConsumptionKwh = monthlyUnits,
            availableAreaSqM = 60.0,
            mountType = "Rooftop",
            systemType = "Hybrid",
            panelType = "DCR",
            customerType = "Commercial"
        )

        // Hybrid system base rate includes storage inverter premium (55,000 + 20,000 = ₹75,000/kW)
        assertEquals(75000.0, estimate.baseRatePerKW, 0.001)
        assertEquals(375000.0, estimate.grossTotal, 0.001)
        // Hybrid systems are ineligible for residential PM Surya Ghar subsidy
        assertFalse(estimate.subsidyEligible)
        assertEquals(0.0, estimate.subsidyAmount, 0.001)
        assertEquals(375000.0, estimate.netPayable, 0.001)

        // High annual savings from grid displacement: 600 * 12 * 7.0 = ₹50,400/year
        assertEquals(50400.0, estimate.annualSavings, 0.001)
    }

    // =========================================================================
    // SCENARIO 5: End-to-End Meter OCR to Verified Reward Ledger (Salem Lifecycle)
    // =========================================================================

    @Test
    fun scenario5_salemMeterOcrToVerifiedRewardLedgerLifecycle() {
        // Step 1: User starts with historical baseline of 16,250.0 kWh (395 units in previous 60-day cycle = 6.58 kWh/day)
        val baseline = MeterCycleBaseline(cycleId = "C1", baselineDate = "2026-08-01", baselineReading = 16250.0)
        val priorReading = MeterReadingRecord(readingValue = 16250.0, readingDate = "2026-08-01")

        // Step 2: Two months later, user captures meter photo; OCR recognizes cumulative reading
        val ocrRaw = "L&T TNEB\n16597.7 kWh\n240V 50Hz"
        val candidate = TnebReadingParser.parse(ocrRaw)
        assertEquals(MeterOcrStatus.DETECTED_SUCCESS, candidate.status)
        assertEquals(16597.7, candidate.numericValue, 0.01)

        // Step 3: Plausibility validation confirms reading is higher than baseline and within realistic bounds
        val plausibility = ReadingPlausibilityValidator.validate(candidate.numericValue, baseline, priorReading)
        assertEquals(ReadingPlausibilityStatus.Valid, plausibility)

        // Step 4: Calculate consumption delta (16,597.7 - 16,250.0 = 347.7 units)
        val cycleUnits = candidate.numericValue - baseline.baselineReading
        assertEquals(347.7, cycleUnits, 0.01)

        // Step 5: TNEB Bill Calculator calculates exact bill on 347.7 units:
        // <=500 slab: 200 free, 147.7 @ 4.70 = ₹694.19
        val bill = TNEBBillCalculator.calculateTNEBBill(cycleUnits)
        assertEquals(694.19, bill.totalEnergyCharge, 0.01)

        // Step 6: Daily consumption calculation & reduction comparison
        val daysInCycle = 60
        val currentDaily = cycleUnits / daysInCycle // 5.795 kWh/day
        val previousDaily = 395.0 / 60.0 // 6.583 kWh/day
        val reductionPct = ((previousDaily - currentDaily) / previousDaily) * 100.0
        assertTrue("Reduction should be approx 12%", reductionPct in 11.5..12.5)

        // Step 7: Reward Ledger Calculation:
        // 10 coins for processing new bill
        val processCoins = RewardConfig.BILL_PROCESSED_COINS
        assertEquals(10, processCoins)

        // 30 bonus coins for verified reduction between 10% and 20% (Tier 2)
        val reductionBonus = RewardConfig.getReductionBonus(reductionPct)
        assertEquals(30, reductionBonus)

        // Step 8: Streak progression: User maintained 7-day energy saving streak
        var streak = StreakState(currentStreak = 6)
        streak = streak.copy(currentStreak = 7)
        assertTrue(streak.isMilestonePending)
        val streakCoins = RewardConfig.STREAK_MILESTONE_COINS
        assertEquals(5, streakCoins)

        // Step 9: Assemble ledger transactions
        val transactions = listOf(
            CoinTransaction(userId = "USER_SALEM", eventType = CoinEventType.BILL_PROCESSED, amount = processCoins, idempotencyKey = "tx_bill", description = "New bill verified"),
            CoinTransaction(userId = "USER_SALEM", eventType = CoinEventType.VERIFIED_CONSUMPTION_REDUCTION, amount = reductionBonus, idempotencyKey = "tx_bonus", description = "12% reduction bonus"),
            CoinTransaction(userId = "USER_SALEM", eventType = CoinEventType.STREAK_MILESTONE, amount = streakCoins, idempotencyKey = "tx_streak", description = "7-Day streak milestone")
        )
        val userRewardData = UserRewardData(userId = "USER_SALEM", streakState = streak, transactions = transactions)

        // Authoritative ledger balance = 10 + 30 + 5 = 45 coins!
        assertEquals(45, userRewardData.balance)
    }
}
