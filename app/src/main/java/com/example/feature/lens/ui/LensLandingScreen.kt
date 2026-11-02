package com.example.feature.lens.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.legal.LegalComplianceDialog
import com.example.core.legal.LegalSection
import com.example.feature.lens.ui.theme.LensColors
import com.example.feature.lens.ui.theme.LensTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LensLandingScreen(
    viewModel: LensViewModel,
    onNavigateBack: () -> Unit,
    onOpenCamera: () -> Unit,
    onNavigateToReview: () -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val rooms by viewModel.rooms.collectAsState()
    val selectedPhotos by viewModel.selectedRoomPhotos.collectAsState()

    var roomToRename by remember { mutableStateOf<Room?>(null) }
    var showAddRoomDialog by remember { mutableStateOf(false) }
    var currentCameraUri by remember { mutableStateOf<android.net.Uri?>(null) }

    fun createCameraUri(ext: String): android.net.Uri {
        val cameraDir = java.io.File(context.cacheDir, "camera").apply { mkdirs() }
        val tempFile = java.io.File(cameraDir, "lens_upload_${System.currentTimeMillis()}.$ext")
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
        currentCameraUri = uri
        return uri
    }

    val galleryPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (uris.isNotEmpty() && uiState is LensUiState.RoomOverview) {
                viewModel.processUrisAsync(context, uris, (uiState as LensUiState.RoomOverview).room)
            }
        }
    )

    val galleryVideoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null && uiState is LensUiState.RoomOverview) {
                viewModel.processUrisAsync(context, listOf(uri), (uiState as LensUiState.RoomOverview).room)
            }
        }
    )

    val cameraPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            val capturedUri = currentCameraUri
            if (success && capturedUri != null && uiState is LensUiState.RoomOverview) {
                val added = viewModel.addSelectedPhoto(capturedUri)
                if (!added && viewModel.selectedRoomPhotos.value.size >= LensViewModel.MAX_ROOM_PHOTOS) {
                    android.widget.Toast.makeText(
                        context,
                        "Maximum of 4 photos reached. Remove a photo to add another.",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    )

    val cameraVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo(),
        onResult = { success ->
            val capturedUri = currentCameraUri
            if (success && capturedUri != null && uiState is LensUiState.RoomOverview) {
                viewModel.processUrisAsync(context, listOf(capturedUri), (uiState as LensUiState.RoomOverview).room)
            }
        }
    )

    if (roomToRename != null) {
        val target = roomToRename!!
        RenameRoomDialog(
            currentName = target.name,
            title = "Rename Room",
            confirmButtonText = "Save",
            onDismiss = { roomToRename = null },
            onConfirm = { newName ->
                viewModel.renameRoom(target.id, newName)
                roomToRename = null
            }
        )
    }

    if (showAddRoomDialog) {
        RenameRoomDialog(
            currentName = "",
            title = "Add Room",
            confirmButtonText = "Add Room",
            onDismiss = { showAddRoomDialog = false },
            onConfirm = { newName ->
                viewModel.addRoom(newName)
                showAddRoomDialog = false
            }
        )
    }

    BackHandler(enabled = uiState !is LensUiState.Introduction && uiState !is LensUiState.Processing) {
        viewModel.popUiState()
    }

    LaunchedEffect(uiState) {
        if (uiState is LensUiState.CameraActive) {
            onOpenCamera()
        } else if (uiState is LensUiState.Review) {
            onNavigateToReview()
        } else if (uiState is LensUiState.WholeHouseAnalytics) {
            onNavigateToAnalytics()
        }
    }

    if (uiState is LensUiState.Error) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LensColors.Background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .padding(24.dp)
                    .background(LensColors.SurfaceContainerLowest, RoundedCornerShape(12.dp))
                    .padding(24.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = LensColors.Error,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Analysis Error",
                    style = LensTypography.headlineSm,
                    color = LensColors.SlateText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (uiState as LensUiState.Error).message,
                    style = LensTypography.bodyMd,
                    color = LensColors.SlateCaption,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { viewModel.dismissError() },
                    colors = ButtonDefaults.buttonColors(containerColor = LensColors.Primary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Try Again", color = LensColors.OnPrimary)
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            LensTopAppBar(
                title = when (uiState) {
                    is LensUiState.Introduction -> "SolarEase Lens"
                    is LensUiState.RoomSetup -> "Configure Rooms"
                    is LensUiState.RoomOverview -> (uiState as LensUiState.RoomOverview).room.name
                    is LensUiState.RoomCompleted -> "Room Certified"
                    is LensUiState.Processing -> "Inference"
                    else -> "SolarEase Lens"
                },
                subTitle = when (uiState) {
                    is LensUiState.Introduction -> "HOME AUDIT"
                    is LensUiState.RoomSetup -> "STEP 01 OF 04"
                    is LensUiState.RoomOverview -> "ROOM ${rooms.indexOfFirst { it.id == (uiState as LensUiState.RoomOverview).room.id } + 1} OF ${rooms.size}"
                    else -> null
                },
                onBackClicked = {
                    if (uiState is LensUiState.Introduction) {
                        onNavigateBack()
                    } else {
                        viewModel.popUiState()
                    }
                }
            )
        },
        containerColor = LensColors.Background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(LensColors.Background),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 680.dp)
                    .fillMaxSize()
            ) {
                when (uiState) {
                is LensUiState.Introduction -> {
                    IntroductionContent(
                        onGoRoomByRoom = { viewModel.startRoomSetup() }
                    )
                }
                is LensUiState.RoomSetup -> {
                    RoomSetupContent(
                        rooms = rooms,
                        onAddRoom = { showAddRoomDialog = true },
                        onAddPresetRoom = { name -> viewModel.addRoom(name) },
                        onRenameRoom = { room -> roomToRename = room },
                        onRemoveRoom = { viewModel.removeRoom(it) },
                        onMoveRoomUp = { index -> viewModel.reorderRooms(index, index - 1) },
                        onMoveRoomDown = { index -> viewModel.reorderRooms(index, index + 1) },
                        onProceed = { viewModel.proceedToScanning() }
                    )
                }
                is LensUiState.RoomOverview -> {
                    val room = (uiState as LensUiState.RoomOverview).room
                    val sortedRooms = rooms.sortedBy { it.order }
                    val activeIdx = sortedRooms.indexOfFirst { it.id == room.id } + 1
                    val total = sortedRooms.size

                    RoomOverviewContent(
                        room = room,
                        activeIdx = if (activeIdx > 0) activeIdx else 1,
                        total = total,
                        selectedPhotos = selectedPhotos,
                        onRenameRoom = { roomToRename = room },
                        onLiveScanClicked = {
                            viewModel.startCamera(CameraMode.LIVE_SCAN)
                        },
                        onUploadPhotosClicked = {
                            galleryPhotoPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        onUploadVideoClicked = {
                            galleryVideoPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.VideoOnly
                                )
                            )
                        },
                        onCapturePhotoClicked = {
                            if (selectedPhotos.size >= LensViewModel.MAX_ROOM_PHOTOS) {
                                android.widget.Toast.makeText(
                                    context,
                                    "Maximum of 4 photos reached. Remove a photo to add another.",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val uri = createCameraUri("jpg")
                                cameraPhotoLauncher.launch(uri)
                            }
                        },
                        onRecordVideoClicked = {
                            val uri = createCameraUri("mp4")
                            cameraVideoLauncher.launch(uri)
                        },
                        onRemovePhoto = { uri ->
                            viewModel.removeSelectedPhoto(uri)
                        },
                        onAnalyzePhotos = { photos ->
                            if (photos.isNotEmpty()) {
                                viewModel.processUrisAsync(context, photos, room)
                            }
                        },
                        onSkipRoom = {
                            viewModel.skipCurrentRoom()
                        }
                    )
                }
                is LensUiState.Processing -> {
                    val proc = uiState as LensUiState.Processing
                    ProcessingContent(
                        room = proc.room,
                        message = proc.message,
                        onCancel = { viewModel.cancelAnalysis() }
                    )
                }
                is LensUiState.RoomCompleted -> {
                    val completed = uiState as LensUiState.RoomCompleted
                    val sortedRooms = rooms.sortedBy { it.order }
                    val activeIdx = sortedRooms.indexOfFirst { it.id == completed.room.id } + 1

                    RoomCompletedContent(
                        room = completed.room,
                        nextRoom = completed.nextRoom,
                        activeIdx = if (activeIdx > 0) activeIdx else 1,
                        total = sortedRooms.size,
                        onContinue = { viewModel.advanceFromRoomCompleted() },
                        onEdit = { viewModel.returnToEditRoom(completed.room.id) }
                    )
                }
                else -> {}
                }
            }
        }
    }
}

// ─── Stitch Header Bar ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LensTopAppBar(
    title: String,
    subTitle: String?,
    onBackClicked: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = LensColors.Background.copy(alpha = 0.95f),
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, LensColors.Outline.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClicked, modifier = Modifier.size(48.dp)) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = LensColors.SlateText,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = title,
                        style = LensTypography.headlineSm,
                        color = LensColors.SlateText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subTitle != null) {
                        Text(
                            text = subTitle.uppercase(),
                            style = LensTypography.labelCaps,
                            color = LensColors.SlateCaption
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(LensColors.SurfaceContainerLow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = LensColors.SlateCaption,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(LensColors.Primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = LensColors.OnPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ─── Screen A: Solar Lens Welcome (Stitch Design) ───────────────────────────

@Composable
fun SolarLensWelcomeIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val baseWidth = 280f
        val baseHeight = 210f
        val scale = minOf(size.width / baseWidth, size.height / baseHeight)
        val dx = (size.width - baseWidth * scale) / 2f
        val dy = (size.height - baseHeight * scale) / 2f

        withTransform({
            translate(left = dx, top = dy)
            scale(scale, scale, pivot = Offset.Zero)
        }) {
            // Soft Ground & Environment
            drawOval(
                color = Color(0xFFEFF4FF),
                topLeft = Offset(20f, 178f),
                size = Size(240f, 24f)
            )

            // Soft Left Bush
            val bush1 = Path().apply {
                moveTo(40f, 188f)
                cubicTo(50f, 176f, 70f, 176f, 80f, 188f)
                close()
            }
            drawPath(bush1, color = Color(0xFF7BD8B1).copy(alpha = 0.4f))

            // Soft Right Bush
            val bush2 = Path().apply {
                moveTo(200f, 188f)
                cubicTo(210f, 172f, 230f, 172f, 240f, 188f)
                close()
            }
            drawPath(bush2, color = Color(0xFF7BD8B1).copy(alpha = 0.5f))

            // Subtle Sun Rays in Background
            drawCircle(color = Color(0xFFE5EEFF), radius = 28f, center = Offset(215f, 55f))
            drawCircle(color = Color(0xFF9FFDD3).copy(alpha = 0.6f), radius = 18f, center = Offset(215f, 55f))

            val sunDashEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 6f), 0f)
            drawLine(
                color = Color(0xFF047857).copy(alpha = 0.4f),
                start = Offset(215f, 30f),
                end = Offset(215f, 22f),
                strokeWidth = 2f,
                cap = StrokeCap.Round,
                pathEffect = sunDashEffect
            )
            drawLine(
                color = Color(0xFF047857).copy(alpha = 0.4f),
                start = Offset(215f, 88f),
                end = Offset(215f, 80f),
                strokeWidth = 2f,
                cap = StrokeCap.Round,
                pathEffect = sunDashEffect
            )
            drawLine(
                color = Color(0xFF047857).copy(alpha = 0.4f),
                start = Offset(190f, 55f),
                end = Offset(182f, 55f),
                strokeWidth = 2f,
                cap = StrokeCap.Round,
                pathEffect = sunDashEffect
            )
            drawLine(
                color = Color(0xFF047857).copy(alpha = 0.4f),
                start = Offset(248f, 55f),
                end = Offset(240f, 55f),
                strokeWidth = 2f,
                cap = StrokeCap.Round,
                pathEffect = sunDashEffect
            )

            // Main House Structure
            drawRoundRect(
                color = Color(0xFFF8F9FF),
                topLeft = Offset(75f, 96f),
                size = Size(130f, 92f),
                cornerRadius = CornerRadius(8f, 8f)
            )

            // Roof with Overhang
            val roof = Path().apply {
                moveTo(60f, 98f)
                lineTo(140f, 38f)
                lineTo(220f, 98f)
                close()
            }
            drawPath(roof, color = Color(0xFF047857))

            // Solar Panels on Roof
            val panelLeft = Path().apply {
                moveTo(100f, 76f)
                lineTo(136f, 49f)
                lineTo(136f, 88f)
                lineTo(100f, 88f)
                close()
            }
            drawPath(panelLeft, color = Color(0xFF005683).copy(alpha = 0.95f))

            val panelRight = Path().apply {
                moveTo(144f, 49f)
                lineTo(180f, 76f)
                lineTo(180f, 88f)
                lineTo(144f, 88f)
                close()
            }
            drawPath(panelRight, color = Color(0xFF005683).copy(alpha = 0.95f))

            // Solar Grid Lines
            val gridLineColor = Color(0xFF9FFDD3).copy(alpha = 0.7f)
            drawLine(gridLineColor, Offset(118f, 62f), Offset(118f, 88f), strokeWidth = 1f)
            drawLine(gridLineColor, Offset(162f, 62f), Offset(162f, 88f), strokeWidth = 1f)
            drawLine(gridLineColor, Offset(104f, 80f), Offset(134f, 80f), strokeWidth = 1f)
            drawLine(gridLineColor, Offset(146f, 80f), Offset(176f, 80f), strokeWidth = 1f)

            // Front Door
            drawRoundRect(
                color = Color(0xFF047857),
                topLeft = Offset(125f, 142f),
                size = Size(30f, 46f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // Door Handle
            drawCircle(Color(0xFF9FFDD3), radius = 2.5f, center = Offset(149f, 165f))

            // Left Room: Cozy Kitchenette (Refrigerator)
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(85f, 132f),
                size = Size(28f, 42f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            drawLine(Color(0xFFE5EEFF), Offset(85f, 146f), Offset(113f, 146f), strokeWidth = 1.5f)
            drawRoundRect(Color(0xFF047857), Offset(88f, 137f), Size(2f, 6f), CornerRadius(1f, 1f))
            drawRoundRect(Color(0xFF047857), Offset(88f, 152f), Size(2f, 10f), CornerRadius(1f, 1f))
            // Magnet Note
            drawRoundRect(Color(0xFF9FFDD3), Offset(98f, 136f), Size(6f, 5f), CornerRadius(1f, 1f))

            // Right Room: Warm Living Area (Window & Ceiling Fan)
            drawRoundRect(Color.White, Offset(167f, 122f), Size(26f, 32f), CornerRadius(4f, 4f))
            drawRoundRect(Color(0xFFEFF4FF), Offset(170f, 125f), Size(20f, 26f), CornerRadius(2f, 2f))
            // Ceiling fan
            drawLine(Color(0xFF565E74), Offset(180f, 125f), Offset(180f, 131f), strokeWidth = 1.5f)
            drawOval(Color(0xFF565E74), Offset(172f, 130f), Size(16f, 4f))
            drawCircle(Color(0xFF047857), radius = 2f, center = Offset(180f, 132f))
            // Warm Ambient Lamp Glow
            drawCircle(Color(0xFF9FFDD3).copy(alpha = 0.6f), radius = 5f, center = Offset(180f, 144f))
            drawCircle(Color(0xFF047857), radius = 2f, center = Offset(180f, 144f))

            // Whimsical Clean Energy Sparkle
            val sparkle = Path().apply {
                moveTo(68f, 62f)
                lineTo(70f, 54f)
                lineTo(72f, 62f)
                lineTo(80f, 64f)
                lineTo(72f, 66f)
                lineTo(70f, 74f)
                lineTo(68f, 66f)
                lineTo(60f, 64f)
                close()
            }
            drawPath(sparkle, color = Color(0xFF047857).copy(alpha = 0.35f))
        }
    }
}

@Composable
fun IntroductionContent(
    onGoRoomByRoom: () -> Unit
) {
    var activeLegalSection by remember { mutableStateOf<LegalSection?>(null) }

    activeLegalSection?.let { section ->
        LegalComplianceDialog(
            initialSection = section,
            onDismiss = { activeLegalSection = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcoming Card with Delightful Native Vector Illustration
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = LensColors.SurfaceContainerLowest,
            border = BorderStroke(1.dp, LensColors.Outline.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Vector Illustration: House with rooftop solar panels & cozy cutaways
                SolarLensWelcomeIllustration(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Let's understand your home's electricity use",
                    style = LensTypography.headlineLg,
                    color = LensColors.SlateText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Check which appliances use electricity and explore the right solar setup for your home.",
                    style = LensTypography.bodyMd,
                    color = LensColors.SlateCaption,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = LensColors.SurfaceContainerLow
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = LensColors.Primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            "Usually takes ~3 minutes",
                            style = LensTypography.labelSm.copy(fontWeight = FontWeight.SemiBold),
                            color = LensColors.Primary
                        )
                    }
                }
            }
        }

        // Section Title: 3 Simple Steps
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "3 Simple Steps",
                style = LensTypography.headlineSm,
                color = LensColors.SlateText
            )
            Text(
                "QUICK & EASY",
                style = LensTypography.labelCaps,
                color = LensColors.Primary,
                fontWeight = FontWeight.SemiBold
            )
        }

        // 3 Simple Friendly Step Cards
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CitizenStepCard(
                stepNumber = "1",
                icon = Icons.Default.MeetingRoom,
                title = "Set up your rooms",
                description = "Add the rooms you want to check, like living room, kitchen, or bedrooms."
            )
            CitizenStepCard(
                stepNumber = "2",
                icon = Icons.Default.PhotoCamera,
                title = "Add photos & appliances",
                description = "Snap a photo or add appliances quickly from our everyday list."
            )
            CitizenStepCard(
                stepNumber = "3",
                icon = Icons.Default.SolarPower,
                title = "Get your energy report",
                description = "See estimated electricity use and solar sizing recommended just for you."
            )
        }

        // Helpful Neighbor Tip Callout
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = LensColors.SurfaceContainerLow,
            border = BorderStroke(1.dp, LensColors.Outline.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = LensColors.PrimaryFixed,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = LensColors.Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "No bill needed right now! You can freely adjust numbers or check back anytime.",
                        style = LensTypography.bodyMd,
                        color = LensColors.SlateText,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Cloud AI & On-Device Processing Disclosure (DPDP Act 2023 § 5)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = LensColors.SurfaceContainerLowest,
            border = BorderStroke(1.dp, LensColors.Outline.copy(alpha = 0.35f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = LensColors.Primary,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(top = 2.dp)
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "AI Processing & Privacy Notice",
                        style = LensTypography.labelSm.copy(fontWeight = FontWeight.Bold),
                        color = LensColors.SlateText
                    )
                    Text(
                        text = "Live camera scanning runs on-device via Google ML Kit. Uploaded room photos/videos are transiently analyzed by Google Gemini AI (GCP Mumbai asia-south1) to detect appliances and immediately purged after inference.",
                        style = LensTypography.labelSm,
                        color = LensColors.SlateCaption,
                        lineHeight = 16.sp
                    )
                    Text(
                        text = "View AI & Subprocessor Disclosures",
                        style = LensTypography.labelSm.copy(fontWeight = FontWeight.SemiBold),
                        color = LensColors.Primary,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { activeLegalSection = LegalSection.SUBPROCESSORS_AI }
                    )
                }
            }
        }

        // Bottom CTA Action Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onGoRoomByRoom,
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
                        "Start Home Audit",
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

            Text(
                text = "Free for homeowners • Takes ~3 mins",
                style = LensTypography.labelSm,
                color = LensColors.SlateCaption
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
