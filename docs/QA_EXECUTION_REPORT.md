# CareTail QA Execution Report

## Run Metadata

- Date/time: 2026-06-12 11:35:15 +03:00
- App versionCode: 4
- App versionName: 0.1.3
- Environment: Windows PowerShell, local Gradle wrapper, Android SDK from local `local.properties`
- Release artifact checked: `app/build/outputs/bundle/release/app-release.aab`
- AAB size: 13,632,852 bytes
- Scope: pre-production QA verification for CareTail 0.1.3 release readiness

## Commands Run

| Command | Result | Notes |
| --- | --- | --- |
| `.\gradlew.bat test` | Pass | 6 unit tests passed: 2 billing product ID tests and 4 review prompt eligibility tests. |
| `.\gradlew.bat :app:assembleDebug` | Pass | Debug APK build completed successfully. |
| `.\gradlew.bat :app:lintDebug` | Pass after fixes | Initial run found notification permission lint and local SDK path escaping issues. Both were fixed and lint passed. |
| `.\gradlew.bat :app:connectedDebugAndroidTest` | Pass with no UI tests | Gradle task completed; project currently has no `androidTest` sources, so this does not replace manual device QA. |
| `.\gradlew.bat :app:bundleRelease` | Pass | Release AAB generated successfully. |
| Security/release scans with `rg` and `git ls-files` | Pass with note | No tracked keystore, APK, AAB, or `local.properties`; no release signing passwords exposed. Existing `app/google-services.json` contains a normal Google services client API key. |

## Checklist Results

| Area | Result | Evidence / Notes |
| --- | --- | --- |
| Version metadata | Pass | `versionCode = 4`, `versionName = "0.1.3"`. |
| Debug build | Pass | `.\gradlew.bat :app:assembleDebug`. |
| Release bundle build | Pass | `.\gradlew.bat :app:bundleRelease`; AAB generated at expected path. |
| Unit tests | Pass | `PremiumPlanTest` and `ReviewPromptEligibilityTest`, 6/6 passing. |
| Lint | Pass | `.\gradlew.bat :app:lintDebug` passed after fixes. |
| Billing product IDs | Pass | Tests verify `caretail_premium_monthly` / `monthly` and `caretail_premium_yearly` / `yearly`. |
| Billing unavailable-product safety | Pass | Missing base-plan offers now map to unavailable products instead of unsafe fallback pricing. |
| Premium purchase buttons | Pass by code inspection | Premium action buttons are disabled while loading, unavailable, in progress, or already premium. |
| Active subscription unlock | Pass by code inspection | Purchased subscription state updates `PremiumManager`. |
| Canceled and pending purchases | Pass by code inspection | Pending purchases do not unlock Premium; purchase state is checked before unlock. |
| Restore purchases | Pass by code inspection | Settings exposes restore flow through BillingRepository. |
| Debug Premium override | Pass | Debug override is guarded by `BuildConfig.DEBUG` and hidden from release behavior. |
| Free and Premium gates | Pass by code inspection | Pet, reminder, diary, document, advanced recurrence, and export gates remain present. |
| Reminder add/edit/delete/complete | Blocked for manual device QA | Code paths preserve scheduling/cancel behavior, but full UI and notification timing require a device. |
| Reminder date/time picker flow | Blocked for manual device QA | Requires tapping Add/Edit Reminder on device to verify picker UX and saved due time. |
| Notification permission denied flow | Blocked for manual device QA | Code handles notification permission, but Android runtime permission UX requires device testing. |
| Health diary create/edit/delete/limits | Blocked for manual device QA | Code inspection found gates and persistence paths; UI workflow still needs manual verification. |
| Documents picker/open/delete | Blocked for manual device QA | Uses Android document picker APIs; file picker and file opening require device testing. |
| Settings delete local data | Blocked for manual device QA | Code path exists; destructive local data flow should be manually confirmed before release. |
| In-app review prompt | Pass for eligibility logic; blocked for Play UI | Eligibility unit tests pass; actual Play review dialog is controlled by Google Play and needs tester/device validation. |
| Tracked secrets/artifacts | Pass | No tracked keystore, APK, AAB, or `local.properties` found. |
| Firebase/Auth/Billing scope | Pass | No Firebase Auth added; Play Billing remains the billing provider; no RevenueCat or Stripe integration found. |
| Install and launch | Blocked for manual device QA | Plain `adb` was not available on PATH; Gradle connected task passed but no interactive launch was verified. |
| Real Google Play subscription purchase | Blocked for Play track QA | Requires uploaded build, licensed tester account, and active Play Console products/base plans. |

## Bugs Found

| Issue | Severity | Status | Fix |
| --- | --- | --- | --- |
| Lint flagged `MissingPermission` for reminder notification posting. | Medium | Fixed | Added a guarded notification posting helper in `ReminderNotificationReceiver`. |
| Lint flagged invalid escaping in local `local.properties` SDK path. | Local build blocker | Fixed locally | Corrected the untracked local SDK path escaping. This file is not tracked and is not a release artifact. |
| No automated coverage existed for Play Billing product identifiers. | Medium | Fixed | Added unit tests for monthly/yearly subscription product IDs and base plan IDs. |
| No automated coverage existed for review prompt eligibility rules. | Medium | Fixed | Extracted eligibility logic and added unit tests for allowed, first-launch, throttled, and diary-save cases. |
| Billing product mapping could fall back when the expected base plan offer was missing. | Medium | Fixed | Missing expected base-plan offer now makes that product unavailable instead of selecting an unintended offer. |

## Fixes Applied

- Added unit coverage for Premium subscription product IDs and base plan IDs.
- Added unit coverage for review prompt eligibility and throttling behavior.
- Extracted review prompt eligibility into a testable helper.
- Hardened billing product mapping so mismatched Play Console base plans do not silently produce purchase options.
- Fixed notification permission lint in reminder notification posting.
- Fixed local untracked SDK path escaping so lint can run.

## Remaining Manual Checks

- Install the release candidate from the Google Play testing track with a licensed tester account.
- Verify monthly and yearly subscription products load with localized Play prices.
- Test successful purchase, cancellation, pending purchase behavior, and restore purchases from Play.
- Verify all free-limit and Premium-unlocked gates on a real device.
- Run full Add/Edit Reminder UI QA, including date picker, time picker, saved due time, notification scheduling, edit, complete, and delete.
- Verify notification permission handling and scheduled notification delivery on Android 13+.
- Verify diary, document picker, export, settings restore, delete local data, and navigation/back-stack flows.
- Verify Google Play In-App Review prompt behavior where Play allows the dialog to appear.
- Confirm the existing `app/google-services.json` client API key is intentionally committed for this app configuration.

## Verdict

Conditionally ready for production AAB generation/upload from the automated QA perspective.

Automated build, lint, unit tests, release bundle generation, and release safety scans passed. The release should not be treated as fully production-ready until the remaining manual device and Google Play closed-testing checks pass, especially real subscription purchases, notification delivery, and Add/Edit Reminder picker behavior.
