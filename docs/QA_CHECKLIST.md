# CareTail MVP QA Checklist

Use this checklist for a fresh manual pass before sharing an MVP build.

## 1. Fresh install

- Install a clean debug build.
- Open the app and confirm onboarding appears.
- Tap Get Started and confirm Home opens without a crash.
- Confirm light mode text is readable on all first-run screens.

## 2. Pet flow

- Add the first pet with name, species, breed, gender, weight, and notes.
- Confirm the pet appears on Home, Pets, and Pet Profile.
- Edit the pet and confirm changes persist after leaving the screen.
- Try to add a second pet as a free user and confirm Premium opens.
- Enable Premium test mode and confirm a second pet can be added.
- Delete a pet from Pet Profile and confirm the confirmation dialog appears.
- Confirm deleted pet reminders, health notes, and document records no longer appear.

## 3. Reminder flow

- Add a reminder linked to a real pet.
- Confirm it appears on Reminders and Home.
- Edit the reminder and confirm title, type, date/time, repeat, and notes persist.
- Mark the reminder complete and confirm it moves to Completed.
- Mark it incomplete and confirm it returns to the active groups.
- Delete a reminder and confirm it disappears.
- Create a reminder a few minutes in the future and confirm the local notification appears.
- Tap the notification and confirm CareTail opens.
- Deny notification permission on Android 13+ and confirm the reminder still saves.

## 4. Health diary flow

- Add a health note linked to a real pet.
- Confirm mood, appetite, energy, symptoms, and notes are saved.
- Confirm the entry appears in Health Diary and Pet Profile.
- Edit the entry and confirm changes persist.
- Delete the entry and confirm the confirmation dialog appears.

## 5. Documents flow

- Add a document record linked to a real pet.
- Pick a file using the Android document picker.
- Confirm the record appears in Documents and Pet Profile.
- Open the attached file and confirm no crash if no compatible app is available.
- Edit the document title, type, file, and notes.
- Delete the document record and confirm the original file is not deleted.

## 6. Premium flow

- Confirm free limits are enforced: 1 pet, 5 active reminders, 5 health notes, 3 document records.
- Confirm completed reminders do not count toward the active reminder limit.
- Confirm Monthly and Yearly reminder repeats open the Premium gate for free users.
- Confirm PremiumScreen shows contextual reasons for pet, reminder, diary, document, export, and advanced repeat gates.
- Confirm Start Premium shows the billing-not-implemented message.
- Enable Premium test mode and confirm limits are disabled.
- Confirm export report is locked for free users.
- Confirm export report works with Premium test mode enabled.

## 7. Settings

- Confirm Premium test mode toggles on and off.
- Confirm Privacy, Data, and About rows render without crashes.
- Confirm unavailable MVP actions are clearly labeled and do not look like production billing or cloud features.

## 8. Navigation

- Confirm bottom nav tabs work: Home, Pets, Reminders, Diary, Settings.
- Confirm add and edit screens return to the expected screen after save.
- Confirm Back returns to the previous screen from add, edit, profile, Premium, and Settings flows.
- Confirm deleting a pet returns to Pets.
- Confirm repeated tab taps do not create confusing back stack loops.

## 9. Visual QA

- Confirm no invisible text in text fields, chips, cards, dialogs, or buttons.
- Confirm empty states appear for Pets, Home, Reminders, Health Diary, Documents, and Pet Profile sections.
- Confirm no visible TODO, lorem ipsum, misleading fake names, or unfinished placeholder text appears in user-facing screens.
- Confirm icon-only buttons have understandable accessibility labels.
- Confirm forms scroll and save buttons remain reachable on common phone sizes.
- Confirm light mode is polished and dark mode does not look broken if enabled.

## 10. Build and install

- Run `.\gradlew.bat :app:assembleDebug`.
- Confirm the build passes.
- Run `.\gradlew.bat :app:installDebug` on a physical device or emulator.
- Repeat the critical pet, reminder, diary, document, premium, and export flows on device.
