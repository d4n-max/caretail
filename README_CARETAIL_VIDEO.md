# CareTail TikTok/Reels Video

This Remotion template renders **What CareTail keeps organized** as a 1080x1920, 30fps, seven-slide vertical MP4.

## Screenshot Assets

Remotion reads local images from `public/`, so the raw screenshots are mirrored here:

```text
public/caretail/screenshots/raw/
```

The original source screenshots remain in:

```text
marketing-assets/social-source/caretail/screenshots/raw/
```

Replace or refresh screenshots by copying new PNGs into the matching `public/caretail/screenshots/raw/` paths. The composition uses the images directly with `Img`; it does not recreate or alter app UI.

## Preview

Install dependencies once:

```bash
npm install
```

Open Remotion Studio:

```bash
npm run preview
```

Select the `CareTailTikTokVideo` composition.

## Render MP4

```bash
npm run render:caretail
```

The output file is:

```text
out/caretail-tiktok-video.mp4
```

## Export Slide Stills

```bash
npm run stills:caretail
```

Stills are written to:

```text
out/stills/
```

## Change Hooks, Subtext, or Screenshots

Edit:

```text
src/slides.ts
```

Each slide has:

- `id`
- `screenshot`
- `hook`
- `subtext`
- `layoutVariant`
- `accentTarget`
- `outputFilename`

Keep screenshot paths relative to `public/`, for example:

```ts
screenshot: 'caretail/screenshots/raw/caretail-01-home-dashboard.png'
```

## Reuse for Another DCP Labs App

1. Add the new app screenshots under `public/<app-name>/screenshots/raw/`.
2. Replace the entries in `src/slides.ts`.
3. Reuse or extend the `layoutVariant` options in `src/CareTailTikTokVideo.tsx`.
4. Update colors and copy while keeping the same safe margins for TikTok/Reels overlays.
5. Render with `npm run render:caretail`, or rename the composition/script for the new app.
