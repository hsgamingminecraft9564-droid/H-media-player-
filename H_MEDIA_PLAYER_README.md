# H Media Player — H Media Player Engine Edition

This project keeps the H Media Player for Android application and LibH Media Player playback stack as its foundation,
while applying the H Media Player identity and the supplied typography starter.

## What has been changed in this source package
- Android app display name in the supplied base project is `H Media Player`.
- H Media Player theme accent references in the Android UI styles have been shifted from orange to a
  silver/lavender/violet palette.
- The supplied `HmpTypography` and `HmpTypographySettings` Java classes are placed inside
  `application/vlc-android/src/com/hmediaplayer/ui/`.
- The supplied Ideogram app-icon and UI-control artwork are included in
  `application/vlc-android/assets/hmp-branding/`.
- H Media Player playback, media parsing, codecs, playlists, audio/video services and LibH Media Player code have
  been retained from the provided source archive.

## Typography integration
The typography classes are included as source code but are deliberately not force-applied to
every H Media Player screen: H Media Player uses a mixture of fragments, RecyclerViews, custom controls and data
binding, so blindly applying a recursive TextView pass can disrupt layouts.

To apply the font settings to a specific Activity after its content is inflated:

```java
HmpTypography.applyTree(findViewById(android.R.id.content));
```

For a screen with dynamically-bound song rows, call `HmpTypography.apply(textView)` in the
adapter's `onBindViewHolder` for each relevant title/artist TextView.

To open the font settings dialog from an Activity:

```java
HmpTypographySettings.show(this, findViewById(android.R.id.content));
```

Package: `com.hmediaplayer.ui`.

Place properly licensed `.ttf` or `.otf` font files in:
`application/vlc-android/assets/fonts/`

## Build
Open the `vlc-android-master` folder in Android Studio. Use the Gradle wrapper included in the
project. The original H Media Player build requirements still apply, including a compatible JDK, Android
SDK/NDK configuration, and the project's native dependency setup.

This environment did not run a full Android Gradle build, so this archive is source code and
not a verified installable APK. Resolve any Gradle/native dependency errors in Android Studio
before distributing an APK.

## H Media Player licensing and attribution
This project is based on the supplied H Media Player for Android source tree. Preserve its COPYING files,
third-party notices, trademarks and GPL/LGPL obligations. A distributed application based on
the H Media Player Android application must comply with the applicable H Media Player source and license requirements.
