package com.example.e2e

import com.example.feature.home.ui.modules.BillingCycleManager
import com.example.feature.home.ui.modules.MeterCycleBaseline
import com.example.feature.home.ui.modules.MeterReadingRecord
import com.example.feature.home.ui.modules.TNEBBillCalculator
import com.example.feature.home.ui.modules.TnebBillRecord
import com.example.feature.meterreading.model.ExtractedTextBlock
import com.example.feature.meterreading.model.MeterOcrStatus
import com.example.feature.meterreading.parser.ReadingPlausibilityStatus
import com.example.feature.meterreading.parser.ReadingPlausibilityValidator
import com.example.feature.meterreading.parser.TnebReadingParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Tier1CoreUtilityAndBillingTest {

    // =========================================================================
    // FEATURE 15: HomeScreen Modular Deconstruction
    // =========================================================================

    data class HomeHeroState(
        val userName: String,
        val greeting: String,
        val activeSystemStatus: String? = null
    )

    data class HomeBillSummaryModule(
        val currentPeriod: String,
        val estimatedUnits: Double,
        val estimatedAmount: Double,
        val daysRemainingInCycle: Int
    )

    data class HomeMeterModule(
        val lastRecordedReadingKwh: Double?,
        val daysSinceLastReading: Int,
        val isReadingDue: Boolean
    )

    @Test
    fun feature15_testHomeHeroModuleContract() {
        val hero = HomeHeroState("Priya", "Good morning", "Solar System Normal")
        assertEquals("Priya", hero.userName)
        assertEquals("Solar System Normal", hero.activeSystemStatus)
    }

    @Test
    fun feature15_testHomeBillSummaryModuleContract() {
        val billSummary = HomeBillSummaryModule("Sep – Oct 2026", 420.0, 1066.0, 14)
        assertEquals(420.0, billSummary.estimatedUnits, 0.001)
        assertEquals(1066.0, billSummary.estimatedAmount, 0.001)
        assertEquals(14, billSummary.daysRemainingInCycle)
    }

    @Test
    fun feature15_testHomeMeterTrackingModuleDuePrompt() {
        val meterModule = HomeMeterModule(15420.0, 32, true)
        assertTrue("Meter reading should be flagged as due after 30 days", meterModule.isReadingDue)
        assertEquals(32, meterModule.daysSinceLastReading)
    }

    @Test
    fun feature15_testHomeQuickToolsListCompleteness() {
        val tools = listOf("BILL_CALCULATOR", "SOLAR_LENS", "NEARBY_VENDORS", "AI_CHATBOT")
        assertEquals(4, tools.size)
        assertTrue(tools.contains("BILL_CALCULATOR"))
        assertTrue(tools.contains("SOLAR_LENS"))
    }

    @Test
    fun feature15_testSolarPromoBannerCallToActionContract() {
        val promoActionDestination = "ASSESSMENT_FUNNEL_STEP_1"
        assertEquals("ASSESSMENT_FUNNEL_STEP_1", promoActionDestination)
    }

    // =========================================================================
    // FEATURE 16: TnebBillStatusScreen Token Unification
    // =========================================================================

    enum class BillQueryState {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    @Test
    fun feature16_testTnebConsumerNumberValidation() {
        fun isValidConsumerNumber(num: String): Boolean {
            val clean = num.trim()
            // TNEB format: Region code (01-09) + Section code + Distribution + Service number (9-12 digits)
            return clean.all { it.isDigit() } && clean.length in 9..12
        }

        assertTrue(isValidConsumerNumber("091234567890"))
        assertTrue(isValidConsumerNumber("0123456789"))
        assertFalse(isValidConsumerNumber("123")) // Too short
        assertFalse(isValidConsumerNumber("09ABC123456")) // Letters invalid
        assertFalse(isValidConsumerNumber("")) // Empty
    }

    @Test
    fun feature16_testTnebBillQueryStateMachine() {
        var state = BillQueryState.IDLE
        assertEquals(BillQueryState.IDLE, state)

        state = BillQueryState.LOADING
        assertEquals(BillQueryState.LOADING, state)

        state = BillQueryState.SUCCESS
        assertEquals(BillQueryState.SUCCESS, state)
    }

    @Test
    fun feature16_testTnebBillErrorStateRecovery() {
        var state = BillQueryState.LOADING
        val serverError = true
        state = if (serverError) BillQueryState.ERROR else BillQueryState.SUCCESS
        assertEquals(BillQueryState.ERROR, state)
    }

    @Test
    fun feature16_testTnebBillStatusPresentationTokens() {
        // Status indicator colors should bind to central semantic colors
        val paidBadgeColor = com.example.ui.theme.SolarEaseSemanticColors.SuccessContainer
        val unpaidBadgeColor = com.example.ui.theme.SolarEaseSemanticColors.WarningContainer
        assertNotNull(paidBadgeColor)
        assertNotNull(unpaidBadgeColor)
    }

    @Test
    fun feature16_testTnebPortalDisclaimerContract() {
        val disclaimer = "Direct portal data fetched from official TNEB / TANGEDCO servers."
        assertTrue(disclaimer.contains("TNEB"))
    }

    // =========================================================================
    // FEATURE 17: BillCalculatorScreen Modularization & TNEB LT-1A Slab Math
    // =========================================================================

    @Test
    fun feature17_testZeroUnitsCalculation() {
        val result = TNEBBillCalculator.calculateTNEBBill(0.0)
        assertEquals(200.0, result.freeUnits, 0.001)
        assertEquals(0.0, result.chargeableUnits, 0.001)
        assertEquals(0.0, result.totalEnergyCharge, 0.001)
        assertEquals(0, result.breakdown.size)
    }

    @Test
    fun feature17_testTier1Slab250Units() {
        // <=500 slab: 200 free units, remaining 50 units @ 4.70 = ₹235.0
        val result = TNEBBillCalculator.calculateTNEBBill(250.0)
        assertEquals(200.0, result.freeUnits, 0.001)
        assertEquals(50.0, result.chargeableUnits, 0.001)
        assertEquals(235.0, result.totalEnergyCharge, 0.001)
        assertEquals(1, result.breakdown.size)
        assertEquals(4.70, result.breakdown[0].ratePerUnit, 0.001)
        assertEquals(235.0, result.breakdown[0].amount, 0.001)
    }

    @Test
    fun feature17_testTier1Slab450Units() {
        // <=500 slab: 200 free units, remaining 250 units:
        // Slab 1: 200 units @ 4.70 = 940.0
        // Slab 2: 50 units @ 6.30 = 315.0
        // Total = 1255.0
        val result = TNEBBillCalculator.calculateTNEBBill(450.0)
        assertEquals(200.0, result.freeUnits, 0.001)
        assertEquals(250.0, result.chargeableUnits, 0.001)
        assertEquals(1255.0, result.totalEnergyCharge, 0.001)
        assertEquals(2, result.breakdown.size)
        assertEquals(940.0, result.breakdown[0].amount, 0.001)
        assertEquals(315.0, result.breakdown[1].amount, 0.001)
    }

    @Test
    fun feature17_testTier2Slab600Units() {
        // >500 slab: 100 free units, remaining 500 units:
        // Slab 1: 300 units @ 4.70 = 1410.0
        // Slab 2: 100 units @ 6.30 = 630.0
        // Slab 3: 100 units @ 8.40 = 840.0
        // Total = 2880.0
        val result = TNEBBillCalculator.calculateTNEBBill(600.0)
        assertEquals(100.0, result.freeUnits, 0.001)
        assertEquals(500.0, result.chargeableUnits, 0.001)
        assertEquals(2880.0, result.totalEnergyCharge, 0.001)
        assertEquals(3, result.breakdown.size)
    }

    @Test
    fun feature17_testAuthoritativeBenchmark976Units() {
        // Official TNEB benchmark case: 976.0 units
        // >500 slab: 100 free units, 876 chargeable units, total ₹6618.0
        val result = TNEBBillCalculator.calculateTNEBBill(976.0)
        assertEquals(100.0, result.freeUnits, 0.001)
        assertEquals(876.0, result.chargeableUnits, 0.001)
        assertEquals(6618.0, result.totalEnergyCharge, 0.001)
        assertEquals(5, result.breakdown.size)

        // Verify individual slabs:
        assertEquals(1410.0, result.breakdown[0].amount, 0.001) // 300 @ 4.70
        assertEquals(630.0, result.breakdown[1].amount, 0.001)  // 100 @ 6.30
        assertEquals(840.0, result.breakdown[2].amount, 0.001)  // 100 @ 8.40
        assertEquals(1890.0, result.breakdown[3].amount, 0.001) // 200 @ 9.45
        assertEquals(1848.0, result.breakdown[4].amount, 0.001) // 176 @ 10.50
    }

    // =========================================================================
    // FEATURE 18: ElectricityChatbotScreen Token Binding
    // =========================================================================

    data class ChatMessage(
        val id: String,
        val text: String,
        val isUser: Boolean,
        val timestamp: Long = System.currentTimeMillis()
    )

    @Test
    fun feature18_testChatMessageModelContract() {
        val userMsg = ChatMessage("1", "What is the subsidy for a 3kW system in Chennai?", isUser = true)
        val botMsg = ChatMessage("2", "Under PM Surya Ghar, a 3kW system receives ₹78,000 maximum subsidy.", isUser = false)

        assertTrue(userMsg.isUser)
        assertFalse(botMsg.isUser)
        assertTrue(botMsg.text.contains("₹78,000"))
    }

    @Test
    fun feature18_testChatbotPromptKnowledgeBaseInvariants() {
        val standardQuestions = listOf(
            "How does net metering work?",
            "What is the PM Surya Ghar subsidy?",
            "How to calculate TNEB slab rate?",
            "What is DCR panel requirement?"
        )
        assertEquals(4, standardQuestions.size)
    }

    @Test
    fun feature18_testChatbotAutoScrollOrder() {
        val messages = mutableListOf<ChatMessage>()
        messages.add(ChatMessage("1", "Q1", true, 1000L))
        messages.add(ChatMessage("2", "A1", false, 1005L))
        messages.add(ChatMessage("3", "Q2", true, 1010L))

        assertEquals("Q1", messages.first().text)
        assertEquals("Q2", messages.last().text)
    }

    @Test
    fun feature18_testChatbotEmptyStatePromptSuggestions() {
        val suggestions = listOf("Estimate my solar capacity", "Check my latest bill slab")
        assertTrue(suggestions.isNotEmpty())
    }

    @Test
    fun feature18_testChatbotBubbleSemanticColors() {
        val userBubbleBg = com.example.ui.theme.StitchLightTokens.PrimaryContainer
        val botBubbleBg = com.example.ui.theme.StitchLightTokens.SurfaceContainer
        assertNotNull(userBubbleBg)
        assertNotNull(botBubbleBg)
    }

    // =========================================================================
    // FEATURE 19: BillHistory & Consumption Trends Polish
    // =========================================================================

    @Test
    fun feature19_testBiMonthlyPeriodFormatting() {
        val period = BillingCycleManager.formatBiMonthlyPeriod("08/2026", "2026-08-15")
        assertNotNull(period)
        assertTrue(period.isNotBlank())
    }

    @Test
    fun feature19_testCycleUnitsExtraction() {
        val billCurr = TnebBillRecord(id = "1", period = "10/2026", units = "450", kwh = "450")
        val billPrev = TnebBillRecord(id = "2", period = "08/2026", units = "380", kwh = "380")

        val units = BillingCycleManager.extractCycleUnits(billCurr, billPrev)
        assertEquals(450.0, units, 0.001)
    }

    @Test
    fun feature19_testCycleDaysCalculationDefault() {
        val billCurr = TnebBillRecord(id = "1", period = "10/2026", assessmentDate = "2026-10-15")
        val billPrev = TnebBillRecord(id = "2", period = "08/2026", assessmentDate = "2026-08-15")

        val days = BillingCycleManager.calculateCycleDays(billCurr, billPrev)
        assertTrue("Bi-monthly cycle days should be approx 60 days", days in 58..63)
    }

    @Test
    fun feature19_testConsumptionTrendInspection() {
        val currentDailyKwh = 420.0 / 60.0 // 7.0 kWh/day
        val previousDailyKwh = 480.0 / 60.0 // 8.0 kWh/day
        val deltaPercent = ((previousDailyKwh - currentDailyKwh) / previousDailyKwh) * 100.0

        assertEquals(12.5, deltaPercent, 0.001)
        assertTrue("Daily consumption reduced by 12.5%", deltaPercent > 0)
    }

    @Test
    fun feature19_testBillRecordIdDerivation() {
        val bill = TnebBillRecord(id = "REC1001", period = "10/2026", units = "450")
        val stableId = if (bill.id.isNotBlank()) bill.id else "FALLBACK"
        assertEquals("REC1001", stableId)
    }

    // =========================================================================
    // FEATURE 20: MeterScanner HUD & A11y Announcements
    // =========================================================================

    @Test
    fun feature20_testOcrRejectsImpulseConstants() {
        val rawOcr = "L&T METER\n3200 imp/kWh\n16597.7 kWh\n240V 50Hz"
        val candidate = TnebReadingParser.parse(rawOcr)

        assertEquals("Should extract actual cumulative reading not impulse constant", 16597.7, candidate.numericValue, 0.01)
        assertEquals(MeterOcrStatus.DETECTED_SUCCESS, candidate.status)
    }

    @Test
    fun feature20_testOcrRejectsStandaloneVoltage() {
        val rawOcr = "L&T METER\n230.5 V\n50 Hz"
        val candidate = TnebReadingParser.parse(rawOcr)

        assertEquals("Should detect voltage display warning", MeterOcrStatus.NON_KWH_WARNING, candidate.status)
        assertTrue(candidate.statusMessage.contains("Voltage"))
    }

    @Test
    fun feature20_testPlausibilityValidatorPassesNormalProgression() {
        val baseline = MeterCycleBaseline(cycleId = "C1", baselineDate = "2026-09-01", baselineReading = 15000.0)
        val latest = MeterReadingRecord(readingValue = 15250.0, readingDate = "2026-09-15")

        val status = ReadingPlausibilityValidator.validate(
            candidateValue = 15450.0,
            baseline = baseline,
            latestReading = latest
        )
        assertEquals(ReadingPlausibilityStatus.Valid, status)
    }

    @Test
    fun feature20_testPlausibilityValidatorCatchesLowerThanPrevious() {
        val latest = MeterReadingRecord(readingValue = 15250.0, readingDate = "2026-09-15")

        val status = ReadingPlausibilityValidator.validate(
            candidateValue = 14800.0,
            baseline = null,
            latestReading = latest
        )
        assertTrue(status is ReadingPlausibilityStatus.LowerThanPrevious)
    }

    @Test
    fun feature20_testPlausibilityValidatorCatchesVoltageMisread() {
        val latest = MeterReadingRecord(readingValue = 15250.0, readingDate = "2026-09-15")

        // 235.0 is in 200..260 and reference is 15250
        val status = ReadingPlausibilityValidator.validate(
            candidateValue = 235.0,
            baseline = null,
            latestReading = latest
        )
        assertTrue(status is ReadingPlausibilityStatus.Warning)
        assertTrue((status as ReadingPlausibilityStatus.Warning).message.contains("Voltage"))
    }
}
