#!/data/data/com.termux/files/usr/bin/bash
# Fix 2: allow the Google sign-in libraries (they need Android 5+) in the app's own manifest
set -e
[ -f settings.gradle ] || { echo "Run this inside the project folder (where settings.gradle is)"; exit 1; }
M=application/app/src/main/AndroidManifest.xml
[ -f "$M" ] || { echo "Missing $M"; exit 1; }
if ! grep -q "overrideLibrary" "$M"; then
  sed -i 's|android:installLocation="auto">|android:installLocation="auto">\n\n    <uses-sdk tools:overrideLibrary="androidx.credentials, androidx.credentials.play.services.auth, com.google.android.libraries.identity.googleid" />|' "$M"
fi
grep -n "overrideLibrary" "$M"
echo "Done. Next: git add -A && git commit -m 'allow credentials libs' && git push origin main"
