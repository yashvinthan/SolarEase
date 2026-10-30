package com.example.feature.home.ui.modules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SolarPower
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolarPanelRoute(
    onNavigateBack: () -> Unit
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
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(16.dp),
                color = StitchSurfaceContainerLowest,
                border = BorderStroke(1.dp, StitchOutline)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = StitchPrimaryFixed,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SolarPower,
                                contentDescription = null,
                                tint = StitchPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Text(
                        text = "Solar Panel Module",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = StitchOnSurface
                    )
                    Text(
                        text = "Live inverter telemetry and panel string diagnostics will appear here once your rooftop solar plant is commissioned.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = StitchOnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.heightIn(min = 48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Return to Home")
                    }
                }
            }
        }
    }
}
