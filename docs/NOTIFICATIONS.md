# CareTail Local Notifications

CareTail schedules local reminder alerts with Android `AlarmManager`. When a reminder is saved, the app creates an alarm for the reminder's due time. The alarm is local to the device and does not use a backend, auth, cloud sync, or server jobs.

## Notification Channel

- Channel ID: `caretail_reminders`
- Channel name: `Care reminders`
- Description: `Notifications for pet care reminders`
- Importance: default

The channel is created when the app starts through `CareTailApplication`.

## Android 13 Permission

Android 13 and newer require `POST_NOTIFICATIONS`. CareTail does not ask for this permission on first app launch.

CareTail asks only after the user creates a reminder while care reminders are enabled, or after the user explicitly enables care reminders from Settings. Before the system prompt, the app shows a CareTail explanation dialog. If the user chooses `Not now` or denies permission, the reminder still saves in Room and local notification scheduling remains off.

Settings includes a `Care reminders` toggle. Turning it off cancels scheduled reminder alarms. Turning it on asks for permission if needed and reschedules future incomplete reminders after permission is available.

## Scheduling Behavior

- Creating a reminder schedules a local alarm.
- Creating a reminder still saves if notifications are off or permission is denied.
- Completing a reminder cancels its alarm.
- Deleting a reminder cancels its alarm.
- Marking an incomplete future reminder reschedules its alarm.
- A helper exists for rescheduling edited reminders.
- Deleting local data cancels scheduled reminder alarms before local records are removed.

CareTail uses `setAndAllowWhileIdle` for a simple MVP implementation and does not request special exact alarm permissions.

## Repeat Behavior

Repeating reminders schedule the next local alarm after the notification fires:

- `Daily`: plus 1 day
- `Weekly`: plus 7 days
- `Monthly`: plus 1 calendar month
- `Yearly`: plus 1 calendar year

Current limitation: repeat alarms are rescheduled locally by the receiver, but the Room reminder row is not advanced to the next due date yet. A future edit/reminder recurrence task should update `dueAtMillis` in Room when a repeat fires.

## Manual QA Checklist

1. Create a reminder a few minutes in the future.
2. Confirm the permission explanation appears on Android 13+ before the system prompt.
3. Grant permission and confirm notification appears.
4. Tap notification and confirm the app opens.
5. Mark a reminder completed and verify its notification is cancelled.
6. Delete a reminder and verify its notification is cancelled.
7. Deny notification permission and confirm the reminder still saves.
8. Turn care reminders off in Settings and confirm scheduled reminder notifications stop.
9. Turn care reminders on in Settings and confirm future incomplete reminders are rescheduled.
