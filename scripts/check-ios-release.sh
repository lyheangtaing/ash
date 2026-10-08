#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."
if [[ "$(uname -s)" != "Darwin" ]]; then
    echo "The iOS release check requires macOS and Xcode." >&2
    exit 1
fi
if [[ -n "${JAVA_HOME:-}" ]]; then
    export ASH_JAVA_HOME="$JAVA_HOME"
fi

xcodebuild -version
# An unsigned device build checks both Swift and the arm64 Kotlin framework.
# It does not validate distribution signing or create an App Store archive.
exec xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
    -configuration Release -sdk iphoneos -destination 'generic/platform=iOS' \
    -derivedDataPath iosApp/build/DerivedData CODE_SIGNING_ALLOWED=NO build "$@"
