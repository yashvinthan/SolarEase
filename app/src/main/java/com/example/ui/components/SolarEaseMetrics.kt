package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.util.Locale

enum class MetricTrendDirection {
    POSITIVE,
    NEGATIVE,
    NEUTRAL
}

fun evaluateTrendDirection(delta: Double?): MetricTrendDirection = when {
    delta == null -> MetricTrendDirection.NEUTRAL
    delta > 0.0 -> MetricTrendDirection.POSITIVE
    delta < 0.0 -> MetricTrendDirection.NEGATIVE
    else -> MetricTrendDirection.NEUTRAL
}

fun formatMetricValue(value: Double, unit: String): String = when (unit) {
    "₹" -> "₹%,.0f".format(Locale.US, value)
    "kWh" -> "%,.1f kWh".format(Locale.US, value)
    "kg" -> "%,.0f kg".format(Locale.US, value)
    "Tons" -> "%,.2f Tons".format(Locale.US, value)
    else -> if (unit.isBlank()) "$value" else "$value $unit"
}

data class SolarEaseMetricItem(
    val label: String,
    val value: Double,
    val unit: String,
    val icon: ImageVector? = null,
    val trendDeltaPct: Double? = null,
    val subtitle: String? = null
)

@Composable
fun SolarEaseMetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    trendDeltaPct: Double? = null,
    subtitle: String? = null
) {
    SolarEaseCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = StitchOnSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = StitchSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = StitchOnSurface
                )

                if (trendDeltaPct != null) {
                    val direction = evaluateTrendDirection(trendDeltaPct)
                    val (badgeBg, badgeText, badgeIcon) = when (direction) {
                        MetricTrendDirection.POSITIVE -> Triple(
                            SolarEaseSemanticColors.SuccessContainer,
                            SolarEaseSemanticColors.SuccessText,
                            Icons.AutoMirrored.Filled.TrendingUp
                        )
                        MetricTrendDirection.NEGATIVE -> Triple(
                            SolarEaseSemanticColors.DangerContainer,
                            SolarEaseSemanticColors.DangerText,
                            Icons.AutoMirrored.Filled.TrendingDown
                        )
                        MetricTrendDirection.NEUTRAL -> Triple(
                            StitchSurfaceContainerHigh,
                            StitchOnSurfaceVariant,
                            Icons.AutoMirrored.Filled.TrendingFlat
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = badgeBg
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = badgeIcon,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = badgeText
                            )
                            val prefix = if (trendDeltaPct > 0) "+" else ""
                            Text(
                                text = "$prefix%.1f%%".format(Locale.US, trendDeltaPct),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeText
                            )
                        }
                    }
                }
            }

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = StitchOnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SolarEaseMetricCard(
    label: String,
    value: Double,
    unit: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    trendDeltaPct: Double? = null,
    subtitle: String? = null
) {
    SolarEaseMetricCard(
        label = label,
        value = formatMetricValue(value, unit),
        modifier = modifier,
        icon = icon,
        trendDeltaPct = trendDeltaPct,
        subtitle = subtitle
    )
}

@Composable
fun SolarEaseMetricStrip(
    metrics: List<SolarEaseMetricItem>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (item in metrics) {
            SolarEaseMetricCard(
                label = item.label,
                value = item.value,
                unit = item.unit,
                icon = item.icon,
                trendDeltaPct = item.trendDeltaPct,
                subtitle = item.subtitle,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
