package com.example.feature.lens.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature.lens.ui.theme.LensColors
import com.example.feature.lens.ui.theme.LensTypography

@Composable
fun RoomCompletedContent(
    room: Room,
    nextRoom: Room?,
    activeIdx: Int,
    total: Int,
    onContinue: () -> Unit,
    onEdit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress Rail
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = LensColors.SurfaceContainerLow,
            border = BorderStroke(1.dp, LensColors.Outline)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(LensColors.Primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = LensColors.OnPrimary, modifier = Modifier.size(12.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Room $activeIdx of $total Completed",
                            style = LensTypography.headlineSm.copy(fontSize = 14.sp),
                            color = LensColors.SlateText
                        )
                    }
                    val pct = ((activeIdx.toFloat() / total) * 100).toInt()
                    Text(
                        "$pct% OF TOTAL AUDIT",
                        style = LensTypography.labelCaps,
                        color = LensColors.Primary
                    )
                }
            }
        }

        // Zone Verified Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = LensColors.SurfaceContainerLowest,
            border = BorderStroke(1.dp, LensColors.Outline)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("ZONE SYNCHRONIZED", style = LensTypography.labelCaps, color = LensColors.Primary)
                        Text(room.name, style = LensTypography.headlineMd, color = LensColors.SlateText)
                    }
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = LensColors.PrimaryFixed
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = LensColors.Primary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("VERIFIED", style = LensTypography.labelCaps, color = LensColors.OnPrimaryFixed)
                        }
                    }
                }
            }
        }

        // Telemetry Data Readout Card (2x2 Grid)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = LensColors.SurfaceContainerLowest,
            border = BorderStroke(1.dp, LensColors.Outline)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("LOAD TELEMETRY BREAKDOWN", style = LensTypography.labelCaps, color = LensColors.SlateMuted)
                    Text("${room.appliances.size} APPLIANCES ACTIVE", style = LensTypography.labelCaps, color = LensColors.Primary)
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricBox(
                        label = "Connected Load",
                        value = "%.2f".format(room.connectedLoadKw),
                        unit = "kW",
                        icon = Icons.Default.Bolt,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        label = "Est. Monthly Power",
                        value = "%.1f".format(room.monthlyKwh),
                        unit = "kWh",
                        icon = Icons.Default.Speed,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricBox(
                        label = "Monthly Cost",
                        value = "₹%.0f".format(room.monthlyCost),
                        unit = "/mo",
                        icon = Icons.Default.CurrencyRupee,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        label = "Solar Offset Target",
                        value = "100.0",
                        unit = "%",
                        icon = Icons.Default.Eco,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("CONFIGURED HARDWARE NODES", style = LensTypography.labelCaps, color = LensColors.SlateCaption)
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    room.appliances.forEach { app ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = LensColors.SurfaceContainerLow
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${app.quantity}× ${app.label}", style = LensTypography.bodyMd, color = LensColors.SlateText)
                                Text("${app.powerWatts.toInt()}W • ${app.usageHoursPerDay}h/d", style = LensTypography.labelSm, color = LensColors.SlateCaption)
                            }
                        }
                    }
                }
            }
        }

        // Upcoming Phase Card
        if (nextRoom != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = LensColors.SurfaceContainerHighest.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, LensColors.Outline)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SkipNext, contentDescription = null, tint = LensColors.Tertiary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("UPCOMING PHASE", style = LensTypography.labelCaps, color = LensColors.Tertiary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Room ${activeIdx + 1} of $total — ${nextRoom.name}", style = LensTypography.headlineSm, color = LensColors.SlateText)
                    Text("Next sequential space for load profile aggregation.", style = LensTypography.bodyMd, color = LensColors.SlateCaption)
                }
            }
        }

        // Bottom CTA Buttons
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LensColors.Primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (nextRoom != null) "Continue to ${nextRoom.name}" else "View Whole House Analysis",
                    style = LensTypography.labelSm.copy(fontWeight = FontWeight.SemiBold),
                    color = LensColors.OnPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = LensColors.OnPrimary, modifier = Modifier.size(18.dp))
            }
        }

        TextButton(
            onClick = onEdit,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(Icons.Default.EditNote, contentDescription = null, tint = LensColors.SlateCaption, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Modify ${room.name} appliances", style = LensTypography.labelSm, color = LensColors.SlateCaption)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun MetricBox(
    label: String,
    value: String,
    unit: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = LensColors.SurfaceContainerLow
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = LensColors.SlateCaption, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(label, style = LensTypography.labelCaps.copy(fontSize = 9.sp), color = LensColors.SlateCaption)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, style = LensTypography.metricValMd, color = LensColors.SlateText)
                Spacer(modifier = Modifier.width(2.dp))
                Text(unit, style = LensTypography.labelCaps, color = LensColors.SlateCaption)
            }
        }
    }
}
