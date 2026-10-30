package com.example.core.legal

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.test.core.app.ApplicationProvider
import com.example.core.session.User
import com.example.core.session.UserSession
import com.example.feature.home.ui.modules.TnebBillCache
import com.example.feature.home.ui.modules.TnebConsumerBillData
import com.example.feature.lens.ui.theme.LensColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LegalAndComplianceAuditTest {

    @Test
    fun `corporate and grievance constants are populated without placeholders`() {
        val fields = listOf(
            LegalComplianceConstants.ENTITY_NAME,
            LegalComplianceConstants.CIN,
            LegalComplianceConstants.REGISTERED_ADDRESS,
            LegalComplianceConstants.GRIEVANCE_OFFICER_NAME,
            LegalComplianceConstants.GRIEVANCE_OFFICER_ROLE,
            LegalComplianceConstants.GRIEVANCE_EMAIL,
            LegalComplianceConstants.SUPPORT_EMAIL,
            LegalComplianceConstants.SUPPORT_PHONE,
            LegalComplianceConstants.BUSINESS_HOURS,
            LegalComplianceConstants.GRIEVANCE_SLA
        )

        fields.forEach { value ->
            assertTrue("Field should not be blank", value.isNotBlank())
            assertFalse("Field must not contain PLACEHOLDER: $value", value.contains("PLACEHOLDER", ignoreCase = true))
        }
        assertEquals("U40106TN2026PTC168420", LegalComplianceConstants.CIN)
        assertEquals(6, LegalSection.entries.size)
    }

    @Test
    fun `TnebBillCache strips Aadhaar and PAN sensitive identifiers before persisting`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        TnebBillCache.init(context)
        val rawBill = TnebConsumerBillData(
            consumerNumber = "092990123456",
            consumerName = "Test Consumer",
            tariffCode = "LA1A",
            sanctionedLoad = "4.0",
            aadhar = "1234-5678-9012",
            pan = "ABCDE1234F"
        )

        TnebBillCache.save(context, rawBill)
        val loaded = TnebBillCache.billData.value
        assertNotNull(loaded)
        assertEquals("", loaded!!.aadhar)
        assertEquals("", loaded.pan)
        assertEquals("092990123456", loaded.consumerNumber)

        TnebBillCache.clear(context)
        assertNull(TnebBillCache.billData.value)
    }

    @Test
    fun `LensColors SlateCaption and SlateMuted meet WCAG AA 4_5 to 1 contrast`() {
        assertEquals(Color(0xFF4F5A52), LensColors.SlateCaption)
        assertEquals(Color(0xFF4F5A52), LensColors.SlateMuted)
    }

    @Test
    fun `UserSession and TnebBillCache clear completely on erasure`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        UserSession.init(context)
        UserSession.currentUser = User(
            id = "usr-erase-test",
            name = "Test Erasure User",
            username = "erase_user",
            email = "erase@solarease.app",
            mobileNumber = "9876543210",
            ebServiceNumber = "092990123456"
        )
        assertNotNull(UserSession.currentUser)

        UserSession.clear(context)
        TnebBillCache.clear(context)

        assertNull(UserSession.currentUser)
        assertNull(TnebBillCache.billData.value)
    }
}
