# CareTail Smooth Raw Recording Summary

Date: 2026-06-23

Output folder: `C:\Projects\caretail\marketing-recordings\raw-smooth`

## Recording method used

Android Studio Emulator built-in recorder via the emulator console (`adb emu screenrecord`), captured from the running portrait Android emulator. The emulator recorder produced WebM captures that were transcoded to H.264 MP4 with ffmpeg.

OBS Studio was available, but it was not used because desktop/window capture of the emulator surface was unreliable during setup. The Android Emulator built-in recorder captured the correct CareTail display buffer. `adb shell screenrecord` was not used.

## Smoothness confirmation

All completed files below are real 24 fps motion recordings at `1344x2992` portrait resolution. They are not screenshot-sequence videos and were not created with `adb shell screenrecord`.

## Completed files

| Filename | Duration | FPS | Notes |
| --- | ---: | ---: | --- |
| `01_app_overview_one_calm_place_smooth.mp4` | 00:00:15.04 | 24 | Completed |
| `03_multi_pet_profiles_smooth.mp4` | 00:00:17.04 | 24 | Completed |
| `05_add_reminder_in_a_few_taps_smooth.mp4` | 00:00:21.42 | 24 | Completed; ends on saved reminder list with demo `WaterCheck` reminder |
| `07_weekly_pet_care_reset_smooth.mp4` | 00:00:24.21 | 24 | Completed |
| `08_health_diary_notes_smooth.mp4` | 00:00:18.58 | 24 | Completed |
| `10_documents_and_records_smooth.mp4` | 00:00:18.46 | 24 | Completed |
| `11_vet_visit_prep_diary_documents_report_smooth.mp4` | 00:00:23.88 | 24 | Completed |
| `13_local_first_optional_account_smooth.mp4` | 00:00:12.04 | 24 | Completed |
| `14_premium_more_room_smooth.mp4` | 00:00:17.46 | 24 | Completed |
| `16_best_use_cases_montage_smooth.mp4` | 00:00:32.46 | 24 | Completed |
| `18_clean_idle_screens_pack_smooth.mp4` | 00:00:38.25 | 24 | Completed |

## Skipped files

None.

## Manual re-record recommendations

None required from the smoothness verification pass.

## Additional notes

- Final recordings show only the emulator/CareTail app content, not Android Studio code panels.
- Demo-looking CareTail data was used.
- Clip 05 includes a short in-app confirmation toast saying notifications are disabled after saving the reminder.
