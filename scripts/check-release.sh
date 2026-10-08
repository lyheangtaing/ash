#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."
if [[ -n "${JAVA_HOME:-}" ]]; then
    export PATH="$JAVA_HOME/bin:$PATH"
fi
if ! command -v javac >/dev/null 2>&1; then
    echo "A complete JDK 17 or 21 is required; set JAVA_HOME." >&2
    exit 1
fi

# This validates an unsigned bundle. Distribution uses bundleStoreRelease.
exec ./gradlew --max-workers="${ASH_GRADLE_WORKERS:-3}" \
    -Pash.unsignedRelease=true \
    :composeApp:lintRelease :composeApp:testDebugUnitTest \
    :composeApp:bundleRelease "$@"
