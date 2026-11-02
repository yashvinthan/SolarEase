package com.example.feature.lens.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature.lens.ui.theme.LensColors
import com.example.feature.lens.ui.theme.LensTypography

@Composable
fun RenameRoomDialog(
    currentName: String,
    title: String = "Rename Room",
    confirmButtonText: String = "Save",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    val isValid = name.trim().isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = LensTypography.headlineSm, color = LensColors.SlateText) },
        text = {
            Column {
                Text(
                    if (currentName.isEmpty()) "Enter the name for the new room:" else "Enter the new name for this room:",
                    style = LensTypography.bodyMd,
                    color = LensColors.SlateCaption
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    label = { Text("Room Name") },
                    placeholder = { Text("e.g. Master Bedroom, Living Room, Kitchen") },
                    isError = !isValid && name.isNotEmpty(),
                    supportingText = if (!isValid && name.isNotEmpty()) {
                        { Text("Room name cannot be empty", color = LensColors.Error) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isValid) {
                        onConfirm(name.trim())
                    }
                },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(containerColor = LensColors.Primary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(confirmButtonText, color = LensColors.OnPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = LensColors.SlateCaption)
            }
        },
        containerColor = LensColors.SurfaceContainerLowest,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun CitizenStepCard(
    stepNumber: String,
    icon: ImageVector,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = LensColors.SurfaceContainerLowest,
        border = BorderStroke(1.dp, LensColors.Outline.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = LensColors.SurfaceContainerLow,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        stepNumber,
                        style = LensTypography.headlineSm,
                        color = LensColors.Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        title,
                        style = LensTypography.headlineSm.copy(fontSize = 15.sp),
                        color = LensColors.SlateText
                    )
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = LensColors.Primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    description,
                    style = LensTypography.bodyMd,
                    color = LensColors.SlateCaption,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ─── Screen B: Room Setup (Stitch Design) ───────────────────────────────────

@Composable
fun RoomSetupContent(
    rooms: List<Room>,
    onAddRoom: () -> Unit,
    onAddPresetRoom: (String) -> Unit = {},
    onRenameRoom: (Room) -> Unit,
    onRemoveRoom: (String) -> Unit,
    onMoveRoomUp: (Int) -> Unit,
    onMoveRoomDown: (Int) -> Unit,
    onProceed: () -> Unit
) {
    val sortedRooms = rooms.sortedBy { it.order }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Friendly Header Banner
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = LensColors.SurfaceContainerHigh
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(LensColors.Primary, CircleShape)
                    )
                    Text(
                        "Your rooms · ${sortedRooms.size} added",
                        style = LensTypography.labelCaps,
                        color = LensColors.SlateText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "Configure your rooms",
                style = LensTypography.headlineLg,
                color = LensColors.SlateText
            )
            Text(
                "We will check your home room by room. You can add, rename, or reorder them anytime.",
                style = LensTypography.bodyMd,
                color = LensColors.SlateCaption,
                lineHeight = 18.sp
            )
        }

        // Helpful Walking Order Tip Callout
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = LensColors.SurfaceContainerLow,
            border = BorderStroke(1.dp, LensColors.Outline.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LensColors.PrimaryFixed,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = LensColors.Primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "HELPFUL TIP",
                        style = LensTypography.labelCaps,
                        color = LensColors.Primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Keep rooms in your daily walking order so scanning is fast, natural, and seamless.",
                        style = LensTypography.bodyMd,
                        color = LensColors.SlateText,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Citizen-Friendly Room Cards List
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            sortedRooms.forEachIndexed { index, room ->
                val roomIcon = when (room.name.lowercase()) {
                    "bedroom", "master bedroom", "guest bedroom" -> Icons.Default.Bed
                    "kitchen" -> Icons.Default.Kitchen
                    "bathroom" -> Icons.Default.Shower
                    "hall", "living room", "hall / living room" -> Icons.Default.Chair
                    else -> Icons.Default.MeetingRoom
                }

                val expectedAppliances = when (room.name.lowercase()) {
                    "bedroom" -> "Fan, AC & lighting"
                    "hall", "living room", "hall / living room" -> "Living area & entertainment"
                    "kitchen" -> "Cooking & refrigeration"
                    "bathroom" -> "Water heater zone"
                    else -> "Everyday household appliances"
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = LensColors.SurfaceContainerLowest,
                    border = BorderStroke(1.dp, LensColors.Outline.copy(alpha = 0.15f)),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 44px circular room icon badge
                        Surface(
                            shape = CircleShape,
                            color = LensColors.PrimaryFixed,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = roomIcon,
                                    contentDescription = null,
                                    tint = LensColors.Primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Room Info
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    room.name,
                                    style = LensTypography.headlineSm.copy(fontSize = 15.sp),
                                    color = LensColors.SlateText
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = LensColors.SurfaceContainer
                                ) {
                                    Text(
                                        "Room ${index + 1}",
                                        style = LensTypography.labelCaps.copy(fontSize = 10.sp),
                                        color = LensColors.SlateCaption,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                expectedAppliances,
                                style = LensTypography.bodyMd.copy(fontSize = 12.sp),
                                color = LensColors.SlateCaption,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Room Actions (Edit, Up, Down, Delete)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onRenameRoom(room) },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit ${room.name}",
                                    tint = LensColors.SlateCaption,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = { onMoveRoomUp(index) },
                                enabled = index > 0,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Move Up",
                                    tint = if (index > 0) LensColors.Primary else LensColors.SlateCaption.copy(alpha = 0.25f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = { onMoveRoomDown(index) },
                                enabled = index < sortedRooms.size - 1,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Move Down",
                                    tint = if (index < sortedRooms.size - 1) LensColors.Primary else LensColors.SlateCaption.copy(alpha = 0.25f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = { onRemoveRoom(room.id) },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete ${room.name}",
                                    tint = LensColors.Error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // "+ Add Another Room" Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAddRoom() },
            shape = RoundedCornerShape(12.dp),
            color = LensColors.SurfaceContainerLow,
            border = BorderStroke(1.dp, LensColors.Primary.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AddCircle,
                    contentDescription = null,
                    tint = LensColors.Primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "+ Add Another Room",
                    style = LensTypography.headlineSm.copy(fontSize = 15.sp),
                    color = LensColors.Primary
                )
            }
        }

        // Quick Add Common Presets
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "QUICK PRESETS",
                style = LensTypography.labelCaps,
                color = LensColors.SlateCaption
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Balcony", "Terrace", "Dining Room", "Home Office").forEach { preset ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LensColors.SurfaceContainerLowest,
                        border = BorderStroke(1.dp, LensColors.Outline.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onAddPresetRoom(preset) }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = LensColors.Primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                preset,
                                style = LensTypography.labelSm.copy(fontSize = 11.sp),
                                color = LensColors.SlateText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // "Take your time" Comfort Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = LensColors.SurfaceContainer
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LensColors.SurfaceContainerHigh,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = LensColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Take your time",
                        style = LensTypography.headlineSm.copy(fontSize = 14.sp),
                        color = LensColors.SlateText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "You can pause and resume this audit whenever convenient.",
                        style = LensTypography.bodyMd.copy(fontSize = 12.sp),
                        color = LensColors.SlateCaption
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Sticky Bottom Trigger Bar
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(LensColors.Primary, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Next: ${sortedRooms.firstOrNull()?.name ?: "Living Room"}",
                        style = LensTypography.labelSm.copy(fontWeight = FontWeight.SemiBold),
                        color = LensColors.SlateText
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "(Room 1 of ${sortedRooms.size})",
                        style = LensTypography.labelCaps,
                        color = LensColors.SlateCaption
                    )
                }
                Text(
                    "READY TO AUDIT",
                    style = LensTypography.labelCaps,
                    color = LensColors.Primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onProceed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LensColors.Primary),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Start Room-by-Room Audit",
                        style = LensTypography.headlineSm.copy(fontSize = 16.sp),
                        color = LensColors.OnPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = LensColors.OnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
