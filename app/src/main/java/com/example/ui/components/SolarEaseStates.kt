package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun SolarEaseEmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    illustration: ImageVector? = Icons.Outlined.FolderOpen,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (illustration != null) {
            Icon(
                imageVector = illustration,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = StitchSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = StitchOnSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = StitchOnSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            SolarEaseButton(
                text = actionText,
                onClick = onAction,
                variant = SolarEaseButtonVariant.PRIMARY
            )
        }
    }
}

@Composable
fun SolarEaseErrorState(
    message: String,
    modifier: Modifier = Modifier,
    errorCode: String? = null,
    icon: ImageVector = Icons.Outlined.ErrorOutline,
    retryText: String = "Try Again",
    onRetry: (() -> Unit)? = null
) {
    val displayMessage = if (errorCode != null) "$message ($errorCode)" else message

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = SolarEaseSemanticColors.DangerText
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = displayMessage,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = StitchOnSurface,
            textAlign = TextAlign.Center
        )

        if (onRetry != null) {
            Spacer(modifier = Modifier.height(24.dp))
            SolarEaseButton(
                text = retryText,
                onClick = onRetry,
                variant = SolarEaseButtonVariant.DANGER
            )
        }
    }
}
