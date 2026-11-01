package com.example.feature.assessment.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class StitchVendorItem(
    val id: String,
    val name: String,
    val rating: Double,
    val reviewCount: Int,
    val distance: String,
    val chips: List<String>
)

@Composable
fun NearbyVendorsRoute(
    vendorViewModel: SolarVendorViewModel,
    onNavigateNext: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by vendorViewModel.state.collectAsState()

    NearbyVendorsScreen(
        capacityKw = state.recommendedCapacityKw,
        propertyType = state.propertyType,
        systemType = state.systemType,
        mountType = state.mountType,
        panelType = if (state.panelType.contains("Non-DCR")) "Non-DCR" else "DCR",
        onSelectVendor = { vendorId, vendorName ->
            vendorViewModel.selectVendor(vendorId, vendorName)
            onNavigateNext(vendorId)
        },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NearbyVendorsScreen(
    capacityKw: Double,
    propertyType: String,
    systemType: String,
    mountType: String,
    panelType: String,
    onSelectVendor: (String, String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val vendors = remember {
        listOf(
            StitchVendorItem(
                id = "kaveri_solar",
                name = "Kaveri Solar Systems (Chennai)",
                rating = 4.9,
                reviewCount = 342,
                distance = "3.8 km away",
                chips = listOf("Tata Power Partner", "PM Surya Ghar Empaneled", "DCR Subsidy")
            ),
            StitchVendorItem(
                id = "kongu_renewable",
                name = "Kongu Renewable Energy (Coimbatore)",
                rating = 4.8,
                reviewCount = 280,
                distance = "5.2 km away",
                chips = listOf("Adani Solar Certified", "On-Grid Net Metering", "Tier-1 Mono")
            ),
            StitchVendorItem(
                id = "pandian_green",
                name = "Pandian Green Power EPC (Madurai)",
                rating = 4.8,
                reviewCount = 195,
                distance = "6.4 km away",
                chips = listOf("Waaree Energies", "DCR Subsidy Approved", "Hybrid Inverter")
            ),
            StitchVendorItem(
                id = "sunvolt_power",
                name = "SunVolt CleanTech Engineering (Trichy)",
                rating = 4.9,
                reviewCount = 210,
                distance = "7.1 km away",
                chips = listOf("Turnkey EPC", "Microtek Inverters", "25-Yr Warranty")
            ),
            StitchVendorItem(
                id = "salem_ecodynamics",
                name = "Salem EcoDynamics Solar (Salem)",
                rating = 4.7,
                reviewCount = 165,
                distance = "8.5 km away",
                chips = listOf("Havells Partner", "Residential Rooftop", "DCR")
            )
        )
    }

    Scaffold(
        containerColor = StitchSurface,
        topBar = {
            StitchVendorTopBar(
                title = "Vendor Discovery",
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Nearby Solar Vendors",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = StitchOnSurface,
                            letterSpacing = 0.sp
                        )
                        Text(
                            text = "Choose an empaneled vendor to request an official proposal.",
                            fontSize = 15.sp,
                            color = StitchSecondary
                        )
                    }
                }

                // Requirement Matched Filter Banner
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = StitchSurfaceContainerHigh,
                        shadowElevation = 0.5.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StitchPrimaryContainer,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = null,
                                        tint = StitchOnPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "REQUIREMENT MATCHED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StitchSecondary,
                                    letterSpacing = 0.8.sp
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = StitchPrimaryContainer,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "${String.format("%.1f", capacityKw)} kW • $propertyType • $systemType • $mountType • $panelType",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = StitchOnSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Vendor Cards (Exact Stitch layout)
                items(vendors) { vendor ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = StitchSurfaceContainerLowest,
                        border = BorderStroke(1.dp, StitchOutline.copy(alpha = 0.5f)),
                        shadowElevation = 0.5.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Header row: Name, rating, distance & verified pill
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = vendor.name,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StitchOnSurface
                                        )
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = StitchPrimaryContainer,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.semantics(mergeDescendants = true) {
                                            contentDescription = "Rating ${vendor.rating} out of 5 from ${vendor.reviewCount} reviews, ${vendor.distance}"
                                        },
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = StitchPrimaryContainer,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "${vendor.rating}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StitchPrimaryContainer
                                        )
                                        Text(
                                            text = "(${vendor.reviewCount} reviews)",
                                            fontSize = 12.sp,
                                            color = StitchSecondary
                                        )
                                        Text(
                                            text = "•",
                                            fontSize = 12.sp,
                                            color = StitchSecondary
                                        )
                                        Text(
                                            text = vendor.distance,
                                            fontSize = 12.sp,
                                            color = StitchSecondary
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = StitchPrimaryContainer.copy(alpha = 0.12f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = StitchPrimaryContainer,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "Verified Vendor",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StitchPrimaryContainer
                                        )
                                    }
                                }
                            }

                            // Tags row
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                vendor.chips.forEach { tag ->
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = StitchSurfaceContainerHigh
                                    ) {
                                        Text(
                                            text = tag,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = StitchSecondary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            // View Profile Button
                            Button(
                                onClick = { onSelectVendor(vendor.id, vendor.name) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StitchPrimary,
                                    contentColor = StitchOnPrimary
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "View Profile",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom trust card
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = StitchPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "All vendors are licensed and certified for TNEB grid-tied installations.",
                            fontSize = 12.sp,
                            color = StitchSecondary
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

