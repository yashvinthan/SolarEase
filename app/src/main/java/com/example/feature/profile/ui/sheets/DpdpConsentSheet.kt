package com.example.feature.profile.ui.sheets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.legal.LegalComplianceConstants
import com.example.core.legal.LegalSection
import com.example.ui.theme.SolarEaseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DpdpConsentSheet(
    onExportData: () -> Unit = {},
    onRevokeConsent: () -> Unit = {},
    onDeleteAccount: () -> Unit,
    onOpenLegalSection: (LegalSection) -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "DPDP Act & Consent Rights",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )
                        Text(
                            text = "Statutory disclosures under DPDP Act, 2023",
                            fontSize = 12.sp,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = colors.onSurfaceVariant
                    )
                }
            }

            // Statutory Data Fiduciary Card
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
                        text = "Data Fiduciary Disclosure (§ 5 & § 8)",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                    Text(
                        text = "Data Fiduciary: ${LegalComplianceConstants.ENTITY_NAME}\nCIN: ${LegalComplianceConstants.CIN}\nRegistered Office: ${LegalComplianceConstants.REGISTERED_ADDRESS}",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = colors.onSurfaceVariant
                    )
                    Text(
                        text = "Data Protection Officer: ${LegalComplianceConstants.GRIEVANCE_OFFICER_NAME} (${LegalComplianceConstants.GRIEVANCE_EMAIL})\nStatutory SLA: 48h acknowledgement, 30 calendar days resolution.",
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            // Rights Summary Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceVariant,
                border = BorderStroke(1.dp, colors.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Your Rights as a Data Principal",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                    Text(
                        text = "• Right to Access & Summary of Personal Data (§ 11)\n• Right to Correction and Erasure of Personal Data (§ 12)\n• Right of Grievance Redressal (§ 13)\n• Right to Nominate (§ 14)",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            // Action Buttons for Principals
            OutlinedButton(
                onClick = onExportData,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, colors.outline)
            ) {
                Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export My Encrypted Data Archive", fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = { onOpenLegalSection(LegalSection.PRIVACY_POLICY) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, colors.outline)
            ) {
                Icon(Icons.Filled.Gavel, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Read Full Statutory Privacy Policy", fontSize = 13.sp)
            }

            // Right to Erasure / Account Deletion
            Button(
                onClick = onDeleteAccount,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.error,
                    contentColor = colors.onError
                )
            ) {
                Icon(Icons.Filled.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Exercise Right to Erasure (Delete Account)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
