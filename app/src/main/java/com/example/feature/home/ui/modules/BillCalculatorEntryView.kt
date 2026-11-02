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
fun BillCalculatorEntryView(
    inputText: String,
    inputError: String?,
    billingCycle: BillingCycle,
    onInputChanged: (String) -> Unit,
    onClearInput: () -> Unit,
    onSelectPreset: (Double) -> Unit,
    onSelectCycle: (BillingCycle) -> Unit,
    onCalculate: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val presets = listOf(150.0, 300.0, 500.0, 650.0, 800.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Subtitle Header
        Text(
            text = "Estimate your electricity bill from your monthly usage.",
            fontSize = 14.sp,
            color = StitchOnSurfaceVariant,
            modifier = Modifier.padding(horizontal = 2.dp)
        )

        // 1. Monthly Electricity Consumption Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = StitchSurfaceLowest),
            border = BorderStroke(1.dp, StitchOutline)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Monthly Electricity Consumption",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StitchOnSurface
                    )
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = StitchOnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Input Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StitchSurfaceLow)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (inputText.isEmpty()) {
                            Text(
                                text = "0",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = StitchOnSurfaceVariant.copy(alpha = 0.65f)
                            )
                        }
                        BasicTextField(
                            value = inputText,
                            onValueChange = onInputChanged,
                            textStyle = TextStyle(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = StitchOnSurface
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    if (inputText.isNotBlank()) onCalculate()
                                }
                            ),
                            cursorBrush = SolidColor(StitchPrimary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (inputText.isNotEmpty()) {
                        IconButton(
                            onClick = onClearInput,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "Clear input",
                                tint = StitchOnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = "kWh",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = StitchPrimary
                    )
                }

                // Error validation message
                if (inputError != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = inputError,
                        fontSize = 12.sp,
                        color = StitchError,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter the electricity units shown on your latest bill.",
                    fontSize = 12.sp,
                    color = StitchOnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Common Monthly Presets
                Text(
                    text = "COMMON MONTHLY PRESETS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StitchOnSurfaceVariant,
                    letterSpacing = 0.6.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { presetVal ->
                        val isSelected = inputText.toDoubleOrNull() == presetVal
                        val bg = if (isSelected) StitchPrimaryFixed else StitchSurfaceContainer
                        val txtColor = if (isSelected) StitchOnPrimaryFixedVariant else StitchOnSurface

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(bg)
                                .clickable {
                                    focusManager.clearFocus()
                                    onSelectPreset(presetVal)
                                }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "${presetVal.toInt()} kWh",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = txtColor
                            )
                        }
                    }
                }
            }
        }

        // 2. Billing Configuration Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = StitchSurfaceLowest),
            border = BorderStroke(1.dp, StitchOutline)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Billing Configuration",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StitchOnSurface
                    )
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = StitchOnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Billing Cycle Toggle
                Text(
                    text = "BILLING CYCLE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StitchOnSurfaceVariant,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(StitchSurfaceLow)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    BillingCycle.entries.forEach { cycle ->
                        val isSelected = cycle == billingCycle
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) StitchPrimary else Color.Transparent)
                                .clickable { onSelectCycle(cycle) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cycle.displayName,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) StitchOnPrimary else StitchOnSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tariff Readout Card
                Text(
                    text = "ELECTRICITY BOARD & TARIFF",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StitchOnSurfaceVariant,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(StitchSurfaceLow)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tamil Nadu (TANGEDCO)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StitchOnSurface
                        )
                        Text(
                            text = "Domestic LT-1A (Subsidized Residential)",
                            fontSize = 11.sp,
                            color = StitchOnSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = StitchOnSurfaceVariant
                    )
                }
            }
        }

        // 3. Information Tip Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(StitchSurfaceLow)
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(StitchPrimaryFixed),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = StitchOnPrimaryFixedVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Where to find your units?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StitchOnSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Check \"Units Consumed\" or \"Total Billed kWh\" on your physical or digital electricity bill statement.",
                    fontSize = 12.sp,
                    color = StitchOnSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }

        // 4. Primary CTA & Disclaimer
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onCalculate()
                },
                enabled = inputText.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StitchPrimary,
                    disabledContainerColor = StitchPrimary.copy(alpha = 0.4f),
                    contentColor = StitchOnPrimary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Calculate My Bill",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.padding(horizontal = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = StitchOnSurfaceVariant,
                    modifier = Modifier
                        .size(14.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "This is an estimated bill. Your actual bill may vary based on fuel surcharge adjustments, fixed meter fees, and local state duty taxes.",
                    fontSize = 11.sp,
                    color = StitchOnSurfaceVariant,
                    lineHeight = 15.sp
                )
            }
        }

        // 5. Calculation History Card Entry Point
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onNavigateToHistory() },
            shape = RoundedCornerShape(12.dp),
            color = StitchSurfaceLowest,
            border = BorderStroke(1.dp, StitchOutline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(StitchSurfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = StitchPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Calculation History",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StitchOnSurface
                        )
                        Text(
                            text = "View and compare past bill estimates",
                            fontSize = 12.sp,
                            color = StitchOnSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View History",
                    tint = StitchOnSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

