# CareTail Premium

CareTail Premium uses native Google Play Billing subscriptions and keeps the existing local-first feature gates. Entitlement is client-side in this release: active Google Play subscription purchases update `PremiumManager`, and debug-only test mode can also unlock Premium in debug builds.

## Free Limits

- 1 pet profile
- 5 active reminders
- 5 health diary entries total
- 3 document records total
- Monthly and Yearly reminder repeats locked
- Exportable reports locked

Completed reminders do not count toward the active reminder limit.

## Premium Benefits

- Unlimited pets
- Unlimited reminders
- Unlimited health diary
- Unlimited documents
- Advanced recurring reminders: Monthly and Yearly
- Exportable health reports
- Future cloud backup support

## Subscription Products

- Monthly subscription product ID: `caretail_premium_monthly`
- Monthly base plan ID: `monthly`
- Yearly subscription product ID: `caretail_premium_yearly`
- Yearly base plan ID: `yearly`

CareTail displays localized prices from Google Play Billing product details. It does not hardcode production prices in the paywall.

## Entitlement Source

Premium is active when either condition is true:

- Google Play reports an active CareTail Premium subscription purchase.
- `BuildConfig.DEBUG` is true and Premium test mode is enabled.

Release builds do not expose the Premium test mode toggle and cannot unlock Premium through the debug override.

## Restore Purchases

Settings and PremiumScreen can query active Google Play subscription purchases:

- Active subscription found: `Premium restored.`
- No active subscription found: `No active Premium subscription found.`

## Limitations

- There is no backend purchase-token verification in this task.
- Entitlement is refreshed from Google Play on app startup, PremiumScreen open, reconnect, and restore.
- Real purchase tests require an app build installed from a Google Play testing track with a licensed tester account.
