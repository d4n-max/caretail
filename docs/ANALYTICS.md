# CareTail Analytics

Last updated: 2026-07-08

## Implementation Status

Analytics is implemented with Firebase Analytics / GA4.

Found files:

| File | Purpose |
| --- | --- |
| `app/build.gradle.kts` | Adds Firebase BoM, Firebase Auth, Firebase Analytics, and Google services plugin. |
| `app/google-services.json` | Firebase Android app configuration. |
| `app/src/main/java/com/caretail/app/analytics/AnalyticsTracker.kt` | Central optional-safe analytics event wrapper. |
| `app/src/main/java/com/caretail/app/data/local/database/AppContainer.kt` | Creates the shared `AnalyticsTracker`. |
| `app/src/main/java/com/caretail/app/ui/navigation/CareTailNavGraph.kt` | Tracks screen views, onboarding, settings, and premium navigation. |
| `app/src/main/java/com/caretail/app/ui/viewmodel/*.kt` | Tracks successful local creation/completion/export actions. |
| `app/src/main/java/com/caretail/app/ui/screens/premium/PremiumScreen.kt` | Tracks paywall views, upgrade clicks, purchase starts, and restore clicks. |
| `app/src/main/java/com/caretail/app/billing/BillingRepository.kt` | Tracks purchase completion and purchase failure outcomes. |
| `app/src/main/java/com/caretail/app/auth/AuthViewModel.kt` | Tracks optional Google Sign-In start/completed/failed events. |

## Events

| Event | Trigger | Parameters |
| --- | --- | --- |
| `screen_view` | Route changes in the app navigation graph. | `source_screen`, `screen_name` |
| `onboarding_started` | Onboarding screen is shown. | `source_screen` |
| `onboarding_completed` | User taps the onboarding get-started action. | `source_screen` |
| `first_pet_created` | First pet profile is saved successfully. | `source_screen`, `pet_count_bucket` |
| `pet_created` | A new pet profile is saved successfully. | `source_screen`, `pet_count_bucket` |
| `reminder_created` | A new reminder is saved successfully. | `source_screen`, `reminder_type` |
| `reminder_completed` | A reminder is marked complete. | `source_screen`, `reminder_type` |
| `diary_entry_created` | A new health diary entry is saved successfully. | `source_screen`, `diary_has_symptoms` |
| `document_added` | A new document record is saved successfully. | `source_screen`, `document_type` |
| `paywall_viewed` | Premium screen is shown. | `source_screen`, `paywall_reason`, `plan_type` |
| `upgrade_clicked` | User opens Premium from a gate/CTA or taps Start Premium. | `source_screen`, `paywall_reason`, `plan_type` |
| `purchase_started` | User starts the Google Play Billing flow. | `source_screen`, `plan_type` |
| `purchase_completed` | Google Play Billing reports an active purchased Premium subscription. | `source_screen`, `plan_type`, `success` |
| `purchase_failed` | Billing flow cannot start, is canceled, or returns an error. | `source_screen`, `plan_type`, `success`, `result`, `error_type` |
| `restore_purchases_clicked` | User taps Restore purchase on the Premium screen. | `source_screen` |
| `export_report_clicked` | Premium user exports a pet care report. | `source_screen` |
| `google_sign_in_started` | User starts optional Google Sign-In from Settings. | `source_screen` |
| `google_sign_in_completed` | Optional Google Sign-In succeeds. | `source_screen`, `success` |
| `google_sign_in_failed` | Optional Google Sign-In fails or cannot launch. | `source_screen`, `success` |
| `settings_opened` | Settings screen is shown. | `source_screen`, `source` |

## Privacy Notes

CareTail remains local-first. Analytics events do not upload pet records, notes, document content, file URIs, emails, Google account IDs, or pet names.

Allowed analytics values are categorical:

| Parameter | Notes |
| --- | --- |
| `source_screen` | App screen or system source such as `billing`. |
| `pet_count_bucket` | Bucket only: `1`, `2`, `3_4`, `5_plus`. No exact pet names. |
| `reminder_type` | Normalized reminder category such as `vaccine`, `medication`, or `vet_visit`. |
| `diary_has_symptoms` | Boolean only. Symptom text is never logged. |
| `document_type` | Normalized document category only. Title, URI, and filename are never logged. |
| `paywall_reason` | Premium gate/source such as `pet_limit`, `reminder_limit`, or `export_locked`. |
| `plan_type` | `monthly` or `yearly`. |
| `success` | Boolean outcome for sign-in and purchase completion/failure. |

Firebase Analytics is wrapped in `AnalyticsTracker` with defensive `runCatching` calls. If Firebase Analytics is unavailable, CareTail continues without crashing. This implementation does not add Firestore, Firebase Storage, FCM, cloud sync, an ads SDK, or backend pet-data upload.

## GA4 Funnel Recommendation

Recommended activation funnel:

1. `first_open`
2. `onboarding_started`
3. `onboarding_completed`
4. `first_pet_created`
5. `reminder_created`
6. `diary_entry_created` or `document_added`

Recommended retention/engagement events:

1. `reminder_completed`
2. `diary_entry_created`
3. `document_added`
4. `export_report_clicked`

Recommended monetization funnel:

1. `paywall_viewed`
2. `upgrade_clicked`
3. `purchase_started`
4. `purchase_completed`

Mark `onboarding_completed`, `first_pet_created`, `reminder_created`, and `purchase_completed` as GA4 key events after they appear in the GA4 event list.
