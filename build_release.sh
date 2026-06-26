#!/usr/bin/env bash

set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_DIR="$DIR"
VERSION=$(grep '^VERSION_NAME=' "$PROJECT_DIR/gradle.properties" | cut -d'=' -f2)

export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
APKSIGNER="$ANDROID_HOME/build-tools/$(ls "$ANDROID_HOME/build-tools" | sort -V | tail -1)/apksigner"

echo "=== Gainful Release Build ==="
echo "PROJECT_DIR=$PROJECT_DIR"
echo "VERSION=$VERSION"
echo "ANDROID_HOME=$ANDROID_HOME"

# --- Android Release (unsigned) ---
echo ""
echo ">>> Building Android release APK (unsigned)..."
cd "$PROJECT_DIR"
./gradlew :androidApp:assembleRelease --no-configuration-cache

APK_DIR="$PROJECT_DIR/androidApp/build/outputs/apk/release"
UNSIGNED_APK=$(find "$APK_DIR" -name "*.apk" | head -1)

if [ -z "$UNSIGNED_APK" ]; then
    echo "ERROR: No APK found in $APK_DIR"
    exit 1
fi

echo ">>> Unsigned APK: $UNSIGNED_APK"

# --- Sign APK ---
SIGNING_PROPS_FILE="$PROJECT_DIR/local.properties"
if [ -f "$SIGNING_PROPS_FILE" ]; then
    STORE_FILE=$(grep '^RELEASE_STORE_FILE=' "$SIGNING_PROPS_FILE" | cut -d'=' -f2)
    STORE_PWD=$(grep '^RELEASE_STORE_PASSWORD=' "$SIGNING_PROPS_FILE" | cut -d'=' -f2)
    KEY_ALIAS=$(grep '^RELEASE_KEY_ALIAS=' "$SIGNING_PROPS_FILE" | cut -d'=' -f2)
    KEY_PWD=$(grep '^RELEASE_KEY_PASSWORD=' "$SIGNING_PROPS_FILE" | cut -d'=' -f2)
fi

if [ -n "$STORE_FILE" ] && [ -f "$STORE_FILE" ]; then
    SIGNED_APK="$APK_DIR/app-release-v${VERSION}.apk"

    echo ">>> Signing APK..."
    "$APKSIGNER" sign \
        --ks "$STORE_FILE" \
        --ks-pass "pass:$STORE_PWD" \
        --ks-key-alias "$KEY_ALIAS" \
        --key-pass "pass:$KEY_PWD" \
        --out "$SIGNED_APK" \
        "$UNSIGNED_APK"

    echo ">>> Verifying signature..."
    "$APKSIGNER" verify "$SIGNED_APK"

    rm -f "$UNSIGNED_APK"

    echo ">>> Signed APK: $SIGNED_APK"
else
    echo ">>> WARNING: No signing config found. Release APK is unsigned."
    mv "$UNSIGNED_APK" "$APK_DIR/app-release-v${VERSION}-unsigned.apk"
fi

# --- Summary ---
echo ""
echo "=== Build Complete ==="
ls -lh "$APK_DIR"/*.apk 2>/dev/null
