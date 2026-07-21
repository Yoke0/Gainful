#!/usr/bin/env bash

set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_DIR="$DIR"
VERSION=$(grep '^VERSION_NAME=' "$PROJECT_DIR/gradle.properties" | cut -d'=' -f2)

echo "=== Gainful Release Build ==="
echo "VERSION=$VERSION"

# --- Android Release ---
echo ""
echo ">>> Building Android release APK..."
cd "$PROJECT_DIR"
./gradlew :androidApp:assembleRelease

APK_DIR="$PROJECT_DIR/androidApp/build/outputs/apk/release"
APK=$(find "$APK_DIR" -name "*.apk" | head -1)

if [ -z "$APK" ]; then
    echo "ERROR: No APK found in $APK_DIR"
    exit 1
fi

# --- Rename APK ---
SIGNED_APK="$APK_DIR/Gainful-v${VERSION}.apk"
mv "$APK" "$SIGNED_APK"
echo ">>> Renamed to: Gainful-v${VERSION}.apk"

# --- Summary ---
echo ""
echo "=== Build Complete ==="
ls -lh "$APK_DIR"/*.apk 2>/dev/null
