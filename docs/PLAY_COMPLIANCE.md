# CareTail Google Play Compliance Preparation

## 1. App Data Model Summary

CareTail stores local pet care records for the MVP. Current local data includes pet profiles, care reminders, health diary entries, and document record metadata.

- Pet profiles: name, species, breed, gender, birth date, weight, photo URI, notes, timestamps.
- Reminders: pet link, title, reminder type, due date/time, repeat type, completion state, notes, timestamps.
- Health diary entries: pet link, entry date, mood, appetite, energy level, symptoms, notes, optional image URI, timestamps.
- Documents and records: pet link, title, document type, file URI, notes, timestamps.

## 2. Local-First MVP Explanation

The MVP is local-first. App records are stored on the user's device using the CareTail Room database. The MVP does not require a backend service to create, view, edit, or delete records.

## 3. No Account System In MVP

CareTail MVP does not include account creation, login, user profiles, or account-based identity.

## 4. No Backend Or Cloud Sync In MVP

CareTail MVP does not send pet care records to CareTail servers and does not include cloud sync. Cloud backup or sync may be evaluated after MVP.

## 5. No Selling User Data

CareTail MVP does not sell user data. There is no advertising SDK or third-party data sale flow in the MVP.

## 6. No Broad Storage Permissions

CareTail should not request broad storage permissions such as `MANAGE_EXTERNAL_STORAGE` or legacy external storage permissions. Document records should rely on user-selected file URIs rather than broad device file access.

## 7. Storage Access Framework

Document picking uses Android user-mediated file selection. CareTail stores document metadata and the selected file URI in the local database. Deleting local app data removes CareTail's database record, but does not delete the original user file from device storage.

## 8. Notifications Permission

CareTail uses notification permission for care reminders. Reminder notifications are for user-created pet care reminders only.

## 9. Health Wording Limitation

CareTail organizes personal pet care data. It supports care notes, health notes, symptom journaling, care history, vet records, reminders, and documents.

CareTail does not diagnose or treat conditions, does not provide medical advice, and does not replace a veterinarian. Exported reports include the note: "This report is for personal pet care organization only and does not replace veterinary advice."

## 10. Data Deletion

Users can delete local CareTail records from Settings. The Delete Local Data action removes pet profiles, reminders, health diary entries, and document records from the local Room database. Existing user files referenced by document records are not deleted from device storage.

## 11. Future Work Before Release

- Add a hosted Privacy Policy URL.
- Add a hosted Terms of Use URL.
- Complete the Google Play Data Safety form using the final permission and data usage behavior.
- Add account deletion only if an account system is introduced later.
- Add billing disclosures when Google Play Billing is implemented.
- Re-check notification permission copy and onboarding before production release.
