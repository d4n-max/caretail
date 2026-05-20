# CareTail Database

CareTail is local-first for MVP. The Room database stores pet care data on device and does not require a backend, account system, cloud sync, billing, or notifications.

## Database Purpose

The database provides durable local storage for:

- Pet profiles
- Care reminders
- Health diary entries
- Pet documents and vet record references

Database name: `caretail.db`

## Entity Overview

`PetEntity`

- Stores one pet profile.
- Includes name, species, breed, gender, birth date, weight, photo URI, notes, and timestamps.
- MVP species values are `Cat`, `Dog`, and `Other`.

`ReminderEntity`

- Stores care reminders for a pet.
- Includes title, type, notes, due time, repeat type, completion state, and timestamps.
- Uses a foreign key to `PetEntity`.
- Deleting a pet cascades to its reminders.

`HealthDiaryEntryEntity`

- Stores health notes and symptom journal entries for a pet.
- Includes mood, appetite, energy level, symptoms, notes, optional image URI, and timestamps.
- Uses a foreign key to `PetEntity`.
- Deleting a pet cascades to its diary entries.

`PetDocumentEntity`

- Stores local document references for vet records and care documents.
- Includes title, document type, optional file URI, notes, and timestamps.
- Uses a foreign key to `PetEntity`.
- Deleting a pet cascades to its documents.

## DAO Overview

`PetDao`

- Observes all pets.
- Gets or observes a pet by ID.
- Inserts, updates, and deletes pets.
- Returns pet count for free-limit checks later.

`ReminderDao`

- Observes all reminders.
- Observes reminders for a pet.
- Observes upcoming, today, and completed reminders.
- Inserts, updates, deletes, and toggles completion state.

`HealthDiaryDao`

- Observes diary entries for a pet.
- Observes recent diary entries for a pet.
- Gets, inserts, updates, and deletes entries.

`PetDocumentDao`

- Observes documents for a pet.
- Observes all documents.
- Gets, inserts, updates, and deletes document records.

## Repository Overview

Repositories wrap DAOs and expose MVVM-ready methods using `Flow` and suspend functions:

- `PetRepository`
- `ReminderRepository`
- `HealthDiaryRepository`
- `PetDocumentRepository`

Repositories currently accept Room entity objects directly. Domain models and mapping can be added later when business logic grows.

## Database Provider

`DatabaseProvider` creates a singleton Room database using `applicationContext`.

`AppContainer` provides simple repository construction without Hilt. This keeps dependency setup lightweight for the MVP.

## Sample Data

`SampleDataProvider` contains static sample entities matching the current UI examples, including Luna, Max, Heartworm Meds, Rabies Booster, and Grooming Session.

Sample data is not inserted automatically into the database.

## Current Limitations

- No automatic sample seeding.
- No migrations yet because this is version 1.
- No cloud sync.
- No account system.
- No notification scheduling.
- No Google Play Billing.
- No domain model mapping layer yet.

## Future Notes

Future database work should include:

- Room migrations for schema changes.
- Domain model mapping if business logic becomes more complex.
- Exportable care reports.
- Optional cloud backup and sync after MVP.
- Privacy review before any cloud or sharing feature is introduced.
