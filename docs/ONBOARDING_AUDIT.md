# CareTail Onboarding Audit

## Scope

Audit date: 2026-06-15

Reviewed surfaces:

- First launch experience
- Empty Home state
- Add first pet flow
- First reminder creation
- Settings Account section
- Premium entry points
- Notification permission timing
- Review prompt timing and aggressive copy

## Activation Goal

CareTail's first-session value is: create the first pet profile, understand that reminders are the next useful action, and feel safe continuing locally without Google Sign-In or Premium.

## Current Findings

### First Launch Experience

Finding: The launch screen explained value clearly, but the primary action said "Get started" and navigated to Home instead of the first value action.

Impact: A new user landed in an empty dashboard and had to infer that adding a pet was the next step.

Recommendation: Make the CTA specific and send first-time users directly to Add Pet.

Priority: High

Implemented: Yes. The CTA now says "Add your first pet," onboarding is remembered locally, and completion navigates to Add Pet.

### Empty Home State

Finding: Empty Home used returning-user copy ("Welcome back") and had a Premium card visible before the user created any value.

Impact: The first empty state felt slightly mismatched and could make monetization feel early.

Recommendation: Use first-use copy when there are no pets, make the next action obvious, and reassure users that the free/local plan is useful.

Priority: High

Implemented: Yes. Empty Home now says "Welcome to CareTail," points users toward one pet profile and reminders, and shows a free-plan reassurance card instead of a Premium CTA before a pet exists.

### Add First Pet Flow

Finding: The form is production-safe and already low friction, but the intro copy did not clarify which fields are required.

Impact: Optional fields could make setup feel heavier than it is.

Recommendation: Tell users only name and species are required.

Priority: Medium

Implemented: Yes.

### First Reminder Creation

Finding: Reminder creation already routes users to add a pet first when needed and delays notification permission until reminder intent.

Impact: Good timing, but users may not know reminders save even if notifications are off.

Recommendation: Add small explanatory copy before the reminder form.

Priority: Medium

Implemented: Yes.

### Settings Account Section

Finding: Google Sign-In lives in Settings and is not a login wall, but the intro copy led with future backup/sync rather than optionality.

Impact: Some users could interpret sign-in as expected or required.

Recommendation: Lead with optional/local-first reassurance, then explain future backup/sync.

Priority: High

Implemented: Yes.

### Premium Entry Points

Finding: Premium gates are tied to limits and include "Continue with Free." Premium messaging avoids guilt and explains free plan limits.

Impact: Mostly healthy. The only early-pressure issue was the Home Premium card before first pet creation.

Recommendation: Keep Premium visible after value is created or when a real limit is reached. Avoid first-session paywall pressure.

Priority: Medium

Implemented: Yes for empty Home. Existing limit gates were preserved.

### Notification Permission Timing

Finding: CareTail does not request notification permission on first launch. Android 13+ users see an explanation only when saving a reminder with reminders enabled or when enabling reminders in Settings.

Impact: This matches the notification strategy and avoids first-launch permission friction.

Recommendation: Preserve current timing.

Priority: High

Implemented: No code change needed.

### Review Prompt Timing

Finding: Review eligibility requires at least a pet profile, two launches, at least one day since first launch, and meaningful reminder or diary activity.

Impact: No review prompt during onboarding.

Recommendation: Preserve current thresholds.

Priority: High

Implemented: No code change needed.

## Implemented Changes

- First-time onboarding now routes to Add Pet and is remembered locally.
- Onboarding copy now says no account is required, the first pet is free, and Google Sign-In is optional later.
- Empty Home copy now matches a new user state.
- Empty Home now reassures free/local usefulness instead of showing a Premium CTA before first value.
- Add Pet copy now clarifies only name and species are required.
- Add Reminder copy now clarifies permission timing.
- Settings Account copy now leads with optional Google Sign-In and local-first reassurance.

## Non-Goals Preserved

- No login wall.
- Google Sign-In remains optional.
- Premium is not forced during onboarding.
- Notification permission is not requested on first launch.
- Review prompts are not requested during onboarding.
- No large onboarding system or full app redesign was added.

## Suggested Future Improvements

- After first pet creation, consider a gentle inline "Add first reminder" prompt on the pet profile or Home. Keep it dismissible and avoid a modal.
- Consider skipping onboarding automatically for users who already have local pet data after app updates.
- Track activation metrics when analytics policy is defined: first pet creation rate, time to first pet, first reminder creation rate, and Premium views after first value.
