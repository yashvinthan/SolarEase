# SolarEase Test Infrastructure & Quality Architecture (`TEST_INFRA.md`)

## 1. Overview & Testing Philosophy
SolarEase adopts a **4-Tier Opaque-Box Quality Assurance Architecture** designed to rigorously validate all functional invariants, regulatory compliances, domain calculation engines, UI design token contracts, and multi-step user journeys. 

Testing follows strict opaque-box principles: tests assert expected outputs against authoritative external sources (e.g., TNEB LT-1A tariff schedules, MNRE PM Surya Ghar guidelines, WCAG 2.2 AA contrast standards, DPDP Act 2023 statutory disclosures) without depending on private internal implementation details.

---

## 2. Test Execution & Runner Invocations

### Standard Gradle Commands
| Scope | Command | Purpose |
| :--- | :--- | :--- |
| **Full E2E Test Suite (Tiers 1–4)** | `./gradlew testDebugUnitTest --tests "com.example.e2e.*"` | Runs all opaque-box E2E tests across all 35 features |
| **Tier 1 (Feature Coverage)** | `./gradlew testDebugUnitTest --tests "com.example.e2e.Tier1*"` | Runs unit & contract tests covering all 35 features |
| **Tier 2 (Boundary & Corner Cases)** | `./gradlew testDebugUnitTest --tests "com.example.e2e.Tier2*"` | Runs edge-case and boundary verification |
| **Tier 3 (Cross-Feature Combinations)**| `./gradlew testDebugUnitTest --tests "com.example.e2e.Tier3*"` | Runs pairwise multi-module interaction tests |
| **Tier 4 (Real-World Scenarios)** | `./gradlew testDebugUnitTest --tests "com.example.e2e.Tier4*"` | Runs 5+ complete end-to-end household journeys |
| **All Project Unit Tests** | `./gradlew testDebugUnitTest` | Runs all unit and module tests in `:app` |
| **Release Build Verification** | `./gradlew assembleRelease` | Verifies release compilation, ProGuard, and resource shrinking |

*On Windows environments, use `.\gradlew.bat` in place of `./gradlew`.*

---

## 3. Four-Tier Test Architecture

```
com.example.e2e/
├── Tier1DesignSystemAndThemeTest.kt       -> Features 1–6 (Theme, Colors, Contrast, AppLinks, Motion)
├── Tier1ComponentsAndNavigationTest.kt   -> Features 7–14 (Buttons, Cards, Nav, Progress, States)
├── Tier1CoreUtilityAndBillingTest.kt      -> Features 15–20 (Home Hub, TNEB Bill, OCR Scanner)
├── Tier1SolarFunnelAndMarketplaceTest.kt  -> Features 21–26 (12-Step Funnel, Marketplace, Telemetry)
├── Tier1LensRecommendsAndComplianceTest.kt-> Features 27–35 (Lens Audit, Rewards, DPDP, Layouts)
├── Tier2BoundaryAndCornerTest.kt          -> Boundary values, 0 units, extremes, invalid inputs
├── Tier3CrossFeatureCombinationsTest.kt   -> Pairwise interactions (Sizing -> Subsidy -> Bill Offset -> ROI)
└── Tier4RealWorldScenariosTest.kt         -> 5 End-to-End Multi-Step Household Lifecycles
```

### Tier Descriptions & Coverage Requirements:
1. **Tier 1: Feature Coverage (>=5 test cases per feature)**
   - Isolated, deterministic tests covering the contract and behavior of every feature in `PROJECT.md § Feature Inventory`.
   - Validates semantic color values, WCAG contrast compliance, theme persistence, button and card state contracts, TNEB LT-1A slab math, OCR filtering, and legal requirements.
2. **Tier 2: Boundary & Corner Cases (>=5 test cases per domain)**
   - Focuses on boundary transitions, zero inputs, negative values, extreme consumption (>50,000 kWh), maximum subsidy caps (₹78,000), OCR ambiguities (dropped decimal, impulse noise), and corrupted data resilience.
3. **Tier 3: Cross-Feature Combinations**
   - Pairwise multi-component pipelines: Solar Sizing $\rightarrow$ PM Surya Ghar Subsidy $\rightarrow$ Net Payable $\rightarrow$ TNEB Bill Offset $\rightarrow$ Payback ROI.
   - Meter OCR Extraction $\rightarrow$ Plausibility Check $\rightarrow$ TNEB Bill Calculator $\rightarrow$ Baseline $\rightarrow$ Reward Ledger.
   - Lens Room Inventory $\rightarrow$ Load Aggregation $\rightarrow$ PV Capacity Recommendation $\rightarrow$ Markdown Report Export.
4. **Tier 4: Real-World Application Scenarios (>=5 multi-step workflows)**
   - **Scenario 1**: Urban Apartment Retrofit (Chennai 2BHK, 360 kWh bi-monthly, rooftop area limitation, On-Grid DCR).
   - **Scenario 2**: High-Consumption Suburban Villa (Coimbatore, 980 kWh bi-monthly, 5kW On-Grid, max subsidy cap ₹78k, 4.2-year payback).
   - **Scenario 3**: Rural Agricultural / Off-Grid Outpost (Madurai, pump load, whole-house Lens audit, Off-Grid non-subsidy check).
   - **Scenario 4**: Professional Clinic / Daytime Commercial (Tiruchirappalli, daytime load, hybrid solar feasibility, multi-cycle billing).
   - **Scenario 5**: OCR Meter Verification to Reward Ledger Pipeline (Salem, digital LCD capture, dropped-decimal recovery, 12% verified reduction, 30 bonus coins earned).

---

## 4. Authoritative Invariants & Domain Calculation Thresholds

| Domain Formula | Authoritative Specification | Invariant / Reference Source |
| :--- | :--- | :--- |
| **Solar Sizing Capacity** | `Capacity (kW) = Monthly Consumption (kWh) / 120.0` | MNRE Residential Rooftop Benchmark (4.5 PSH, 79% PR) |
| **PM Surya Ghar Subsidy** | Applicable ONLY for `On-Grid` + `DCR` panels: <br>• $\le 2\text{ kW}$: ₹30,000/kW <br>• $2\text{ to }3\text{ kW}$: ₹60,000 + (kW - 2.0) $\times$ ₹18,000 <br>• $> 3\text{ kW}$: Fixed ₹78,000 cap | Ministry of New & Renewable Energy (MNRE) Notification 2024 |
| **TNEB LT-1A Slab Billing** | **Bi-monthly consumption $\le 500$ kWh**: <br>• First 200 units: ₹0.00 <br>• 201–400 units: ₹4.70/unit <br>• 401–500 units: ₹6.30/unit <br>**Bi-monthly consumption $> 500$ kWh**: <br>• First 100 units: ₹0.00 <br>• 101–400 units: ₹4.70/unit <br>• 401–500 units: ₹6.30/unit <br>• 501–600 units: ₹8.40/unit <br>• 601–800 units: ₹9.45/unit <br>• 801–1000 units: ₹10.50/unit <br>• $>1000$ units: ₹11.55/unit | TNERC Tariff Order for Domestic LT-1A |
| **Color Contrast (WCAG 2.2 AA)** | Normal text: $\ge 4.5:1$<br>Large text & UI components: $\ge 3.0:1$ | W3C Web Content Accessibility Guidelines (WCAG) 2.2 AA |
| **Touch Target Ergonomics** | Interactive touch targets $\ge 48 \times 48\text{ dp}$ | Android Material 3 Accessibility Guidelines |
| **Coin Reward Rules** | • Daily Activity: 1 coin <br>• 7-Day Streak Milestone: 5 coins <br>• New Bill Processed: 10 coins <br>• Verified Reduction: 5–9% (15 coins), 10–19% (30 coins), $\ge 20\%$ (50 coins) | SolarEase Citizen Reward Ledger Specification |
| **Statutory DPDP Disclosures** | Entity: SolarEase Energy Technologies Pvt Ltd <br>CIN: U40106TN2026PTC168420 <br>Grievance Officer: Priya Rangarajan <br>Statutory SLA: 48h acknowledgment / 30 days resolution | Digital Personal Data Protection Act 2023 § 8(9) & § 19 |

---

## 5. Feature Inventory Test Matrix (All 35 Features)

| Feature # | Feature Name | Primary Test File | Tier 1 Tests | Tier 2/3 Tests |
| :---: | :--- | :--- | :---: | :---: |
| 1 | Unified Semantic Color Tokens | `Tier1DesignSystemAndThemeTest` | 5 | Yes |
| 2 | Contrast Remediation & Dark Mode Fix | `Tier1DesignSystemAndThemeTest` | 5 | Yes |
| 3 | Theme Persistence & Mode Switcher | `Tier1DesignSystemAndThemeTest` | 5 | Yes |
| 4 | Startup Window Flash Elimination | `Tier1DesignSystemAndThemeTest` | 5 | Yes |
| 5 | Centralized Motion Tokens | `Tier1DesignSystemAndThemeTest` | 5 | Yes |
| 6 | AndroidManifest AppLink Lint Fix | `Tier1DesignSystemAndThemeTest` | 5 | Yes |
| 7 | SolarEaseButton Suite | `Tier1ComponentsAndNavigationTest` | 5 | Yes |
| 8 | SolarEaseCard & SelectableCard | `Tier1ComponentsAndNavigationTest` | 5 | Yes |
| 9 | SolarEaseNavigation (TopBar, BottomNav, NavRail) | `Tier1ComponentsAndNavigationTest` | 5 | Yes |
| 10 | Unified SolarEaseStepProgressBar | `Tier1ComponentsAndNavigationTest` | 5 | Yes |
| 11 | SolarEaseEmptyState & SolarEaseErrorState | `Tier1ComponentsAndNavigationTest` | 5 | Yes |
| 12 | SolarEaseMetricCard & MetricStrip | `Tier1ComponentsAndNavigationTest` | 5 | Yes |
| 13 | SolarEaseTimeline & SolarEaseFilterChipGroup | `Tier1ComponentsAndNavigationTest` | 5 | Yes |
| 14 | Legacy Component Deprecation | `Tier1ComponentsAndNavigationTest` | 5 | Yes |
| 15 | HomeScreen Modular Deconstruction | `Tier1CoreUtilityAndBillingTest` | 5 | Yes |
| 16 | TnebBillStatusScreen Token Unification | `Tier1CoreUtilityAndBillingTest` | 5 | Yes |
| 17 | BillCalculatorScreen Modularization | `Tier1CoreUtilityAndBillingTest` | 5 | Yes |
| 18 | ElectricityChatbotScreen Token Binding | `Tier1CoreUtilityAndBillingTest` | 5 | Yes |
| 19 | BillHistory & Consumption Trends Polish | `Tier1CoreUtilityAndBillingTest` | 5 | Yes |
| 20 | MeterScanner HUD & A11y Announcements | `Tier1CoreUtilityAndBillingTest` | 5 | Yes |
| 21 | 12-Step Guided Solar Assessment Journey | `Tier1SolarFunnelAndMarketplaceTest` | 5 | Yes |
| 22 | Contractor Marketplace Reconnection | `Tier1SolarFunnelAndMarketplaceTest` | 5 | Yes |
| 23 | Dead Legacy Screen Pruning | `Tier1SolarFunnelAndMarketplaceTest` | 5 | Yes |
| 24 | CustomerDashboard Re-Integration | `Tier1SolarFunnelAndMarketplaceTest` | 5 | Yes |
| 25 | Accessible Canvas Data Visualizations | `Tier1SolarFunnelAndMarketplaceTest` | 5 | Yes |
| 26 | Action Wire-Up & Inverter Telemetry Link | `Tier1SolarFunnelAndMarketplaceTest` | 5 | Yes |
| 27 | Solar Lens Landing & Review Modularization | `Tier1LensRecommendsAndComplianceTest` | 5 | Yes |
| 28 | RecommendsScreen Modularization | `Tier1LensRecommendsAndComplianceTest` | 5 | Yes |
| 29 | ProfileScreen 8-Sheet Modular Deconstruction | `Tier1LensRecommendsAndComplianceTest` | 5 | Yes |
| 30 | Modernized Auth Flows | `Tier1LensRecommendsAndComplianceTest` | 5 | Yes |
| 31 | Statutory DPDP Act & Legal Dialog Polish | `Tier1LensRecommendsAndComplianceTest` | 5 | Yes |
| 32 | Adaptive Layouts (Phone, Tablet, Desktop) | `Tier1LensRecommendsAndComplianceTest` | 5 | Yes |
| 33 | Global Motion Reduced-Motion Enforcement | `Tier1LensRecommendsAndComplianceTest` | 5 | Yes |
| 34 | E2E Test Suite Validation (Tiers 1–4) | `Tier1LensRecommendsAndComplianceTest` | 5 | Yes |
| 35 | Adversarial Hardening & Verification | `Tier1LensRecommendsAndComplianceTest` | 5 | Yes |
| — | Boundary & Corner Cases (Tier 2) | `Tier2BoundaryAndCornerTest` | — | 35+ |
| — | Cross-Feature Interactions (Tier 3) | `Tier3CrossFeatureCombinationsTest` | — | 12+ |
| — | Real-World Lifecycles (Tier 4) | `Tier4RealWorldScenariosTest` | — | 5+ |
