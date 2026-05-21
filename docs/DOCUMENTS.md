# CareTail Documents & Records

Documents & Records keeps local metadata for pet care documents such as vet records, vaccine records, prescriptions, insurance documents, and other care documents.

## Local-First Storage

Document records are saved in Room with `PetDocumentEntity`. CareTail stores the title, type, notes, linked pet, timestamps, and an optional file URI. Files selected through the Android picker remain in their original provider location; CareTail does not upload them or copy them into cloud storage.

## Storage Access Framework

CareTail uses Android's Storage Access Framework through `OpenDocument`. Supported picker types include:

- PDF files
- Images
- Text files
- Word documents

When a file is selected, the app attempts to persist read access with `takePersistableUriPermission` and stores the URI string in Room.

## Storage Permission Policy

CareTail does not request broad storage permissions. It does not use `MANAGE_EXTERNAL_STORAGE`, legacy external storage, cloud sync, backend upload, or account-based document storage.

## Current Limitations

- File URI access depends on the document provider continuing to grant access.
- Files are not copied into app-private storage.
- No thumbnails yet.
- No search or advanced filters yet.
- No export health report feature yet.

## Future Improvements

- Optional cloud backup.
- PDF health report export.
- Document categories.
- Search and filters.
- Document thumbnails.
- OCR for quick record lookup if useful later.
