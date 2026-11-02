package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// ============================================================================
// SolarEase Unified Commercial Design Tokens
// WCAG 2.2 AA compliant across Light and Dark themes.
// Primary: Deep Solar Emerald | Secondary: Clean Slate Cyan | Tertiary: Warm Sunrise Amber
// ============================================================================

// ----------------------------------------------------------------------------
// 1. Primary Palette: Deep Solar Emerald
// ----------------------------------------------------------------------------
val PrimaryLight = Color(0xFF245B3A)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFDDEFD9)
val OnPrimaryContainerLight = Color(0xFF0F3820)
val PrimaryFixedLight = Color(0xFFDDEFD9)
val PrimaryFixedDimLight = Color(0xFFC9E1C5)
val OnPrimaryFixedLight = Color(0xFF0F3820)
val OnPrimaryFixedVariantLight = Color(0xFF1E4C32)

val PrimaryDark = Color(0xFFB0D49A)
val OnPrimaryDark = Color(0xFF1A370B)
val PrimaryContainerDark = Color(0xFF1E4E30) // Remediated: Deep emerald container (7.04:1 AAA contrast)
val OnPrimaryContainerDark = Color(0xFFA6EDB4) // Remediated: Crisp pastel mint
val PrimaryFixedDark = Color(0xFF1E4E30)
val PrimaryFixedDimDark = Color(0xFF1E4E30)
val OnPrimaryFixedDark = Color(0xFFA6EDB4)
val OnPrimaryFixedVariantDark = Color(0xFF9AD2A8)

// ----------------------------------------------------------------------------
// 2. Secondary Palette: Clean Slate Cyan
// ----------------------------------------------------------------------------
val SecondaryLight = Color(0xFF276A70)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFCEEFF3)
val OnSecondaryContainerLight = Color(0xFF002024)
val SecondaryFixedLight = Color(0xFFCEEFF3)
val SecondaryFixedDimLight = Color(0xFFB2E7EF)
val OnSecondaryFixedLight = Color(0xFF002024)
val OnSecondaryFixedVariantLight = Color(0xFF0E4F55)

val SecondaryDark = Color(0xFF9ACAD2)
val OnSecondaryDark = Color(0xFF00363C)
val SecondaryContainerDark = Color(0xFF1E4549) // Remediated: Slate cyan container (7.92:1 AAA contrast)
val OnSecondaryContainerDark = Color(0xFFB0EAEF)
val SecondaryFixedDark = Color(0xFF1E4549)
val SecondaryFixedDimDark = Color(0xFF1E4549)
val OnSecondaryFixedDark = Color(0xFFB0EAEF)
val OnSecondaryFixedVariantDark = Color(0xFF9ACAD2)

// ----------------------------------------------------------------------------
// 3. Tertiary Palette: Warm Sunrise Amber
// ----------------------------------------------------------------------------
val TertiaryLight = Color(0xFFAA5418)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFFFDBC8)
val OnTertiaryContainerLight = Color(0xFF381400)
val TertiaryFixedLight = Color(0xFFFFDBC8)
val TertiaryFixedDimLight = Color(0xFFFFB77C)
val OnTertiaryFixedLight = Color(0xFF381400)
val OnTertiaryFixedVariantLight = Color(0xFF8B3E00)

val TertiaryDark = Color(0xFFFFB77C)
val OnTertiaryDark = Color(0xFF4E2600)
val TertiaryContainerDark = Color(0xFF5C2800) // Remediated: Deep amber container (9.22:1 AAA contrast)
val OnTertiaryContainerDark = Color(0xFFFFDBC8)
val TertiaryFixedDark = Color(0xFF5C2800)
val TertiaryFixedDimDark = Color(0xFF5C2800)
val OnTertiaryFixedDark = Color(0xFFFFDBC8)
val OnTertiaryFixedVariantDark = Color(0xFFFFB77C)

// ----------------------------------------------------------------------------
// 4. Neutral Palette: Surfaces & Backgrounds (Non-Inverted, Calibrated)
// ----------------------------------------------------------------------------
val BackgroundLight = Color(0xFFF8F8F2)
val OnBackgroundLight = Color(0xFF17201B)
val SurfaceLight = Color(0xFFF8F8F2)
val OnSurfaceLight = Color(0xFF17201B)
val SurfaceVariantLight = Color(0xFFEEF2E9)
val OnSurfaceVariantLight = Color(0xFF4F5A52)
val SurfaceDimLight = Color(0xFFE6EBE2)
val SurfaceBrightLight = Color(0xFFF8F8F2)
val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
val SurfaceContainerLowLight = Color(0xFFF4F6F0)
val SurfaceContainerMediumLight = Color(0xFFEEF2E9)
val SurfaceContainerHighLight = Color(0xFFE6ECE1)
val SurfaceContainerHighestLight = Color(0xFFDDE5D9)
val OutlineLight = Color(0xFF727970) // Remediated: >= 3.48:1 on all surfaces
val OutlineVariantLight = Color(0xFFC5CFC3)
val InverseSurfaceLight = Color(0xFF2E352A)
val InverseOnSurfaceLight = Color(0xFFF1F5E9)

val BackgroundDark = Color(0xFF11140E)
val OnBackgroundDark = Color(0xFFE2E3DD)
val SurfaceDark = Color(0xFF11140E)
val OnSurfaceDark = Color(0xFFE2E3DD)
val SurfaceVariantDark = Color(0xFF292E25)
val OnSurfaceVariantDark = Color(0xFFC0C8B9)
val SurfaceDimDark = Color(0xFF11140E)
val SurfaceBrightDark = Color(0xFF292E25)
val SurfaceContainerLowestDark = Color(0xFF171B14)
val SurfaceContainerLowDark = Color(0xFF1C2119)
val SurfaceContainerMediumDark = Color(0xFF20251D)
val SurfaceContainerHighDark = Color(0xFF2B3127)
val SurfaceContainerHighestDark = Color(0xFF353C31)
val OutlineDark = Color(0xFF8C9388) // Remediated: >= 3.61:1 on all surfaces
val OutlineVariantDark = Color(0xFF454B40)
val InverseSurfaceDark = Color(0xFFE2E3DD)
val InverseOnSurfaceDark = Color(0xFF17201B)

// ----------------------------------------------------------------------------
// 5. Semantic Error Palette
// ----------------------------------------------------------------------------
val ErrorLight = Color(0xFFBA1A1A)
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFFFDAD6)
val OnErrorContainerLight = Color(0xFF410002)

val ErrorDark = Color(0xFFFFB4AB)
val OnErrorDark = Color(0xFF690005)
val ErrorContainerDark = Color(0xFF93000A)
val OnErrorContainerDark = Color(0xFFFFDAD6)

// ============================================================================
// Legacy Top-Level Constant Aliases (Backward Compatibility)
// ============================================================================
val PrimaryEnergy = PrimaryLight
val OnPrimaryEnergy = OnPrimaryLight
val PrimaryContainerEnergy = PrimaryContainerLight
val OnPrimaryContainerEnergy = OnPrimaryContainerLight
val SecondaryBlue = SecondaryContainerLight
val OnSecondaryBlue = OnSecondaryLight
val SecondaryContainerBlue = SecondaryLight
val OnSecondaryContainerBlue = OnSecondaryLight
val TertiaryPurple = TertiaryLight
val OnTertiaryPurple = OnTertiaryLight
val TertiaryContainerYellow = TertiaryContainerLight
val OnTertiaryContainerYellow = OnTertiaryContainerLight

// ============================================================================
// Stitch Static Tokens (Preserved for JVM unit tests & design system auditing)
// ============================================================================
object StitchLightTokens {
    val Primary = PrimaryLight
    val PrimaryContainer = PrimaryContainerLight
    val PrimaryFixed = PrimaryFixedLight
    val PrimaryFixedDim = PrimaryFixedDimLight
    val OnPrimary = OnPrimaryLight
    val OnPrimaryContainer = OnPrimaryContainerLight
    val OnPrimaryFixed = OnPrimaryFixedLight
    val OnPrimaryFixedVariant = OnPrimaryFixedVariantLight
    val Surface = SurfaceLight
    val Background = BackgroundLight
    val SurfaceDim = SurfaceDimLight
    val SurfaceBright = SurfaceBrightLight
    val SurfaceContainerLowest = SurfaceContainerLowestLight
    val SurfaceContainerLow = SurfaceContainerLowLight
    val SurfaceContainer = SurfaceContainerMediumLight
    val SurfaceContainerHigh = SurfaceContainerHighLight
    val SurfaceContainerHighest = SurfaceContainerHighestLight
    val OnSurface = OnSurfaceLight
    val OnSurfaceVariant = OnSurfaceVariantLight
    val InverseSurface = InverseSurfaceLight
    val InverseOnSurface = InverseOnSurfaceLight
    val Tertiary = TertiaryLight
    val TertiaryContainer = TertiaryContainerLight
    val Secondary = SecondaryLight
    val SecondaryContainer = SecondaryContainerLight
    val Outline = OutlineLight
    val OutlineVariant = OutlineVariantLight
    val Error = ErrorLight
    val ErrorContainer = ErrorContainerLight
    val OnErrorContainer = OnErrorContainerLight
}

object StitchDarkTokens {
    val Primary = PrimaryDark
    val PrimaryContainer = PrimaryContainerDark // Remediated: was PrimaryDark (1.3:1 contrast defect eliminated)
    val PrimaryFixed = PrimaryFixedDark
    val PrimaryFixedDim = PrimaryFixedDimDark
    val OnPrimary = OnPrimaryDark
    val OnPrimaryContainer = OnPrimaryContainerDark // Remediated: 7.04:1 contrast against PrimaryContainerDark
    val OnPrimaryFixed = OnPrimaryFixedDark
    val OnPrimaryFixedVariant = OnPrimaryFixedVariantDark
    val Surface = SurfaceDark
    val Background = BackgroundDark
    val SurfaceDim = SurfaceDimDark
    val SurfaceBright = SurfaceBrightDark
    val SurfaceContainerLowest = SurfaceContainerLowestDark
    val SurfaceContainerLow = SurfaceContainerLowDark
    val SurfaceContainer = SurfaceContainerMediumDark
    val SurfaceContainerHigh = SurfaceContainerHighDark
    val SurfaceContainerHighest = SurfaceContainerHighestDark
    val OnSurface = OnSurfaceDark
    val OnSurfaceVariant = OnSurfaceVariantDark
    val InverseSurface = InverseSurfaceDark
    val InverseOnSurface = InverseOnSurfaceDark
    val Tertiary = TertiaryDark
    val TertiaryContainer = TertiaryContainerDark
    val Secondary = SecondaryDark
    val SecondaryContainer = SecondaryContainerDark
    val Outline = OutlineDark
    val OutlineVariant = OutlineVariantDark
    val Error = ErrorDark
    val ErrorContainer = ErrorContainerDark
    val OnErrorContainer = OnErrorContainerDark
}

// ============================================================================
// 33 Backward-Compatible Composable Getters
// Bound directly to MaterialTheme.colorScheme and LocalIsDarkTheme.
// Resolves decoupling defect while preserving caller source-compatibility.
// ============================================================================

val StitchPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primary

val StitchPrimaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primaryContainer

val StitchPrimaryFixed: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) StitchDarkTokens.PrimaryFixed else StitchLightTokens.PrimaryFixed

val StitchPrimaryFixedDim: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) StitchDarkTokens.PrimaryFixedDim else StitchLightTokens.PrimaryFixedDim

val StitchOnPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onPrimary

val StitchOnPrimaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onPrimaryContainer

val StitchOnPrimaryFixed: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) StitchDarkTokens.OnPrimaryFixed else StitchLightTokens.OnPrimaryFixed

val StitchOnPrimaryFixedVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) StitchDarkTokens.OnPrimaryFixedVariant else StitchLightTokens.OnPrimaryFixedVariant

val StitchSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surface

val StitchBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.background

val StitchSurfaceDim: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) StitchDarkTokens.SurfaceDim else StitchLightTokens.SurfaceDim

val StitchSurfaceBright: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) StitchDarkTokens.SurfaceBright else StitchLightTokens.SurfaceBright

val StitchSurfaceContainerLowest: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceContainerLowest

val StitchSurfaceLowest: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceContainerLowest

val StitchSurfaceContainerLow: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceContainerLow

val StitchSurfaceLow: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceContainerLow

val StitchSurfaceContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceContainer

val StitchSurfaceContainerHigh: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceContainerHigh

val StitchSurfaceHigh: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceContainerHigh

val StitchSurfaceContainerHighest: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceContainerHighest

val StitchOnSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurface

val StitchOnSurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

val StitchInverseSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.inverseSurface

val StitchInverseOnSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.inverseOnSurface

val StitchTertiary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.tertiary

val StitchTertiaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.tertiaryContainer

val StitchSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.secondary

val StitchSecondaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.secondaryContainer

val StitchOutline: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.outline

val StitchOutlineVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.outlineVariant

val StitchError: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.error

val StitchErrorContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.errorContainer

val StitchOnErrorContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onErrorContainer
