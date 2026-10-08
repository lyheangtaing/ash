# Store preparation implementation plan

> **For agentic workers:** Use superpowers:executing-plans to implement these tasks inline.

**Goal:** Prepare Ash's existing Android and iOS app for store submission and a GitHub prerelease, without claiming store readiness or unexecuted checks.

**Architecture:** Retain the existing Kotlin Multiplatform app, identifiers and local storage. Add shared offline privacy information to Settings, repair Android release checks, and add reproducible platform release workflows and submission documentation.

**Tech Stack:** Kotlin 2.2.20, Compose 1.9.1, Gradle 8.14.3, Android API 24–36, iOS 17.2+, Xcode.

**Spec:** User-approved scope in this chat: prepare what is possible for both stores and push using good Git practice.

## Global constraints
- Keep Ash and com.lyheang.ash.
- No production signing passwords/keys or invented contact details. GitHub prerelease publication is authorized; store submission is not.
- Existing isolated cloud checkout; no new worktree.
- Do not suppress lint errors or imply iOS compilation has run on Linux.
- Reminders are an in-app checklist, not scheduled OS notifications.

## Review focus
- Android 24–27 image decoding uses the existing BitmapFactory fallback.
- Theme attributes introduced at API 27 are confined to versioned resources.
- Privacy text must distinguish app collection from OS backup and copied-photo retention.
- Release signing checks must fail on missing credentials.
- iOS must retain its UserDefaults privacy manifest and photo-picker access without unnecessary library permission.

### Task 1: Android release checks and shared privacy
**Files:** Android image picker and themes; shared SettingsScreen; new shared PrivacyPolicy model and existing common tests; Android backup rules.
**Interfaces:** PrivacyPolicy.sections supplies text rendered by Settings on both platforms.
- [x] Preserve the failing lint baseline; verify privacy text against implemented storage and backup behavior.
- [x] Fix SDK annotations and remove redundant picker state wrapping; version the navigation-bar theme attribute.
- [x] Add explicit Android backup exclusions and inline shared policy information.
- [x] Run common tests and release lint; verify zero errors.
- [x] Commit the independently validated app fixes.

### Task 2: Release tooling and store materials
**Files:** scripts/check-release.sh; .github/workflows/release-checks.yml; iOS shared scheme; docs/release/README.md, privacy-policy.md and store-listing.md; README.md.
**Interfaces:** Release commands consume existing Gradle tasks and iosApp target; no account secrets are consumed by CI.
- [x] Add repeatable Android validation and unsigned device iOS compile checks.
- [x] Add a shared Xcode scheme with Release archive and no signing identity.
- [x] Document secure signing, real feature limitations, listing text, console declarations and external account requirements.
- [x] Validate shell, workflow structure, plist/scheme XML and metadata; run the Android release script.
- [x] Capture actual Android screenshots; label them Android only.
- [x] Commit the release tooling and materials.

### Task 3: Whole-branch validation and push
**Files:** Existing test/build outputs outside tracked source; release documentation.
- [x] Run optimized Android release build, lint and shared tests; inspect test counts and bundle.
- [x] Install a locally test-signed optimized APK on the emulator; exercise policy access and representative asset/photo persistence.
- [x] Request one independent whole-branch review and resolve important findings.
- [x] Check diff, absence of secrets/generated binaries, and clean status.
- [x] Push release/store-preparation without force or changes to main; verify remote SHA.

### Task 4: User-requested GitHub prerelease
- [x] Package a test-signed optimized Android APK, unsigned AAB, mapping, and unsigned iOS device app.
- [x] Gate publication on both platform builds and pushed candidate tags; no production signing keys.
- [x] Review publication permissions and fix workflow-dispatch gating.
- [x] Publish v1.0.0-rc.3 after correcting CI prerequisites; verify both builds and all primary release downloads. Preserve earlier tags.

Both platform jobs and publication passed. See docs/release/validation.md for the run, release URL and downloaded artifact checks.
