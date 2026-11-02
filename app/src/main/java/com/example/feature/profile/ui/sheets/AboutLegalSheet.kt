package com.example.feature.profile.ui.sheets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.legal.LegalComplianceConstants
import com.example.core.legal.LegalSection
import com.example.ui.theme.SolarEaseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutLegalSheet(
    onOpenLegalSection: (LegalSection) -> Unit = {},
    onDismiss: () -> Unit
) {
    AboutSolarEaseSheet(
        onOpenLegalSection = onOpenLegalSection,
        onDismiss = onDismiss
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutSolarEaseSheet(
    onOpenLegalSection: (LegalSection) -> Unit = {},
    onDismiss: () -> Unit
) {
    val colors = SolarEaseTheme.colors

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // SolarEase logo badge
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.EnergySavingsLeaf,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = colors.primary
                )
            }

            Text(
                text = "SolarEase",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.onSurface
            )

            Surface(
                shape = RoundedCornerShape(50),
                color = colors.primaryContainer.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "Version 2.4.1 (Build 184) • Clean Energy Hub",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primary
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceVariant,
                border = BorderStroke(1.dp, colors.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Empowering Sustainable Homes",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                    Text(
                        text = "SolarEase helps consumers monitor domestic electricity consumption, avoid tariff spikes under TNEB LT-1A slabs, and transition seamlessly to rooftop solar power.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = colors.onSurfaceVariant
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = colors.outlineVariant)
                    Text(
                        text = "Statutory Disclaimer: SolarEase is an independent analytical software developed to assist consumers. It is not affiliated with, authorized, or endorsed by TANGEDCO, TNEB, MNRE, or any governmental authority. Tariff estimates are based on public LT-1A tariff schedules.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            // Statutory Corporate & Grievance Redressal Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceVariant,
                border = BorderStroke(1.dp, colors.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Statutory Corporate & Grievance Details",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                    Text(
                        text = "Entity: ${LegalComplianceConstants.ENTITY_NAME} (CIN: ${LegalComplianceConstants.CIN})",
                        fontSize = 11.sp,
                        color = colors.onSurfaceVariant
                    )
                    Text(
                        text = "Registered Office: ${LegalComplianceConstants.REGISTERED_ADDRESS}",
                        fontSize = 11.sp,
                        color = colors.onSurfaceVariant
                    )
                    Text(
                        text = "Grievance Officer & DPO: ${LegalComplianceConstants.GRIEVANCE_OFFICER_NAME} (${LegalComplianceConstants.GRIEVANCE_EMAIL})",
                        fontSize = 11.sp,
                        color = colors.onSurfaceVariant
                    )
                    Text(
                        text = "Helpline: ${LegalComplianceConstants.SUPPORT_PHONE} (${LegalComplianceConstants.BUSINESS_HOURS}) • SLA: 48h ack / 30d resolution",
                        fontSize = 11.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            // Interactive Legal Documents List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "• View Terms of Service (Indian Contract Act, 1872) →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primary,
                    modifier = Modifier
                        .clickable { onOpenLegalSection(LegalSection.TERMS_OF_SERVICE) }
                        .padding(vertical = 4.dp)
                )
                Text(
                    text = "• View Privacy Policy (DPDP Act 2023 Compliant) →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primary,
                    modifier = Modifier
                        .clickable { onOpenLegalSection(LegalSection.PRIVACY_POLICY) }
                        .padding(vertical = 4.dp)
                )
                Text(
                    text = "• View Refund & Cancellation Policy (100% Pre-Dispatch) →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primary,
                    modifier = Modifier
                        .clickable { onOpenLegalSection(LegalSection.REFUND_POLICY) }
                        .padding(vertical = 4.dp)
                )
                Text(
                    text = "• View Cookie, Storage & Third-Party SDK Disclosures →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primary,
                    modifier = Modifier
                        .clickable { onOpenLegalSection(LegalSection.SUBPROCESSORS_AI) }
                        .padding(vertical = 4.dp)
                )
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                Text("Close", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
