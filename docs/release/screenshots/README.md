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
