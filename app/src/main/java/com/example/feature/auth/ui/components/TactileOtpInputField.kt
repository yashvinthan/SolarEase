package com.example.feature.auth.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SolarEaseTheme

/**
 * Commercial tactile 6-box segmented OTP input field.
 * Each cell is >= 48dp wide and >= 48dp tall (satisfying WCAG 2.2 AA target size),
 * with elevation feedback, active focus outline, and automatic digit progression.
 */
@Composable
fun TactileOtpInputField(
    otp: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    digitCount: Int = 6,
    isError: Boolean = false,
    enabled: Boolean = true
) {
    val colors = SolarEaseTheme.colors
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    // Internal text field value synced with incoming otp string
    var textFieldValue by remember(otp) {
        mutableStateOf(TextFieldValue(text = otp, selection = androidx.compose.ui.text.TextRange(otp.length)))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusRequester.requestFocus()
            },
        contentAlignment = Alignment.Center
    ) {
        // Hidden BasicTextField capturing keyboard, backspace, and paste events
        BasicTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                val digitsOnly = newValue.text.filter { it.isDigit() }.take(digitCount)
                textFieldValue = newValue.copy(text = digitsOnly, selection = androidx.compose.ui.text.TextRange(digitsOnly.length))
                onOtpChange(digitsOnly)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = enabled,
            cursorBrush = SolidColor(androidx.compose.ui.graphics.Color.Transparent),
            modifier = Modifier
                .size(1.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { isFocused = it.isFocused }
        )

        // Rendered 6 tactile cells
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (index in 0 until digitCount) {
                val digit = otp.getOrNull(index)?.toString() ?: ""
                val isCurrentCell = isFocused && (index == otp.length || (index == digitCount - 1 && otp.length == digitCount))
                val isFilled = digit.isNotEmpty()

                val borderColor = when {
                    isError -> colors.error
                    isCurrentCell -> colors.primary
                    isFilled -> colors.primary.copy(alpha = 0.7f)
                    else -> colors.outlineVariant
                }

                val borderWidth = when {
                    isCurrentCell || isError -> 2.dp
                    isFilled -> 1.5.dp
                    else -> 1.dp
                }

                val elevation = when {
                    isCurrentCell -> 3.dp
                    isFilled -> 1.dp
                    else -> 0.dp
                }

                Surface(
                    modifier = Modifier
                        .widthIn(min = 48.dp)
                        .heightIn(min = 56.dp)
                        .weight(1f, fill = false)
                        .padding(horizontal = 3.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCurrentCell) colors.primaryContainer.copy(alpha = 0.2f) else colors.surface,
                    border = BorderStroke(borderWidth, borderColor),
                    shadowElevation = elevation
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = digit,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isError -> colors.error
                                isFilled -> colors.onSurface
                                else -> colors.onSurfaceVariant
                            },
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
