package com.example.feature.home.ui.modules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.SolarPower
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.feature.performance.domain.model.EnergyFlow
import com.example.feature.performance.domain.model.LiveGeneration
import com.example.feature.performance.ui.PerformanceUiState
import com.example.feature.performance.ui.PerformanceViewModel
import com.example.ui.components.SolarEaseCard
import com.example.ui.components.SolarEaseMetricCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolarPanelRoute(
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit = {},
    viewModel: PerformanceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val (liveGen, energyFlow) = when (val state = uiState) {
        is PerformanceUiState.Success -> Pair(state.data.liveGeneration, state.data.energyFlow)
        else -> Pair(
            LiveGeneration(
                currentOutput = 2.45,
                todayGeneration = 12.8,
                systemStatus = "Generating",
                gridStatus = "Connected (238V)",
                lastUpdated = System.currentTimeMillis(),
                weather = "Sunny"
            ),
            EnergyFlow(
                solarGenerated = 14.5,
                consumed = 8.2,
                exportedToGrid = 6.3,
                importedFromGrid = 0.0,
                batteryStatus = "Not Installed"
            )
        )
    }

    SolarPanelScreen(
        liveGeneration = liveGen,
        energyFlow = energyFlow,
        onNavigateBack = onNavigateBack,
        onNavigateToDashboard = onNavigateToDashboard
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolarPanelScreen(
    liveGeneration: LiveGeneration,
    energyFlow: EnergyFlow,
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    Scaffold(
        containerColor = StitchSurface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Solar Panel Telemetry",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = StitchOnSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                            tint = StitchOnSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StitchSurface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(StitchSurface),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Live Status Banner
                SolarEaseCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = StitchPrimaryFixed,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.SolarPower,
                                        contentDescription = null,
                                        tint = StitchPrimary,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Inverter Status: ${liveGeneration.systemStatus}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = StitchOnSurface
                                )
                                Text(
                                    text = "${liveGeneration.gridStatus} • ${liveGeneration.weather}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = StitchOnSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SolarEaseSemanticColors.SuccessContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SolarEaseSemanticColors.SuccessText,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Online",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SolarEaseSemanticColors.SuccessText
                                )
                            }
                        }
                    }
                }

                // Metric Cards Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SolarEaseMetricCard(
                        label = "Current Output",
                        value = "${liveGeneration.currentOutput} kW",
                        icon = Icons.Default.Bolt,
                        trendDeltaPct = 4.5,
                        subtitle = "Instantaneous Power",
                        modifier = Modifier.weight(1f)
                    )

                    SolarEaseMetricCard(
                        label = "Today's Yield",
                        value = "${liveGeneration.todayGeneration} kWh",
                        icon = Icons.Default.WbSunny,
                        trendDeltaPct = 8.2,
                        subtitle = "Cumulative Energy",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Energy Flow Telemetry Card
                SolarEaseCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics(mergeDescendants = true) {
                            contentDescription = "Energy Flow: Generated ${energyFlow.solarGenerated} kWh, Consumed ${energyFlow.consumed} kWh, Exported ${energyFlow.exportedToGrid} kWh, Imported ${energyFlow.importedFromGrid} kWh"
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Energy Balance & Inverter Flow",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = StitchOnSurface
                        )

                        HorizontalDivider(color = StitchOutline.copy(alpha = 0.3f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Solar Plant Generation",
                                style = MaterialTheme.typography.bodyMedium,
                                color = StitchOnSurfaceVariant
                            )
                            Text(
                                text = "${energyFlow.solarGenerated} kWh",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = StitchPrimary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Direct Home Consumption",
                                style = MaterialTheme.typography.bodyMedium,
                                color = StitchOnSurfaceVariant
                            )
                            Text(
                                text = "${energyFlow.consumed} kWh",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = StitchOnSurface
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Net Export to DISCOM Grid",
                                style = MaterialTheme.typography.bodyMedium,
                                color = StitchOnSurfaceVariant
                            )
                            Text(
                                text = "${energyFlow.exportedToGrid} kWh",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = StitchSecondary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Battery Storage System",
                                style = MaterialTheme.typography.bodyMedium,
                                color = StitchOnSurfaceVariant
                            )
                            Text(
                                text = energyFlow.batteryStatus,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = StitchOnSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Primary CTA: View Full Customer Dashboard
                Button(
                    onClick = onNavigateToDashboard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StitchPrimary,
                        contentColor = StitchOnPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "View Full Monitoring Dashboard",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Secondary CTA: Return to Home
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, StitchOutline)
                ) {
                    Text(
                        text = "Return to Home",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = StitchOnSurface
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
