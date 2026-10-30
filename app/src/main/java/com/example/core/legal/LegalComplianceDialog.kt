package com.example.core.legal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.StitchOnSurface
import com.example.ui.theme.StitchOnSurfaceVariant
import com.example.ui.theme.StitchOutline
import com.example.ui.theme.StitchPrimaryContainer
import com.example.ui.theme.StitchSurfaceContainerLow
import com.example.ui.theme.StitchSurfaceContainerLowest

enum class LegalSection(val title: String) {
    PRIVACY_POLICY("Privacy Policy (DPDP Act 2023)"),
    TERMS_OF_SERVICE("Terms of Service"),
    REFUND_POLICY("Refund & Cancellation Policy"),
    COOKIE_STORAGE("Cookie & Local Storage Notice"),
    SUBPROCESSORS_AI("Third-Party SDKs & Cloud AI"),
    CORPORATE_GRIEVANCE("Corporate & Grievance Redressal")
}

object LegalComplianceConstants {
    const val ENTITY_NAME = "SolarEase Energy Technologies Private Limited"
    const val CIN = "U40106TN2026PTC168420"
    const val REGISTERED_ADDRESS = "Tech Innovation Hub, Guindy Industrial Estate, Chennai, Tamil Nadu 600032, India"
    const val SUPPORT_EMAIL = "support@solarease.app"
    const val SUPPORT_PHONE = "+91 44 2852 9000"
    const val BUSINESS_HOURS = "Mon–Fri, 9:00 AM – 6:00 PM IST"
    const val GRIEVANCE_OFFICER_NAME = "Priya Rangarajan"
    const val GRIEVANCE_OFFICER_ROLE = "Grievance Officer & Data Protection Officer (DPO)"
    const val GRIEVANCE_EMAIL = "grievance@solarease.app / privacy@solarease.app"
    const val GRIEVANCE_SLA = "Acknowledgment within 48 hours; resolution within 30 calendar days."
}

@Composable
fun LegalComplianceDialog(
    initialSection: LegalSection = LegalSection.PRIVACY_POLICY,
    onDismiss: () -> Unit
) {
    var selectedSection by remember { mutableStateOf(initialSection) }
    val primaryGreen = StitchPrimaryContainer
    val surfaceBg = StitchSurfaceContainerLowest
    val surfaceLow = StitchSurfaceContainerLow
    val textDark = StitchOnSurface
    val textMuted = StitchOnSurfaceVariant

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(20.dp),
            color = surfaceBg,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Legal Compliance",
                            tint = primaryGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "Legal, Privacy & Compliance",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textDark
                            )
                            Text(
                                text = "DPDP Act 2023 • Consumer Protection Act 2019",
                                fontSize = 11.sp,
                                color = textMuted
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close legal dialog",
                            tint = textMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Section selector chips (2 rows of 3 or scrollable)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val sections = LegalSection.entries
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        sections.take(3).forEach { section ->
                            LegalTabChip(
                                label = when (section) {
                                    LegalSection.PRIVACY_POLICY -> "Privacy"
                                    LegalSection.TERMS_OF_SERVICE -> "Terms"
                                    LegalSection.REFUND_POLICY -> "Refunds"
                                    else -> section.title
                                },
                                selected = selectedSection == section,
                                onClick = { selectedSection = section },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        sections.drop(3).forEach { section ->
                            LegalTabChip(
                                label = when (section) {
                                    LegalSection.COOKIE_STORAGE -> "Cookies & Storage"
                                    LegalSection.SUBPROCESSORS_AI -> "SDKs & Cloud AI"
                                    LegalSection.CORPORATE_GRIEVANCE -> "Grievance & CIN"
                                    else -> section.title
                                },
                                selected = selectedSection == section,
                                onClick = { selectedSection = section },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Document Body
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = surfaceLow,
                    border = BorderStroke(1.dp, StitchOutline.copy(alpha = 0.65f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = selectedSection.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryGreen
                        )
                        HorizontalDivider(thickness = 0.5.dp, color = StitchOutline.copy(alpha = 0.65f))

                        when (selectedSection) {
                            LegalSection.PRIVACY_POLICY -> PrivacyPolicyBody(textDark, textMuted)
                            LegalSection.TERMS_OF_SERVICE -> TermsOfServiceBody(textDark, textMuted)
                            LegalSection.REFUND_POLICY -> RefundPolicyBody(textDark, textMuted)
                            LegalSection.COOKIE_STORAGE -> CookieStorageBody(textDark, textMuted)
                            LegalSection.SUBPROCESSORS_AI -> SubprocessorsAiBody(textDark, textMuted)
                            LegalSection.CORPORATE_GRIEVANCE -> CorporateGrievanceBody(textDark, textMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
                ) {
                    Text(
                        text = "Close",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun LegalTabChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryGreen = StitchPrimaryContainer
    Surface(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clickable(
                role = androidx.compose.ui.semantics.Role.Tab,
                onClick = onClick
            ),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) primaryGreen else StitchSurfaceContainerLow,
        border = BorderStroke(1.dp, if (selected) primaryGreen else StitchOutline.copy(alpha = 0.65f))
    ) {
        Box(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) Color.White else StitchOnSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PrivacyPolicyBody(textDark: Color, textMuted: Color) {
    LegalParagraph(
        "1. Notice of Personal Data Collected & Lawful Purpose (DPDP Act 2023 § 5 & § 6)",
        "SolarEase collects only minimal personal data strictly required for rooftop solar planning and electricity tracking:\n" +
            "• Identity & Authentication: Mobile number, email, username, and Firebase UID to authenticate accounts and prevent unauthorized access.\n" +
            "• Geolocation Telemetry: Coarse and fine GPS coordinates (with optional runtime permission) used solely to estimate local solar irradiance (4.5 PSH benchmark), roof tilt, and nearby empaneled vendors.\n" +
            "• Utility Billing Data: TNEB/TANGEDCO service connection number and billing history to analyze Domestic LT-1A tariff progression.\n" +
            "• Strict Data Minimization (Aadhaar & PAN Stripping): Any Aadhaar or PAN numbers present in utility portal responses are programmatically stripped and discarded immediately before local caching or persistence. SolarEase never stores Aadhaar or PAN numbers.\n" +
            "• Solar Lens Media: Room photos and video sweeps uploaded by you are transmitted over TLS 1.3 to Google Gemini Vision AI to identify appliances and estimate wattage.",
        textDark, textMuted
    )
    LegalParagraph(
        "2. Data Localization, Retention & Security",
        "• Cloud Residency: Backend databases and services are hosted in Google Cloud Platform region asia-south1 (Mumbai, India).\n" +
            "• On-Device Encryption: Session tokens, cached utility bills, and meter readings are stored in hardware-backed EncryptedSharedPreferences (AES256-GCM / AndroidKeyStore).\n" +
            "• Media Retention: Uploaded Solar Lens media is processed transiently in memory buffers (and purged within a maximum of 30 days if diagnostic review is requested).",
        textDark, textMuted
    )
    LegalParagraph(
        "3. Your Statutory Rights as Data Principal (DPDP Act 2023 § 11–14)",
        "You have the right to: (a) Access a summary of your processed personal data and third-party processors; (b) Correct and update inaccurate profile or appliance data; (c) Erase your personal data and delete your account at any time via Profile > Privacy & Permissions > Delete Account or by emailing privacy@solarease.app; (d) Withdraw consent at any time; (e) Nominate an individual to exercise your rights; and (f) Lodge a grievance with our Grievance Officer or the Data Protection Board of India.",
        textDark, textMuted
    )
}

@Composable
private fun TermsOfServiceBody(textDark: Color, textMuted: Color) {
    LegalParagraph(
        "1. Eligibility & Contractual Scope (Indian Contract Act, 1872)",
        "By registering for or using SolarEase, you confirm that you are at least 18 years of age and legally competent to contract under the Indian Contract Act, 1872.",
        textDark, textMuted
    )
    LegalParagraph(
        "2. Independent Platform & Government Non-Affiliation Disclaimer",
        "SolarEase is an independent private analytical application developed by SolarEase Energy Technologies Private Limited. SolarEase is NOT affiliated with, sponsored by, endorsed by, or an official application of TANGEDCO, TNEB, TNPDCL, the Ministry of New and Renewable Energy (MNRE), or the National Portal for Rooftop Solar.",
        textDark, textMuted
    )
    LegalParagraph(
        "3. Computational Estimates & PM Surya Ghar Subsidy Disclaimer",
        "• Educational & Planning Estimates Only: All solar PV capacity recommendations, energy generation yields, payback timelines, and ROI projections are computational simulations based on MNRE/IEC 61724 benchmarks (4.5 Peak Sun Hours/day, 79% Performance Ratio). Actual performance depends on roof azimuth, shading, weather, panel degradation, and future TNERC tariff revisions.\n" +
            "• Subsidy Eligibility: Central Financial Assistance (up to ₹30,000 for 1 kW, ₹60,000 for 2 kW, ₹78,000 for 3 kW+ residential systems) under PM Surya Ghar: Muft Bijli Yojana requires Domestic Content Requirement (DCR) ALMM-listed solar modules, DISCOM net-metering feasibility, and final sanction by MNRE.",
        textDark, textMuted
    )
    LegalParagraph(
        "4. Independent EPC Vendors & Governing Law",
        "Listed solar EPC installers are independent contractors. Equipment warranties (typically 25-year linear performance on PV modules, 5–10 years on inverters) are provided directly by OEMs and installers. These Terms are governed by the laws of the Republic of India, with exclusive jurisdiction in the courts of Chennai, Tamil Nadu.",
        textDark, textMuted
    )
}

@Composable
private fun RefundPolicyBody(textDark: Color, textMuted: Color) {
    LegalParagraph(
        "1. 100% Pre-Dispatch Full Refund Guarantee",
        "In compliance with the Consumer Protection (E-Commerce) Rules, 2020:\n" +
            "• You may cancel your solar system booking or order at any time prior to physical hardware dispatch or installation technician mobilization.\n" +
            "• A 100% full refund of the net amount paid will be issued without any cancellation penalty.",
        textDark, textMuted
    )
    LegalParagraph(
        "2. Statutory Refund Turnaround Timeline",
        "• Approved refunds are initiated within 48 hours of cancellation request and credited back to your original payment method (UPI, Net Banking, Debit/Credit Card, or Loan source) within 5 to 7 banking days.\n" +
            "• No hidden payment gateway retention fees are deducted on pre-dispatch cancellations.",
        textDark, textMuted
    )
    LegalParagraph(
        "3. Post-Dispatch & Site Mobilization Terms",
        "• Once solar PV modules, inverters, or mounting structures have been dispatched to your premises or civil site work has commenced, cancellations are subject to deduction of actual verified two-way freight and site mobilization charges.\n" +
            "• For refund assistance or escalation, contact support@solarease.app or our Grievance Officer at grievance@solarease.app.",
        textDark, textMuted
    )
}

@Composable
private fun CookieStorageBody(textDark: Color, textMuted: Color) {
    LegalParagraph(
        "1. Native Mobile Application Storage (Zero Tracking Cookies)",
        "The SolarEase Android application does not use third-party advertising cookies, tracking pixels, or cross-app behavioral identifiers (AAID). Local session state, cached TNEB bill records, and meter baselines are stored exclusively inside hardware-backed EncryptedSharedPreferences (AES256-GCM) on your device.",
        textDark, textMuted
    )
    LegalParagraph(
        "2. TNEB Utility WebView Session Cookies",
        "When you query the official TANGEDCO/TNEB bill portal through the embedded verification WebView, transient HTTP session cookies (JSESSIONID) are issued directly by www.tnebltd.gov.in strictly to maintain CAPTCHA and form session state on the government server. SolarEase never exports, tracks, or monetizes utility session cookies.",
        textDark, textMuted
    )
}

@Composable
private fun SubprocessorsAiBody(textDark: Color, textMuted: Color) {
    LegalParagraph(
        "1. Disclosed Third-Party SDKs & Cloud Infrastructure",
        "In accordance with DPDP Act 2023 § 8 and Google Play Developer Policy, SolarEase utilizes the following authorized processors:\n" +
            "• Google Cloud Platform / Cloud Run (asia-south1, Mumbai): Hosts containerized backend APIs and PostgreSQL database.\n" +
            "• Firebase Authentication & App Check (Google LLC): Provides OTP/Google identity verification and Play Integrity / reCAPTCHA API abuse protection.\n" +
            "• Google Play Services Location: Provides optional device GPS coordinates for solar insolation modeling.",
        textDark, textMuted
    )
    LegalParagraph(
        "2. Artificial Intelligence & Computer Vision Disclosures",
        "• On-Device Vision (Google ML Kit): Live camera scanning and electricity meter OCR run locally on your Android device without transmitting live camera frames to external servers.\n" +
            "• Cloud AI Vision (Google Gemini 3.8 Flash / 2.5 Flash): When you upload room photos or video sweeps in Solar Lens or query the SolarEase AI assistant, media is transmitted over TLS 1.3 to Google Gemini AI via our backend to detect visible appliances and estimate load. Enterprise API payloads are not used to train public foundation models.",
        textDark, textMuted
    )
}

@Composable
private fun CorporateGrievanceBody(textDark: Color, textMuted: Color) {
    LegalParagraph(
        "1. Statutory Corporate Identity (Consumer Protection E-Commerce Rules 2020 Rule 5)",
        "• Legal Entity: ${LegalComplianceConstants.ENTITY_NAME}\n" +
            "• Corporate Identification Number (CIN): ${LegalComplianceConstants.CIN}\n" +
            "• Registered Office: ${LegalComplianceConstants.REGISTERED_ADDRESS}\n" +
            "• Customer Support: ${LegalComplianceConstants.SUPPORT_EMAIL} | ${LegalComplianceConstants.SUPPORT_PHONE} (${LegalComplianceConstants.BUSINESS_HOURS})",
        textDark, textMuted
    )
    LegalParagraph(
        "2. Designated Grievance Redressal Officer & DPO (DPDP Act 2023 § 8(9) & § 19)",
        "• Name: ${LegalComplianceConstants.GRIEVANCE_OFFICER_NAME}\n" +
            "• Designation: ${LegalComplianceConstants.GRIEVANCE_OFFICER_ROLE}\n" +
            "• Email: ${LegalComplianceConstants.GRIEVANCE_EMAIL}\n" +
            "• Telephone: ${LegalComplianceConstants.SUPPORT_PHONE}\n" +
            "• Postal Address: ${LegalComplianceConstants.REGISTERED_ADDRESS}\n" +
            "• Statutory SLA: ${LegalComplianceConstants.GRIEVANCE_SLA}\n" +
            "• Escalation: Unresolved privacy complaints may be escalated to the Data Protection Board of India.",
        textDark, textMuted
    )
}

@Composable
private fun LegalParagraph(
    heading: String,
    body: String,
    textDark: Color,
    textMuted: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = heading,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = textDark
        )
        Text(
            text = body,
            fontSize = 11.5.sp,
            lineHeight = 16.5.sp,
            color = textMuted
        )
    }
}

