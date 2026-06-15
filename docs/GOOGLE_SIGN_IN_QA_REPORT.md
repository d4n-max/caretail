# Google Sign-In QA Report

## Scope

CareTail v1.1 optional Google Sign-In with Firebase Authentication and Android Credential Manager.

## Commands Run

```powershell
.\gradlew.bat :app:signingReport
.\gradlew.bat :app:testDebugUnitTest --tests "com.caretail.app.auth.*"
.\gradlew.bat :app:assembleDebug
.\gradlew.bat test
.\gradlew.bat :app:lintDebug
```

Full Google Sign-In device QA should be refreshed after final Firebase Console setup and before v1.1 submission.

## Current Automated Result

- Debug build: pass.
- Full unit tests: pass, 12 tests, 0 failures, 0 errors, 0 skipped.
- Auth unit tests: pass.
- Android lint: pass.
- Signing report: pass.
- Security/release scan: pass with note. The scan finds the normal Firebase client API key in `app/google-services.json`; no Firestore, Firebase Storage, FCM, Firebase Analytics, RevenueCat, or Stripe dependencies are present.

## Manual Firebase Setup Still Required

- Add debug SHA-1 and SHA-256 in Firebase.
- Add upload/release SHA-1 and SHA-256 in Firebase.
- Add Google Play App Signing SHA-1 and SHA-256 in Firebase.
- Enable Google provider in Firebase Authentication.
- Set public-facing app name and support email.
- Download a fresh `app/google-services.json`.

The current checked `app/google-services.json` has no OAuth client entries, so real sign-in may show the app's calm configuration error until Firebase setup is completed.

## SHA Fingerprints Needed

Observed local fingerprints:

```text
Debug SHA-1:    6C:7A:73:B9:B7:37:B9:98:BB:0D:35:34:80:91:83:FA:16:9A:97:03
Debug SHA-256:  1D:B6:B1:11:C9:D8:DE:30:A8:24:C4:E1:CC:6C:EB:0D:79:A4:80:A5:19:72:6A:C0:3D:3E:F4:DA:47:7F:DC:A0
Upload SHA-1:   EF:CD:E4:30:30:E3:23:C4:BB:07:79:55:F5:B2:09:E7:17:2E:59:FC
Upload SHA-256: 4D:40:4D:63:11:CE:77:8A:D3:33:1F:D9:2C:C4:67:37:0F:7D:79:B7:05:48:85:E4:4A:98:46:7C:48:69:EE:6F
```

Google Play App Signing fingerprints must be copied from Google Play Console.

## Manual QA Needed

- App launches signed out.
- App is usable without login.
- Settings shows Account section.
- Google Sign-In starts from Settings.
- Successful sign-in shows display name and/or email.
- Failed sign-in shows calm error.
- Sign out works and local pet data remains.
- Delete account only removes Firebase sign-in and keeps local data.
- Delete account and local app data clears local records.
- Premium purchase and restore do not require Google Sign-In.
- No cloud backup or sync appears as an active feature.
- App does not crash if Firebase SHA/OAuth setup is incomplete.
- Release build does not expose debug-only account tools.

## Play Console Policy Updates Needed

- Update Data Safety for optional account sign-in.
- Add/confirm account deletion answers.
- Provide account deletion web URL.
- Update Privacy Policy URL/content for Firebase Auth and Google account profile data.
- Confirm Premium remains Google Play Billing.

## Changed Files

- `docs/ACCOUNT_DELETION_POLICY.md`
- `docs/DATABASE.md`
- `docs/EXPORT_REPORT.md`
- `docs/GOOGLE_SIGN_IN_QA_REPORT.md`
- `docs/GOOGLE_SIGN_IN_SETUP.md`
- `docs/HEALTH_DIARY.md`
- `docs/OPTIONAL_GOOGLE_SIGN_IN_CAPABILITY.md`
- `docs/PLAY_COMPLIANCE.md`
- `docs/PRIVACY_POLICY_NOTES.md`
- `docs/QA_CHECKLIST.md`
- `docs/QA_EXECUTION_REPORT.md`

Note: the optional Google Sign-In implementation files and auth unit tests are already present in the current tracked app source; this report documents the v1.1 setup, QA, and policy handoff around that implementation.
