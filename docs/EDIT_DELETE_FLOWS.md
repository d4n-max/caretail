# CareTail Edit and Delete Flows

CareTail supports editing and deleting the core local-first records: pets, reminders, health diary entries, and documents.

## Supported Edit Flows

- Pet profiles can be edited from Pet Profile.
- Reminders can be edited from Reminders.
- Health notes can be edited from Health Diary.
- Document records can be edited from Documents & Records.

The add screens are reused in edit mode where practical. Edit mode loads the existing Room row, pre-fills the form, preserves `createdAtMillis`, and updates `updatedAtMillis`.

## Supported Delete Flows

- Pet profiles can be deleted from Pet Profile after confirmation.
- Reminders can be deleted from Reminders after confirmation.
- Health notes can be deleted from Health Diary after confirmation.
- Document records can be deleted from Documents & Records after confirmation.

Document deletion removes only the CareTail Room record. It does not delete the original file from device storage.

## Pet Cascade Behavior

Pet-related reminders, health diary entries, and document records use Room foreign keys with cascade delete. Deleting a pet removes related local records from this device.

Before deleting a pet, CareTail cancels scheduled notifications for that pet's reminders.

## Reminder Notification Behavior

- Editing an incomplete future reminder reschedules its notification.
- Editing a completed reminder keeps notification cancelled.
- Deleting a reminder cancels its scheduled notification.
- Deleting a pet cancels scheduled notifications for that pet's reminders before the pet is removed.

## Current Limitations

- No undo delete yet.
- No recycle bin.
- No audit trail.
- Date editing still uses simple text/default fields where date pickers are not implemented.
- Photo editing is still a placeholder.

## Future Improvements

- Undo delete.
- Recycle bin.
- Audit trail.
- Photo editing.
- Better date picker.
