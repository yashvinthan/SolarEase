package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class SolarEaseNavTab {
    HOME,
    ANALYTICS, // Preserved for future re-enabling
    RECOMMENDS,
    PROFILE
}

@Composable
fun SolarEaseBottomNavigation(
    currentTab: SolarEaseNavTab,
    onNavigateToHome: () -> Unit,
    onNavigateToRecommends: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAnalytics: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = StitchSurface.copy(alpha = 0.96f),
        border = BorderStroke(1.dp, StitchOutline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SolarEaseNavItem(
                icon = Icons.Outlined.Home,
                label = "Home",
                isSelected = currentTab == SolarEaseNavTab.HOME,
                onClick = onNavigateToHome,
                modifier = Modifier.weight(1f)
            )
            SolarEaseNavItem(
                icon = Icons.Outlined.Lightbulb,
                label = "Recommends",
                isSelected = currentTab == SolarEaseNavTab.RECOMMENDS,
                onClick = onNavigateToRecommends,
                modifier = Modifier.weight(1f)
            )
            SolarEaseNavItem(
                icon = Icons.Outlined.AccountCircle,
                label = "Profile",
                isSelected = currentTab == SolarEaseNavTab.PROFILE,
                onClick = onNavigateToProfile,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Adaptive vertical Navigation Rail for Medium/Expanded windows (tablets, foldables, landscape).
 */
@Composable
fun SolarEaseNavigationRail(
    currentTab: SolarEaseNavTab,
    onNavigateToHome: () -> Unit,
    onNavigateToRecommends: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAnalytics: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .widthIn(min = 88.dp, max = 120.dp),
        color = StitchSurface,
        border = BorderStroke(1.dp, StitchOutline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 24.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
        ) {
            SolarEaseNavItem(
                icon = Icons.Outlined.Home,
                label = "Home",
                isSelected = currentTab == SolarEaseNavTab.HOME,
                onClick = onNavigateToHome
            )
            SolarEaseNavItem(
                icon = Icons.Outlined.Lightbulb,
                label = "Recommends",
                isSelected = currentTab == SolarEaseNavTab.RECOMMENDS,
                onClick = onNavigateToRecommends
            )
            SolarEaseNavItem(
                icon = Icons.Outlined.AccountCircle,
                label = "Profile",
                isSelected = currentTab == SolarEaseNavTab.PROFILE,
                onClick = onNavigateToProfile
            )
        }
    }
}

/**
 * Overload for backward compatibility with 4-item caller signatures during transitional phases.
 * Analytics tab is omitted from the UI while keeping call-site compatibility.
 */
@Composable
fun SolarEaseBottomNavigation(
    currentTab: SolarEaseNavTab,
    onNavigateToHome: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToRecommends: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    SolarEaseBottomNavigation(
        currentTab = currentTab,
        onNavigateToHome = onNavigateToHome,
        onNavigateToRecommends = onNavigateToRecommends,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToAnalytics = onNavigateToAnalytics
    )
}

@Composable
private fun SolarEaseNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .semantics(mergeDescendants = true) {
                role = Role.Tab
                selected = isSelected
                stateDescription = if (isSelected) "Selected" else "Not selected"
            }
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 52.dp, height = 28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isSelected) StitchPrimaryFixed.copy(alpha = 0.7f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) StitchPrimaryContainer else StitchOnSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) StitchPrimaryContainer else StitchOnSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
