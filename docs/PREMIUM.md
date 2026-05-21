# CareTail Premium

CareTail Premium is currently implemented as local gating logic with a testing toggle. Google Play Billing is not integrated yet.

## Free Limits

- 1 pet profile
- 5 active reminders
- 5 health diary entries total
- 3 document records total
- Exportable reports locked
- Cloud backup locked

Completed reminders do not count toward the active reminder limit.

## Premium Benefits

- Unlimited pets
- Unlimited reminders
- Unlimited health diary
- Unlimited documents
- Advanced recurring reminders
- Exportable health reports
- Future cloud backup

## Current Billing Placeholder

`PremiumManager` exposes an in-memory `isPremium` state. The default is `false`. Premium purchase buttons show placeholder messaging and do not start real billing.

## Debug Premium Toggle

Settings includes a clearly labeled `Premium test mode` switch. When enabled, `isPremium = true` and free limits are disabled. When disabled, free limits apply again.

This testing switch should be replaced by Google Play Billing entitlement state before production release.

## Future Google Play Billing Plan

Planned product IDs:

- `caretail_premium_monthly`
- `caretail_premium_yearly`

Future work should connect purchase state, restore flow, entitlement persistence, and renewal status to `PremiumManager`.
