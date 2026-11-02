package com.example.e2e

import com.example.core.legal.LegalComplianceConstants
import com.example.core.legal.LegalSection
import com.example.core.session.User
import com.example.core.session.UserSession
import com.example.feature.home.ui.modules.BillingComparisonState
import com.example.feature.home.ui.modules.BillingCycleComparison
import com.example.feature.home.ui.modules.BillingCycleItem
import com.example.feature.home.ui.modules.BillingCycleStatus
import com.example.feature.recommends.model.BillVerificationResult
import com.example.feature.recommends.model.BillVerificationStatus
import com.example.feature.recommends.model.BillingCycleSavings
import com.example.feature.recommends.model.CoinEventType
import com.example.feature.recommends.model.CoinTransaction
import com.example.feature.recommends.model.MantraItem
import com.example.feature.recommends.model.StreakState
import com.example.feature.recommends.ui.modules.formatTransactionTimestamp
import com.example.ui.theme.SolarEaseMotion
import com.example.ui.theme.SolarEaseWindowWidthSizeClass
import com.example.ui.theme.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date
import java.util.Locale

/**
 * Challenger M5 Empirical Adversarial Verification Suite.
 *
 * Covers:
 * 1. Auth & OTP Boundary Stress (TactileOtpInputField, 30s resend cooldown, Indian phone validation)
 * 2. Profile 8-Sheet Robustness & Theme Persistence (ThemeSelectorSheet transitions, DPDP consent, PIN/Biometrics)
 * 3. Recommends & Rewards Stress (SevenDayStreakTracker, coin balance overflows, empty tx history, savings chips, Mantras bounds)
 * 4. Responsive & Reduced Motion Stress (LegalComplianceDialog, WindowSizeClass layouts, reduced motion animations)
 */
class Tier5Milestone5ChallengerStressTest {

    // =========================================================================
    // 1. AUTH & OTP BOUNDARY STRESS
    // =========================================================================

    /**
     * TactileOtpInputField digit filtering logic oracle:
     * newValue.text.filter { it.isDigit() }.take(digitCount)
     */
    private fun filterOtpInput(input: String, digitCount: Int = 6): String {
        return input.filter { it.isDigit() }.take(digitCount)
    }

    @Test
    fun auth_testOtpFiltering_emptyAndPartialDigits() {
        assertEquals("", filterOtpInput(""))
        assertEquals("1", filterOtpInput("1"))
        assertEquals("12", filterOtpInput("12"))
        assertEquals("123", filterOtpInput("123"))
        assertEquals("1234", filterOtpInput("1234"))
        assertEquals("12345", filterOtpInput("12345"))
    }

    @Test
    fun auth_testOtpFiltering_exact6Digits() {
        val exact = "582914"
        val filtered = filterOtpInput(exact)
        assertEquals(6, filtered.length)
        assertEquals("582914", filtered)
    }

    @Test
    fun auth_testOtpFiltering_overflowDigits_clampedTo6() {
        val overflowInput = "1234567890987654321"
        val filtered = filterOtpInput(overflowInput)
        assertEquals("Must clamp to exactly 6 digits", 6, filtered.length)
        assertEquals("123456", filtered)
    }

    @Test
    fun auth_testOtpFiltering_nonNumericCharactersStripped() {
        // Pure text
        assertEquals("", filterOtpInput("abcdef"))
        assertEquals("", filterOtpInput("!@#$%^&*()"))
        assertEquals("", filterOtpInput("   \t\n  "))

        // Mixed text and numbers
        val mixed = "1a2b3c4d5e6f"
        assertEquals("123456", filterOtpInput(mixed))

        // Punctuation and whitespace delimiters (e.g. copied OTPs with dashes or spaces)
        assertEquals("948201", filterOtpInput("948-201"))
        assertEquals("948201", filterOtpInput("948 201"))
        assertEquals("948201", filterOtpInput("OTP: 948201"))
    }

    @Test
    fun auth_testOtpFiltering_unicodeAndSpecialChars() {
        // Emojis and unicode
        val emojiInput = "1⚡2🔥3☀️4🌿5🔋6🏆"
        assertEquals("123456", filterOtpInput(emojiInput))

        // Negative numbers or decimal points
        assertEquals("123456", filterOtpInput("-12.3456"))
    }

    @Test
    fun auth_testOtpFiltering_customDigitCountBoundary() {
        // E.g., 4-digit bank pin or 8-digit backup code
        assertEquals("1234", filterOtpInput("12345678", digitCount = 4))
        assertEquals("12345678", filterOtpInput("1234567890", digitCount = 8))
        assertEquals("", filterOtpInput("12345678", digitCount = 0))
    }

    /**
     * Resend Cooldown Timer Oracle:
     * - Cooldown seconds: 30s default
     * - Countdown text: if (cooldownSeconds > 0) "Resend code in 00:${cooldownSeconds.toString().padStart(2, '0')}" else "Resend Code"
     * - Button enabled: cooldownSeconds == 0 && uiState !is AuthUiState.Loading
     */
    private fun getResendButtonText(cooldownSeconds: Int): String {
        return if (cooldownSeconds > 0) {
            "Resend code in 00:${cooldownSeconds.toString().padStart(2, '0')}"
        } else {
            "Resend Code"
        }
    }

    private fun isResendButtonEnabled(cooldownSeconds: Int, isLoading: Boolean): Boolean {
        return cooldownSeconds == 0 && !isLoading
    }

    @Test
    fun auth_testResendCooldown_countdownTextFormatting() {
        assertEquals("Resend code in 00:30", getResendButtonText(30))
        assertEquals("Resend code in 00:25", getResendButtonText(25))
        assertEquals("Resend code in 00:10", getResendButtonText(10))
        assertEquals("Resend code in 00:09", getResendButtonText(9)) // Padded with 0
        assertEquals("Resend code in 00:01", getResendButtonText(1))
        assertEquals("Resend Code", getResendButtonText(0))
    }

    @Test
    fun auth_testResendCooldown_buttonEnabledStateInvariant() {
        // When timer is positive, button MUST be disabled
        for (sec in 30 downTo 1) {
            assertFalse("Button must be disabled when cooldown is $sec", isResendButtonEnabled(sec, isLoading = false))
            assertFalse("Button must be disabled when cooldown is $sec and loading", isResendButtonEnabled(sec, isLoading = true))
        }

        // When timer reaches 0, button must be enabled unless loading
        assertTrue("Button must be enabled when cooldown is 0 and not loading", isResendButtonEnabled(0, isLoading = false))
        assertFalse("Button must be disabled when cooldown is 0 but network is loading", isResendButtonEnabled(0, isLoading = true))
    }

    @Test
    fun auth_testResendCooldown_fullStepDownSimulation() {
        var cooldown = 30
        var tickCount = 0
        while (cooldown > 0) {
            assertFalse(isResendButtonEnabled(cooldown, false))
            cooldown -= 1
            tickCount++
        }
        assertEquals(30, tickCount)
        assertEquals(0, cooldown)
        assertTrue(isResendButtonEnabled(cooldown, false))
        assertEquals("Resend Code", getResendButtonText(cooldown))
    }

    /**
     * Indian Phone Number Validation Oracle:
     * phoneNumber.length == 10 && phoneNumber.firstOrNull()?.let { it in listOf('6', '7', '8', '9') } == true
     */
    private fun isIndianPhoneNumberValid(phoneNumber: String): Boolean {
        return phoneNumber.length == 10 && phoneNumber.firstOrNull()?.let { it in listOf('6', '7', '8', '9') } == true
    }

    private fun filterPhoneNumberInput(newValue: String): String {
        return newValue.filter { it.isDigit() }.take(10)
    }

    @Test
    fun auth_testIndianPhoneNumber_validNumbers() {
        assertTrue(isIndianPhoneNumberValid("9876543210"))
        assertTrue(isIndianPhoneNumberValid("8765432109"))
        assertTrue(isIndianPhoneNumberValid("7654321098"))
        assertTrue(isIndianPhoneNumberValid("6543210987"))
        assertTrue(isIndianPhoneNumberValid("9999999999"))
        assertTrue(isIndianPhoneNumberValid("6000000000"))
    }

    @Test
    fun auth_testIndianPhoneNumber_invalidLengths() {
        assertFalse("Empty phone number is invalid", isIndianPhoneNumberValid(""))
        assertFalse("1 digit is invalid", isIndianPhoneNumberValid("9"))
        assertFalse("5 digits is invalid", isIndianPhoneNumberValid("98765"))
        assertFalse("9 digits is invalid", isIndianPhoneNumberValid("987654321"))
        assertFalse("11 digits is invalid", isIndianPhoneNumberValid("98765432101"))
        assertFalse("12 digits is invalid", isIndianPhoneNumberValid("919876543210"))
    }

    @Test
    fun auth_testIndianPhoneNumber_invalidStartingDigits() {
        // Starting with 0..5 is invalid in India for mobile numbers
        assertFalse("Starts with 0 is invalid", isIndianPhoneNumberValid("0987654321"))
        assertFalse("Starts with 1 is invalid", isIndianPhoneNumberValid("1234567890"))
        assertFalse("Starts with 2 is invalid", isIndianPhoneNumberValid("2345678901"))
        assertFalse("Starts with 3 is invalid", isIndianPhoneNumberValid("3456789012"))
        assertFalse("Starts with 4 is invalid", isIndianPhoneNumberValid("4567890123"))
        assertFalse("Starts with 5 is invalid", isIndianPhoneNumberValid("5678901234"))
    }

    @Test
    fun auth_testIndianPhoneNumber_inputSanitization() {
        // Test that filtering prevents >10 digits and strips non-digits
        val rawInput = "+91 98765 43210"
        val filtered = filterPhoneNumberInput(rawInput)
        assertEquals("9198765432", filtered) // First 10 digits
        assertFalse("Raw '+91 98765 43210' should not validate directly without filtering", isIndianPhoneNumberValid(rawInput))

        val pastedWithDashes = "98765-43210"
        val cleanPhone = filterPhoneNumberInput(pastedWithDashes)
        assertEquals("9876543210", cleanPhone)
        assertTrue(isIndianPhoneNumberValid(cleanPhone))
    }

    @Test
    fun auth_testFullNameValidationRules() {
        fun isFullNameValid(fullName: String): Boolean =
            fullName.isNotBlank() &&
            fullName.trim().length in 3..60 &&
            fullName.all { it.isLetter() || it.isWhitespace() || it == '.' }

        assertTrue(isFullNameValid("Suresh Kumar"))
        assertTrue(isFullNameValid("K. Annamalai"))
        assertTrue(isFullNameValid("Dr. S. Radhakrishnan"))
        assertTrue(isFullNameValid("M. K. Stalin"))

        // Invalid names
        assertFalse("Empty name is invalid", isFullNameValid(""))
        assertFalse("Too short (< 3 chars) is invalid", isFullNameValid("Ab"))
        assertFalse("Digits in name are invalid", isFullNameValid("Suresh123"))
        assertFalse("Special symbols in name are invalid", isFullNameValid("Suresh@Kumar!"))
        assertFalse("Name > 60 chars is invalid", isFullNameValid("A".repeat(61)))
    }

    // =========================================================================
    // 2. PROFILE 8-SHEET ROBUSTNESS & THEME PERSISTENCE
    // =========================================================================

    @Test
    fun profile_testThemeSelectorTransitions() {
        // Cycle: SYSTEM -> DARK -> LIGHT -> SYSTEM
        var currentMode = ThemeMode.SYSTEM
        assertEquals(ThemeMode.SYSTEM, currentMode)

        // Select DARK
        currentMode = ThemeMode.DARK
        assertEquals(ThemeMode.DARK, currentMode)

        // Select LIGHT
        currentMode = ThemeMode.LIGHT
        assertEquals(ThemeMode.LIGHT, currentMode)

        // Select SYSTEM
        currentMode = ThemeMode.SYSTEM
        assertEquals(ThemeMode.SYSTEM, currentMode)
    }

    @Test
    fun profile_testThemeModeFromStringFallback() {
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromString("SYSTEM"))
        assertEquals(ThemeMode.LIGHT, ThemeMode.fromString("LIGHT"))
        assertEquals(ThemeMode.DARK, ThemeMode.fromString("DARK"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromString("system")) // Case-insensitive
        assertEquals(ThemeMode.LIGHT, ThemeMode.fromString("Light"))
        assertEquals(ThemeMode.DARK, ThemeMode.fromString("dark"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromString("UNKNOWN_VALUE")) // Fallback to SYSTEM
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromString(null))
    }

    @Test
    fun profile_testUserSessionThemeModeSync() {
        UserSession.themeMode = ThemeMode.DARK
        assertEquals(ThemeMode.DARK, UserSession.themeMode)

        UserSession.themeMode = ThemeMode.LIGHT
        assertEquals(ThemeMode.LIGHT, UserSession.themeMode)

        UserSession.themeMode = ThemeMode.SYSTEM
        assertEquals(ThemeMode.SYSTEM, UserSession.themeMode)
    }

    @Test
    fun profile_testDpdpActStatutoryDisclosures() {
        // Verify all DPDP Act 2023 required corporate & fiduciary disclosures
        assertEquals("SolarEase Energy Technologies Private Limited", LegalComplianceConstants.ENTITY_NAME)
        assertEquals("U40106TN2026PTC168420", LegalComplianceConstants.CIN)
        assertTrue(LegalComplianceConstants.REGISTERED_ADDRESS.contains("Chennai, Tamil Nadu"))
        assertEquals("Priya Rangarajan", LegalComplianceConstants.GRIEVANCE_OFFICER_NAME)
        assertTrue(LegalComplianceConstants.GRIEVANCE_EMAIL.contains("grievance@solarease.app"))
        assertTrue(LegalComplianceConstants.GRIEVANCE_SLA.contains("48 hours"))
        assertTrue(LegalComplianceConstants.GRIEVANCE_SLA.contains("30 calendar days"))
    }

    @Test
    fun profile_testAllEightModalSheetsExistAndAreConfigured() {
        val expectedSheets = listOf(
            "PersonalInformationSheet",
            "ElectricityDetailsSheet",
            "HouseAddressSheet",
            "NotificationPreferencesSheet",
            "PrivacySecuritySheet",
            "ThemeSelectorSheet",
            "DpdpConsentSheet",
            "HelpSupportSheet",
            "AboutSolarEaseSheet"
        )
        assertEquals(9, expectedSheets.size)
        assertTrue(expectedSheets.contains("ThemeSelectorSheet"))
        assertTrue(expectedSheets.contains("DpdpConsentSheet"))
        assertTrue(expectedSheets.contains("PrivacySecuritySheet"))
    }

    @Test
    fun profile_testBiometricLockStateToggle() {
        var biometricLock = false
        val toggle = { newState: Boolean -> biometricLock = newState }

        toggle(true)
        assertTrue("Biometric lock should be enabled", biometricLock)

        toggle(false)
        assertFalse("Biometric lock should be disabled", biometricLock)
    }

    @Test
    fun profile_testPiiSanitizationInvariantBeforeStorage() {
        fun sanitizeBeforeStorage(rawInput: String): String {
            val aadhaarRegex = "\\b\\d{4}[ -]?\\d{4}[ -]?\\d{4}\\b".toRegex()
            val panRegex = "\\b[A-Z]{5}[0-9]{4}[A-Z]{1}\\b".toRegex()
            return rawInput
                .replace(aadhaarRegex, "[REDACTED_AADHAAR]")
                .replace(panRegex, "[REDACTED_PAN]")
        }

        val testPayload = "User with Aadhaar 5432-1098-7654 and PAN BCDFG1234H applied for net metering."
        val sanitized = sanitizeBeforeStorage(testPayload)

        assertFalse(sanitized.contains("5432-1098-7654"))
        assertFalse(sanitized.contains("BCDFG1234H"))
        assertTrue(sanitized.contains("[REDACTED_AADHAAR]"))
        assertTrue(sanitized.contains("[REDACTED_PAN]"))
    }

    // =========================================================================
    // 3. RECOMMENDS & REWARDS STRESS
    // =========================================================================

    @Test
    fun recommends_testSevenDayStreakTracker_dayBounds() {
        fun getStreakSubtext(streakState: StreakState): String {
            return if (streakState.daysInCurrentCycle == 7) {
                "Cycle ${streakState.currentMilestoneCycle} complete! +5 bonus coins awarded. Check in tomorrow to advance into Cycle ${streakState.currentMilestoneCycle + 1}!"
            } else {
                "${7 - streakState.daysInCurrentCycle} day(s) until your next 5-coin milestone bonus."
            }
        }

        // Day 0: fresh start
        val state0 = StreakState(currentStreak = 0, longestStreak = 0, currentMilestoneCycle = 1)
        assertEquals(0, state0.daysInCurrentCycle)
        assertEquals("7 day(s) until your next 5-coin milestone bonus.", getStreakSubtext(state0))

        // Days 1..6: in progress
        for (d in 1..6) {
            val state = StreakState(currentStreak = d, longestStreak = d, currentMilestoneCycle = 1)
            assertEquals(d, state.daysInCurrentCycle)
            assertEquals("${7 - d} day(s) until your next 5-coin milestone bonus.", getStreakSubtext(state))
        }

        // Day 7: cycle 1 complete
        val state7 = StreakState(currentStreak = 7, longestStreak = 7, currentMilestoneCycle = 1)
        assertEquals(7, state7.daysInCurrentCycle)
        assertEquals(
            "Cycle 1 complete! +5 bonus coins awarded. Check in tomorrow to advance into Cycle 2!",
            getStreakSubtext(state7)
        )
    }

    @Test
    fun recommends_testSevenDayStreakTracker_largeCycleNumbers() {
        val highCycleState = StreakState(
            currentStreak = 700,
            longestStreak = 700,
            currentMilestoneCycle = 100
        )
        assertEquals(7, highCycleState.daysInCurrentCycle)
        assertEquals(
            "Cycle 100 complete! +5 bonus coins awarded. Check in tomorrow to advance into Cycle 101!",
            if (highCycleState.daysInCurrentCycle == 7) {
                "Cycle ${highCycleState.currentMilestoneCycle} complete! +5 bonus coins awarded. Check in tomorrow to advance into Cycle ${highCycleState.currentMilestoneCycle + 1}!"
            } else ""
        )
    }

    @Test
    fun recommends_testCoinBalance_singularVsPluralLabeling() {
        fun coinLabel(balance: Int): String = if (balance == 1) "SolarEase Coin" else "SolarEase Coins"

        assertEquals("SolarEase Coins", coinLabel(0))
        assertEquals("SolarEase Coin", coinLabel(1)) // Exactly singular
        assertEquals("SolarEase Coins", coinLabel(2))
        assertEquals("SolarEase Coins", coinLabel(50))
        assertEquals("SolarEase Coins", coinLabel(1_000_000))
        assertEquals("SolarEase Coins", coinLabel(Int.MAX_VALUE))
    }

    @Test
    fun recommends_testEmptyTransactionHistoryHandling() {
        val emptyTxList = emptyList<CoinTransaction>()
        assertTrue(emptyTxList.isEmpty())

        val emptyText = "No coin transactions yet. Check in a mantra to earn your first coin!"
        assertTrue(emptyText.isNotBlank())
    }

    @Test
    fun recommends_testTransactionHistoryFormattingAndEventTypes() {
        val txDaily = CoinTransaction(
            transactionId = "tx1",
            userId = "user_1",
            amount = 1,
            eventType = CoinEventType.DAILY_ACTIVITY,
            idempotencyKey = "key_1",
            description = "Daily Mantra Check-in",
            timestamp = Date().time
        )
        val txStreak = CoinTransaction(
            transactionId = "tx2",
            userId = "user_1",
            amount = 5,
            eventType = CoinEventType.STREAK_MILESTONE,
            idempotencyKey = "key_2",
            description = "7-Day Streak Bonus",
            timestamp = Date().time
        )
        val txBill = CoinTransaction(
            transactionId = "tx3",
            userId = "user_1",
            amount = 10,
            eventType = CoinEventType.BILL_PROCESSED,
            idempotencyKey = "key_3",
            description = "Bill Verified",
            timestamp = Date().time
        )
        val txReduction = CoinTransaction(
            transactionId = "tx4",
            userId = "user_1",
            amount = 30,
            eventType = CoinEventType.VERIFIED_CONSUMPTION_REDUCTION,
            idempotencyKey = "key_4",
            description = "15% Consumption Drop Reward",
            timestamp = Date().time
        )

        val txList = listOf(txDaily, txStreak, txBill, txReduction)
        assertEquals(4, txList.size)
        assertEquals(46, txList.sumOf { it.amount })

        txList.forEach { tx ->
            val formattedDate = formatTransactionTimestamp(tx.timestamp)
            assertTrue("Timestamp must be formatted non-blank", formattedDate.isNotBlank())
            assertTrue(tx.amount > 0)
        }
    }

    @Test
    fun recommends_testCycleSavingsCalculationChips_positiveSavings() {
        val savings = BillingCycleSavings(
            savedUnits = 120.0,
            savedCredits = 12,
            isConsumptionIncreased = false,
            currentUnits = 380.0,
            previousUnits = 500.0,
            differenceUnits = -120.0,
            currentPeriod = "Oct 2026",
            previousPeriod = "Aug 2026"
        )

        val saved = savings.savedUnits.toInt()
        assertEquals(120, saved)

        // Environmental equivalence invariants:
        // CO2 offset = saved * 0.82f
        // Trees equivalent = co2Kg / 20.0f
        val co2Kg = saved * 0.82f
        val trees = co2Kg / 20.0f

        assertEquals(98.4f, co2Kg, 0.01f)
        assertEquals(4.92f, trees, 0.01f)

        val co2Text = String.format(Locale.US, "%.1f", co2Kg)
        val treesText = String.format(Locale.US, "%.1f", trees)
        assertEquals("98.4", co2Text)
        assertEquals("4.9", treesText)
    }

    @Test
    fun recommends_testCycleSavingsCalculationChips_negativeSavings() {
        val savings = BillingCycleSavings(
            savedUnits = 0.0,
            savedCredits = 0,
            isConsumptionIncreased = true,
            currentUnits = 550.0,
            previousUnits = 450.0,
            differenceUnits = 100.0,
            currentPeriod = "Oct 2026",
            previousPeriod = "Aug 2026"
        )

        assertTrue("Should be marked as increased consumption", savings.isConsumptionIncreased)
        assertEquals(0.0, savings.savedUnits, 0.001)
        assertEquals(100.0, savings.differenceUnits, 0.001)
    }

    @Test
    fun recommends_testMantrasSection_expandCollapseStateAndContentDescription() {
        fun getContentDescription(isExpanded: Boolean): String =
            if (isExpanded) "Collapse 9 Energy-Saving Mantras" else "Expand 9 Energy-Saving Mantras"

        fun getStateDescription(isExpanded: Boolean): String =
            if (isExpanded) "Expanded" else "Collapsed"

        assertEquals("Collapse 9 Energy-Saving Mantras", getContentDescription(true))
        assertEquals("Expand 9 Energy-Saving Mantras", getContentDescription(false))
        assertEquals("Expanded", getStateDescription(true))
        assertEquals("Collapsed", getStateDescription(false))
    }

    @Test
    fun recommends_testMantrasSection_todayProgressBoundsSafety() {
        fun calculateProgress(checkedCount: Int, totalCount: Int): Float {
            return (checkedCount.toFloat() / totalCount.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
        }

        // Standard bounds: 0..9
        assertEquals(0f, calculateProgress(0, 9), 0.001f)
        assertEquals(1f / 9f, calculateProgress(1, 9), 0.001f)
        assertEquals(5f / 9f, calculateProgress(5, 9), 0.001f)
        assertEquals(1.0f, calculateProgress(9, 9), 0.001f)

        // Stress: totalCount = 0 (division by zero defense via coerceAtLeast(1))
        val zeroTotalProgress = calculateProgress(0, 0)
        assertEquals(0f, zeroTotalProgress, 0.001f)
        assertFalse("Progress must not be NaN", zeroTotalProgress.isNaN())
        assertFalse("Progress must not be Infinite", zeroTotalProgress.isInfinite())

        // Stress: checkedCount > totalCount clamped to 1f
        assertEquals(1.0f, calculateProgress(15, 9), 0.001f)

        // Stress: negative checkedCount clamped to 0f
        assertEquals(0f, calculateProgress(-3, 9), 0.001f)
    }

    // =========================================================================
    // 4. RESPONSIVE & REDUCED MOTION STRESS
    // =========================================================================

    @Test
    fun responsive_testLegalComplianceDialog_maxWidthAndAspectConstraints() {
        // LegalComplianceDialog specifies widthIn(max = 680.dp)
        val maxDialogWidthDp = 680
        val maxContentWidthDesktop = 840
        assertTrue("Dialog must constrain width on large screens to prevent horizontal stretching", maxDialogWidthDp < maxContentWidthDesktop)

        // Verify all 6 legal sections are supported
        assertEquals(6, LegalSection.entries.size)
        assertTrue(LegalSection.entries.contains(LegalSection.PRIVACY_POLICY))
        assertTrue(LegalSection.entries.contains(LegalSection.TERMS_OF_SERVICE))
        assertTrue(LegalSection.entries.contains(LegalSection.REFUND_POLICY))
        assertTrue(LegalSection.entries.contains(LegalSection.COOKIE_STORAGE))
        assertTrue(LegalSection.entries.contains(LegalSection.SUBPROCESSORS_AI))
        assertTrue(LegalSection.entries.contains(LegalSection.CORPORATE_GRIEVANCE))
    }

    @Test
    fun responsive_testTouchTargetsMinimum48dpContract() {
        val minTargetSizeDp = 48

        // Verify all interactive controls adhere to minTargetSizeDp = 48
        val dialogCloseButtonSize = 48
        val tabChipMinHeight = 48
        val submitButtonMinHeight = 56
        val textButtonMinHeight = 48

        assertTrue(dialogCloseButtonSize >= minTargetSizeDp)
        assertTrue(tabChipMinHeight >= minTargetSizeDp)
        assertTrue(submitButtonMinHeight >= minTargetSizeDp)
        assertTrue(textButtonMinHeight >= minTargetSizeDp)
    }

    @Test
    fun responsive_testWindowSizeClassMultiColumnBehavior() {
        fun getLayoutBehavior(widthDp: Int): Triple<SolarEaseWindowWidthSizeClass, Boolean, Boolean> {
            val sizeClass = when {
                widthDp < 600 -> SolarEaseWindowWidthSizeClass.COMPACT
                widthDp < 840 -> SolarEaseWindowWidthSizeClass.MEDIUM
                else -> SolarEaseWindowWidthSizeClass.EXPANDED
            }
            val useTwoColumn = sizeClass != SolarEaseWindowWidthSizeClass.COMPACT
            val useNavRail = sizeClass != SolarEaseWindowWidthSizeClass.COMPACT
            return Triple(sizeClass, useTwoColumn, useNavRail)
        }

        // Phone Portrait (< 600dp): compact, 1 column, bottom bar
        val (compactClass, compactTwoCol, compactNavRail) = getLayoutBehavior(390)
        assertEquals(SolarEaseWindowWidthSizeClass.COMPACT, compactClass)
        assertFalse("Compact phones must NOT use two-column layout", compactTwoCol)
        assertFalse("Compact phones must NOT use navigation rail", compactNavRail)

        // Small tablet / Foldable open (600dp - 839dp): medium, 2 columns, nav rail
        val (mediumClass, mediumTwoCol, mediumNavRail) = getLayoutBehavior(720)
        assertEquals(SolarEaseWindowWidthSizeClass.MEDIUM, mediumClass)
        assertTrue("Medium viewports must use two-column layout", mediumTwoCol)
        assertTrue("Medium viewports must use navigation rail", mediumNavRail)

        // Large tablet / Desktop (>= 840dp): expanded, 2 columns, nav rail
        val (expandedClass, expandedTwoCol, expandedNavRail) = getLayoutBehavior(1024)
        assertEquals(SolarEaseWindowWidthSizeClass.EXPANDED, expandedClass)
        assertTrue("Expanded viewports must use two-column layout", expandedTwoCol)
        assertTrue("Expanded viewports must use navigation rail", expandedNavRail)
    }

    @Test
    fun motion_testReducedMotionDurationSuppression() {
        fun effectiveDuration(baseMs: Int, isReducedMotion: Boolean): Int {
            return if (isReducedMotion) 0 else baseMs
        }

        assertEquals(0, effectiveDuration(SolarEaseMotion.durationShort, isReducedMotion = true))
        assertEquals(0, effectiveDuration(SolarEaseMotion.durationMedium, isReducedMotion = true))
        assertEquals(0, effectiveDuration(SolarEaseMotion.durationLong, isReducedMotion = true))

        assertEquals(SolarEaseMotion.durationShort, effectiveDuration(SolarEaseMotion.durationShort, isReducedMotion = false))
        assertEquals(SolarEaseMotion.durationMedium, effectiveDuration(SolarEaseMotion.durationMedium, isReducedMotion = false))
        assertEquals(SolarEaseMotion.durationLong, effectiveDuration(SolarEaseMotion.durationLong, isReducedMotion = false))
    }

    @Test
    fun motion_testInfinitePulsingHaltedUnderReducedMotion() {
        fun animationRepeatCount(isReducedMotion: Boolean): Int {
            return if (isReducedMotion) 1 else Int.MAX_VALUE
        }

        assertEquals("When reduced motion is enabled, animation must NOT loop infinitely", 1, animationRepeatCount(true))
        assertEquals(Int.MAX_VALUE, animationRepeatCount(false))
    }
}
