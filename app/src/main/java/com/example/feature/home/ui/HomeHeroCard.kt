package com.example.feature.home.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SolarEaseSemanticColors
import com.example.ui.theme.StitchOnSurface
import com.example.ui.theme.StitchOnSurfaceVariant
import com.example.ui.theme.StitchPrimary
import com.example.ui.theme.StitchPrimaryContainer

/**
 * Hero Header greeting and stage orientation banner for SolarEase Home.
 */
@Composable
fun GreetingHeader(
    greeting: String,
    userName: String?,
    onboardingStep: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (onboardingStep) {
                        1 -> if (!userName.isNullOrBlank()) "$userName's Overview" else "SolarEase"
                        2 -> "Electricity connection active"
                        else -> if (!userName.isNullOrBlank()) "$userName's Energy" else "Energy Overview"
                    },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 0.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = when (onboardingStep) {
                1 -> "Connect your electricity bill to get started."
                2 -> "Enter the number shown on your electricity meter to track your usage."
                else -> "See how much electricity you use and explore ways to save."
            },
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )
    }
}

/**
 * 3-Stage Progressive Onboarding Progress Stepper Banner.
 */
@Composable
fun OnboardingProgressBanner(
    currentStep: Int,
    isAuditCompleted: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = when (currentStep) {
                            1 -> "STEP 1 OF 3 • CONNECT YOUR BILL"
                            2 -> "STEP 2 OF 3 • ADD METER READING"
                            else -> "STEP 3 OF 3 • CHECK HOME ENERGY"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when {
                        currentStep == 1 -> SolarEaseSemanticColors.WarningContainer
                        currentStep == 2 -> MaterialTheme.colorScheme.secondaryContainer
                        isAuditCompleted -> MaterialTheme.colorScheme.primaryContainer
                        else -> SolarEaseSemanticColors.SuccessContainer
                    }
                ) {
                    Text(
                        text = when {
                            currentStep == 1 -> "33% Ready"
                            currentStep == 2 -> "66% Complete"
                            isAuditCompleted -> "100% Ready"
                            else -> "Final Setup Step"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            currentStep == 1 -> SolarEaseSemanticColors.WarningText
                            currentStep == 2 -> MaterialTheme.colorScheme.onSecondaryContainer
                            else -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            val targetProgress = when (currentStep) {
                1 -> 0.33f
                2 -> 0.66f
                else -> 1.0f
            }
            val animatedProgress by animateFloatAsState(
                targetValue = targetProgress,
                animationSpec = tween(500),
                label = "onboardingProgress"
            )

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OnboardingStepChip(
                    modifier = Modifier.weight(1f),
                    stepNumber = "1",
                    label = "Connect Bill",
                    isCompleted = currentStep > 1,
                    isActive = currentStep == 1
                )
                OnboardingStepChip(
                    modifier = Modifier.weight(1f),
                    stepNumber = "2",
                    label = "Meter Log",
                    isCompleted = currentStep > 2,
                    isActive = currentStep == 2
                )
                OnboardingStepChip(
                    modifier = Modifier.weight(1f),
                    stepNumber = "3",
                    label = "Solar Lens",
                    isCompleted = isAuditCompleted,
                    isActive = currentStep == 3 && !isAuditCompleted
                )
            }
        }
    }
}

@Composable
fun OnboardingStepChip(
    stepNumber: String,
    label: String,
    isCompleted: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = when {
            isActive -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            isCompleted -> MaterialTheme.colorScheme.surfaceVariant
            else -> MaterialTheme.colorScheme.surfaceContainerLow
        },
        border = BorderStroke(
            1.dp,
            when {
                isActive -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                isCompleted -> SolarEaseSemanticColors.SuccessText.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SolarEaseSemanticColors.SuccessText,
                    modifier = Modifier.size(13.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(
                            if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stepNumber,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = when {
                    isActive -> MaterialTheme.colorScheme.primary
                    isCompleted -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                maxLines = 1
            )
        }
    }
}

/**
 * Preview card shown in Step 2 indicating that Step 3 will unlock after establishing a baseline.
 */
@Composable
fun LockedStep3PreviewCard(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Step 3: Audit with Solar Lens",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Unlocks after 2nd meter reading is saved",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "Locked",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = "Once your second reading establishes your household burn rate, you'll be able to audit power-hungry appliances using camera-assisted Solar Lens.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 15.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Air Conditioner", "Water Heater", "Water Pump").forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}
