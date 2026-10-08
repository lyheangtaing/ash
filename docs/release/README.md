# Release guide

Ash keeps its current identifiers: Android application ID and iOS bundle ID
`com.lyheang.ash`. Android supports API 24+ and targets API 36; iOS supports
17.2+. Confirm these identifiers before the first store upload. Existing listings
must keep their identifiers, use the correct signing identity, and increment
their build numbers.

## What these changes prepare

- Android API compatibility fixes, explicit backup exclusions, release lint,
  shared tests, and optimized unsigned builds.
- An offline privacy policy accessible from Settings on Android and iOS.
- A shared Xcode scheme with a Release archive action.
- GitHub Actions checks for Android and an unsigned iOS device build.
- Store copy and a privacy policy draft.

The Linux cloud environment cannot run Xcode, produce an iOS archive, or validate
Apple signing. An unsigned bundle is not ready to upload. CI is verification,
not deployment: it contains no publishing step and requires no signing secrets.

## Android checks

Use a complete JDK 17 or 21, the committed Gradle wrapper, Android platform 36,
and build-tools 35.0.0. Set `JAVA_HOME` and `ANDROID_HOME`, or use Android
Studio's ignored `local.properties` for the SDK location.

```sh
bash scripts/check-release.sh
```

This must pass release lint and execute the existing shared tests before building
an unsigned bundle. Reports are in `composeApp/build/reports/`; the bundle is
`composeApp/build/outputs/bundle/release/composeApp-release.aab`. Preserve the
matching `composeApp/build/outputs/mapping/release/` output for crash diagnosis.
The CI artifact explicitly labels its bundle unsigned.

### Distribution signing

Create or reuse your Google Play **upload key** on a trusted machine and retain an
encrypted backup outside Git. Use Play App Signing; an upload key and Google's
app-signing key have different roles. If this is an update, follow the signing
configuration already registered in Play Console. Do not generate a replacement
key merely because this checkout has none.

Copy `release.properties.example` to the ignored `release.properties`, then
supply the real keystore path, alias, passwords, `VERSION_CODE`, and
`VERSION_NAME` locally. Alternatively, bind the existing `ASH_ANDROID_*` and
`ASH_VERSION_*` environment variables in a trusted signing environment.
The properties file takes precedence over those variables. Do not put credentials
in shell command arguments, commits, issue comments, or chat.

```sh
./gradlew :composeApp:bundleStoreRelease
```

This task checks that signing settings and the keystore exist, then builds the
signed, optimized bundle. Inspect the signing result and validate the bundle in
Play Console's internal testing track before promoting it.

## iOS checks on a Mac

Install Xcode supporting this project's synchronized-folder format (Xcode 16+
for the project format), **and use the Xcode/iOS SDK version currently required
for submission by App Store Connect**. Check Apple's current submission
requirements; project compatibility alone does not establish store eligibility.
Install JDK 17 or 21 and set `JAVA_HOME` or `ASH_JAVA_HOME`.

```sh
bash scripts/check-ios-release.sh
```

This compiles a Release device app and the arm64 Kotlin framework without signing.
It does not execute iOS tests or validate an archive. The CI uses this same check
on a macOS runner. Its status must be read before claiming iOS compilation passes.

Open `iosApp/iosApp.xcodeproj`, select the shared `iosApp` scheme, and choose
an Apple Developer team. The public `TEAM_ID` setting in
`iosApp/Configuration/Config.xcconfig` is currently empty; it is not a signing
certificate or password. You may supply `TEAM_ID` as an Xcode build setting
instead of changing shared configuration. Increment `CURRENT_PROJECT_VERSION`
for each uploaded build; set `MARKETING_VERSION` for the release.

For a signed archive on a configured Mac:

```sh
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -configuration Release -destination 'generic/platform=iOS' \
  -archivePath iosApp/build/Ash.xcarchive \
  TEAM_ID="$ASH_APPLE_TEAM_ID" archive
```

Use Xcode Organizer to Validate App and Distribute App to App Store Connect.
Certificates, provisioning profiles and account authentication remain on the Mac.
Verify the archived app includes `PrivacyInfo.xcprivacy`, audit dependency
privacy manifests and required-reason API use, and confirm the encryption
declaration matches the final build. The existing UserDefaults reason is
`CA92.1`. The system photo picker is used; do not add broad photo-library or
camera permissions without implementing a feature that needs them.

The project supports iPhone and iPad, including landscape. Test and supply store
screenshots for the required device families. Android screenshots are not iOS
screenshots.

## Functional release checklist

Run this against the **optimized** Android build and an actual iOS build:

- [ ] Add an asset with a selected photo, name, and purchase value.
- [ ] Cancel photo selection without creating a broken asset.
- [ ] Pick multiple photos; handle unavailable documents and large images.
- [ ] Search, filter, edit and delete an asset.
- [ ] Add a maintenance record and complete a reminder.
- [ ] Restart the app and confirm records and copied photos remain readable.
- [ ] Open Settings and expand/read/collapse the privacy policy.
- [ ] Check Android 24–27 photo decoding, current target-API behavior,
  navigation/back, edge-to-edge layouts, and a 16 KB-page Android device.
- [ ] Check iPhone and iPad, landscape, dark mode, accessibility/text scaling,
  system back/navigation and the photo picker.
- [ ] Verify deletion/offloading/backup behavior matches the privacy policy.

Reminders are an in-app checklist. The app does **not** schedule system
notifications. Copied photos are not automatically removed when an asset record
is deleted. Treat both as current behavior, not promised functionality.

## Store account work still required

- Publisher/developer identity and a public support contact.
- A public, maintained privacy-policy URL; finalize and host
  [the policy draft](privacy-policy.md), and keep its content consistent with the app.
- Store-specific listing copy and real screenshots; see
  [store listing drafts](store-listing.md).
- Android upload signing and Play App Signing enrollment; Apple Developer
  membership, bundle registration, signing, and App Store Connect access.
- Data safety/App Privacy declarations, age/content ratings, audience,
  advertising and app-access questions based on the shipped build.
- Google Play internal testing and any account-specific closed-testing or
  production-access requirements shown in Play Console.
- TestFlight validation and App Store Connect archive validation.
- Submission and store review. A passing build does not guarantee approval.

Check the current requirements in the store consoles and their official
documentation at submission time; account and policy requirements can change.
