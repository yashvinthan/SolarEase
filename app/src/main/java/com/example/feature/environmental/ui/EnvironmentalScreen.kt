package com.example.feature.environmental.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.example.ui.components.SolarEaseCard
import com.example.ui.components.SolarEaseMetricCard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.feature.environmental.domain.model.*
import com.example.ui.theme.rememberIsReducedMotionEnabled

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvironmentalRoute(
    onNavigateBack: () -> Unit,
    viewModel: EnvironmentalViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        val userName = com.example.core.session.UserSession.currentUser?.username
                        Text(if (userName != null) "$userName's Environmental Impact" else "Environmental Impact", style = MaterialTheme.typography.titleLarge)
                        Text("See how your solar system helps the planet", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(
                                android.content.Intent.EXTRA_TEXT,
                                "My SolarEase system reduced carbon emissions and planted trees for the environment!"
                            )
                            type = "text/plain"
                        }
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Environmental Impact"))
                    }, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "Share Impact")
                    }
                    IconButton(onClick = {
                        val downloadIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(
                                android.content.Intent.EXTRA_TEXT,
                                "solarease_report_${System.currentTimeMillis()}.md\n# SolarEase Environmental Impact Report\nCO2 Avoided: Verified"
                            )
                            type = "text/plain"
                        }
                        context.startActivity(android.content.Intent.createChooser(downloadIntent, "Download Environmental Report"))
                    }, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.Download, contentDescription = "Download Report")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Crossfade(targetState = uiState, label = "EnvironmentalState") { state ->
                when (state) {
                    is EnvironmentalUiState.Loading -> EnvironmentalLoadingSkeleton()
                    is EnvironmentalUiState.Success -> {
                        if (!state.data.isSystemActive) {
                            EmptyEnvironmentalState()
                        } else {
                            EnvironmentalContent(state.data)
                        }
                    }
                    is EnvironmentalUiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message, color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}

@Composable
fun EnvironmentalLoadingSkeleton() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(modifier = Modifier.widthIn(max = 720.dp).fillMaxSize().padding(16.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(220.dp).clip(MaterialTheme.shapes.large).background(MaterialTheme.colorScheme.surfaceVariant))
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f).height(120.dp).clip(MaterialTheme.shapes.large).background(MaterialTheme.colorScheme.surfaceVariant))
                Box(modifier = Modifier.weight(1f).height(120.dp).clip(MaterialTheme.shapes.large).background(MaterialTheme.colorScheme.surfaceVariant))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().height(300.dp).clip(MaterialTheme.shapes.large).background(MaterialTheme.colorScheme.surfaceVariant))
        }
    }
}

@Composable
fun EmptyEnvironmentalState() {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.Eco, contentDescription = null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(24.dp))
        Text("No Impact Data Yet", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Environmental statistics will appear as your solar system generates clean energy.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
fun EnvironmentalContent(data: EnvironmentalDashboard) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier.widthIn(max = 720.dp).fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { EnvironmentalOverviewCard(data.overview) }
            item { TodayImpactSection(data.today) }
            item { LifetimeImpactSection(data.lifetime) }
            item { EnvironmentalComparisonSection(data.comparison) }
            item { MonthlyImpactSection(data.history) }
            item { SustainabilityGoalsSection(data.sustainabilityGoals) }
            item { AchievementsSection(data.achievements) }
            item { SmartInsightsSection(data.insights) }
            item { ImpactTimelineSection(data.timeline) }
            item { ShareYourImpactSection() }
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun EnvironmentalOverviewCard(overview: EnvironmentalSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            val contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            Text("CO₂ Emissions Prevented", style = MaterialTheme.typography.bodyMedium, color = contentColor)
            Text("${overview.co2SavedTons} Tons", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Medium, color = contentColor)
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Trees Equivalent", style = MaterialTheme.typography.bodySmall, color = contentColor)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Park, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${overview.treesEquivalent} Trees", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = contentColor)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Green Energy", style = MaterialTheme.typography.bodySmall, color = contentColor)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${overview.greenEnergyKwh} kWh", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = contentColor)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Impact Score: ${overview.impactScore}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
fun TodayImpactSection(today: TodayImpact) {
    Column {
        Text("Today's Impact", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, bottom = 12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(title = "Green Energy", value = "${today.greenEnergyKwh} kWh", icon = Icons.Default.FlashOn, modifier = Modifier.weight(1f))
            MetricCard(title = "CO₂ Reduction", value = "${today.co2ReductionKg} kg", icon = Icons.Default.Co2, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(title = "Trees Equivalent", value = "${today.treesEquivalent}", icon = Icons.Default.Nature, modifier = Modifier.weight(1f))
            MetricCard(title = "Carbon Offset", value = "${today.carbonOffsetKg} kg", icon = Icons.Default.CloudQueue, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun LifetimeImpactSection(lifetime: LifetimeImpact) {
    Column {
        Text("Lifetime Impact", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, bottom = 12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Green Energy", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${lifetime.greenEnergyMwh} MWh", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("CO₂ Reduction", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${lifetime.co2ReductionTons} Tons", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Trees Equivalent", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${lifetime.treesEquivalent}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Coal Offset", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${lifetime.coalOffsetKg} kg", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("Fuel Saved", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${lifetime.fuelSavedLiters} L", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
fun EnvironmentalComparisonSection(comparison: EnvironmentalComparison) {
    Column {
        Text("Environmental Comparison", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, bottom = 12.dp))
        Text("Your clean energy generation is equivalent to:", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp, bottom = 12.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ComparisonRow(icon = Icons.Default.Park, text = "${comparison.treesPlanted} Trees Planted")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                ComparisonRow(icon = Icons.Default.DirectionsCar, text = "${comparison.carsRemoved} Cars Removed from Roads")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                ComparisonRow(icon = Icons.Default.Factory, text = "${comparison.coalNotBurnedKg} kg Coal Not Burned")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                ComparisonRow(icon = Icons.Default.LocalGasStation, text = "${comparison.petrolSavedLiters} Liters Petrol Saved")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                ComparisonRow(icon = Icons.Default.Home, text = "${comparison.homesPowered} Homes Powered (1 year)")
            }
        }
    }
}

@Composable
fun ComparisonRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun MetricCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun MonthlyImpactSection(history: ImpactHistory) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Month", "Year")
    val chartData = when (selectedTab) {
        0 -> history.monthlyEnergy
        else -> history.yearlyEnergy
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Impact Over Time", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(16.dp))
            
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (chartData.isNotEmpty()) {
                val maxValue = chartData.maxOf { it.value }
                val barColor = MaterialTheme.colorScheme.primary
                val peakLabel = chartData.maxByOrNull { it.value }?.label ?: ""
                val chartSummary = chartData.joinToString(", ") { "${it.label}: ${it.value}" }
                val stateDesc = "Interactive impact chart with ${chartData.size} periods. Peak: $peakLabel ($maxValue)."
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .semantics {
                            contentDescription = "Impact over time chart ($chartSummary)"
                            stateDescription = stateDesc
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        val barGroupWidth = width / chartData.size
                        val spacing = barGroupWidth * 0.2f
                        val singleBarWidth = barGroupWidth - spacing
                        
                        chartData.forEachIndexed { index, point ->
                            val barHeight = if (maxValue > 0) ((point.value / maxValue) * height * 0.8f).toFloat() else 0f
                            val x = (index * barGroupWidth) + (spacing / 2f)
                            val y = height - barHeight - 20.dp.toPx()
                            
                            drawRoundRect(
                                color = barColor,
                                topLeft = Offset(x, y),
                                size = Size(singleBarWidth, barHeight),
                                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                            )
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        chartData.forEach { point ->
                            Text(
                                text = point.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(barColor))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Green Energy Generated", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun SustainabilityGoalsSection(goals: List<EnvironmentalMilestone>) {
    Column {
        Text("Sustainability Goals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, bottom = 12.dp))
        goals.forEach { goal ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (goal.isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
                colors = CardDefaults.cardColors(
                    containerColor = if (goal.isCompleted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (goal.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (goal.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            goal.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = if (goal.isCompleted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    if (!goal.isCompleted && goal.progressValue != null && goal.targetValue != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        val progress = goal.currentProgressPercentage ?: 0f
                        val animatedProgress by animateFloatAsState(targetValue = progress)
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${goal.progressValue} ${goal.unit}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${goal.targetValue} ${goal.unit}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = animatedProgress,
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementsSection(achievements: List<EnvironmentalAchievement>) {
    Column {
        Text("Achievements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, bottom = 12.dp))
        achievements.forEach { achievement ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFFFFD54F)), // Gold-ish
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = when (achievement.iconId) {
                            "starter" -> Icons.Default.Eco
                            "champion" -> Icons.Default.WbSunny
                            "protector" -> Icons.Default.Public
                            else -> Icons.Default.EmojiEvents
                        }
                        Icon(icon, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(achievement.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Unlocked: ${achievement.unlockedDate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(achievement.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun SmartInsightsSection(insights: List<EnvironmentalInsight>) {
    Column {
        Text("Smart Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
        insights.forEach { insight ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(insight.message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
fun ImpactTimelineSection(timeline: ImpactTimeline) {
    Column {
        Text("Impact Timeline", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 4.dp, bottom = 12.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                timeline.events.forEachIndexed { index, event ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(32.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(if (event.isCompleted) MaterialTheme.colorScheme.primary else Color.Gray)
                            )
                            if (index < timeline.events.size - 1 || timeline.nextMilestone.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(40.dp)
                                        .background(if (event.isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.3f))
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.padding(bottom = 16.dp)) {
                            Text(event.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = if (event.isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
                            if (event.date != null) {
                                Text(event.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (event.description != null) {
                                Text(event.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
                
                if (timeline.nextMilestone.isNotEmpty()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(32.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                                    .padding(2.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        color = Color.Gray,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Next Milestone", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(timeline.nextMilestone, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            val animatedProgress by animateFloatAsState(targetValue = timeline.nextMilestoneProgress)
                            LinearProgressIndicator(
                                progress = animatedProgress,
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            )
                            Text("${(timeline.nextMilestoneProgress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.End).padding(top = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShareYourImpactSection() {
    val context = LocalContext.current

    Column {
        Text("Share Your Impact", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, bottom = 12.dp))
        Button(
            onClick = {
                val shareIntent = android.content.Intent().apply {
                    action = android.content.Intent.ACTION_SEND
                    putExtra(
                        android.content.Intent.EXTRA_TEXT,
                        "My SolarEase rooftop solar system generated clean green energy and avoided CO₂ emissions! Join the clean energy movement."
                    )
                    type = "text/plain"
                }
                context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Environmental Report"))
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share Environmental Report")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = {
                val downloadIntent = android.content.Intent().apply {
                    action = android.content.Intent.ACTION_SEND
                    putExtra(
                        android.content.Intent.EXTRA_TEXT,
                        "solarease_report_${System.currentTimeMillis()}.md\n# SolarEase Environmental Impact Summary\nVerified clean energy generation."
                    )
                    type = "text/plain"
                }
                context.startActivity(android.content.Intent.createChooser(downloadIntent, "Download PDF Summary"))
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Download PDF")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = {
                val cardIntent = android.content.Intent().apply {
                    action = android.content.Intent.ACTION_SEND
                    putExtra(
                        android.content.Intent.EXTRA_TEXT,
                        "Proud to power my home with SolarEase! Generating solar power and fighting climate change. #SolarEase #CleanEnergy"
                    )
                    type = "text/plain"
                }
                context.startActivity(android.content.Intent.createChooser(cardIntent, "Share Social Card"))
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generate Social Media Card")
        }
    }
}
