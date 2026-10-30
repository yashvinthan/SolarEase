package com.example.feature.lens.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Precision Telemetry Energy System Design Tokens
 * Replicating the exact color palette, typography, and shape specs from the Stitch UX Flow.
 */
val GeistFontFamily: FontFamily = FontFamily.Default
val HankenGroteskFontFamily: FontFamily = FontFamily.Default

object LensColors {
    // Primary Emerald Tier
    val Primary = Color(0xFF245B3A)
    val PrimaryHover = Color(0xFF2E7048)
    val PrimaryLive = Color(0xFF3D8A5A)
    val PrimaryFixed = Color(0xFFDDEFD9)
    val PrimaryFixedDim = Color(0xFFC9E1C5)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnPrimaryFixed = Color(0xFF123B27)
    val PrimaryContainer = Color(0xFF245B3A)
    val OnPrimaryContainer = Color(0xFFDDEFD9)

    // Secondary & Industrial Slate Tier (WCAG 2.1 AA 4.5:1+ compliant on #F8F9FF)
    val SlateDark = Color(0xFF0F172A)
    val SlateText = Color(0xFF17201B)
    val SlateMuted = Color(0xFF4F5A52)
    val SlateVariant = Color(0xFF4F5A52)
    val SlateCaption = Color(0xFF4F5A52)

    // Standard Stitch Color Aliases
    val OnSurface = SlateText
    val OnSurfaceVariant = SlateVariant
    val Secondary = SlateMuted
    val SecondaryFixed = Color(0xFFE7F2F3)
    val SecondaryFixedDim = Color(0xFFC6E0E2)
    val SecondaryContainer = Color(0xFFE7F2F3)

    // Tertiary Cyan Tier
    val Tertiary = Color(0xFF276A70)
    val TertiaryContainer = Color(0xFF1F7378)
    val TertiaryFixed = Color(0xFFE7F2F3)
    val TertiaryFixedDim = Color(0xFFC6E0E2)
    val OnTertiary = Color(0xFFFFFFFF)

    // Surfaces & Spatial Elevation
    val Background = Color(0xFFF8F8F2)
    val Surface = Color(0xFFF8F8F2)
    val SurfaceContainerLowest = Color(0xFFFFFFFF)
    val SurfaceContainerLow = Color(0xFFF4F6F0)
    val SurfaceContainer = Color(0xFFEEF2E9)
    val SurfaceContainerHigh = Color(0xFFE6ECE1)
    val SurfaceContainerHighest = Color(0xFFDDE5D9)
    val Outline = Color(0xFFD2DBD1)
    val OutlineVariant = Color(0xFFC5CFC3)

    // Dark HUD & Inverted Surfaces
    val InverseSurface = Color(0xFF193126)
    val InverseOnSurface = Color(0xFFE8F1E6)

    // Status & Warnings
    val Error = Color(0xFFBA1A1A)
    val ErrorContainer = Color(0xFFFFDAD6)
}

object LensTypography {
    val displayLg = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.02).sp
    )

    val headlineLg = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.015).sp
    )

    val headlineMd = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.01).sp
    )

    val headlineSm = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    )

    // Tabular Numerical Telemetry (Geist inspired)
    val metricValLg = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.03).sp
    )

    val metricValMd = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.02).sp
    )

    val labelCaps = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.6.sp // +0.06em uppercase
    )

    val labelSm = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )

    val bodyLg = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    )

    val bodyMd = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    )
}
