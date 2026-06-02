# CareTail Release Signing

This guide prepares a signed Android App Bundle for Google Play Internal Testing.

## Current Release Configuration

- App name: CareTail
- Package name: `com.caretail.app`
- Namespace: `com.caretail.app`
- Version code: `1`
- Version name: `0.1.0`
- Expected release output: `app/build/outputs/bundle/release/app-release.aab`

## Generate The Upload Keystore

Run this from the project root in PowerShell:

```powershell
& "C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe" -genkeypair -v -keystore caretail-upload-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias caretail
```

If Android Studio is installed somewhere else, update the `keytool.exe` path.

## Create keystore.properties

Create `keystore.properties` in the project root:

```properties
storeFile=caretail-upload-key.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=caretail
keyPassword=YOUR_KEY_PASSWORD
```

Do not commit `caretail-upload-key.jks` or `keystore.properties`.

Back up the keystore and passwords securely. Losing the upload key can block future updates unless the key is reset through Play App Signing.

## PowerShell Environment

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
$env:ANDROID_SDK_ROOT = "$env:LOCALAPPDATA\Android\Sdk"
$env:Path = "$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:ANDROID_HOME\cmdline-tools\latest\bin;$env:Path"
```

## Build Debug APK

```powershell
.\gradlew.bat :app:assembleDebug -Dorg.gradle.problems.report=false
```

If PowerShell treats `-Dorg.gradle.problems.report=false` as a task name, use:

```powershell
.\gradlew.bat "-Dorg.gradle.problems.report=false" :app:assembleDebug
```

## Build Signed Release AAB

After `caretail-upload-key.jks` and `keystore.properties` exist locally:

```powershell
.\gradlew.bat :app:bundleRelease -Dorg.gradle.problems.report=false
```

If PowerShell treats `-Dorg.gradle.problems.report=false` as a task name, use:

```powershell
.\gradlew.bat "-Dorg.gradle.problems.report=false" :app:bundleRelease
```

Expected output:

```text
app/build/outputs/bundle/release/app-release.aab
```

## Google Play Notes

- Upload the `.aab` to Google Play Console Internal Testing.
- Keep Play App Signing enabled when prompted.
- Do not share the upload key or passwords.
- Increase `versionCode` for every future upload.
