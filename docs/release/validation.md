# Validation evidence

Local checks on 2026-10-08, in the Linux cloud checkout:

- `bash scripts/test-release-signing.sh :composeApp:assembleRelease` passed.
  This runs release lint, the existing shared unit tests and an optimized unsigned
  bundle build with a temporary signing key configured. The bundle remained
  unsigned, and the distribution task correctly rejected unsigned-check mode.
- All 9 shared tests passed, with zero failures. Release lint reported zero
  errors and 30 warnings; warnings are not suppressed.
- The optimized APK and unsigned bundle built successfully. Bundle ZIP integrity
  and absence of signature blocks were checked. APK zip alignment and packaged
  native-library 16 KB alignment were inspected.
- A public-debug-key-signed optimized APK ran on an Android 29 x86_64 software
  emulator. The privacy policy expanded. A selected image, asset name `Demo`,
  and purchase value $49 were saved; the collection, value and photo remained
  visible after force-stopping and restarting the app.
- Shell syntax, workflow candidate/publication gates, Info.plist, privacy manifest,
  shared Xcode scheme XML, and the opaque 1024-pixel iOS icon were checked.

The screenshots in `screenshots/android/` are actual emulator captures.
`03-demo-collection.png` uses synthetic example data and the existing Ash icon
as its selected image. These are Android captures, not iOS store screenshots.

Linux cannot compile or sign iOS. Native iOS compilation is delegated to the
macOS GitHub Actions job; inspect the candidate-tag run before claiming it passes.
No iOS device testing, signed archive validation, TestFlight or store submission
has been performed locally. Android 24–27, current-target device behavior,
actual 16 KB-page device testing, and the remaining functional/accessibility
checks in the release guide still need device verification.

The GitHub prerelease publishes only when both platform jobs succeed. An
unsigned iOS app ZIP is a developer artifact and cannot be installed as-is.
