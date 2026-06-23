#!/usr/bin/env bash

set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_DIR="$DIR"
VERSION=$(grep '^VERSION_NAME=' "$PROJECT_DIR/gradle.properties" | cut -d'=' -f2)

export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"

echo "=== Gainful Release Build ==="
echo "PROJECT_DIR=$PROJECT_DIR"
echo "VERSION=$VERSION"
echo "ANDROID_HOME=$ANDROID_HOME"

# --- Sync iOS version ---
echo ""
echo ">>> Syncing iOS version..."
sed -i '' "s/MARKETING_VERSION=.*/MARKETING_VERSION=${VERSION}/" "$PROJECT_DIR/iosApp/Configuration/Config.xcconfig"

# --- Android Release ---
echo ""
echo ">>> Building Android release APK..."
cd "$PROJECT_DIR"
./gradlew :androidApp:assembleRelease --no-configuration-cache

APK_DIR="$PROJECT_DIR/androidApp/build/outputs/apk/release"
if [ -d "$APK_DIR" ]; then
    for f in "$APK_DIR"/*.apk; do
        mv "$f" "${f%.apk}-v${VERSION}.apk"
    done
fi

# --- Desktop Release ---
echo ""
echo ">>> Building Desktop release (with ProGuard obfuscation)..."
cd "$PROJECT_DIR"
./gradlew :desktopApp:packageReleaseDistributionForCurrentOS --no-configuration-cache

DESKTOP_DIR="$PROJECT_DIR/desktopApp/build/compose/binaries"
for ext in dmg msi deb; do
    find "$DESKTOP_DIR" -type f -name "*.${ext}" | while read f; do
        dir=$(dirname "$f")
        base=$(basename "$f" ".${ext}")
        newBase=$(echo "$base" | sed -E 's/[- ]?[0-9]+(\.[0-9]+)*$//')
        mv "$f" "$dir/${newBase}-v${VERSION}.${ext}"
    done
done

# --- Summary ---
echo ""
echo "=== Build Complete ==="
echo "Android APK:"
ls -lh androidApp/build/outputs/apk/release/*.apk 2>/dev/null
echo ""
echo "Desktop:"
find desktopApp/build/compose/binaries -type f \( -name "*.dmg" -o -name "*.msi" -o -name "*.deb" -o -name "*.exe" \) -exec ls -lh {} \;
