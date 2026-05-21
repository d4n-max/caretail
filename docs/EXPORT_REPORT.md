# CareTail Export Care Report

Export Care Report creates a plain-text summary for one pet. It is intended for personal pet care organization and can be shared through Android's Sharesheet.

## Premium Gating

Export is a CareTail Premium feature. Free users who tap Export Care Report are routed to the Premium screen with the `ExportLocked` reason. Premium test mode in Settings enables export for local testing.

## Report Contents

The report includes:

- CareTail header
- Generated date
- Pet profile details
- Upcoming reminders
- Completed reminders
- Recent health diary entries
- Document records
- Safe footer note

Empty sections are handled with short messages such as `No upcoming reminders.` and `No documents yet.`

## Current Implementation

The MVP generates readable plain text and launches Android's Sharesheet with `Intent.ACTION_SEND`. No PDF library, backend, cloud sync, auth, or billing integration is added in this version.

## Safe Wording

The report uses organization-focused language such as care report, health notes, care history, symptom journal, vet records, reminders, and documents. It does not interpret conditions or guide care decisions.

## Current Limitations

- No PDF export yet.
- No custom date range yet.
- Attached files are listed as document records, not embedded.
- Photos are not included.
- Formatting is plain text.

## Future Improvements

- PDF export.
- Custom date range.
- Include attached document list with richer metadata.
- Include photos.
- Vet-friendly formatting.
- Cloud backup or sync when that feature exists.
