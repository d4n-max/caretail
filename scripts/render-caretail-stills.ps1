$ErrorActionPreference = "Stop"

$frames = @(24, 99, 174, 249, 324, 399, 474)
$names = @(
  "caretail-tiktok-01-todays-care.png",
  "caretail-tiktok-02-pet-profile.png",
  "caretail-tiktok-03-active-reminders.png",
  "caretail-tiktok-04-pick-care.png",
  "caretail-tiktok-05-diary-logged.png",
  "caretail-tiktok-06-vet-records.png",
  "caretail-tiktok-07-more-room.png"
)

New-Item -ItemType Directory -Force out\stills | Out-Null

for ($i = 0; $i -lt $frames.Count; $i++) {
  npx remotion still CareTailTikTokVideo "out/stills/$($names[$i])" --frame=$($frames[$i])
}
