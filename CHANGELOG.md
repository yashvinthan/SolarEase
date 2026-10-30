# Changelog

All notable changes to the SolarEase Android project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- Enterprise-grade CI/CD pipeline definition (`.github/workflows/android-ci.yml`) running on OpenJDK 21 (Temurin) with `assembleDebug`, `testDebugUnitTest`, `lintDebug`, `assembleRelease`, and `bundleRelease`.
- In-app interactive 6-tab Legal & Compliance Viewer (`LegalComplianceDialog.kt`) covering Privacy Policy (DPDP Act 2023), Terms of Service, Refund & Cancellation Policy, Cookie & Local Storage Policy, AI & Subprocessors Disclosure, and Corporate & Grievance Redressal Officer details.
- Self-service in-app **"Delete Account & Erase Personal Data"** workflow in `ProfileViewModel` and `ProfileScreen` (`PrivacySecuritySheet`), fulfilling Google Play Account Deletion requirements and India DPDP Act 2023 § 12(3) Right to Erasure.
- Explicit DPDP Act 2023 consent notice and interactive legal links on `AuthMainScreen`, `PaymentOptionScreen`, `TnebBillStatusScreen`, and `LensLandingScreen`.
- Repository hygiene configurations: `.editorconfig`, `.gitattributes`, `lint.xml`, `keystore.properties.example`.
- Architecture and system design documentation (`docs/ARCHITECTURE.md`).

### Changed
- Upgraded Java/Kotlin compilation target to JVM 17 with `-Xannotation-default-target=param-property` and configured CI for JDK 21 compatibility with Gradle 9.6.1 and AGP 9.3.2.
- Standardized release signing in `app/build.gradle.kts` to read from `keystore.properties` or CI environment variables without falling back to debug keys.
- Improved WCAG 2.1 AA color contrast in `LensColors` (`SlateCaption` and `SlateMuted` darkened to `#475569`, achieving 6.8:1 contrast ratio) and fixed TalkBack `contentDescription` and `ImeAction.Done` keyboard navigation on the account-linking password field in `AuthMainScreen`.
- Added transparent GST/net-metering fee and PM Surya Ghar computational estimate disclaimers in `CostEstimationScreen` and BIS IS 19000:2022 illustrative feedback disclosures in `VendorProfileScreen`.
- Replaced all `[PLACEHOLDER]` markers across `docs/legal/PRIVACY_POLICY.md`, `docs/legal/TERMS_AND_CONDITIONS.md`, and `docs/LEGAL_AND_COMPLIANCE_SUITE.md` with authoritative corporate registration, CIN, DPO/Grievance Officer, and SLA details.

### Security
- Added R8 `-assumenosideeffects` rules in `app/proguard-rules.pro` to strip `Log.d` and `Log.v` calls from release binaries.
- Migrated all remaining `SecurePreferencesHelper` references in `UserSession` and `RewardStorage` to `SecureSessionPreferences` (`EncryptedSharedPreferences` with `AES256_GCM`).
- Hardened `.gitignore` to exclude signing keys, editor caches, `local.properties`, and `lancedb/` directories.

## [2.0.0] - 2026-10-28
### Added
- Offline meter reading scanner with edge LCD detection and reticle cropping.
- Dynamic TNEB bill calculator and tariff estimation engine.
- SolarLens multi-photo appliance audit report generation.
- Full bilingual Q&A retrieval chatbot for electricity advisory.

## [1.0.0] - 2026-08-15
### Added
- Initial release with consumer number lookup, customer category mapping, and baseline quote flow.

