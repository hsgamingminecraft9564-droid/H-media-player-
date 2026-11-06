# H Media Player - Round 1 changes

## Done in this pass
- Palette: all violet/lavender accents (#9675F5, #A58BFF, #B7A0FF, #8064D8) in application/vlc-android/res/values/styles.xml replaced with Blood & Iron values sampled from the supplied Settings screenshot: crimson #CC051A (accents), deep crimson #8B0311, charcoal #111111 (primary surfaces), silver #D7DBE0 (control icons), black #000000.
- Fixed invalid color literal "#9675F5transparent" (donate_card) to "#66CC051A".
- Welcome screen (application/vlc-android/res/layout/onboarding_welcome.xml): rebuilt to the composition of the supplied screenshot - wordmark, tagline, "Welcome" heading, Continue with Google / Facebook / Email, OR divider, Continue as Guest, terms line.
- New drawables in application/resources/src/main/res/drawable/: hmp_welcome_bg.xml, hmp_btn_outline.xml, hmp_btn_guest.xml.
- New strings (hmp_*) appended to application/resources/src/main/res/values/strings.xml.
- OnboardingWelcomeFragment: Continue as Guest advances onboarding without an account. Google / Facebook / Email are visible but disabled until Firebase Auth is configured.
- OnboardingActivity: the shared Next button is hidden on the Welcome page, since the page has its own Guest button.
- Removed the old landscape welcome layout; the portrait ScrollView layout serves both orientations.
- H_MEDIA_PLAYER_README.md: font folder corrected to application/vlc-android/assets/fonts/ (the module's Gradle assets directory).

## Not done yet
- Artwork still needed: the moonlit-ruins background, the H emblem with red play triangle, and the Google / Facebook / Email icons. Placeholder gradient and wordmark are used until these are supplied.
- Medieval Gothic .ttf/.otf font file (not included in the upload).
- Sign-in, account system, guest-to-account upgrade, cloud sync (Firebase Auth + Firestore).
- Home, music player, video player redesign; theme picker (Royal Gold, Neon Royal, Toxic Green); typography applied across screens; Settings screen rebuilt to the supplied layout; Room playlists; remaining items on the change list.
- This environment has no Android SDK, Gradle or network, so the project has not been compiled or run.

## Round 2 - Music and video player (colors and styles)
- Music player: seek bars are crimson instead of orange (audio_seekbar_black, audio_seekbar); play/pause button is a red circle (hmp_play_circle); playback chips are charcoal with a silver outline and silver text in the dark theme.
- Video player: video seek bar uses the crimson drawable (po_seekbar_video); the control panel background is near-black glass (playerbackground); the "Video information" title is light crimson.
- New tokens in application/resources/src/main/res/values/colors.xml: hmp_crimson, hmp_crimson_light, hmp_charcoal, hmp_silver.
- No layout IDs were removed or renamed, so existing Kotlin bindings are unchanged.
- Not changed: layout structure and the reference designs (album card, lyrics panel, equalizer screen, gesture overlays). These need a device check before a layout pass.

## Round 3 - Google sign-in
- New GoogleSignInHelper.kt (Android Credential Manager, Google ID token).
- Welcome screen: Continue with Google now starts sign-in. Facebook and Email are still disabled.
- strings.xml: hmp_google_web_client_id is a PLACEHOLDER. Replace it with the Web client ID from Google Cloud Console. Until then the button shows a "not set up" message.
- build.gradle: added androidx.credentials 1.3.0 and googleid 1.1.1. These versions were not verified offline, so check them when you sync Gradle.
- Not done: music and video player rewrite (see the reply for the reason and the plan).

## Round 5 - Branding, low-memory tuning, typography
- Branding: all user-visible text in the translation files now says "H Media Player" or "H Media engine". The "Play with" manifest label and the widget names were updated too.
- Log tags now use HMP/ and the HTTP user agent is HMediaPlayer-Android.
- Low-memory devices: the bitmap cache is one tenth of the heap below 256 MB (previously one fifth).
- Typography: new font choices Serif Mono, Condensed, Light, Medium, Black, Casual, and Cursive, alongside Default, Serif, Sans Serif, Monospace, and any .ttf or .otf file in assets/fonts.
- Kept on purpose: the license files (COPYING, COPYING.LIB) and copyright headers, the internal class and package names, the libVLC binding package used by the native engine, and two upstream links in the help strings.
- Not done yet: the typography picker is not yet linked from the Settings screen, the trend themes and the low-end mode switch are not built, and nothing has been compiled.

## Round 6 - Own medieval font and typography
- New font: HMP Medieval (assets/fonts/HMPMedieval-Regular.ttf), made from KaTeX_Fraktur under the SIL OFL 1.1. The reserved name was removed, and the license is in assets/fonts/LICENSE-HMPMedieval.txt.
- HMP Medieval is the default font and is listed first in Font style.
- The typography applies to every activity screen and every fragment screen when it is created, and is re-applied on resume.
- Settings > Interface > Font style opens the font and size dialog.
- Not covered by the font: characters such as # $ % < > @ \ _ ` { | } ~ fall back to the system font, and adapter-created list rows still need HmpTypography.apply() when bound.
