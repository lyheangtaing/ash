# Store Screenshot Capture Implementation Plan

> **For agentic workers:** Use superpowers:executing-plans to implement inline.

**Goal:** Deliver two genuine Android screenshots plus one genuine iPhone and one genuine iPad screenshot, without generated layouts.

**Architecture:** Copy the existing Android emulator captures. Build the existing iOS app for arm64 simulators on a macOS GitHub runner; create fresh iPhone Pro Max and 13-inch iPad Pro simulators, seed synthetic local data and capture their rendered screens. Package the four images for download with capture metadata.

**Tech Stack:** Python 3 standard library, Xcode, simctl, Kotlin Multiplatform, GitHub Actions.

**Spec:** User clarified in this chat: minimum screenshots only, make them; prior code push and GitHub release authorization persists.

## Global constraints
- Keep com.lyheang.ash and current app behavior.
- Use actual app rendering; no generated or resized screenshots.
- Use synthetic Demo/$49 data and the existing app icon as its selected image.
- No Apple signing credentials; simulator build only.
- Use the existing isolated cloud checkout and a screenshot feature branch.

## Review focus
- Reject unsupported runner architectures and missing available iOS runtimes/models.
- Create/delete only fresh simulators belonging to this capture process.
- Ensure app fixture matches existing serialized storage and photo URI scheme.
- Reject unexpected native dimensions; retain actual device/runtime/build metadata.
- Publish the screenshot ZIP only after both iOS captures and two Android inputs exist; preserve existing binary release assets.

### Task 1: Native capture and packaging
**Files:** scripts/capture-ios-screenshots.py; scripts/verify-store-screenshot.swift; .github/workflows/store-screenshots.yml; docs/release/screenshots/README.md.
**Interfaces:** Python capture script produces build/store-screenshots/{google-play,app-store} PNG files, capture.json, README.md and ash-store-screenshots.zip.
- [x] Implement macOS/arm64 guards, simulator build, fresh device selection, fixture installation, capture and cleanup.
- [x] Validate syntax, workflow expressions and fixture schema against the existing app; independently review capture/publication scope.
- [ ] Push screenshot feature branch and observe the macOS run.

### Task 2: Artifact verification and delivery
**Files:** Actual captures downloaded to docs/release/screenshots/ios/; validation metadata outside tracked source.
- [ ] Download the published ZIP and verify integrity, four PNGs, native dimensions and actual rendered screens.
- [ ] Save iPhone/iPad captures and capture evidence in the repository; push documentation/assets.
- [ ] Deliver all four actual screenshots and the download link, with any console-size limitations stated accurately.

Independent review found no blocking issues. Native execution and visual inspection remain pending.
