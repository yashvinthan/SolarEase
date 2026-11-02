package com.example.feature.home.ui.modules

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.ui.theme.*



@Composable
fun BillCalculatorHistoryView(
    historyList: List<SavedCalculation>,
    onSelectCalculation: (SavedCalculation) -> Unit,
    onDeleteItem: (String) -> Unit,
    onCalculateNew: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredList = when (selectedFilter) {
        "Bi-monthly" -> historyList.filter { it.cycle == BillingCycle.BIMONTHLY }
        "Monthly" -> historyList.filter { it.cycle == BillingCycle.MONTHLY }
        else -> historyList
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 2.dp)) {
            Text(
                text = "Calculation History",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = StitchOnSurface
            )
            Text(
                text = "Review your past estimates and comparison logs",
                fontSize = 13.sp,
                color = StitchOnSurfaceVariant
            )
        }

        if (historyList.isNotEmpty()) {
            // Metrics summary cards
            val avgUnits = historyList.map { it.units }.average()
            val avgBill = historyList.map { it.billResult.totalEnergyCharge }.average()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = StitchSurfaceLowest),
                    border = BorderStroke(1.dp, StitchOutline)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Insights,
                                contentDescription = null,
                                tint = StitchOnSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AVG. CONSUMPTION", fontSize = 10.sp, color = StitchOnSurfaceVariant, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${avgUnits.toInt()} kWh",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = StitchOnSurface
                        )
                        Text(
                            text = "across ${historyList.size} runs",
                            fontSize = 11.sp,
                            color = StitchOnSurfaceVariant
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = StitchSurfaceLowest),
                    border = BorderStroke(1.dp, StitchOutline)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = StitchPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AVG. BILL", fontSize = 10.sp, color = StitchOnSurfaceVariant, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currencyFormat.format(avgBill),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = StitchPrimary
                        )
                        Text(
                            text = "domestic estimate",
                            fontSize = 11.sp,
                            color = StitchOnSurfaceVariant
                        )
                    }
                }
            }

            // Filter rail
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Bi-monthly", "Monthly").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    val bg = if (isSelected) StitchPrimary else StitchSurfaceContainer
                    val txtColor = if (isSelected) StitchOnPrimary else StitchOnSurface

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(bg)
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = txtColor
                        )
                    }
                }
            }

            // Cards list
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredList.forEach { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectCalculation(item) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = StitchSurfaceLowest),
                        border = BorderStroke(1.dp, StitchOutline)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = StitchSurfaceLow
                                ) {
                                    Text(
                                        text = "${item.cycle.displayName} • Tamil Nadu (LT-1A)",
                                        fontSize = 11.sp,
                                        color = StitchOnSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteItem(item.id) },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete calculation",
                                        tint = StitchError.copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text("POWER CONSUMED", fontSize = 10.sp, color = StitchOnSurfaceVariant)
                                    Text(
                                        text = "${item.units.toInt()} kWh",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StitchOnSurface
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("ESTIMATED BILL", fontSize = 10.sp, color = StitchOnSurfaceVariant)
                                    Text(
                                        text = currencyFormat.format(item.billResult.totalEnergyCharge),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StitchPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Free Allowance: ${item.billResult.freeUnits.toInt()} kWh",
                                    fontSize = 11.sp,
                                    color = StitchPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Chargeable: ${item.billResult.chargeableUnits.toInt()} kWh",
                                    fontSize = 11.sp,
                                    color = StitchOnSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = dateFormat.format(Date(item.timestamp)),
                                fontSize = 11.sp,
                                color = StitchOnSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else {
            // Empty state matching Stitch
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StitchSurfaceLowest),
                border = BorderStroke(1.dp, StitchOutline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(StitchSurfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueryStats,
                            contentDescription = null,
                            tint = StitchOnSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "No calculations yet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = StitchOnSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Your previous bill calculations will appear here.",
                        fontSize = 13.sp,
                        color = StitchOnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Local Storage Privacy Reassurance
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(StitchSurfaceLow)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = StitchOnSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Calculations are saved locally on your device for quick comparison.",
                fontSize = 11.sp,
                color = StitchOnSurfaceVariant
            )
        }

        // Calculate New Bill CTA
        Button(
            onClick = onCalculateNew,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StitchSurfaceLowest,
                contentColor = StitchPrimary
            ),
            border = BorderStroke(1.dp, StitchPrimary)
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Calculate New Bill",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
