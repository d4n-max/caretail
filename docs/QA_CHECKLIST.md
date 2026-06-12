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
- Open Add Reminder.
- Tap Date.
- Confirm calendar picker opens.
- Select a date.
- Confirm keyboard does not open.
- Tap Time.
- Confirm time picker opens.
- Select hour and minute.
- Save reminder.
- Confirm reminder appears with correct date/time.
- Edit reminder.
- Confirm existing date/time are prefilled.
- Change date/time and save.
- Confirm notification scheduling still works.
- Confirm Monthly/Yearly Premium repeat options do not cause PremiumScreen navigation loop.
- Confirm it appears on Reminders and Home.
- Edit the reminder and confirm title, type, date/time, repeat, and notes persist.
- Mark the reminder complete and confirm it moves to Completed.
- Mark it incomplete and confirm it returns to the active groups.
- Delete a reminder and confirm it disappears.
- Create a reminder a few minutes in the future and confirm the local notification appears.
- Tap the notification and confirm CareTail opens.
- Deny notification permission on Android 13+ and confirm the reminder still saves.

## 3a. Notification QA

1. Create reminder.
2. Permission explanation appears only after first reminder or enabling reminders.
3. Grant permission.
4. Reminder notification appears.
5. Deny permission.
6. Reminder still saves.
7. Complete reminder cancels notification.
8. Delete reminder cancels notification.
9. Edit reminder reschedules notification.
10. Delete local data cancels notifications if implemented.
11. No notification appears on first app launch.

## 3b. Review prompt QA

1. Fresh install: no review prompt.
2. First launch: no review prompt.
3. Create pet: no review prompt.
4. Create first reminder: no review prompt.
5. Complete first reminder before 24h/2 launches: no prompt.
6. After eligibility conditions, complete reminder: review flow may trigger.
7. Repeated completion should not spam prompt.
8. Delete actions never trigger prompt.
9. Failed validation never triggers prompt.
10. PremiumScreen never triggers prompt.

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
- Enable Premium test mode and confirm limits are disabled.
- Confirm export report is locked for free users.
- Confirm export report works with Premium test mode enabled.

## 6a. Billing QA

1. PremiumScreen loads products.
2. Monthly price appears from Play.
3. Yearly price appears from Play.
4. Monthly purchase starts billing flow.
5. Yearly purchase starts billing flow.
6. Canceled purchase does not unlock Premium.
7. Successful purchase unlocks Premium.
8. Restore purchases works.
9. Free limits still work without Premium.
10. Premium gates unlock with active subscription.
11. Debug Premium test mode is not visible in release build.
12. App does not crash if BillingClient cannot connect.
13. Products unavailable state is handled gracefully.

## 7. Settings

- Confirm Premium test mode toggles on and off.
- Confirm Restore purchases checks Google Play and reports restored or no active subscription.
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
