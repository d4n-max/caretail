# CareTail Notification Strategy

## Capability

CareTail notifies pet owners only about care reminders they explicitly create, using local Android notifications. The goal is a calm reminder system that helps users remember vaccines, medication, grooming, food, vet visits, and other user-created care tasks without marketing pushes, engagement spam, or alarming medical language.

## Fixed Constraints

- Notifications remain local-only.
- CareTail does not use Firebase Cloud Messaging, a push backend, auth, or server jobs for reminders.
- Notifications are only for user-created care reminders.
- No promotional, Premium upsell, daily engagement, comeback, or random diary notifications are allowed.
- Reminder records continue to own `dueAtMillis`; notification preferences do not change the database schema.
- Reminder creation, editing, completion, deletion, and repeat gating must continue to work.
- Reminder notification copy must stay calm and non-diagnostic.

## Permission Timing

CareTail does not request `POST_NOTIFICATIONS` on first launch.

On Android 13 and newer, the app asks for notification permission only after the user creates a reminder while care reminders are enabled, or when the user enables care reminders from Settings. Before the system prompt, CareTail shows an explanation:

- Title: `Enable care reminders?`
- Body: `CareTail can remind you about pet care tasks like vaccines, medication, grooming, and vet visits.`
- Actions: `Enable reminders` and `Not now`

If the user chooses `Not now` or denies the system permission, the reminder still saves. Local notification scheduling stays off until the user enables care reminders from Settings.

## Settings Behavior

Settings includes a Notifications section.

- `Care reminders`: real toggle. When off, CareTail does not schedule local reminder alarms and cancels existing scheduled reminder alarms. When turned on, Android 13+ users are asked for permission if needed, then future incomplete reminders are rescheduled.
- `Gentle overdue reminder`: documented as planned and not active yet.
- `Quiet hours`: documented as planned fixed window, 21:00 to 08:00, and not active yet.

The planned rows are intentionally not toggles so the UI does not imply unfinished behavior works.

## Notification Copy

Notification title:

`CareTail reminder`

Notification body:

`{petName}: {reminderTitle}`

Examples:

- `Luna: Vaccine due today`
- `Max: Heartworm medication`

Copy must avoid diagnosis, treatment recommendations, urgency escalation, or alarming language.

## Reminder Lifecycle

- Creating a future reminder schedules one local notification if care reminders are enabled and permission allows posting.
- Editing a future reminder cancels the old alarm and reschedules using the new `dueAtMillis`.
- Completing a reminder cancels its scheduled notification.
- Deleting a reminder cancels its scheduled notification.
- Deleting a pet cancels scheduled notifications for that pet's reminders.
- Deleting local data cancels scheduled reminder notifications before removing local records.

## Overdue Reminder Strategy

Gentle overdue reminders are not implemented in this release.

Future behavior should be:

- Send at most one overdue notification per reminder.
- Do not repeat endlessly.
- Do not send during quiet hours.
- Do not send if the reminder was completed.
- Use wording like: `{petName} has a care reminder that was not marked complete.`

This likely needs durable per-reminder overdue state or another reliable local tracking mechanism before implementation.

## Quiet Hours Strategy

Quiet hours are not implemented in this release.

Future behavior should avoid delivering non-urgent reminders during 21:00 to 08:00 by shifting eligible local notification alarms to the next allowed time. The UI should only expose editable quiet-hour times after scheduling behavior fully supports them.

## Limitations

- Android system settings can still block notifications outside CareTail.
- Repeating reminder rows are not advanced in Room when a repeat notification fires.
- Gentle overdue reminders are strategy-only for now.
- Quiet hours are strategy-only for now.
- CareTail does not send remote push notifications.

## Implementation Contract

- Actor: pet owner using CareTail reminders.
- Surfaces: Add/Edit Reminder, Settings, Android notification permission prompt, local notification receiver.
- State: care reminder notifications enabled or disabled in local preferences.
- Inputs: reminder `dueAtMillis`, reminder title, pet name, completion status, Android notification permission.
- Outputs: local alarm scheduling, local notification display, cancellation on completion/deletion/data deletion.
- Data implications: no Room schema change; preferences are stored locally in SharedPreferences.

## Handoff

This release is ready for implementation of the first production-safe notification slice: permission timing, care reminder toggle, and documentation. Gentle overdue reminders and quiet hours need a separate implementation task with lifecycle state and scheduling rules.
