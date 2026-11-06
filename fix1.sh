#!/data/data/com.termux/files/usr/bin/bash
# Fix 1: let the Google sign-in library work with the app's minimum Android version,
# and make the build save a short error summary you can share.
set -e
[ -f settings.gradle ] || { echo "Run this inside the project folder (where settings.gradle is)"; exit 1; }
M=application/vlc-android/AndroidManifest.xml
if ! grep -q "androidx.credentials" $M; then
  sed -i 's|tools:overrideLibrary="|tools:overrideLibrary="androidx.credentials, androidx.credentials.playservices, com.google.android.libraries.identity.googleid, |' $M
fi
F=application/vlc-android/src/org/videolan/vlc/gui/onboarding/OnboardingWelcomeFragment.kt
if ! grep -q "SDK_INT < 23" $F; then
  sed -i '/private fun startGoogleSignIn() {/a\        if (android.os.Build.VERSION.SDK_INT < 23) return' $F
fi
mkdir -p .github/workflows
cat > .github/workflows/build.yml <<'X'
name: Build H Media Player APK
on:
  workflow_dispatch:
  push:
    branches: [main]
permissions:
  contents: write
jobs:
  build:
    runs-on: ubuntu-latest
    timeout-minutes: 90
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 21
      - uses: gradle/actions/setup-gradle@v4
        with:
          gradle-version: 9.6.0
      - name: Android SDK/NDK
        run: |
          yes | "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" --licenses > /dev/null || true
          "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" "platforms;android-36" "build-tools;36.0.0" "ndk;21.4.7075529"
          echo "sdk.dir=$ANDROID_HOME" > local.properties
      - name: Create signing key
        run: keytool -genkeypair -keystore "$PWD/h.jks" -alias hkey -keyalg RSA -keysize 2048 -validity 10000 -storepass hmedia123 -keypass hmedia123 -dname "CN=H Media Player"
      - name: Build
        run: |
          set -o pipefail
          gradle :application:app:assembleSignedRelease --no-daemon -PkeyStoreFile="$PWD/h.jks" -Pstorealias=hkey -Pstorepwd=hmedia123 2>&1 | tee build.log
      - name: Save error summary
        if: failure()
        run: |
          mkdir -p /tmp/bl
          { echo "== ERRORS"; grep -n -E "^e: |error:|What went wrong|FAILURE:|Execution failed|Manifest merger|AAPT|Duplicate|Caused by|uses-sdk" build.log | head -60; echo; echo "== LAST 50 LINES"; tail -50 build.log; } > /tmp/bl/last-build.txt
          cd /tmp/bl
          git init -q
          git checkout -q -b buildlogs
          git config user.name "build-bot"
          git config user.email "bot@example.com"
          git add last-build.txt
          git commit -q -m "build error summary"
          git push -q -f "https://x-access-token:${{ github.token }}@github.com/${{ github.repository }}.git" buildlogs
      - uses: actions/upload-artifact@v4
        with:
          name: H-Media-Player-apk
          path: application/app/build/outputs/apk/**/*.apk
X
echo "Done. Next: git add -A && git commit -m 'fix minSdk for Google sign-in' && git push origin main"
