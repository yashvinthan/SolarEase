package com.example.feature.recommends.ui.modules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SolarPower
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SolarEaseTheme
import com.example.ui.theme.StitchOnSurface
import com.example.ui.theme.StitchOnSurfaceVariant
import com.example.ui.theme.StitchOutline
import com.example.ui.theme.StitchPrimary
import com.example.ui.theme.StitchSecondary
import com.example.ui.theme.StitchSurface

@Composable
fun RecommendsTopBar(
    userAddress: String,
    onProfileClick: () -> Unit
) {
    val colors = SolarEaseTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colors.surface.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, colors.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SolarPower,
                        contentDescription = "SolarEase",
                        tint = colors.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SolarEase",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurface,
                        letterSpacing = 0.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = userAddress,
                            fontSize = 11.sp,
                            color = colors.secondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .semantics(mergeDescendants = true) {
                        role = Role.Button
                    }
                    .clickable(role = Role.Button) { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = colors.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendsHeroHeader() {
    val colors = SolarEaseTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 2.dp)
    ) {
        Text(
            text = "Efficiency Guidelines",
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = colors.onSurface,
            letterSpacing = 0.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Follow the 9 verified guidelines to reduce electricity consumption.",
            fontSize = 12.sp,
            color = colors.onSurfaceVariant,
            lineHeight = 16.sp
        )
    }
}
