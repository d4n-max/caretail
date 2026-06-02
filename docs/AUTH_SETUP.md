# CareTail Firebase Auth Setup

CareTail supports optional Google sign-in with Firebase Authentication and Android Credential Manager. The app remains local-first: signing in does not upload, sync, migrate, or delete local Room data.

## 1. Create Or Select A Firebase Project

Open Firebase Console and create a project, or use an existing CareTail Firebase project.

## 2. Add The Android App

Add an Android app with this package name:

```text
com.caretail.app
```

Use the same package for debug, internal testing, closed testing, and release unless a separate application ID is intentionally introduced later.

## 3. Add SHA Fingerprints

Add SHA-1 and SHA-256 fingerprints for the upload/debug certificates used to test Google sign-in.

Useful local command:

```powershell
.\gradlew.bat :app:signingReport
```

Add the relevant SHA-1 and SHA-256 values in Firebase Project Settings > Your apps > Android app.

## 4. Enable Google Provider

In Firebase Console:

1. Open Authentication.
2. Open Sign-in method.
3. Enable Google.
4. Save the provider configuration.

## 5. Add google-services.json

Download `google-services.json` from Firebase Project Settings and place it here:

```text
app/google-services.json
```

Do not rename it. The Google services Gradle plugin reads this file and generates resources used by the app.

## 6. Verify default_web_client_id

After adding `app/google-services.json`, build the app and confirm the generated resource exists:

```text
R.string.default_web_client_id
```

CareTail uses this generated value for Credential Manager Google sign-in. The web client ID must not be hardcoded in source files.

## 7. Local-First Limitation

Current MVP behavior:

- Google sign-in is optional.
- The app works without login.
- Local Room data remains on the device.
- Signing in does not upload local pet data.
- Signing out does not delete local pet data.
- Delete Local Data remains a separate explicit Settings action.

Future cloud backup or sync can use the Firebase Auth identity, but it is not implemented yet.

## 8. Troubleshooting

### DEVELOPER_ERROR

Check that the Firebase Android app package is `com.caretail.app`, the SHA-1/SHA-256 fingerprints are correct, and Google provider is enabled.

### Missing SHA

Google sign-in often fails if the signing certificate fingerprint for the installed build is not registered in Firebase.

### Wrong Package Name

Confirm Firebase uses:

```text
com.caretail.app
```

### Missing google-services.json

If `app/google-services.json` is missing, the build will fail after the Google services plugin is applied. Download it from Firebase and place it in the app module.

### No Credentials Available

Credential Manager may report no credentials if no eligible Google account is available on the device, Google Play services is unavailable, or Firebase is misconfigured.

### Sign-In Cancelled

This is expected when the user dismisses Credential Manager. CareTail remains usable locally.
