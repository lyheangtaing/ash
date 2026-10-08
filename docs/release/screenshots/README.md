# Actual store screenshots

For Ash's supported devices, the standard minimum screenshot set is two Android
screenshots, one iPhone screenshot, and one iPad screenshot. Icons, Google Play's
feature graphic and optional videos are separate listing assets.

Android captures already exist in `android/`. The screenshot workflow builds the
existing iOS simulator app on an arm64 macOS runner and captures an iPhone Pro Max
and a 13-inch iPad Pro. It uses synthetic Demo/$49 data, with the existing Ash
icon as its selected photo, matching the Android collection capture.

Run `python3 scripts/capture-ios-screenshots.py` on a configured arm64 Mac with
Xcode, a supported iOS simulator runtime, Java 21 and the project's Android SDK.
The workflow does not need Apple signing credentials. It creates and deletes
only its fresh capture simulators, and packages four unmodified native PNGs plus
capture metadata. macOS Vision OCR checks that Collection, Demo and its value
actually rendered before packaging. It does not resize images or generate app UI.

The dedicated screenshot branch triggers this workflow. Successful push runs
attach a ZIP and its checksum to the existing `v1.0.0-rc.3` GitHub prerelease with
unique run-numbered names. The binary assets remain attached to their original
release. Manual runs capture artifacts without publishing them.

Check the current screenshot display slots in Play Console/App Store Connect
before uploading. Inspect all actual captures; native dimensions alone do not
establish store approval.

## Verified captures

[Capture run 37741966224](https://github.com/lyheangtaing/ash/actions/runs/37741966224)
passed on 2026-10-08. The downloaded ZIP checksum and integrity were verified;
all four PNGs are fully opaque and were visually inspected.

- Android Collection and Privacy: 480 x 800 each.
- iPhone 17 Pro Max, iOS 26.5: 1320 x 2868.
- iPad Pro 13-inch (M5), iOS 26.5: 2064 x 2752.

The iOS PNGs and capture metadata are saved under `ios/`.
[Download all four actual screenshots](https://github.com/lyheangtaing/ash/releases/download/v1.0.0-rc.3/ash-store-screenshots-37741966224-1.zip).
