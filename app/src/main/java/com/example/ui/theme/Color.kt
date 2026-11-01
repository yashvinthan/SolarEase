package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// SolarEase unified citizen-friendly palette: calm, readable, and consistent.
val PrimaryEnergy = Color(0xFF245B3A)
val OnPrimaryEnergy = Color(0xFFFFFFFF)
val PrimaryContainerEnergy = Color(0xFFDDEFD9)
val OnPrimaryContainerEnergy = Color(0xFF123B27)
val SecondaryBlue = Color(0xFFE7F2F3)
val OnSecondaryBlue = Color(0xFF18324B)
val SecondaryContainerBlue = Color(0xFF276A70)
val OnSecondaryContainerBlue = Color(0xFFFFFFFF)
val TertiaryPurple = Color(0xFF65558F)
val OnTertiaryPurple = Color(0xFFFFFFFF)
val TertiaryContainerYellow = Color(0xFFFFE7B0)
val OnTertiaryContainerYellow = Color(0xFF3C2D00)
val BackgroundLight = Color(0xFFF8F8F2)
val OnBackgroundLight = Color(0xFF17201B)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF191C18)
val SurfaceVariantLight = Color(0xFFEEF2E9)
val OnSurfaceVariantLight = Color(0xFF4F5A52)
val OutlineLight = Color(0xFFD2DBD1)
val PrimaryDark = Color(0xFFB0D49A)
val OnPrimaryDark = Color(0xFF1A370B)
val PrimaryContainerDark = Color(0xFF2F521C)
val OnPrimaryContainerDark = Color(0xFFD0EDBC)
val SecondaryDark = Color(0xFF9ACAD2)
val OnSecondaryDark = Color(0xFF00363D)
val SecondaryContainerDark = Color(0xFF174E56)
val OnSecondaryContainerDark = Color(0xFFB5E7EF)
val TertiaryDark = Color(0xFFD0BDF7)
val OnTertiaryDark = Color(0xFF35245D)
val TertiaryContainerDark = Color(0xFF4D3B75)
val OnTertiaryContainerDark = Color(0xFFEBDDFF)
val BackgroundDark = Color(0xFF11140E)
val OnBackgroundDark = Color(0xFFE2E3DD)
val SurfaceDark = Color(0xFF1C1F19)
val OnSurfaceDark = Color(0xFFE2E3DD)
val SurfaceVariantDark = Color(0xFF292E25)
val OnSurfaceVariantDark = Color(0xFFC0C8B9)
val OutlineDark = Color(0xFF454B40)

// Centralized static token palettes for JVM testing and design system auditing
object StitchLightTokens {
    val Primary = PrimaryEnergy
    val PrimaryContainer = PrimaryEnergy
    val PrimaryFixed = Color(0xFFDDEFD9)
    val PrimaryFixedDim = Color(0xFFC9E1C5)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnPrimaryContainer = Color(0xFFE5F2D9)
    val OnPrimaryFixed = Color(0xFF123B27)
    val OnPrimaryFixedVariant = Color(0xFF1E4C32)
    val Surface = Color(0xFFF8F8F2)
    val Background = Color(0xFFF8F8F2)
    val SurfaceDim = Color(0xFFE6EBE2)
    val SurfaceBright = Color(0xFFF8F8F2)
    val SurfaceContainerLowest = Color(0xFFFFFFFF)
    val SurfaceContainerLow = Color(0xFFF4F6F0)
    val SurfaceContainer = Color(0xFFEEF2E9)
    val SurfaceContainerHigh = Color(0xFFE6ECE1)
    val SurfaceContainerHighest = Color(0xFFDDE5D9)
    val OnSurface = Color(0xFF17201B)
    val OnSurfaceVariant = Color(0xFF4F5A52)
    val InverseSurface = Color(0xFF2E352A)
    val InverseOnSurface = Color(0xFFF1F5E9)
    val Tertiary = Color(0xFF276A70)
    val TertiaryContainer = Color(0xFF1F7378)
    val Secondary = Color(0xFF525651)
    val SecondaryContainer = Color(0xFFE5F2D9)
    val Outline = Color(0xFFDDE4D5)
    val OutlineVariant = Color(0xFFC9D3C0)
    val Error = Color(0xFFBA1A1A)
    val ErrorContainer = Color(0xFFFFDAD6)
    val OnErrorContainer = Color(0xFF93000A)
}

object StitchDarkTokens {
    val Primary = PrimaryDark
    val PrimaryContainer = PrimaryDark
    val PrimaryFixed = PrimaryContainerDark
    val PrimaryFixedDim = PrimaryContainerDark
    val OnPrimary = OnPrimaryDark
    val OnPrimaryContainer = OnPrimaryContainerDark
    val OnPrimaryFixed = OnPrimaryContainerDark
    val OnPrimaryFixedVariant = OnPrimaryDark
    val Surface = BackgroundDark
    val Background = BackgroundDark
    val SurfaceDim = SurfaceDark
    val SurfaceBright = SurfaceVariantDark
    val SurfaceContainerLowest = SurfaceDark
    val SurfaceContainerLow = Color(0xFF20251D)
    val SurfaceContainer = SurfaceVariantDark
    val SurfaceContainerHigh = Color(0xFF292F25)
    val SurfaceContainerHighest = Color(0xFF333B2E)
    val OnSurface = OnBackgroundDark
    val OnSurfaceVariant = OnSurfaceVariantDark
    val InverseSurface = Color(0xFFE2E3DD)
    val InverseOnSurface = Color(0xFF17201B)
    val Tertiary = SecondaryDark
    val TertiaryContainer = SecondaryContainerDark
    val Secondary = OnSurfaceVariantDark
    val SecondaryContainer = SecondaryContainerDark
    val Outline = OutlineDark
    val OutlineVariant = OutlineDark.copy(alpha = 0.5f)
    val Error = Color(0xFFFFB4AB)
    val ErrorContainer = Color(0xFF93000A)
    val OnErrorContainer = Color(0xFFFFDAD6)
}

// Shared component tokens: fully theme-aware with high contrast in both Light and Dark modes.
val StitchPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.Primary else StitchLightTokens.Primary

val StitchPrimaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.PrimaryContainer else StitchLightTokens.PrimaryContainer

val StitchPrimaryFixed: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.PrimaryFixed else StitchLightTokens.PrimaryFixed

val StitchPrimaryFixedDim: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.PrimaryFixedDim else StitchLightTokens.PrimaryFixedDim

val StitchOnPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.OnPrimary else StitchLightTokens.OnPrimary

val StitchOnPrimaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.OnPrimaryContainer else StitchLightTokens.OnPrimaryContainer

val StitchOnPrimaryFixed: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.OnPrimaryFixed else StitchLightTokens.OnPrimaryFixed

val StitchOnPrimaryFixedVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.OnPrimaryFixedVariant else StitchLightTokens.OnPrimaryFixedVariant

val StitchSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.Surface else StitchLightTokens.Surface

val StitchBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.Background else StitchLightTokens.Background

val StitchSurfaceDim: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.SurfaceDim else StitchLightTokens.SurfaceDim

val StitchSurfaceBright: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.SurfaceBright else StitchLightTokens.SurfaceBright

val StitchSurfaceContainerLowest: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.SurfaceContainerLowest else StitchLightTokens.SurfaceContainerLowest

val StitchSurfaceLowest: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.SurfaceContainerLowest else StitchLightTokens.SurfaceContainerLowest

val StitchSurfaceContainerLow: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.SurfaceContainerLow else StitchLightTokens.SurfaceContainerLow

val StitchSurfaceLow: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.SurfaceContainerLow else StitchLightTokens.SurfaceContainerLow

val StitchSurfaceContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.SurfaceContainer else StitchLightTokens.SurfaceContainer

val StitchSurfaceContainerHigh: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.SurfaceContainerHigh else StitchLightTokens.SurfaceContainerHigh

val StitchSurfaceHigh: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.SurfaceContainerHigh else StitchLightTokens.SurfaceContainerHigh

val StitchSurfaceContainerHighest: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.SurfaceContainerHighest else StitchLightTokens.SurfaceContainerHighest

val StitchOnSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.OnSurface else StitchLightTokens.OnSurface

val StitchOnSurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.OnSurfaceVariant else StitchLightTokens.OnSurfaceVariant

val StitchInverseSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.InverseSurface else StitchLightTokens.InverseSurface

val StitchInverseOnSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.InverseOnSurface else StitchLightTokens.InverseOnSurface

val StitchTertiary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.Tertiary else StitchLightTokens.Tertiary

val StitchTertiaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.TertiaryContainer else StitchLightTokens.TertiaryContainer

val StitchSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.Secondary else StitchLightTokens.Secondary

val StitchSecondaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.SecondaryContainer else StitchLightTokens.SecondaryContainer

val StitchOutline: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.Outline else StitchLightTokens.Outline

val StitchOutlineVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.OutlineVariant else StitchLightTokens.OutlineVariant

val StitchError: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.Error else StitchLightTokens.Error

val StitchErrorContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.ErrorContainer else StitchLightTokens.ErrorContainer

val StitchOnErrorContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StitchDarkTokens.OnErrorContainer else StitchLightTokens.OnErrorContainer

