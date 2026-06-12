# CareTail Review Prompt Strategy

## Capability

CareTail can ask for a Google Play review only after a user has completed meaningful, positive pet-care work, such as marking a reminder complete or saving a health diary entry. The review request uses the official Google Play In-App Review API and must never become a custom rating popup, pressure campaign, first-launch prompt, or blocker in a care workflow.

## Constraints

- Use the official Google Play In-App Review API.
- Do not create custom rating UI.
- Do not ask for 5-star reviews.
- Do not ask pre-qualifying questions like `Do you like the app?`
- Do not prompt on first launch, onboarding, opening Home, deletion, failed saves, validation errors, notification permission denial, PremiumScreen close, or local data deletion.
- Do not implement Firebase/Auth, Google Play Billing, marketing prompts, or remote configuration in this lane.
- Keep the prompt silent and non-blocking if Google Play does not show the review dialog.
- Do not change Room schema for review metadata.

## Implementation Contract

Actors:

- Pet owner using CareTail.
- Google Play review service.

Surfaces:

- Reminders screen after a reminder is successfully marked complete.
- Add/Edit Health Note screen after a diary entry is successfully saved.
- Application startup for launch metadata only.

States and transitions:

- `firstLaunchAtMillis`: set on first app launch.
- `launchCount`: increments on app start.
- `reminderCreatedCount`: increments after a new reminder saves successfully.
- `reminderCompletedCount`: increments after a reminder is marked complete successfully.
- `diaryEntrySavedCount`: increments after a health diary entry saves successfully.
- `lastReviewPromptAttemptAtMillis`: set when CareTail attempts the Play review flow.
- `reviewPromptAttemptCount`: increments when CareTail attempts the Play review flow.

Eligibility:

- User has at least 1 pet profile.
- User has created at least 2 reminders or saved at least 1 health diary entry.
- User has completed at least 1 reminder or saved at least 1 health diary entry.
- App has launched at least 2 times.
- At least 24 hours have passed since first launch.
- No review prompt attempt happened in the last 90 days.
- Current flow just completed successfully.
- No blocking CareTail dialog or bottom sheet is active.
- PremiumScreen is not the current surface.

Interfaces and data:

- Metadata is stored in Preferences DataStore.
- `ReviewPromptManager.recordAppLaunch()` runs from application startup.
- `ReviewPromptManager.onReminderCreated()` runs after successful new reminder creation.
- `ReviewPromptManager.onReminderCompleted()` runs after successful reminder completion.
- `ReviewPromptManager.onDiaryEntrySaved()` runs after successful diary save.
- `ReviewPromptManager.requestReviewIfEligible(...)` calls `ReviewManagerFactory.create(context)`, `requestReviewFlow()`, and `launchReviewFlow(activity, reviewInfo)`.

## Non-Goals

- No custom review prompt UI.
- No review prompt copywriting inside CareTail.
- No Play Store deep-link fallback.
- No analytics event pipeline.
- No cloud sync or remote prompt rules.
- No billing or Premium changes.

## Why Not First Launch

First launch is too early because the user has not experienced value yet. It also risks feeling like a growth prompt instead of a product-quality request. CareTail waits for evidence of actual use and a successful moment.

## Why Reminder Completion Is Best

Marking a reminder complete is a clear positive outcome: the user created a care task, returned to it, and finished it. That is a natural moment to ask for feedback without interrupting setup or recovery from an error.

## Google Play Limitations

Google Play controls whether the in-app review dialog appears. A successful API call does not guarantee visible UI. CareTail records the attempt anyway so users are not repeatedly asked during the throttle window.

## QA Notes

1. Fresh install: no review prompt.
2. First launch: no review prompt.
3. Create pet: no review prompt.
4. Create first reminder: no review prompt.
5. Complete first reminder before 24 hours or 2 launches: no prompt.
6. After eligibility conditions, complete reminder: review flow may trigger.
7. Repeated completion should not spam prompt.
8. Delete actions never trigger prompt.
9. Failed validation never triggers prompt.
10. PremiumScreen never triggers prompt.

## Open Questions

- Whether edit saves should count toward `diaryEntrySavedCount` long term. Current behavior counts any successful diary save because the trigger says saved health diary entry.
- Whether review prompt attempts should later be observable in an analytics dashboard. No analytics exists in this local-first release.

## Handoff

This capability is ready for implementation through the current Android app lane. Future improvements should use a verification pass focused on Play-distributed testing because the review dialog may not appear in ordinary debug/emulator runs.
