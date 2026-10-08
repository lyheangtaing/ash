# Ash release preparation

This prerelease is for testing and developer review before store signing.
It is published only after the Android release checks and the unsigned iOS
device build pass in GitHub Actions.

## Downloads

- **ash-android-test.apk**: optimized Android app signed with a public debug key
  for testing. It is not a Google Play upload. Debug keys can differ between
  build machines; installing over another test build may require uninstalling
  that build first, which deletes its app data.
- **ash-android-unsigned.aab**: optimized unsigned Android App Bundle. Configure
  your real upload key and run `bundleStoreRelease` before a Play upload.
- **ash-android-mapping.zip**: R8 mapping for diagnosing this build.
- **ash-ios-unsigned-device-build.zip**: compiled iOS device app for developers.
  It is unsigned and cannot be installed on an iPhone or uploaded to App Store
  Connect as-is. Use Xcode and your Apple Developer signing configuration to
  produce a validated archive.
- **SHA256SUMS**: checksums of the attached artifacts.

The source archives include both platform projects. Read
`docs/release/README.md` for signing, privacy-policy publication, store listing
drafts and the remaining device tests. No production signing keys are included.
No Play Store or App Store submission is performed by this release.
