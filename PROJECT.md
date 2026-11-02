# Project: SolarEase Commercial Redesign & Experience Transformation

## Architecture
SolarEase is a single-module Android native application (`:app`) built in Kotlin 2.2.10 and Jetpack Compose (BOM 2024.09.00), backed by a FastAPI cloud backend (`backend/`).
The application uses a clean service-locator / manual DI architecture with AndroidKeyStore hardware-backed EncryptedSharedPreferences (AES256-GCM) for security and standard SharedPreferences for preferences and operational cache.

The redesign transforms the presentation layer into an elite, commercial-grade product:
1. **Design System & Token Foundation**: Single source of truth in `com.example.ui.theme` binding color, typography, spacing, shapes, elevations, and motion into semantic tokens. Independent, non-inverted light and dark themes with WCAG 2.2 AA compliance, persistent theme state, and zero cold-start flashing.
2. **Reusable Component Architecture**: Unified component system in `com.example.ui.components` covering all interactive states (default, hover, focus, pressed, disabled, error, loading) with proper touch targets (>=48dp) and semantics.
3. **Screen Deconstruction & Flow Modularization**: Monolithic screens (`HomeScreen`, `ProfileScreen`, `LensLandingScreen`, etc.) decomposed into clean, focused composables.
4. **Information Architecture Unification**: Reconnection of orphaned destinations (`AnalyticsScreen`, `CustomerDashboard`), unification of the 12-step solar proposal funnel, and integration of contractor exploration.
5. **Motion, Layouts & Accessibility**: Centralized motion tokens honoring system reduced-motion preferences, adaptive phone/tablet layouts, visible focus indicators, and accessible canvas chart semantics.
6. **Zero Regression Functional Invariants**: 100% preservation of all 8 domain calculation formulas, 22 ViewModels, EncryptedSharedPreferences storage, and backend API contracts.

---

## Feature Inventory
| # | Feature | Description | Milestone | Source |
|---|---------|-------------|-----------|--------|
| 1 | Unified Semantic Color Tokens | Single source of truth for colors in `Color.kt` & `Theme.kt`, deprecating all local color objects | M1 | Survey (Explorer 1 & 2) |
| 2 | Contrast Remediation & Dark Mode Fix | Eliminate dark mode 1.3:1 contrast defect; enforce WCAG 2.2 AA (>=4.5:1 text, >=3:1 non-text) | M1 | Survey (Explorer 2) |
| 3 | Theme Persistence & Mode Switcher | Persist user preference (System/Light/Dark) in `UserSession`/Preferences with clean state flow | M1 | Survey (Explorer 2) |
| 4 | Startup Window Flash Elimination | Add `windowBackground` and splash screen theme in `themes.xml` to prevent white startup flash | M1 | Survey (Explorer 2) |
| 5 | Centralized Motion Tokens | Define duration, easing, and spring tokens with system reduced-motion detection | M1 | Survey (Explorer 2) |
| 6 | AndroidManifest AppLink Lint Fix | Separate custom scheme `solarease` from `autoVerify="true"` HTTP intent filter | M1 | Survey (Explorer 3) |
| 7 | SolarEaseButton Suite | Unified primary, secondary, outlined, and text buttons with loading and disabled states (>=48dp) | M2 | Survey (Explorer 1 & 2) |
| 8 | SolarEaseCard & SelectableCard | Accessible cards with selection state descriptions, radio/checkbox roles, and elevation | M2 | Survey (Explorer 1) |
| 9 | SolarEaseNavigation (TopBar, BottomNav, NavRail) | 4-tab primary navigation (Home, Analytics, Recommends, Profile), visible focus, ripple, >=48dp | M2 | Survey (Explorer 1 & 2) |
| 10 | Unified SolarEaseStepProgressBar | Dynamic step progress bar replacing conflicting 8-step and 11-step progress bars | M2 | Survey (Explorer 1) |
| 11 | SolarEaseEmptyState & SolarEaseErrorState | Standardized empty states and error recovery states with illustration and retry callback | M2 | Survey (Explorer 1) |
| 12 | SolarEaseMetricCard & MetricStrip | High-density metric display for kWh, ₹, and CO2 with tabular figures and trend badges | M2 | Survey (Explorer 1) |
| 13 | SolarEaseTimeline & SolarEaseFilterChipGroup | Vertical milestone timeline and time range filter chips with >=48dp touch targets | M2 | Survey (Explorer 1 & 2) |
| 14 | Legacy Component Deprecation | Prune duplicate `StepProgress`, `SelectableCard`, and dummy `DashboardBottomNavigation` | M2 | Survey (Explorer 1) |
| 15 | HomeScreen Modular Deconstruction | Deconstruct 4,183-line monolith into Hero, BillStatus, MeterTracking, SolarPromo, QuickTools | M3 | Survey (Explorer 1) |
| 16 | TnebBillStatusScreen Token Unification | Bind to semantic tokens, eliminate private emerald colors, polish live bill query states | M3 | Survey (Explorer 1 & 2) |
| 17 | BillCalculatorScreen Modularization | Modularize 2,085-line screen, maintain TNEB LT-1A slab math, add accessible tabular summary | M3 | Survey (Explorer 1 & 3) |
| 18 | ElectricityChatbotScreen Token Binding | Eliminate `LensColors` leak, bind to central tokens, fix contrast & auto-scroll | M3 | Survey (Explorer 1 & 2) |
| 19 | BillHistory & Consumption Trends Polish | Modernize bill cards, payment history, and bi-monthly consumption trend inspection | M3 | Survey (Explorer 1) |
| 20 | MeterScanner HUD & A11y Announcements | Refine CameraX OCR bounding box, manual correction sheet, and a11y audio/talkback cues | M3 | Survey (Explorer 1 & 3) |
| 21 | 12-Step Guided Solar Assessment Journey | Unify steps 1-12 under single stepper with seamless transitions and validated inputs | M4 | Survey (Explorer 1) |
| 22 | Contractor Marketplace Reconnection | Re-integrate NearbyVendors, VendorProfile, EstimateRequest, RequestConfirmation, SiteSurvey | M4 | Survey (Explorer 1) |
| 23 | Dead Legacy Screen Pruning | Prune `AssessmentInputScreen.kt` and `AuthProfileScreen.kt` dead code | M4 | Survey (Explorer 1) |
| 24 | CustomerDashboard Re-Integration | Connect post-installation monitoring suite (MySolarSystem, Performance, Financial, Environmental) | M4 | Survey (Explorer 1) |
| 25 | Accessible Canvas Data Visualizations | Add accessibility semantics, content descriptions, and tabular fallbacks to canvas charts | M4 | Survey (Explorer 1 & 2) |
| 26 | Action Wire-Up & Inverter Telemetry Link | Connect dead-end share/download buttons and wire SolarPanelScreen to performance telemetry | M4 | Survey (Explorer 1 & 2) |
| 27 | Solar Lens Landing & Review Modularization | Deconstruct 2,483-line LensLandingScreen into RoomGrid, MediaUpload, and AuditSummary | M4 | Survey (Explorer 1) |
| 28 | RecommendsScreen Modularization | Modularize mantras, check-in streak, billing cycle comparisons, and badges/rewards | M5 | Survey (Explorer 1) |
| 29 | ProfileScreen 8-Sheet Modular Deconstruction | Extract 8 modal bottom sheets into dedicated files under `feature/profile/ui/sheets/` | M5 | Survey (Explorer 1) |
| 30 | Modernized Auth Flows | Modernize AuthMainScreen tabs, phone input, and tactile OTP pin field (>=48dp targets) | M5 | Survey (Explorer 1) |
| 31 | Statutory DPDP Act & Legal Dialog Polish | Enhance LegalComplianceDialog ergonomics and responsive scaling on wide viewports | M5 | Survey (Explorer 1) |
| 32 | Adaptive Layouts (Phone, Tablet, Desktop) | Implement adaptive multi-pane / two-column layouts using WindowWidthSizeClass | M5 | Survey (Explorer 1 & 2) |
| 33 | Global Motion Reduced-Motion Enforcement | Enforce reduced-motion overrides across all screens, stopping infinite pulsing animations | M5 | Survey (Explorer 2) |
| 34 | E2E Test Suite Validation (Tiers 1-4) | Achieve 100% pass on comprehensive E2E test suite created by E2E Testing Track | M6 | Project Mandate |
| 35 | Adversarial Hardening & Verification | White-box adversarial testing (Tier 5), build & test checks (`./gradlew test`, release builds) | M6 | Project Mandate |

---

## Milestones
| # | Name | Scope | Dependencies | Status |
|---|------|-------|-------------|--------|
| M1 | Visual Identity, Semantic Tokens & Theme Engine Foundation | `ui.theme`, `Color.kt`, `Theme.kt`, `Type.kt`, `Shape.kt`, `DesignTokens.kt`, `MotionTokens.kt`, `themes.xml`, theme persistence, contrast fixes, lint fix | none | PLANNED |
| M2 | Reusable Commercial Component Library & Primitives | `ui.components`: `SolarEaseButton`, `SolarEaseCard`, `SolarEaseSelectableCard`, `SolarEaseNavigation`, `SolarEaseStepProgressBar`, `SolarEaseStates`, `SolarEaseMetrics`, `SolarEaseTimeline` | M1 | PLANNED |
| M3 | Core Command Center, Home Hub & Utility Experience | `feature/home/ui/` (`HomeScreen` modularization, `BillStatus`, `MeterTracking`, `QuickTools`), `BillCalculatorScreen`, `TnebBillStatusScreen`, `ElectricityChatbotScreen`, `MeterScannerScreen` | M1, M2 | PLANNED |
| M4 | 12-Step Guided Solar Funnel, Marketplace & Dashboard | 12-step guided assessment journey, vendor screens reconnection, dead code removal, `CustomerDashboard`, canvas chart a11y, `LensLandingScreen` modularization | M1, M2 | PLANNED |
| M5 | Engagement, Profile, Statutory Legal, Motion & Adaptability | `RecommendsScreen`, `ProfileScreen` (8 bottom sheets extraction), `AuthMainScreen`, `AuthPhoneScreen`, `AuthOtpScreen`, `LegalComplianceDialog`, adaptive tablet shells, global reduced motion | M1, M2, M3, M4 | PLANNED |
| M6 | Dual Track Final Verification, 100% E2E Pass & Hardening | Phase 1: Pass 100% E2E Tests (Tiers 1-4) from `TEST_READY.md`. Phase 2: Adversarial coverage hardening (Tier 5), verification checks (`./gradlew test`, `./gradlew assembleRelease`, `./gradlew bundleRelease`) | M1-M5, TEST_READY.md | PLANNED |

In parallel:
- **E2E Testing Track Orchestrator**: Generates independent opaque-box test suite (Tiers 1-4) covering all 35 features per `TEST_INFRA.md`, publishing `TEST_READY.md`.

---

## Code Layout & Ownership
- All source code resides in `d:/dev/SolarEase/`:
  - `app/src/main/java/com/example/ui/theme/` (Owned by M1)
  - `app/src/main/res/values/themes.xml` (Owned by M1)
  - `app/src/main/AndroidManifest.xml` (Owned by M1 for AppLink fix)
  - `app/src/main/java/com/example/ui/components/` (Owned by M2)
  - `app/src/main/java/com/example/feature/home/ui/` (Owned by M3)
  - `app/src/main/java/com/example/feature/meterreading/ui/` (Owned by M3)
  - `app/src/main/java/com/example/feature/assessment/ui/` (Owned by M4)
  - `app/src/main/java/com/example/feature/order/ui/` (Owned by M4)
  - `app/src/main/java/com/example/feature/lens/ui/` (Owned by M4)
  - `app/src/main/java/com/example/feature/dashboard/ui/` (Owned by M4)
  - `app/src/main/java/com/example/feature/systemdetails/ui/` (Owned by M4)
  - `app/src/main/java/com/example/feature/performance/ui/` (Owned by M4)
  - `app/src/main/java/com/example/feature/financial/ui/` (Owned by M4)
  - `app/src/main/java/com/example/feature/environmental/ui/` (Owned by M4)
  - `app/src/main/java/com/example/feature/analytics/ui/` (Owned by M4)
  - `app/src/main/java/com/example/feature/recommends/ui/` (Owned by M5)
  - `app/src/main/java/com/example/feature/profile/ui/` (Owned by M5)
  - `app/src/main/java/com/example/feature/auth/ui/` (Owned by M5)
  - `app/src/main/java/com/example/core/legal/` (Owned by M5)
  - `app/src/main/java/com/example/navigation/` (Owned by M3, M4, M5 for clean route wiring)
  - `tests/` / `app/src/test/` (Owned by E2E Testing Track & M6)

---

## Interface Contracts

### 1. `ThemeEngine` (M1) ↔ All Consumers
- Function: `SolarEaseTheme(themeMode: ThemeMode = ThemeMode.SYSTEM, content: @Composable () -> Unit)`
- Theme State Holder: `LocalThemeController` or `UserSession.themePreference`
- Semantic Colors: Access via `MaterialTheme.colorScheme` and `SolarEaseTheme.colors`
  - `Primary`: Deep Solar Emerald (`#245B3A` light / `#9AD2A8` dark, min 4.5:1 on container)
  - `Secondary`: Clean Slate Cyan (`#276A70` light / `#9ACAD2` dark)
  - `Tertiary`: Warm Sunrise Amber (`#AA5418` light / `#FFB77C` dark)
  - `Surface`, `SurfaceContainerLow`, `SurfaceContainer`, `SurfaceContainerHigh`
  - `Error`: `#BA1A1A` light / `#FFB4AB` dark
- Motion Tokens: `SolarEaseMotion.durationShort` (150ms), `durationMedium` (300ms), `durationLong` (500ms), `easingStandard`, `isReducedMotionEnabled()`

### 2. `Reusable Components` (M2) ↔ Feature Screens
- `SolarEaseButton(onClick, text, modifier, variant, enabled, isLoading, leadingIcon, trailingIcon)`
- `SolarEaseSelectableCard(selected, onClick, modifier, title, subtitle, icon, badgeText)`
- `SolarEaseStepProgressBar(currentStep, totalSteps, stepTitle, onBack, modifier)`
- `SolarEaseEmptyState(illustration, title, description, actionText, onAction, modifier)`
- `SolarEaseErrorState(message, errorCode, onRetry, modifier)`
- `SolarEaseMetricCard(label, value, unit, trendDelta, subtitle, modifier)`
- `SolarEaseTopBar(title, onBack, actions, modifier)`
- `SolarEaseBottomNavigation(currentRoute, onNavigate, modifier)`

### 3. Business Logic & Calculation Invariants (Strict Preservation)
- Solar Sizing: `monthlyConsumption / 120.0` (kW capacity)
- PM Surya Ghar Subsidy: On-Grid + DCR only. ₹33,000 for 1st kW, ₹33,000 for 2nd kW, ₹12,000 for 3rd kW, ₹78,000 maximum cap.
- TNEB LT-1A Slab Billing: <=500 kWh slab structure vs >500 kWh slab structure with ₹0.00 for first 100 units.
- Persistence: Hardware-backed EncryptedSharedPreferences (AES256-GCM) via `SecurePreferencesHelper` and standard SharedPreferences with `kotlinx.serialization.json`. No Room database conversions.
