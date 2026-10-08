#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."
if [[ -n "${JAVA_HOME:-}" ]]; then
    export PATH="$JAVA_HOME/bin:$PATH"
fi
fixture_dir=$(mktemp -d)
trap 'rm -rf "$fixture_dir"' EXIT
export ASH_TEST_PASSWORD
ASH_TEST_PASSWORD=$(python3 -c 'import secrets; print(secrets.token_hex(16))')
keytool -genkeypair -noprompt -alias release-check-fixture -keyalg RSA \
    -keysize 2048 -validity 1 -dname "CN=Release Check Fixture" \
    -storetype PKCS12 -keystore "$fixture_dir/upload.p12" \
    -storepass:env ASH_TEST_PASSWORD -keypass:env ASH_TEST_PASSWORD \
    > "$fixture_dir/keytool.log" 2>&1
export ASH_ANDROID_KEYSTORE_PATH="$fixture_dir/upload.p12"
export ASH_ANDROID_KEY_ALIAS=release-check-fixture
export ASH_ANDROID_STORE_PASSWORD="$ASH_TEST_PASSWORD"
export ASH_ANDROID_KEY_PASSWORD="$ASH_TEST_PASSWORD"

# The check must remain unsigned even with valid signing inputs present.
bash scripts/check-release.sh "$@"
python3 - <<'PY'
from zipfile import ZipFile
with ZipFile("composeApp/build/outputs/bundle/release/composeApp-release.aab") as bundle:
    signatures = [n for n in bundle.namelist()
                  if n.startswith("META-INF/") and n.endswith((".RSA", ".EC", ".DSA"))]
    assert not signatures, "Release validation unexpectedly signed the bundle"
print("PASS: release validation remains unsigned with a signing key configured")
PY

if ./gradlew --max-workers="${ASH_GRADLE_WORKERS:-3}" \
    -Pash.unsignedRelease=true :composeApp:verifyStoreRelease \
    > "$fixture_dir/distribution.log" 2>&1; then
    echo "FAIL: distribution accepted unsigned validation mode" >&2
    exit 1
fi
if ! python3 - "$fixture_dir/distribution.log" <<'PY'
import pathlib, sys
assert "Unsigned validation cannot build a store-signed bundle" in pathlib.Path(sys.argv[1]).read_text()
PY
then
    cat "$fixture_dir/distribution.log" >&2
    exit 1
fi
echo "PASS: distribution rejects unsigned validation mode"
