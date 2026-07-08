# CareTail Raw Recording Summary

Date: 2026-06-23

Output format: MP4.

Capture note: Android `adb screenrecord` was not usable on this emulator because it encoded only one frame. The final files were created from direct device-buffer screenshots and encoded as low-frame-rate sequential MP4 clips. They show the correct CareTail screens and flows, but they are not smooth live-motion recordings.

## Completed Clips

| File | Duration |
| --- | ---: |
| 01_app_overview_one_calm_place.mp4 | 18s |
| 02_home_dashboard_scan.mp4 | 10s |
| 03_multi_pet_profiles.mp4 | 18s |
| 04_separate_reminders_per_pet.mp4 | 18s |
| 05_add_reminder_in_a_few_taps.mp4 | 25s |
| 06_reminder_types.mp4 | 15s |
| 07_weekly_pet_care_reset.mp4 | 23s |
| 08_health_diary_notes.mp4 | 18s |
| 09_add_diary_note_flow.mp4 | 23s |
| 10_documents_and_records.mp4 | 18s |
| 11_vet_visit_prep_diary_documents_report.mp4 | 25s |
| 12_export_report.mp4 | 18s |
| 13_local_first_optional_account.mp4 | 15s |
| 14_premium_more_room.mp4 | 18s |
| 15_from_scattered_to_organized_app_only.mp4 | 18s |
| 16_best_use_cases_montage.mp4 | 30s |
| 17_founder_build_log_walkthrough.mp4 | 25s |
| 18_clean_idle_screens_pack.mp4 | 36s |

## Screenshots

Saved in `C:\Projects\caretail\marketing-recordings\screenshots`:

| File |
| --- |
| 01_home.png |
| 02_pets_list.png |
| 03_pet_profile.png |
| 04_reminders.png |
| 05_add_reminder.png |
| 06_diary.png |
| 07_documents.png |
| 08_export_report_or_entry.png |
| 09_premium.png |
| 10_settings_local_first.png |

## Skipped

None. All requested clip names were completed as MP4 files.

## Issues / Manual Re-Record Notes

- The clips are low-frame-rate sequential captures, not smooth screen recordings. For polished social edits, consider re-recording the highest-priority flows manually with Android Studio's Emulator recorder or another host video recorder.
- Add Reminder and Add Diary clips show the form flows and fields, but they avoid relying on a final save step so the demo data stays clean.
- Premium test mode was enabled only to unlock gated debug flows. The final optional-account clip avoids showing the debug toggle.
- Demo data present during capture: Luna and Max, multiple reminders, diary notes, and document records.
