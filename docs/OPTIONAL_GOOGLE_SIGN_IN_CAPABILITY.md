# Optional Google Sign-In Capability

CAPABILITY
- CareTail v1.1 allows a pet owner to optionally sign in with Google from Settings so CareTail can establish a Firebase Authentication identity for future backup and sync work, while the app remains fully usable as a local-first pet care tracker without an account.

CONSTRAINTS
- Google Sign-In is optional and must not appear as a launch blocker or login wall.
- Existing local Room data remains owned by the device until a separate cloud backup/sync capability explicitly changes that behavior.
- Signing in must not upload pet profiles, reminders, health diary entries, documents, reports, or settings.
- Signing out must not delete local pet care data.
- Account deletion deletes the Firebase Auth user only unless the user explicitly chooses local app data deletion.
- Premium entitlement remains sourced from Google Play Billing and debug Premium test mode only.
- Premium purchase and restore must not require Firebase Auth.
- No Firestore, Firebase Storage, FCM, Firebase Analytics, RevenueCat, or Stripe is part of this capability.
- Failures must be calm and recoverable, especially missing Firebase SHA/OAuth setup and recent-login-required account deletion.

IMPLEMENTATION CONTRACT
- Actors: signed-out pet owner, signed-in pet owner, Google/Firebase identity provider, Google Play Billing.
- Surfaces: Settings > Account, Google Credential Manager sheet, account deletion confirmation dialog, Privacy/Play policy docs.
- States and transitions:
  - SignedOut: no Firebase user observed; app remains usable locally.
  - Loading: sign-in or account deletion is in progress.
  - SignedIn: Firebase Auth current user exists and may expose uid, display name, email, and profile photo URL.
  - Error: the last auth operation failed with a user-safe message.
  - Delete account only: Firebase Auth user is deleted; local Room data remains.
  - Delete account and local app data: Firebase Auth user is deleted first, then local pet/reminder/diary/document records are deleted.
- Interface/data implications:
  - `AuthRepository` observes `FirebaseAuth.currentUser`, starts Credential Manager Google Sign-In, signs out, and deletes the Firebase user.
  - `AuthUiState` exposes explicit states plus the optional `AuthUser`.
  - Settings receives auth callbacks from the nav graph and must not directly couple auth to Premium.
  - Local data deletion continues through existing Room repository deletion methods and reminder cancellation.

NON-GOALS
- Cloud backup/sync.
- Firestore pet records.
- Firebase Storage.
- FCM.
- Analytics.
- Login-gated onboarding.
- Premium entitlement migration to Firebase.
- Data migration from local Room to cloud.

OPEN QUESTIONS
- Public account deletion/privacy web URL is still a placeholder and must be supplied before v1.1 submission.
- Final support email/contact email must be confirmed for policy docs and Firebase public-facing app settings.
- Firebase Console must be updated with debug, upload/release, and Google Play App Signing SHA-1/SHA-256 fingerprints.
- Google Play Data Safety answers must be finalized after legal/privacy review.

HANDOFF
- Ready for direct implementation and verification in the Android app lane.
- Next lanes: `tdd-workflow` for auth state and deletion behavior tests, `security-review` for account/deletion/privacy checks, and `verification-loop` before release submission.
