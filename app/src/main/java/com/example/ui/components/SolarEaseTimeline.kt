package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class SolarEaseMilestone(
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val timestamp: String? = null
)

fun calculateTimelineProgress(milestones: List<SolarEaseMilestone>): Float {
    if (milestones.isEmpty()) return 0f
    return milestones.count { it.isCompleted }.toFloat() / milestones.size.toFloat()
}

@Composable
fun SolarEaseTimeline(
    milestones: List<SolarEaseMilestone>,
    modifier: Modifier = Modifier,
    nextMilestoneTitle: String? = null,
    nextMilestoneProgress: Float? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        milestones.forEachIndexed { index, milestone ->
            val isLast = index == milestones.lastIndex

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (milestone.isCompleted) StitchPrimary
                                else StitchSurfaceContainerHigh
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (milestone.isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = StitchOnPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(StitchOutline)
                            )
                        }
                    }

                    if (!isLast) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(44.dp)
                                .background(
                                    if (milestone.isCompleted) StitchPrimary
                                    else StitchOutline.copy(alpha = 0.35f)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = if (isLast) 0.dp else 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = milestone.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (milestone.isCompleted) FontWeight.Bold else FontWeight.Medium,
                            color = if (milestone.isCompleted) StitchOnSurface else StitchOnSurfaceVariant
                        )
                        if (milestone.timestamp != null) {
                            Text(
                                text = milestone.timestamp,
                                style = MaterialTheme.typography.bodySmall,
                                color = StitchSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = milestone.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = StitchOnSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SolarEaseFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onClick
            )
            .semantics(mergeDescendants = true) {
                role = Role.RadioButton
                this.selected = selected
                stateDescription = if (selected) "Selected" else "Not selected"
            },
        shape = RoundedCornerShape(12.dp),
        color = if (selected) StitchPrimaryContainer else StitchSurfaceContainerLow,
        border = BorderStroke(
            1.dp,
            if (selected) StitchPrimaryContainer else StitchOutline.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (selected) StitchOnPrimaryContainer else StitchOnSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) StitchOnPrimaryContainer else StitchOnSurfaceVariant
            )
        }
    }
}

@Composable
fun <T> SolarEaseFilterChipGroup(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    labelProvider: (T) -> String = { it.toString() },
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (option in options) {
            val isSelected = option == selectedOption
            SolarEaseFilterChip(
                selected = isSelected,
                onClick = { onOptionSelected(option) },
                label = labelProvider(option)
            )
        }
    }
}
