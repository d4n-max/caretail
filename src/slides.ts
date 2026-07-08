export type SlideLayoutVariant =
  | 'dashboard-zoom'
  | 'profile-center'
  | 'reminders-full'
  | 'split-right'
  | 'diary-bubble'
  | 'documents-tilt'
  | 'premium-list';

export type CareTailSlide = {
  id: string;
  screenshot: string;
  hook: string;
  subtext: string;
  layoutVariant: SlideLayoutVariant;
  accentTarget: string;
  outputFilename: string;
};

export const slides: CareTailSlide[] = [
  {
    id: 'todays-care',
    screenshot: 'caretail/screenshots/raw/caretail-01-home-dashboard.png',
    hook: "Today's Care. Upcoming. Done.",
    subtext: 'Luna, Max, reminders, quick actions: all on one dashboard.',
    layoutVariant: 'dashboard-zoom',
    accentTarget: "Coral underline pointing toward Today's Care.",
    outputFilename: 'caretail-tiktok-01-todays-care.png',
  },
  {
    id: 'pet-profile',
    screenshot: 'caretail/screenshots/raw/caretail-02-pet-profile.png',
    hook: 'Luna has her own care hub.',
    subtext: 'Profile, reminders, diary, and report export in one place.',
    layoutVariant: 'profile-center',
    accentTarget: 'Soft teal glow behind the avatar area.',
    outputFilename: 'caretail-tiktok-02-pet-profile.png',
  },
  {
    id: 'active-reminders',
    screenshot: 'caretail/screenshots/raw/caretail-03-reminders-list.png',
    hook: '3 active reminders, zero guessing.',
    subtext: 'Medication, vaccine, grooming, completed care.',
    layoutVariant: 'reminders-full',
    accentTarget: 'Subtle ring around Free reminders: 3/5 active.',
    outputFilename: 'caretail-tiktok-03-active-reminders.png',
  },
  {
    id: 'pick-care',
    screenshot: 'caretail/screenshots/raw/caretail-04-add-reminder.png',
    hook: 'Pick a pet. Pick the care.',
    subtext: 'Date, time, repeat, notes.',
    layoutVariant: 'split-right',
    accentTarget: 'Small arrows toward Luna, Vaccine, and date/time cards.',
    outputFilename: 'caretail-tiktok-04-pick-care.png',
  },
  {
    id: 'diary-logged',
    screenshot: 'caretail/screenshots/raw/caretail-05-diary-notes.png',
    hook: "Today's note is already logged.",
    subtext: 'Mood, appetite, energy, and care notes.',
    layoutVariant: 'diary-bubble',
    accentTarget: "Floating coral bubble near Health Diary and today's card.",
    outputFilename: 'caretail-tiktok-05-diary-logged.png',
  },
  {
    id: 'vet-records',
    screenshot: 'caretail/screenshots/raw/caretail-06-documents.png',
    hook: 'Vet records live here too.',
    subtext: 'Vaccine records, insurance, visit notes, all linked to pets.',
    layoutVariant: 'documents-tilt',
    accentTarget: 'Document-card motif with teal border and coral accent.',
    outputFilename: 'caretail-tiktok-06-vet-records.png',
  },
  {
    id: 'more-room',
    screenshot: 'caretail/screenshots/raw/caretail-08-premium.png',
    hook: 'More pets. More reminders. More room.',
    subtext: 'Premium is for bigger care systems.',
    layoutVariant: 'premium-list',
    accentTarget: 'Calm premium glow behind feature list.',
    outputFilename: 'caretail-tiktok-07-more-room.png',
  },
];
