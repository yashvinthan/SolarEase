package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun SolarEaseStepProgressBar(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    stepTitle: String? = null,
    category: String? = null,
    onBack: (() -> Unit)? = null,
    percentageText: String? = null
) {
    val progressRatio = if (totalSteps > 0) {
        (currentStep.toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progressRatio,
        animationSpec = motionTween(durationMillis = SolarEaseMotion.durationMedium),
        label = "stepProgressBarAnimation"
    )

    val contentDesc = if (stepTitle.isNullOrBlank()) {
        "Step $currentStep of $totalSteps"
    } else {
        "Step $currentStep of $totalSteps: $stepTitle"
    }
    val stateDesc = "${(progressRatio * 100).toInt()} percent complete"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = contentDesc
                stateDescription = stateDesc
            },
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                    .semantics { role = Role.Button }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back",
                    tint = StitchOnSurface
                )
            }
        }

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
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(StitchPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$currentStep",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (!stepTitle.isNullOrBlank()) {
                    Text(
                        text = stepTitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = StitchPrimary
                    )
                }
            }

            val rightLabel = percentageText ?: category
            if (rightLabel != null) {
                Text(
                    text = rightLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = StitchSecondary
                )
            }
        }

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = StitchPrimary,
            trackColor = StitchSurfaceContainer
        )
    }
}
