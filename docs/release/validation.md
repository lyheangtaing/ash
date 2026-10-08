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
  visible after force-stopping and restarting the app. Cancelling the photo
  picker returned to the empty form; cancelling that form preserved the existing
  collection without adding another asset.
- Shell syntax, workflow candidate/publication gates, Info.plist, privacy manifest,
  shared Xcode scheme XML, and the opaque 1024-pixel iOS icon were checked.

The screenshots in `screenshots/android/` are actual emulator captures.
`03-demo-collection.png` uses synthetic example data and the existing Ash icon
as its selected image. These are Android captures, not iOS store screenshots.

## GitHub candidate verification

[GitHub Actions run 37728039114](https://github.com/lyheangtaing/ash/actions/runs/37728039114)
passed for candidate `v1.0.0-rc.3` at source commit
`cbb530cdaaaec7a2d2f62af1020c45383fa15951`:

- Android release checks, signing regression and artifact packaging passed.
- The macOS runner compiled and packaged the unsigned iOS Release device app.
- The publication job succeeded. The
  [GitHub prerelease](https://github.com/lyheangtaing/ash/releases/tag/v1.0.0-rc.3)
  is publicly available.
- All four primary downloads were downloaded and matched `SHA256SUMS`.
  The APK signature verified as Android Debug; the AAB had no signing blocks.
- The iOS ZIP contained an arm64 Mach-O executable, the privacy manifest, and
  bundle ID `com.lyheang.ash`, version `1.0.0`, minimum iOS `17.2`. Its app
  contained no `_CodeSignature` directory. ZIP integrity checks passed.

Earlier candidate tags were preserved after CI prerequisite failures; no releases
were published for those failed candidates. Linux cannot compile or sign iOS.
No iOS device testing, signed archive validation, TestFlight or store submission
has been performed locally. Android 24–27, current-target device behavior,
actual 16 KB-page device testing, and the remaining functional/accessibility
checks in the release guide still need device verification.

The GitHub prerelease publishes only when both platform jobs succeed. An
unsigned iOS app ZIP is a developer artifact and cannot be installed as-is.
