# CareTail Health Diary

The Health Diary is a local-first care history for pet wellbeing notes. It helps users keep daily check-ins, symptom journal notes, mood, appetite, and energy observations connected to a pet profile.

## Safe Wording

CareTail uses supportive tracking language:

- health notes
- care history
- symptom journal
- wellbeing notes
- vet records
- daily check-in

The feature only records observations and does not interpret conditions or guide care decisions.

## Data Model

Health diary entries are stored in Room with `HealthDiaryEntryEntity`:

- `id`
- `petId`
- `entryDateMillis`
- `mood`
- `appetite`
- `energyLevel`
- `symptoms`
- `notes`
- `imageUri`
- `createdAtMillis`
- `updatedAtMillis`

Entries are local to the device and tied to existing pet records.

## User Flow

1. User opens Health Diary.
2. User selects a real pet profile.
3. User taps Add Entry.
4. User chooses mood, appetite, and energy.
5. User optionally adds symptoms and wellbeing notes.
6. Entry is saved in Room and appears in the diary grouped by date.
7. Recent entries also appear on the pet profile.

## Current Limitations

- No image picker yet; `imageUri` remains `null`.
- No editing screen yet.
- No export or document generation yet.
- No cloud sync, auth, backend, or sharing.
- No medical interpretation is generated.

## Future Improvements

- Image picker for attaching photos.
- Export to pet health report.
- Advanced filters by date, pet, mood, appetite, or energy.
- Vet-friendly PDF summary.
- Optional edit entry flow.
