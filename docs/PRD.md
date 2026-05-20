# CareTail Product Requirements Document

## 1. Product Summary

CareTail is a premium Android pet care tracker for pet owners who want to manage profiles, care reminders, health notes, vet records, documents, and daily routines in one calm, polished app.

CareTail focuses on care management first. The MVP is local-first, stores data on device, and does not require an account system, backend, cloud sync, booking, marketplace, tele-vet services, insurance products, or diagnostic features.

App details:

- App name: CareTail
- Tagline: Your Pet's Personal Care Tracker
- Package name: com.caretail.app
- Platform: Android
- Initial audience: Cat and dog owners first

## 2. Product Vision

CareTail should become the trusted daily care hub for pet owners. It helps users keep pet care organized, remember important routines, preserve care history, and prepare for vet visits without feeling overwhelmed.

The product should feel premium, warm, and reassuring. It should make pet care easier through clear reminders, structured records, and a polished interface, while avoiding claims that it can diagnose, treat, or replace professional veterinary care.

## 3. Target Users

- First-time pet parents who need structure and confidence.
- Busy professionals who need quick reminders and simple record keeping.
- Multi-pet households managing different routines and records.
- Owners of aging pets who need more frequent medication, grooming, vet visit, and care history tracking.
- Cat and dog owners first, with room to support more pet types later.

## 4. Main User Problems

- Pet care information is scattered across notes, photos, paper records, calendars, and memory.
- Owners forget recurring care tasks such as vaccines, medication, grooming, and vet visits.
- Multi-pet households struggle to keep each pet's care history separate.
- Owners want to track health notes and symptom journals without using a clinical or complex tool.
- Vet records and documents are hard to find when needed.
- Existing tools often feel either too basic, too childish, or too business-focused.

## 5. Core Value Proposition

CareTail gives pet owners a calm, polished, all-in-one place to manage pet profiles, care reminders, health notes, documents, and daily routines.

The MVP value is simple: users can quickly understand what care is due, record what happened, and keep important pet records organized on their device.

## 6. MVP Feature List

The MVP should include:

- Local pet profiles with name, species, breed, age or birthday, weight, photo/avatar, and notes.
- Basic care reminders for vaccines, medication, grooming, food, vet visits, and other tasks.
- Reminder list and add/edit reminder screens.
- Health diary entries for mood, appetite, energy, symptoms, notes, and date.
- Documents section for tracking vet records, vaccine certificates, lab reports, and other files.
- Home dashboard showing pets, today's care, upcoming reminders, and quick actions.
- Pet profile screen showing profile details, upcoming reminders, recent health diary entries, and documents.
- Settings screen with app preferences and premium placeholder.
- Onboarding screen introducing the product.
- Local-first storage on device.
- Sample-friendly UI foundations that can later connect to persistence.

Free version limits:

- 1 pet profile.
- Basic reminders.
- Limited health diary entries.
- Limited documents.

## 7. Premium Feature List

Premium should be positioned as the upgrade for households that need deeper care tracking.

Planned Premium features:

- Unlimited pets.
- Advanced recurring reminders.
- Unlimited health diary entries.
- Unlimited documents.
- Exportable pet health report.
- Future cloud backup.

Google Play Billing is planned later and is not part of Task 1.

## 8. Features Out of Scope for v1

The following are out of scope for MVP/v1:

- Room implementation during initial PRD/task setup.
- Backend services.
- Account system or login.
- Cloud sync.
- Real billing implementation.
- Real push notifications.
- Booking services.
- Pet insurance purchase flows.
- Marketplace or product shopping.
- Tele-vet services.
- AI diagnosis or AI vet features.
- Claims to diagnose, treat, provide medical advice, or replace a veterinarian.

Future monetization may include booking referrals, pet insurance leads, pet supply affiliate revenue, and cloud backup, but these should not be built in the MVP.

## 9. Main App Screens

1. Onboarding
2. Home dashboard
3. Pets list
4. Pet profile
5. Add/Edit pet
6. Reminders
7. Add/Edit reminder
8. Health diary
9. Add diary entry
10. Documents
11. Premium
12. Settings

## 10. Core User Flows

Onboarding to first pet:

1. User opens CareTail.
2. User sees the onboarding value proposition.
3. User taps Get started.
4. User creates the first pet profile.
5. User lands on the Home dashboard.

Create a care reminder:

1. User opens Reminders or taps a quick action.
2. User selects a pet.
3. User selects reminder type.
4. User sets date, time, repeat option, and notes.
5. User saves the care reminder.
6. Reminder appears in upcoming care views.

Log a health diary entry:

1. User opens Health Diary.
2. User selects a pet.
3. User adds a diary entry with mood, appetite, energy, symptom journal notes, and optional details.
4. Entry appears in the timeline grouped by date.

Review pet profile:

1. User opens Pets.
2. User selects a pet.
3. User sees profile details, upcoming reminders, recent health notes, and documents.
4. User can edit the profile or review care history.

Upgrade prompt:

1. User reaches a free limit or opens Premium.
2. User sees Premium benefits.
3. User can review monthly and yearly plan placeholders.
4. Billing implementation is added in a later task.

## 11. Data Model Overview

MVP data is stored on device and does not require a backend.

Core entities:

- Pet: id, name, species, breed, birthday or age, weight, avatar/photo reference, notes, createdAt, updatedAt.
- Reminder: id, petId, type, title, date, time, repeatRule, notes, status, createdAt, updatedAt.
- HealthDiaryEntry: id, petId, entryDate, mood, appetite, energy, symptomJournal, healthNotes, createdAt, updatedAt.
- DocumentRecord: id, petId, title, documentType, localUri, notes, recordDate, createdAt, updatedAt.
- AppSettings: reminder preferences, unit preferences, theme preference, premium status placeholder.

Potential local storage approach:

- Start with simple local state and sample data for UI tasks.
- Add Room later when persistence is explicitly scheduled.
- Keep models clean so local persistence can be introduced without redesigning the UI.

## 12. Monetization Plan

CareTail should monetize through a Premium subscription.

Free version:

- 1 pet profile.
- Basic reminders.
- Limited health diary entries.
- Limited documents.

Premium version:

- Unlimited pets.
- Advanced recurring reminders.
- Unlimited health diary.
- Unlimited documents.
- Exportable pet health report.
- Future cloud backup.

Implementation notes:

- Google Play Billing should be added in a later task.
- MVP can include static Premium screens and upgrade prompts only.
- Premium language should focus on organization, convenience, and expanded limits.

## 13. Privacy and Safety Notes

CareTail may store sensitive pet care information, vet records, document references, and health notes.

MVP privacy posture:

- Local-first storage.
- Data stored on device in MVP.
- No backend required.
- No account system required.
- No cloud sync in MVP.
- No network calls required for MVP.

Safety wording:

- Use terms such as health notes, care history, symptom journal, vet records, and care reminders.
- Do not claim to diagnose or treat conditions.
- Do not present content as medical advice.
- Encourage users to consult a licensed veterinarian for health concerns.

## 14. Google Play Compliance Considerations

CareTail should be positioned as a pet care organization and record keeping app, not a medical or diagnostic app.

Compliance considerations:

- Avoid claims that CareTail can diagnose, treat, or replace a veterinarian.
- Avoid language such as AI vet, medical advice, or diagnosis.
- Clearly describe reminders as user-managed care reminders.
- If billing is added later, use Google Play Billing for digital subscription purchases.
- If document storage is added, request only necessary Android permissions.
- If notifications are added, request notification permission only when needed and explain the value clearly.
- If cloud sync is added later, provide a privacy policy and clear data handling disclosures.
- If affiliate or referral monetization is added later, disclose sponsored or referral relationships where required.

## 15. Success Metrics

MVP success metrics:

- First pet profile creation rate.
- Reminder creation rate.
- Weekly active users.
- Number of health diary entries per active user.
- Number of document records added.
- Day 1, Day 7, and Day 30 retention.
- Free limit encounters.
- Premium screen views.
- Premium conversion rate after billing is implemented.
- App store rating and qualitative review themes.

Quality metrics:

- Crash-free sessions.
- Time to add first pet.
- Time to create a reminder.
- User-reported clarity of UI.

## 16. 14-Day Development Roadmap

Day 1:

- Finalize PRD.
- Confirm app scope, brand colors, screen list, and MVP boundaries.

Day 2:

- Create Android project structure.
- Set package name to com.caretail.app.
- Add Material 3 theme foundations.

Day 3:

- Build reusable UI components.
- Add scaffold, top bar, bottom navigation, cards, buttons, chips, avatars, and status pills.

Day 4:

- Implement onboarding and home dashboard UI with sample data.

Day 5:

- Implement pets list and pet profile UI with sample data.

Day 6:

- Implement add/edit pet UI.

Day 7:

- Implement reminders and add/edit reminder UI.

Day 8:

- Implement health diary and add diary entry UI.

Day 9:

- Implement documents UI with local placeholder states.

Day 10:

- Implement Premium and Settings UI placeholders.

Day 11:

- Add local model classes and simple sample repositories if needed for UI consistency.

Day 12:

- Add local persistence planning or begin Room only if explicitly scheduled after UI foundation approval.

Day 13:

- Polish spacing, typography, accessibility labels, empty states, and navigation behavior.

Day 14:

- Run build checks, fix UI regressions, and prepare MVP implementation notes.

## 17. Future Roadmap After MVP

Near-term after MVP:

- Room database persistence.
- Real notification scheduling.
- Document picker and local file references.
- Exportable pet health report.
- Google Play Billing for Premium.
- Improved tablet layouts.

Later roadmap:

- Cloud backup.
- Account system only if needed for sync.
- Multi-device sync.
- Shared pet records for family members.
- Booking referrals.
- Pet insurance lead generation.
- Pet supply affiliate revenue.
- Expanded pet types beyond cats and dogs.

Future features must preserve the product's care-management focus and avoid medical diagnosis or treatment claims.

## 18. UI/UX Direction Based on the Approved Stitch Concept

CareTail should use the approved Google Stitch direction: polished, subscription-quality, calm, warm, and modern.

Brand system:

- Primary color: #4ECDC4
- Accent / CTA color: #FF6B6B
- Background: #F7FBFB
- Warm surface: #FFF8F4
- Card background: #FFFFFF
- Text primary: #1F2933
- Text secondary: #6B7280

Visual direction:

- Premium, warm, clean, trustworthy, modern, friendly, not childish.
- Turquoise for brand elements, selected states, and status indicators.
- Coral for primary CTAs and important actions.
- Rounded white cards with soft shadows.
- Generous spacing and high readability.
- Subtle pet-themed icons.
- Avoid childish or cartoonish styling.
- Avoid excessive paw icons.

Navigation:

- Bottom navigation tabs: Home, Pets, Reminders, Diary, Settings.
- Main actions should be visible and easy to reach.
- Screens should support quick scanning and repeated daily use.

Screen direction:

- Onboarding should show a large pet image/avatar area, clear value proposition, coral Get started CTA, and small login placeholder.
- Home dashboard should show greeting, pet cards, quick actions, today's care, upcoming care, and bottom navigation.
- Pet profile should show a large rounded profile card, pet avatar, key details, status pill, and record sections.
- Reminder creation should use pet selector chips, reminder type chips, date/time rows, repeat selector, notes, and coral save CTA.
- Health diary should use a timeline grouped by date, care state chips, and coral add action.
- Premium should show benefits, pricing cards, Best Value yearly state, coral Start Premium CTA, and Restore Purchase placeholder.
