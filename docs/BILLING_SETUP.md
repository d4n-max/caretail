# CareTail Google Play Billing Setup

CareTail Premium uses native Google Play Billing subscriptions. The MVP keeps entitlement client-side and updates the existing `PremiumManager` state from active subscription purchases.

## Product IDs

- Monthly subscription: `caretail_premium_monthly`
- Yearly subscription: `caretail_premium_yearly`

## Google Play Console Steps

1. Create the app in Google Play Console using the CareTail package name.
2. Upload a signed build to an internal or closed testing track.
3. Create subscription products:
   - `caretail_premium_monthly`
   - `caretail_premium_yearly`
4. Create and activate base plans and offers for each subscription.
5. Confirm pricing is active in all intended countries.
6. Add license testers under Play Console testing settings.
7. Add tester accounts to the internal or closed testing track.
8. Install CareTail from the Google Play testing track before testing purchases.

## Testing Notes

- Real product details usually require a build installed through a Google Play testing track.
- A locally sideloaded debug APK may not return subscription products, even when the product IDs are correct.
- Test purchases should use license tester accounts, not production purchase accounts.
- Restore purchase queries active Google Play subscription purchases and updates Premium state.

## Current MVP Behavior

- Premium entitlement is client-side only.
- Active subscription purchases set CareTail Premium to active.
- Unacknowledged subscription purchases are acknowledged by the app.
- Premium test mode remains available in debug builds only.
- Existing Premium gates continue to read from `PremiumManager.isPremium`.

TODO: Add server-side purchase verification before relying on purchase tokens for a production-scale launch.

## Restore Purchases

The Restore purchase action queries active Google Play subscriptions:

- Active CareTail Premium subscription found: `Premium restored.`
- No active subscription found: `No active Premium subscription found.`

## Troubleshooting

- Products not found: confirm product IDs match exactly and products/base plans are activated.
- Item unavailable: confirm the app was installed from a Play testing track and the tester is eligible.
- Tester not licensed: add the Google account as a license tester and track tester.
- App not installed from Play: install from the internal or closed testing track.
- Product IDs mismatch: verify `caretail_premium_monthly` and `caretail_premium_yearly` in code and Play Console.
- Purchases not restoring: confirm the tester account owns an active subscription for the same app package.
