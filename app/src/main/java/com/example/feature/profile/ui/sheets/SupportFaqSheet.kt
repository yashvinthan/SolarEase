package com.example.feature.profile.ui.sheets

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.legal.LegalComplianceConstants
import com.example.ui.theme.SolarEaseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportFaqSheet(onDismiss: () -> Unit) {
    HelpSupportSheet(onDismiss = onDismiss)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportSheet(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val colors = SolarEaseTheme.colors
    var expandedFaqIndex by remember { mutableIntStateOf(-1) }

    val faqs = listOf(
        "How are Energy Credits calculated?" to "Energy credits are generated from verified bi-monthly TNEB bill reductions compared to your baseline consumption, plus active daily check-in streaks on the 9 Energy-Saving Mantras.",
        "How do I find my 12-digit TNEB consumer number?" to "Your 12-digit number is printed on your physical TNEB receipt or card. Format is: 2-digit Region Code, 3-digit Section, 3-digit Distribution, 4-digit Service Number (e.g. 09-440-233-135).",
        "How often should I record meter readings?" to "We recommend taking a quick reading bi-weekly or at the start of your billing cycle so that predictive alerts and tariff warnings are 100% accurate.",
        "How does Solar Lens scan my meter?" to "Solar Lens utilizes on-device computer vision and optical character recognition (OCR) to detect LCD or analog meter dial digits in real-time."
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
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
                        text = "Help & Support",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                    Text(
                        text = "Frequently asked questions and advisor assistance",
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

            Text(
                text = "FREQUENTLY ASKED QUESTIONS",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )

            // FAQs
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceVariant,
                border = BorderStroke(1.dp, colors.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    faqs.forEachIndexed { index, (q, a) ->
                        val isExpanded = expandedFaqIndex == index
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedFaqIndex = if (isExpanded) -1 else index }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = q,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = colors.onSurfaceVariant
                                )
                            }
                            AnimatedVisibility(visible = isExpanded) {
                                Text(
                                    text = a,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = colors.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }
                        if (index < faqs.size - 1) {
                            HorizontalDivider(thickness = 0.5.dp, color = colors.outlineVariant)
                        }
                    }
                }
            }

            // Quick Help Topics
            Text(
                text = "SUPPORT TOPICS",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SupportTopicChip(
                    modifier = Modifier.weight(1f),
                    title = "Bill Status Help",
                    onClick = { expandedFaqIndex = 1 }
                )
                SupportTopicChip(
                    modifier = Modifier.weight(1f),
                    title = "Meter Reading Help",
                    onClick = { expandedFaqIndex = 2 }
                )
                SupportTopicChip(
                    modifier = Modifier.weight(1f),
                    title = "Solar Lens Help",
                    onClick = { expandedFaqIndex = 3 }
                )
            }

            // Direct Contact Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = colors.secondaryContainer.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, colors.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Contact Energy Advisor",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.secondary
                    )
                    Text(
                        text = "Helpline: ${LegalComplianceConstants.SUPPORT_PHONE} • Email: ${LegalComplianceConstants.SUPPORT_EMAIL}",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:${LegalComplianceConstants.SUPPORT_EMAIL}?subject=SolarEase%20Support%20Request")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.secondary)
                    ) {
                        Text("Send Support Email", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun SupportTopicChip(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit
) {
    val colors = SolarEaseTheme.colors
    Surface(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        shape = RoundedCornerShape(8.dp),
        color = colors.surfaceVariant,
        border = BorderStroke(1.dp, colors.outlineVariant)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = colors.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}
