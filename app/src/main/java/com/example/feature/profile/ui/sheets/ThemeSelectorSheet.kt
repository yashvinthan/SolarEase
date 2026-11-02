package com.example.feature.profile.ui.sheets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SolarEaseTheme
import com.example.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectorSheet(
    currentThemeMode: ThemeMode,
    onThemeSelect: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = SolarEaseTheme.colors

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Appearance & Theme",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                    Text(
                        text = "Select application visual theme and contrast mode",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = colors.onSurfaceVariant
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = colors.surfaceVariant,
                border = BorderStroke(1.dp, colors.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    ThemeOptionRow(
                        title = "System Default",
                        subtitle = "Follow system dark mode setting automatically",
                        icon = Icons.Filled.BrightnessAuto,
                        selected = currentThemeMode == ThemeMode.SYSTEM,
                        onClick = { onThemeSelect(ThemeMode.SYSTEM) },
                        showDivider = true
                    )
                    ThemeOptionRow(
                        title = "Light Mode",
                        subtitle = "Clean solar emerald palette with high contrast",
                        icon = Icons.Filled.LightMode,
                        selected = currentThemeMode == ThemeMode.LIGHT,
                        onClick = { onThemeSelect(ThemeMode.LIGHT) },
                        showDivider = true
                    )
                    ThemeOptionRow(
                        title = "Dark Mode",
                        subtitle = "Night-optimized surfaces with zero color bleeding",
                        icon = Icons.Filled.DarkMode,
                        selected = currentThemeMode == ThemeMode.DARK,
                        onClick = { onThemeSelect(ThemeMode.DARK) },
                        showDivider = false
                    )
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                Text("Confirm", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    showDivider: Boolean
) {
    val colors = SolarEaseTheme.colors
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .semantics(mergeDescendants = true) {
                    role = Role.RadioButton
                }
                .clickable(role = Role.RadioButton, onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) colors.primary else colors.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
                Column {
                    Text(
                        text = title,
                        fontSize = 14.5.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) colors.primary else colors.onSurface
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.5.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = colors.primary,
                    unselectedColor = colors.onSurfaceVariant
                )
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 14.dp),
                thickness = 0.5.dp,
                color = colors.outlineVariant
            )
        }
    }
}
