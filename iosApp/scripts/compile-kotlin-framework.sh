#!/bin/sh

set -eu

if [ "${OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED:-NO}" = "YES" ]; then
  echo "Skipping Gradle build because OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED is YES."
  exit 0
fi

find_java_home() {
  if [ -n "${ASH_JAVA_HOME:-}" ] && [ -x "$ASH_JAVA_HOME/bin/java" ]; then
    printf '%s\n' "$ASH_JAVA_HOME"
    return 0
  fi

  for version in 21 17; do
    candidate=$(/usr/libexec/java_home -v "$version" 2>/dev/null || true)
    if [ -n "$candidate" ] && [ -x "$candidate/bin/java" ]; then
      printf '%s\n' "$candidate"
      return 0
    fi
  done

  return 1
}

JAVA_HOME=$(find_java_home || true)
if [ -z "$JAVA_HOME" ]; then
  echo "error: Ash requires JDK 21 or JDK 17 for the Kotlin framework build." >&2
  echo "error: Install a supported JDK, or set ASH_JAVA_HOME in the Xcode scheme environment." >&2
  exit 1
fi

export JAVA_HOME
export PATH="$JAVA_HOME/bin:$PATH"

echo "Using Java from $JAVA_HOME"
"$JAVA_HOME/bin/java" -version

cd "$SRCROOT/.."
exec ./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
