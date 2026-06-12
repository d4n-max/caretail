# CareTail Billing Test Plan

## Subscription Products

- Monthly product ID: `caretail_premium_monthly`
- Monthly base plan ID: `monthly`
- Yearly product ID: `caretail_premium_yearly`
- Yearly base plan ID: `yearly`

## Test Requirements

- Install CareTail from a Google Play internal, closed, or production testing track for real billing tests.
- Use a licensed tester Google account.
- Confirm the tester account is included in the selected Play testing track.
- Do not rely on a locally sideloaded debug APK for product loading or purchase-flow validation.

## Product Loading

1. Open PremiumScreen from a free account.
2. Verify the monthly product loads.
3. Verify the yearly product loads.
4. Verify localized monthly price appears from Play.
5. Verify localized yearly price appears from Play.
6. Confirm products unavailable state shows: `Premium is temporarily unavailable. Please try again later.`
7. Confirm purchase button is disabled when products are unavailable.

## Purchase Flow

1. Tap Monthly and confirm Google Play Billing flow starts for `caretail_premium_monthly`.
2. Cancel purchase and confirm Premium does not unlock.
3. Tap Yearly and confirm Google Play Billing flow starts for `caretail_premium_yearly`.
4. Complete a test purchase and confirm Premium unlocks.
5. Confirm Premium gates unlock: pets, reminders, diary entries, documents, Monthly/Yearly repeats, export report.
6. Confirm purchase acknowledgement succeeds.
7. Confirm pending purchase shows: `Your purchase is pending. Premium will unlock when payment is confirmed.`

## Restore Purchases

1. Purchase a subscription with a licensed tester.
2. Reinstall or clear app data.
3. Open Settings or PremiumScreen.
4. Tap Restore purchases.
5. Confirm active purchase restores Premium.
6. Use an account with no active subscription and confirm: `No active Premium subscription found.`

## Cancellation And Expiry

1. Cancel or expire the test subscription from Google Play test tools.
2. Relaunch the app or tap Restore purchases.
3. Confirm Premium does not remain unlocked after Google Play reports no active subscription.

## Release Safety

1. Confirm release build does not show the Premium test mode toggle.
2. Confirm debug build may show Premium test mode under Developer / Testing.
3. Confirm no RevenueCat or Stripe dependencies are present.
4. Confirm no backend, Firebase/Auth, or cloud sync is required for this billing implementation.

## Known Limitations

- This release does not include backend purchase-token verification.
- Google Play product details and purchase flows usually require Play-distributed installs.
- Localized prices depend on Play Console product activation and tester country.
