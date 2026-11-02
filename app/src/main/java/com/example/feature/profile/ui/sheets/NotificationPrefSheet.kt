package com.example.feature.profile.ui.sheets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature.profile.ui.NotificationPreferences
import com.example.ui.theme.SolarEaseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPrefSheet(
    preferences: NotificationPreferences,
    onToggle: (key: String, value: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    NotificationPreferencesSheet(
        preferences = preferences,
        onToggle = onToggle,
        onDismiss = onDismiss
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPreferencesSheet(
    preferences: NotificationPreferences,
    onToggle: (key: String, value: Boolean) -> Unit,
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
                        text = "Notifications",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                    Text(
                        text = "Manage your telemetry alerts and billing notifications",
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

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = colors.surfaceVariant,
                border = BorderStroke(1.dp, colors.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    PreferenceToggleRow(
                        title = "Bill Reminders",
                        description = "Alerts for upcoming TNEB bi-monthly bill due dates and cycles",
                        checked = preferences.billReminders,
                        onCheckedChange = { onToggle("bill_reminders", it) },
                        showDivider = true
                    )
                    PreferenceToggleRow(
                        title = "Meter Reading Reminders",
                        description = "Bi-monthly reminders to capture meter readings for accurate telemetry",
                        checked = preferences.meterReminders,
                        onCheckedChange = { onToggle("meter_reminders", it) },
                        showDivider = true
                    )
                    PreferenceToggleRow(
                        title = "Energy-Saving Mantras",
                        description = "Daily and weekly habit check-in prompts for the 9 Mantras",
                        checked = preferences.mantraReminders,
                        onCheckedChange = { onToggle("mantra_reminders", it) },
                        showDivider = true
                    )
                    PreferenceToggleRow(
                        title = "Energy Savings & Milestone Alerts",
                        description = "Milestone notifications when achieving verified energy reductions",
                        checked = preferences.rewardReminders,
                        onCheckedChange = { onToggle("reward_reminders", it) },
                        showDivider = true
                    )
                    PreferenceToggleRow(
                        title = "Important SolarEase Alerts",
                        description = "Critical grid advisories, tariff updates and system notices",
                        checked = preferences.alertReminders,
                        onCheckedChange = { onToggle("alert_reminders", it) },
                        showDivider = false
                    )
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                Text("Done", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun PreferenceToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    showDivider: Boolean
) {
    val colors = SolarEaseTheme.colors
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface
                )
                Text(
                    text = description,
                    fontSize = 11.5.sp,
                    color = colors.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 14.dp),
                thickness = 0.5.dp,
                color = colors.outlineVariant
            )
        }
    }
}
