package com.example.feature.profile.ui.sheets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.session.User
import com.example.feature.ebservice.util.ConsumerNumberParser
import com.example.feature.ebservice.util.formatConsumerNumber
import com.example.feature.ebservice.util.formatIndianMobile
import com.example.feature.home.ui.modules.TnebBillCache
import com.example.ui.theme.SolarEaseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionDetailsSheet(
    user: User,
    onDismiss: () -> Unit,
    onSaveElectricity: (consumerNumber: String, tnebMobile: String?, onResult: (Boolean, String?) -> Unit) -> Unit
) {
    ElectricityDetailsSheet(
        user = user,
        onDismiss = onDismiss,
        onSave = onSaveElectricity
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectricityDetailsSheet(
    user: User,
    onDismiss: () -> Unit,
    onSave: (consumerNumber: String, tnebMobile: String?, onResult: (Boolean, String?) -> Unit) -> Unit
) {
    val colors = SolarEaseTheme.colors
    val hasSavedData = !user.ebServiceNumber.isNullOrBlank()
    var isEditing by remember(user.ebServiceNumber) { mutableStateOf(!hasSavedData) }

    val initialConsumer = user.ebServiceNumber?.ifBlank { null }
        ?: TnebBillCache.billData.value?.consumerNumber?.ifBlank { null }
        ?: ""
    val initialMobile = user.tnebRegisteredMobile ?: ""

    var consumerNumberInput by remember(user.ebServiceNumber, isEditing) {
        mutableStateOf(initialConsumer)
    }
    var tnebMobileInput by remember(user.tnebRegisteredMobile, isEditing) {
        mutableStateOf(initialMobile)
    }

    var consumerError by remember { mutableStateOf<String?>(null) }
    var mobileError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }
    var parsedRegionName by remember { mutableStateOf<String?>(null) }

    // Live validation for consumer number
    LaunchedEffect(consumerNumberInput) {
        val clean = consumerNumberInput.filter { it.isDigit() }
        if (clean.length in 10..12) {
            val res = ConsumerNumberParser.parse(clean)
            if (res.isSuccess) {
                parsedRegionName = res.getOrNull()?.regionName
                consumerError = null
            } else {
                parsedRegionName = null
                consumerError = res.exceptionOrNull()?.message
            }
        } else {
            parsedRegionName = null
            if (clean.isNotEmpty() && clean.length < 10) {
                consumerError = "${clean.length}/10-12 digits entered"
            } else {
                consumerError = null
            }
        }
    }

    // Live validation for TNEB mobile
    LaunchedEffect(tnebMobileInput) {
        val clean = tnebMobileInput.filter { it.isDigit() }
        if (clean.isNotEmpty() && clean.length == 10 && clean[0] !in '6'..'9') {
            mobileError = "Indian mobile number must start with 6, 7, 8, or 9"
        } else {
            mobileError = null
        }
    }

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
                        text = if (isEditing && hasSavedData) "Edit Electricity Details" else "Electricity Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                    Text(
                        text = "TANGEDCO / TNEB Consumer Connection",
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

            // Connection Details Card (Display Only Info)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceVariant,
                border = BorderStroke(1.dp, colors.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Electricity Board", fontSize = 12.sp, color = colors.onSurfaceVariant)
                        Text(
                            "TANGEDCO (Tamil Nadu)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onSurface
                        )
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = colors.outlineVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tariff Category", fontSize = 12.sp, color = colors.onSurfaceVariant)
                        Text(
                            "LT-1A Domestic",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.primary
                        )
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = colors.outlineVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Connection Status", fontSize = 12.sp, color = colors.onSurfaceVariant)
                        Text(
                            "Verified Consumer • TNEB LT-1A",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                    }
                    val currentRegionName = parsedRegionName
                        ?: user.ebServiceNumber?.let { ConsumerNumberParser.parse(it).getOrNull()?.regionName }
                    if (currentRegionName != null) {
                        HorizontalDivider(thickness = 0.5.dp, color = colors.outlineVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Region Name", fontSize = 12.sp, color = colors.onSurfaceVariant)
                            Text(
                                currentRegionName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.tertiary
                            )
                        }
                    }
                }
            }

            if (!isEditing && hasSavedData) {
                // ── 2. SAVED STATE UI ──
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = colors.surfaceVariant,
                    border = BorderStroke(1.dp, colors.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Consumer Number Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Consumer Number",
                                    fontSize = 12.sp,
                                    color = colors.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = formatConsumerNumber(user.ebServiceNumber ?: ""),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onSurface
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8F5E9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Verified Consumer Number",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        HorizontalDivider(thickness = 0.5.dp, color = colors.outlineVariant)

                        // TNEB Registered Mobile Number Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "TNEB Registered Mobile Number",
                                    fontSize = 12.sp,
                                    color = colors.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                val mobileText = if (!user.tnebRegisteredMobile.isNullOrBlank()) {
                                    formatIndianMobile(user.tnebRegisteredMobile!!)
                                } else {
                                    "Not registered • Tap Edit to add"
                                }
                                Text(
                                    text = mobileText,
                                    fontSize = 14.5.sp,
                                    fontWeight = if (!user.tnebRegisteredMobile.isNullOrBlank()) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (!user.tnebRegisteredMobile.isNullOrBlank()) colors.onSurface else colors.onSurfaceVariant
                                )
                            }
                            if (!user.tnebRegisteredMobile.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE8F5E9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Verified TNEB Mobile Number",
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Edit Button
                Button(
                    onClick = {
                        consumerNumberInput = user.ebServiceNumber ?: ""
                        tnebMobileInput = user.tnebRegisteredMobile ?: ""
                        consumerError = null
                        mobileError = null
                        generalError = null
                        isEditing = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            } else {
                // ── 4. ELECTRICITY DETAILS FORM (EDIT / ENTRY MODE) ──
                OutlinedTextField(
                    value = consumerNumberInput,
                    onValueChange = { input ->
                        consumerNumberInput = input.filter { it.isDigit() }.take(12)
                    },
                    label = { Text("Consumer Number") },
                    supportingText = {
                        if (consumerError != null) {
                            Text(consumerError ?: "", color = colors.error)
                        } else if (parsedRegionName != null) {
                            Text("Region: $parsedRegionName", color = colors.primary)
                        } else {
                            Text("10 to 12 digits (e.g. 09-440-233-135)")
                        }
                    },
                    isError = consumerError != null && consumerNumberInput.length in 10..12,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                OutlinedTextField(
                    value = tnebMobileInput,
                    onValueChange = { input ->
                        tnebMobileInput = input.filter { it.isDigit() }.take(10)
                    },
                    label = { Text("TNEB Registered Mobile Number") },
                    supportingText = {
                        if (mobileError != null) {
                            Text(mobileError ?: "", color = colors.error)
                        } else {
                            Text("Mobile registered with TNEB. Independent of SolarEase login.")
                        }
                    },
                    isError = mobileError != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )

                if (generalError != null) {
                    Text(
                        text = generalError ?: "",
                        color = colors.error,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (hasSavedData) {
                                isEditing = false
                                generalError = null
                                consumerError = null
                                mobileError = null
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, colors.outline)
                    ) {
                        Text("Cancel", color = colors.onSurfaceVariant, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            val cleanConsumer = consumerNumberInput.filter { it.isDigit() }
                            if (cleanConsumer.length !in 10..12) {
                                consumerError = "Consumer number must be 10 to 12 digits."
                                return@Button
                            }
                            val parseResult = ConsumerNumberParser.parse(cleanConsumer)
                            if (parseResult.isFailure) {
                                consumerError = parseResult.exceptionOrNull()?.message ?: "Invalid Consumer Number."
                                return@Button
                            }

                            val cleanMobile = tnebMobileInput.filter { it.isDigit() }.let { raw ->
                                when {
                                    raw.length == 10 -> raw
                                    raw.length == 11 && raw.startsWith("0") -> raw.substring(1)
                                    raw.length == 12 && raw.startsWith("91") -> raw.substring(2)
                                    else -> raw
                                }
                            }
                            if (cleanMobile.isNotEmpty()) {
                                if (cleanMobile.length != 10 || cleanMobile[0] !in '6'..'9') {
                                    mobileError = "Enter a valid 10-digit Indian mobile number."
                                    return@Button
                                }
                            }

                            generalError = null
                            onSave(cleanConsumer, cleanMobile.ifBlank { null }) { success, err ->
                                if (success) {
                                    isEditing = false
                                    generalError = null
                                    consumerError = null
                                    mobileError = null
                                } else {
                                    generalError = err ?: "Validation failed"
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = colors.onPrimary
                        )
                    ) {
                        Text("Save", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
