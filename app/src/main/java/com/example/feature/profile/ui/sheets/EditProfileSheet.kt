package com.example.feature.profile.ui.sheets

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.session.User
import com.example.ui.theme.SolarEaseTheme
import com.google.android.gms.location.LocationServices
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileSheet(
    user: User,
    onDismiss: () -> Unit,
    onSaveProfile: (name: String, username: String, mobile: String, address: String, lat: Double?, lng: Double?, email: String?) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SolarEaseTheme.colors.surface,
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
                        text = "Edit Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SolarEaseTheme.colors.onSurface
                    )
                    Text(
                        text = "Update personal information and installation address",
                        fontSize = 12.sp,
                        color = SolarEaseTheme.colors.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = SolarEaseTheme.colors.onSurfaceVariant
                    )
                }
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SolarEaseTheme.colors.surface,
                contentColor = SolarEaseTheme.colors.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Personal Info", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("House Address", fontWeight = FontWeight.SemiBold) }
                )
            }

            if (selectedTab == 0) {
                PersonalInformationContent(
                    user = user,
                    onDismiss = onDismiss,
                    onSave = { name, username, email, mobile ->
                        onSaveProfile(
                            name,
                            username,
                            mobile ?: (user.mobileNumber ?: ""),
                            user.houseAddress ?: "",
                            user.latitude,
                            user.longitude,
                            email
                        )
                        onDismiss()
                    }
                )
            } else {
                HouseAddressContent(
                    user = user,
                    onDismiss = onDismiss,
                    onSaveAddress = { address, lat, lng ->
                        onSaveProfile(
                            user.name ?: "",
                            user.username,
                            user.mobileNumber ?: "",
                            address,
                            lat,
                            lng,
                            user.email
                        )
                        onDismiss()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInformationSheet(
    user: User,
    onDismiss: () -> Unit,
    onSave: (name: String, username: String, email: String?, mobile: String?) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SolarEaseTheme.colors.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Personal Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SolarEaseTheme.colors.onSurface
                    )
                    Text(
                        text = "View and edit your profile credentials",
                        fontSize = 12.sp,
                        color = SolarEaseTheme.colors.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = SolarEaseTheme.colors.onSurfaceVariant
                    )
                }
            }

            PersonalInformationContent(
                user = user,
                onDismiss = onDismiss,
                onSave = onSave
            )
        }
    }
}

@Composable
private fun PersonalInformationContent(
    user: User,
    onDismiss: () -> Unit,
    onSave: (name: String, username: String, email: String?, mobile: String?) -> Unit
) {
    val colors = SolarEaseTheme.colors
    var name by remember { mutableStateOf(user.name ?: "") }
    var username by remember { mutableStateOf(user.username) }
    var email by remember { mutableStateOf(user.email ?: "") }
    var mobileNumber by remember { mutableStateOf(user.mobileNumber ?: "") }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }

    val usernameRegex = "^(?=.*[a-zA-Z])[a-zA-Z0-9_]{3,30}$".toRegex()

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                if (it.isNotBlank()) nameError = null
            },
            label = { Text("Full Name") },
            isError = nameError != null,
            supportingText = { nameError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                if (usernameRegex.matches(it)) usernameError = null
            },
            label = { Text("Username") },
            isError = usernameError != null,
            supportingText = {
                Text(usernameError ?: "3-30 letters, numbers, or underscores")
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email ID") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        OutlinedTextField(
            value = mobileNumber,
            onValueChange = { mobileNumber = it },
            label = { Text("Mobile Number") },
            supportingText = {
                Text("Linked to authentication. Re-verification required to modify active sign-in.")
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
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
                    if (name.isBlank()) {
                        nameError = "Name cannot be empty"
                        return@Button
                    }
                    if (!usernameRegex.matches(username)) {
                        usernameError = "Invalid username format"
                        return@Button
                    }
                    onSave(name, username, email.ifBlank { null }, mobileNumber.ifBlank { null })
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseAddressSheet(
    user: User,
    onDismiss: () -> Unit,
    onSaveAddress: (address: String, lat: Double?, lng: Double?) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SolarEaseTheme.colors.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "House Address",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SolarEaseTheme.colors.onSurface
                    )
                    Text(
                        text = "Residential address for solar irradiance and grid matching",
                        fontSize = 12.sp,
                        color = SolarEaseTheme.colors.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = SolarEaseTheme.colors.onSurfaceVariant
                    )
                }
            }

            HouseAddressContent(
                user = user,
                onDismiss = onDismiss,
                onSaveAddress = onSaveAddress
            )
        }
    }
}

@Composable
private fun HouseAddressContent(
    user: User,
    onDismiss: () -> Unit,
    onSaveAddress: (address: String, lat: Double?, lng: Double?) -> Unit
) {
    val context = LocalContext.current
    val colors = SolarEaseTheme.colors
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var streetAddress by remember { mutableStateOf(user.houseAddress ?: "") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("Tamil Nadu") }
    var pincode by remember { mutableStateOf("") }

    var detectedLat by remember { mutableStateOf(user.latitude) }
    var detectedLng by remember { mutableStateOf(user.longitude) }
    var isLocating by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf<String?>(null) }

    fun fetchCurrentLocation() {
        isLocating = true
        locationError = null
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                isLocating = false
                if (loc != null) {
                    detectedLat = loc.latitude
                    detectedLng = loc.longitude
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val addr = addresses[0]
                            city = addr.locality ?: ""
                            pincode = addr.postalCode ?: ""
                            state = addr.adminArea ?: "Tamil Nadu"
                            streetAddress = listOfNotNull(addr.subThoroughfare, addr.thoroughfare, addr.locality)
                                .filter { it.isNotBlank() }
                                .joinToString(", ")
                        }
                    } catch (_: Exception) {}
                } else {
                    locationError = "Unable to get GPS location. Please enter manually."
                }
            }.addOnFailureListener {
                isLocating = false
                locationError = it.message ?: "Location request failed"
            }
        } catch (_: SecurityException) {
            isLocating = false
            locationError = "Location permission denied."
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            fetchCurrentLocation()
        } else {
            locationError = "Location permission is required to detect coordinates."
        }
    }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedButton(
            onClick = {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isLocating) "Detecting Location..." else "Detect via GPS Location", fontSize = 13.sp)
        }

        if (locationError != null) {
            Text(locationError!!, color = colors.error, fontSize = 11.sp)
        }

        OutlinedTextField(
            value = streetAddress,
            onValueChange = { streetAddress = it },
            label = { Text("Street Address / Flat / Door No.") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = false,
            maxLines = 3
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City / Town") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = pincode,
                onValueChange = { pincode = it },
                label = { Text("Pincode") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }

        OutlinedTextField(
            value = state,
            onValueChange = { state = it },
            label = { Text("State") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel", fontSize = 13.sp)
            }

            Button(
                onClick = {
                    val fullAddress = listOf(streetAddress, city, pincode, state)
                        .filter { it.isNotBlank() }
                        .joinToString(", ")
                    onSaveAddress(if (fullAddress.isBlank()) streetAddress else fullAddress, detectedLat, detectedLng)
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
