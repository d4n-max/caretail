# CareTail Local Notifications

CareTail schedules local reminder alerts with Android `AlarmManager`. When a reminder is saved, the app creates an alarm for the reminder's due time. The alarm is local to the device and does not use a backend, auth, cloud sync, or server jobs.

## Notification Channel

- Channel ID: `caretail_reminders`
- Channel name: `Care reminders`
- Description: `Notifications for pet care reminders`
- Importance: default

The channel is created when the app starts through `CareTailApplication`.

## Android 13 Permission

Android 13 and newer require `POST_NOTIFICATIONS`. CareTail asks for this permission when the user saves a reminder. If the user denies permission, the reminder is still saved in Room and the app shows: `Reminder saved. Notifications are disabled.`

## Scheduling Behavior

- Creating a reminder schedules a local alarm.
- Completing a reminder cancels its alarm.
- Deleting a reminder cancels its alarm.
- Marking an incomplete future reminder reschedules its alarm.
- A helper exists for rescheduling edited reminders.

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
2. Confirm notification appears.
3. Tap notification and confirm the app opens.
4. Mark a reminder completed and verify its notification is cancelled.
5. Delete a reminder and verify its notification is cancelled.
6. Deny notification permission and confirm the reminder still saves.
7. Re-enable notification permission and test again.
