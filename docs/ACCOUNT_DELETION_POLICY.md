# Account Deletion Policy

CareTail v1.1 supports optional account deletion for users who sign in with Google.

## In-App Deletion Path

```text
Settings > Account > Delete account
```

The confirmation dialog offers:

- Delete account only
- Delete account and local app data
- Cancel

## What Is Deleted

Delete account only:

- Deletes the Firebase Authentication user account for CareTail.
- Signs the user out after Firebase confirms deletion.
- Keeps local pet care data on the device.

Delete account and local app data:

- Deletes the Firebase Authentication user account for CareTail.
- Deletes local CareTail pet profiles, reminders, health diary entries, and document records from the device.
- Cancels scheduled local reminder notifications.

## What Remains Local Unless Explicitly Deleted

Unless the user chooses local app data deletion, CareTail keeps existing local Room data on the device:

- pet profiles
- care reminders
- health diary entries
- document records
- local settings

Sign out does not delete local pet care data.

## Recent Login Requirement

Firebase may require recent sign-in before account deletion. If this happens, CareTail shows:

```text
Please sign in again before deleting your account.
```

The user should sign in again with Google, then retry deletion.

## Public Request URL

Replace this placeholder before Google Play submission:

```text
TODO: https://caretail.example.com/account-deletion
```

## Contact

Replace this placeholder before Google Play submission:

```text
TODO: support@caretail.example
```
