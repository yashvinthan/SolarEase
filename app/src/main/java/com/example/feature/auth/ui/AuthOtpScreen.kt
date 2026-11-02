package com.example.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.feature.auth.ui.components.TactileOtpInputField
import com.example.ui.components.SolarEaseStepProgressBar
import kotlinx.coroutines.delay

@Composable
fun AuthOtpRoute(
    fullName: String,
    phoneNumber: String,
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var otp by rememberSaveable { mutableStateOf("") }
    var cooldownSeconds by rememberSaveable { mutableIntStateOf(30) }
    val context = LocalContext.current

    LaunchedEffect(cooldownSeconds) {
        if (cooldownSeconds > 0) {
            delay(1000L)
            cooldownSeconds -= 1
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            viewModel.resetState()
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            SolarEaseStepProgressBar(
                currentStep = 2,
                totalSteps = 11,
                stepTitle = "OTP Verification",
                category = "Authentication",
                onBack = onNavigateBack
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LockOpen,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Verify OTP",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Sent to $phoneNumber",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            TactileOtpInputField(
                otp = otp,
                onOtpChange = { otp = it },
                digitCount = 6,
                isError = uiState is AuthUiState.Error,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState is AuthUiState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (uiState as AuthUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { viewModel.verifyOtp(otp, fullName, phoneNumber) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                enabled = uiState !is AuthUiState.Loading && otp.isNotBlank()
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Verify", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    val activity = context as? android.app.Activity
                    if (activity != null) {
                        viewModel.sendOtp(phoneNumber, activity)
                    }
                    cooldownSeconds = 30
                },
                enabled = cooldownSeconds == 0 && uiState !is AuthUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text(
                    text = if (cooldownSeconds > 0) {
                        "Resend code in 00:${cooldownSeconds.toString().padStart(2, '0')}"
                    } else {
                        "Resend Code"
                    },
                    style = MaterialTheme.typography.titleSmall
                )
            }
            
            TextButton(
                onClick = onNavigateBack, 
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .padding(top = 4.dp, bottom = 8.dp)
            ) {
                Text("Change Phone Number", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}
