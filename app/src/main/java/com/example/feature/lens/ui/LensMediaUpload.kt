package com.example.feature.lens.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature.lens.ui.theme.LensColors
import com.example.feature.lens.ui.theme.LensTypography

@Composable
fun RoomOverviewContent(
    room: Room,
    activeIdx: Int,
    total: Int,
    selectedPhotos: List<android.net.Uri> = emptyList(),
    onRenameRoom: (Room) -> Unit,
    onLiveScanClicked: () -> Unit,
    onUploadPhotosClicked: () -> Unit,
    onUploadVideoClicked: () -> Unit,
    onCapturePhotoClicked: () -> Unit,
    onRecordVideoClicked: () -> Unit,
    onRemovePhoto: (android.net.Uri) -> Unit = {},
    onAnalyzePhotos: (List<android.net.Uri>) -> Unit = {},
    onSkipRoom: () -> Unit
) {
    var showVideoOptions by remember { mutableStateOf(false) }
    val progressPercent = (activeIdx.toFloat() / total.coerceAtLeast(1).toFloat() * 100).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress Header
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        "Room $activeIdx of $total · ${room.name}",
                        style = LensTypography.labelCaps,
                        color = LensColors.Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { onRenameRoom(room) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Rename Room",
                            tint = LensColors.SlateCaption,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
                Text(
                    "$progressPercent% Done",
                    style = LensTypography.labelSm,
                    color = LensColors.SlateCaption,
                    fontWeight = FontWeight.Medium
                )
            }

            // Segmented Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 1..total.coerceAtLeast(1)) {
                    val isCompleted = i <= activeIdx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(if (isCompleted) LensColors.Primary else LensColors.SurfaceContainerHigh)
                    )
                }
            }
        }

        // Warm Title Section
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "Let's check your ${room.name.lowercase()}",
                style = LensTypography.headlineLg,
                color = LensColors.SlateText
            )
            Text(
                "Take pictures of the appliances in the room",
                style = LensTypography.bodyMd,
                color = LensColors.SlateCaption,
                lineHeight = 18.sp
            )
        }

        // Primary Option: Add Room Photos Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = LensColors.SurfaceContainerLowest,
            border = BorderStroke(1.dp, LensColors.Outline.copy(alpha = 0.15f)),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = LensColors.SurfaceContainerHigh,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = LensColors.Primary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "Add room photos",
                                style = LensTypography.headlineSm,
                                color = LensColors.SlateText
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = LensColors.PrimaryFixed
                            ) {
                                Text(
                                    "EASIEST",
                                    style = LensTypography.labelCaps.copy(fontSize = 9.sp),
                                    color = LensColors.Primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Take pictures of the appliances in the room",
                            style = LensTypography.bodyMd,
                            color = LensColors.SlateCaption,
                            lineHeight = 16.sp
                        )
                    }
                }

                // Selected Photos Section (Thumbnail display, remove, count, and analyze button)
                if (selectedPhotos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = LensColors.Primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "${selectedPhotos.size} of 4 photos selected",
                                style = LensTypography.labelSm.copy(fontWeight = FontWeight.SemiBold),
                                color = LensColors.SlateText
                            )
                        }

                        if (selectedPhotos.size >= 4) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = LensColors.PrimaryFixed
                            ) {
                                Text(
                                    "MAX REACHED",
                                    style = LensTypography.labelCaps.copy(fontSize = 9.sp),
                                    color = LensColors.Primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4-Slot Photo Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (i in 0 until 4) {
                            val photoUri = selectedPhotos.getOrNull(i)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(LensColors.SurfaceContainer)
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            if (photoUri != null) LensColors.Primary.copy(alpha = 0.5f) else LensColors.Outline.copy(alpha = 0.2f)
                                        ),
                                        RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (photoUri != null) {
                                    PhotoThumbnail(
                                        uri = photoUri,
                                        index = i + 1,
                                        onRemove = { onRemovePhoto(photoUri) }
                                    )
                                } else if (i == selectedPhotos.size) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable(onClick = onCapturePhotoClicked),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            Icons.Default.AddAPhoto,
                                            contentDescription = "Add another photo",
                                            tint = LensColors.Primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            "+ Add",
                                            style = LensTypography.labelCaps.copy(fontSize = 10.sp),
                                            color = LensColors.Primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Text(
                                        "#${i + 1}",
                                        style = LensTypography.labelCaps.copy(fontSize = 11.sp),
                                        color = LensColors.SlateCaption.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Primary Analyze Button when at least 1 photo is selected
                    Button(
                        onClick = { onAnalyzePhotos(selectedPhotos) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LensColors.Primary),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Analyze ${selectedPhotos.size} Photo${if (selectedPhotos.size > 1) "s" else ""}",
                                style = LensTypography.labelSm.copy(fontWeight = FontWeight.Bold),
                                color = LensColors.OnPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = LensColors.OnPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dual Action Buttons: Side-by-side [Take Photo] & [Choose Gallery]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isMaxPhotos = selectedPhotos.size >= 4
                    Button(
                        onClick = onCapturePhotoClicked,
                        enabled = !isMaxPhotos,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LensColors.Primary,
                            disabledContainerColor = LensColors.SurfaceContainerHigh
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = if (!isMaxPhotos) LensColors.OnPrimary else LensColors.SlateCaption,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                when {
                                    selectedPhotos.isEmpty() -> "Take Photo"
                                    isMaxPhotos -> "Max 4 Photos"
                                    else -> "Add Photo (${selectedPhotos.size}/4)"
                                },
                                style = LensTypography.labelSm.copy(fontWeight = FontWeight.SemiBold),
                                color = if (!isMaxPhotos) LensColors.OnPrimary else LensColors.SlateCaption
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clickable { onUploadPhotosClicked() },
                        shape = RoundedCornerShape(12.dp),
                        color = LensColors.SurfaceContainer,
                        border = BorderStroke(1.dp, LensColors.Outline.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = null,
                                tint = LensColors.Primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Choose Gallery",
                                style = LensTypography.labelSm.copy(fontWeight = FontWeight.SemiBold),
                                color = LensColors.SlateText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Video Action Toggle
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextButton(onClick = { showVideoOptions = !showVideoOptions }) {
                        Icon(
                            Icons.Default.Videocam,
                            contentDescription = null,
                            tint = LensColors.Tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (showVideoOptions) "Hide video options" else "Or choose a short room video clip",
                            style = LensTypography.labelSm,
                            color = LensColors.Tertiary
                        )
                    }

                    if (showVideoOptions) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onRecordVideoClicked,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Record Video", style = LensTypography.labelSm)
                            }
                            OutlinedButton(
                                onClick = onUploadVideoClicked,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Choose Video", style = LensTypography.labelSm)
                            }
                        }
                    }
                }
            }
        }

        // Option B: Live camera scan Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = LensColors.SurfaceContainerLow,
            border = BorderStroke(1.dp, LensColors.Outline.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LensColors.SurfaceContainerLowest,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.ViewInAr,
                                contentDescription = null,
                                tint = LensColors.SlateCaption,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            "Live camera scan",
                            style = LensTypography.headlineSm.copy(fontSize = 15.sp),
                            color = LensColors.SlateText
                        )
                        Text(
                            "Scan around the room in real time",
                            style = LensTypography.bodyMd.copy(fontSize = 12.sp),
                            color = LensColors.SlateCaption
                        )
                    }
                }

                Button(
                    onClick = onLiveScanClicked,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LensColors.SurfaceContainerLowest),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                ) {
                    Text(
                        "Start Scan",
                        style = LensTypography.labelSm.copy(fontWeight = FontWeight.SemiBold),
                        color = LensColors.SlateText
                    )
                }
            }
        }

        // Helpful Tips Box
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = LensColors.SurfaceContainer
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.TipsAndUpdates,
                        contentDescription = null,
                        tint = LensColors.Primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "HELPFUL TIPS",
                        style = LensTypography.labelCaps,
                        color = LensColors.SlateText,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text("• ", color = LensColors.Primary, fontWeight = FontWeight.Bold)
                        Text(
                            "Try to include main appliances like ceiling fan, AC, or TV.",
                            style = LensTypography.bodyMd,
                            color = LensColors.SlateCaption,
                            lineHeight = 16.sp
                        )
                    }
                    Row(verticalAlignment = Alignment.Top) {
                        Text("• ", color = LensColors.Primary, fontWeight = FontWeight.Bold)
                        Text(
                            "You can snap and add more photos if things are spread out.",
                            style = LensTypography.bodyMd,
                            color = LensColors.SlateCaption,
                            lineHeight = 16.sp
                        )
                    }
                    Row(verticalAlignment = Alignment.Top) {
                        Text("• ", color = LensColors.Primary, fontWeight = FontWeight.Bold)
                        Text(
                            "You will review and edit every detected item in the next step.",
                            style = LensTypography.bodyMd,
                            color = LensColors.SlateCaption,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Bottom Navigation: Skip Room
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TextButton(onClick = onSkipRoom) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Skip this room for now",
                        style = LensTypography.labelSm.copy(fontWeight = FontWeight.SemiBold),
                        color = LensColors.SlateCaption
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = LensColors.SlateCaption,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                "Room $activeIdx of $total • Next: ${if (activeIdx < total) "Next Room" else "Summary"}",
                style = LensTypography.labelCaps,
                color = LensColors.SlateCaption
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ProcessingContent(
    room: Room,
    message: String,
    onCancel: () -> Unit
) {
    BackHandler { onCancel() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = LensColors.SurfaceContainerLowest,
            border = BorderStroke(1.dp, LensColors.Outline)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(LensColors.PrimaryLive, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "NEURAL HARDWARE INFERENCE",
                        style = LensTypography.labelCaps,
                        color = LensColors.Primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Analyzing ${room.name} Telemetry",
                    style = LensTypography.headlineSm,
                    color = LensColors.SlateText,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Radial Dial Telemetry
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxSize(),
                        color = LensColors.Primary,
                        strokeWidth = 6.dp,
                        trackColor = LensColors.SurfaceContainer
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Inference", style = LensTypography.labelCaps.copy(fontSize = 9.sp), color = LensColors.SlateCaption)
                        Text("14ms", style = LensTypography.metricValMd, color = LensColors.SlateText)
                        Text("NPU Core", style = LensTypography.labelCaps.copy(fontSize = 8.sp), color = LensColors.Primary)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Execution Sequence Pipeline
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LensColors.SurfaceContainerLow, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PipelineStep("Spatial mesh bounding features anchored", isDone = true)
                    PipelineStep(message, isRunning = true)
                    PipelineStep("Duty cycle & wattage envelopes calibration", isQueued = true)
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = onCancel,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, LensColors.OutlineVariant)
                ) {
                    Text("Cancel Analysis", style = LensTypography.labelSm, color = LensColors.SlateCaption)
                }
            }
        }
    }
}

@Composable
private fun PipelineStep(
    text: String,
    isDone: Boolean = false,
    isRunning: Boolean = false,
    isQueued: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(
                    when {
                        isDone -> LensColors.PrimaryFixed
                        isRunning -> LensColors.Primary
                        else -> LensColors.SurfaceContainerHighest
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(Icons.Default.Check, contentDescription = null, tint = LensColors.Primary, modifier = Modifier.size(10.dp))
            } else if (isRunning) {
                CircularProgressIndicator(color = LensColors.OnPrimary, strokeWidth = 1.5.dp, modifier = Modifier.size(10.dp))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text,
            style = LensTypography.labelSm.copy(fontSize = 11.sp),
            color = if (isQueued) LensColors.SlateCaption else LensColors.SlateText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PhotoThumbnail(
    uri: android.net.Uri,
    index: Int,
    onRemove: () -> Unit
) {
    val context = LocalContext.current
    val bitmap = remember(uri) {
        try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val options = android.graphics.BitmapFactory.Options().apply {
                    inSampleSize = 4
                }
                android.graphics.BitmapFactory.decodeStream(stream, null, options)?.asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Appliance photo $index",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LensColors.SurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Photo,
                    contentDescription = null,
                    tint = LensColors.SlateCaption,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Index badge (bottom-left)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp),
            shape = RoundedCornerShape(4.dp),
            color = Color.Black.copy(alpha = 0.65f)
        ) {
            Text(
                "#$index",
                style = LensTypography.labelCaps.copy(fontSize = 9.sp),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
        }

        // Remove 'X' button (top-right)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove photo $index",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
