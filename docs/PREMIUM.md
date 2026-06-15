# CareTail Premium

CareTail Premium is a freemium upgrade for better organization and peace of mind. The free tier must remain useful for basic pet care, especially health notes, medication context, vaccines, grooming, vet visits, and access to existing records.

## Free Tier

- 1 pet profile
- 5 active reminders
- 5 health diary entries
- 3 document records
- Basic medication, vaccine, grooming, vet visit, symptom, and health note tracking
- Basic diary and recent care history
- Local document records
- Local device storage
- Access to all existing user-created records after downgrade

Completed reminders do not count toward the active reminder limit.

## Premium Tier

- Unlimited pets
- Unlimited active reminders
- Unlimited health diary entries
- Unlimited document records
- Monthly and yearly reminder repeats
- Exportable care reports for vet visits
- Organize documents and care history
- Multi-pet household organization

Future cloud backup support may become a Premium feature later, but it is not an active benefit in this release.

Premium should feel like upgrading to better organization, not paying to care for a pet.

## Hard Gates

- Creating pet #2
- Creating active reminder #6
- Creating health diary entry #6
- Creating document record #4
- Selecting Monthly or Yearly repeat schedules
- Exporting a care report

## Not Hard-Gated

- Viewing existing health notes and symptom entries
- Editing or deleting health notes and symptom entries
- Viewing existing local document records
- Editing or deleting local document records
- Viewing existing pet profiles, reminders, health notes, documents, and history
- Editing or deleting user-created records
- Receiving notifications for existing reminders

## Paywall Copy Principles

- Lead with organization, vet readiness, and peace of mind.
- Always provide a visible `Continue with Free` action.
- Never use guilt, fear, or copy implying that paying makes someone a better pet owner.
- Reassure users that existing pet records remain available without Premium.

## Subscription Products

- Monthly subscription product ID: `caretail_premium_monthly`
- Monthly base plan ID: `monthly`
- Yearly subscription product ID: `caretail_premium_yearly`
- Yearly base plan ID: `yearly`

The yearly plan is selected by default and should be positioned as best value. Prices are localized from Google Play Billing product details and are not hardcoded in production UI.

## Launch Offers And Lifetime

- Do not show launch-offer copy in the app unless the matching Google Play yearly offer is configured and active.
- Monthly should remain visible and easy to choose.
- Lifetime is not part of launch. It can be documented as a future pricing experiment only and must not appear in the app UI.

## Entitlement Source

Premium is active when either condition is true:

- Google Play reports an active CareTail Premium subscription purchase.
- `BuildConfig.DEBUG` is true and Premium test mode is enabled.

Release builds do not expose the Premium test mode toggle.

## Restore Purchases

Settings and PremiumScreen can query active Google Play subscription purchases:

- Active subscription found: `Premium restored.`
- No active subscription found: `No active Premium subscription found.`

## RevenueCat Notes

CareTail currently uses native Google Play Billing. If RevenueCat is added later, use one entitlement named `premium`, keep the same monthly/yearly product IDs, and create separate offerings for default, pet-limit, reminder-limit, diary-limit, document-limit, advanced-repeat, and export-report paywalls.

## QA Checklist

- Free user can create 1 pet.
- Free user is routed to Premium when creating pet #2.
- Free user can create up to 5 active reminders.
- Free user is routed to Premium when creating active reminder #6.
- Completed reminders do not count toward the free active reminder limit.
- Free user can create up to 5 health diary entries.
- Free user is routed to Premium when creating health diary entry #6.
- Free user can create up to 3 local document records.
- Free user is routed to Premium when creating document record #4.
- Free user can view, edit, and delete existing records.
- Free user sees Premium when selecting Monthly or Yearly repeats.
- Free user sees Premium when exporting a care report.
- Premium user can add unlimited pets and reminders.
- Premium user can add unlimited health diary entries and document records.
- Premium user can export care reports.
- Paywall has a visible close button and `Continue with Free`.
- Purchase success unlocks Premium immediately.
- Restore purchases works from Settings and PremiumScreen.
- Purchase failure or product loading failure does not trap the user.
- Debug Premium test mode is absent from release builds.

## Limitations

- There is no backend purchase-token verification in this release.
- Cloud backup, sync, family sharing, Lifetime, and PDF export are not active launch benefits.
- Real purchase tests require an app build installed from a Google Play testing track with a licensed tester account.
