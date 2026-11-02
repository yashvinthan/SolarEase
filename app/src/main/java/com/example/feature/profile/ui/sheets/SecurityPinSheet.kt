package com.example.feature.profile.ui.sheets

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.core.legal.LegalSection
import com.example.ui.theme.SolarEaseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityPinSheet(
    biometricLock: Boolean,
    onToggleBiometric: (Boolean) -> Unit,
    onSignOutClick: () -> Unit,
    onDismiss: () -> Unit
) {
    PrivacySecuritySheet(
        biometricLock = biometricLock,
        onToggleBiometric = onToggleBiometric,
        onSignOutClick = onSignOutClick,
        onDismiss = onDismiss
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecuritySheet(
    biometricLock: Boolean,
    onToggleBiometric: (Boolean) -> Unit,
    onSignOutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit = {},
    onOpenLegalSection: (LegalSection) -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val colors = SolarEaseTheme.colors
    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

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
                Column {
                    Text(
                        text = "Privacy & Security",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                    Text(
                        text = "Device protection and permission integrity",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = colors.onSurfaceVariant
                    )
                }
            }

            // Biometric App Lock Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = colors.surfaceVariant,
                border = BorderStroke(1.dp, colors.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Fingerprint,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Biometric Lock",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.onSurface
                            )
                            Text(
                                text = "Require fingerprint / face unlock to view bill history",
                                fontSize = 11.5.sp,
                                color = colors.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = biometricLock,
                        onCheckedChange = onToggleBiometric
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = colors.surfaceVariant,
                border = BorderStroke(1.dp, colors.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    // Permission Status Rows
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Location Permission",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.onSurface
                            )
                            Text(
                                if (hasLocationPermission) "Granted • Coordinates enabled" else "Not Granted • GPS disabled",
                                fontSize = 11.5.sp,
                                color = if (hasLocationPermission) colors.primary else colors.error
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            border = BorderStroke(1.dp, colors.outline)
                        ) {
                            Text("Settings", fontSize = 11.sp, color = colors.onSurface)
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        thickness = 0.5.dp,
                        color = colors.outlineVariant
                    )

                    // Data sovereignty info
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Local Data Sovereignty & Encryption",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "All your electricity metrics, meter OCR logs, and solar estimates are protected with TLS 1.3 and AndroidKeyStore AES256-GCM encryption. Aadhaar & PAN fields are automatically stripped before local caching.",
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp,
                            color = colors.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Privacy Policy",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary,
                                modifier = Modifier.clickable { onOpenLegalSection(LegalSection.PRIVACY_POLICY) }
                            )
                            Text("•", fontSize = 11.5.sp, color = colors.onSurfaceVariant)
                            Text(
                                text = "Cookie & Storage Notice",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary,
                                modifier = Modifier.clickable { onOpenLegalSection(LegalSection.COOKIE_STORAGE) }
                            )
                            Text("•", fontSize = 11.5.sp, color = colors.onSurfaceVariant)
                            Text(
                                text = "SDKs & Cloud AI",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary,
                                modifier = Modifier.clickable { onOpenLegalSection(LegalSection.SUBPROCESSORS_AI) }
                            )
                        }
                    }
                }
            }

            // Sign out button
            OutlinedButton(
                onClick = onSignOutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, colors.error.copy(alpha = 0.4f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.error)
            ) {
                Icon(Icons.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sign Out of Session", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            // Statutory Right to Erasure / Account Deletion
            TextButton(
                onClick = onDeleteAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = colors.error)
            ) {
                Icon(Icons.Filled.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Delete Account & Erase Personal Data", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
