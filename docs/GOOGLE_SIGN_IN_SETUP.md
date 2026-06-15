# CareTail Google Sign-In Setup

CareTail v1.1 supports optional Google Sign-In with Firebase Authentication and Android Credential Manager. The app remains local-first: signing in does not upload pet care records, enable cloud sync, or change Premium entitlement.

## Preflight Findings

- Application ID/package: `com.caretail.app`
- Namespace: `com.caretail.app`
- Google services Gradle plugin: present in root and app Gradle files.
- Firebase dependencies: Firebase BoM and Firebase Auth are present.
- Credential Manager dependencies: `androidx.credentials`, `androidx.credentials-play-services-auth`, and `googleid` are present.
- Firebase config: `app/google-services.json` exists and is tracked.
- Current Firebase config gap: the checked config has no OAuth client entries, so `default_web_client_id` may not be generated until Firebase SHA/OAuth setup is completed and a fresh config is downloaded.
- Settings: Account section is the sign-in/sign-out/delete-account surface.
- Premium: `PremiumManager` remains the entitlement source for local gating, with Google Play Billing updates through `BillingRepository`.
- Release/debug handling: release signing is configured from `keystore.properties`; debug-only Premium test mode is gated by `BuildConfig.DEBUG`.

## 1. Firebase Console

1. Create or select the Firebase project for CareTail.
2. Add an Android app with package:

```text
com.caretail.app
```

3. Add SHA-1 and SHA-256 fingerprints for every signing certificate used to install the app:
   - debug keystore
   - release/upload keystore
   - Google Play App Signing certificate

Useful local command:

```powershell
.\gradlew.bat :app:signingReport
```

Current local fingerprints observed during implementation:

```text
Debug SHA-1:    6C:7A:73:B9:B7:37:B9:98:BB:0D:35:34:80:91:83:FA:16:9A:97:03
Debug SHA-256:  1D:B6:B1:11:C9:D8:DE:30:A8:24:C4:E1:CC:6C:EB:0D:79:A4:80:A5:19:72:6A:C0:3D:3E:F4:DA:47:7F:DC:A0
Upload SHA-1:   EF:CD:E4:30:30:E3:23:C4:BB:07:79:55:F5:B2:09:E7:17:2E:59:FC
Upload SHA-256: 4D:40:4D:63:11:CE:77:8A:D3:33:1F:D9:2C:C4:67:37:0F:7D:79:B7:05:48:85:E4:4A:98:46:7C:48:69:EE:6F
```

The Google Play App Signing SHA-1/SHA-256 values must be copied from Google Play Console because they are not available from the local keystore.

4. Download the updated `google-services.json`.
5. Place it at:

```text
app/google-services.json
```

## 2. Firebase Authentication

1. Open Firebase Console > Authentication.
2. Open Sign-in method.
3. Enable Google provider.
4. Set the public-facing app name.
5. Set the support email.
6. Save.
7. Download a fresh `google-services.json` after the provider and SHA fingerprints are configured.

## 3. Google Play Console

Before v1.1 submission:

- Update Data Safety because optional account sign-in is now supported.
- Confirm account creation/sign-in disclosures.
- Add or confirm account deletion answers.
- Provide a public account deletion web URL.
- Update the Privacy Policy to mention Firebase Auth and Google account profile data.
- Confirm Premium remains disclosed as Google Play Billing, not Firebase billing.

## 4. Verification

After replacing `app/google-services.json`, run:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
```

Manual QA must use an installed build whose signing certificate is registered in Firebase.
