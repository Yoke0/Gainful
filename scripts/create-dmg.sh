#!/bin/bash
set -e

APP_PATH="$1"
DMG_PATH="$2"

TMPDIR=$(mktemp -d)
cp -R "$APP_PATH" "$TMPDIR/"
ln -s /Applications "$TMPDIR/Applications"

rm -f "$DMG_PATH"
hdiutil create -volname "Gainful" -srcfolder "$TMPDIR" -ov -format UDZO "$DMG_PATH"

rm -rf "$TMPDIR"
echo "DMG created: $DMG_PATH"
