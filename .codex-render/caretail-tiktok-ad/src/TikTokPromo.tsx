import {
  AbsoluteFill,
  Easing,
  Img,
  Sequence,
  Video,
  interpolate,
  spring,
  staticFile,
  useCurrentFrame,
  useVideoConfig,
} from "remotion";

const INK = "#111827";
const TEAL = "#13B8A6";
const CORAL = "#FF6B57";
const LIME = "#B8F05A";
const PAPER = "#FFF8EA";
const BLUE = "#4C7DFF";

const clamp = {
  extrapolateLeft: "clamp" as const,
  extrapolateRight: "clamp" as const,
};

const promoConfig = {
  phoneScale: 0.76,
  maxZoomScale: 1.08,
  safeMargin: 72,
};

const beats = [
  { from: 0, to: 45, text: "Pet care gets messy fast.", tone: "problem", color: CORAL },
  { from: 45, to: 105, text: "Walks. Meds. Notes. Reminders.", tone: "problem", color: LIME },
  { from: 105, to: 165, text: "Keep it all in one place.", tone: "solution", color: TEAL },
  { from: 165, to: 255, text: "Today's care, at a glance.", tone: "demo", color: TEAL },
  { from: 255, to: 345, text: "Never miss the important stuff.", tone: "demo", color: CORAL },
  { from: 345, to: 435, text: "Save notes in a simple diary.", tone: "demo", color: BLUE },
  { from: 435, to: 525, text: "Manage every pet profile.", tone: "demo", color: LIME },
  { from: 525, to: 630, text: "CareTail - try it free.", tone: "cta", color: TEAL },
];

const demoScenes = [
  { from: 0, duration: 165, trim: 0 },
  { from: 165, duration: 90, trim: 1 },
  { from: 255, duration: 90, trim: 9.4 },
  { from: 345, duration: 90, trim: 13.8 },
  { from: 435, duration: 90, trim: 5.8 },
  { from: 525, duration: 105, trim: 0 },
];

const beatForFrame = (frame: number) =>
  beats.find((beat) => frame >= beat.from && frame < beat.to) ?? beats[beats.length - 1];

const sec = (value: number, fps: number) => Math.round(value * fps);

const TikTokCaption: React.FC = () => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  const beat = beatForFrame(frame);
  const local = frame - beat.from;
  const entry = spring({
    frame: local,
    fps,
    config: { damping: 16, stiffness: 170, mass: 0.7 },
  });
  const opacity = interpolate(local, [0, 5, beat.to - beat.from - 8, beat.to - beat.from], [0, 1, 1, 0], clamp);
  const y = interpolate(entry, [0, 1], [28, 0], clamp);
  const fontSize = beat.text.length > 28 ? 58 : 70;

  return (
    <div
      style={{
        position: "absolute",
        top: 76,
        left: promoConfig.safeMargin,
        right: promoConfig.safeMargin,
        opacity,
        transform: `translateY(${y}px) rotate(${beat.tone === "problem" ? -1.2 : 0.6}deg)`,
      }}
    >
      <div
        style={{
          display: "inline",
          color: "#fff",
          background: INK,
          boxDecorationBreak: "clone",
          WebkitBoxDecorationBreak: "clone",
          borderRadius: 18,
          padding: "8px 16px 12px",
          fontSize,
          lineHeight: 1.08,
          fontWeight: 950,
          letterSpacing: 0,
          textShadow: `4px 4px 0 ${beat.color}`,
        }}
      >
        {beat.text}
      </div>
    </div>
  );
};

const MessyLabels: React.FC = () => {
  const frame = useCurrentFrame();
  const labels = [
    { text: "vet?", x: 82, y: 346, rotate: -7, color: "#fff" },
    { text: "meds 8pm", x: 694, y: 352, rotate: 5, color: "#DFFBF4" },
    { text: "food notes", x: 72, y: 1432, rotate: 4, color: "#FFF0B7" },
    { text: "walk log", x: 700, y: 1442, rotate: -5, color: "#F4E7FF" },
  ];

  return (
    <>
      {labels.map((label, index) => {
        const opacity = interpolate(frame, [index * 7, index * 7 + 12, 112, 132], [0, 1, 1, 0], clamp);
        const scale = interpolate(frame, [index * 7, index * 7 + 12], [0.86, 1], clamp);

        return (
          <div
            key={label.text}
            style={{
              position: "absolute",
              left: label.x,
              top: label.y,
              opacity,
              transform: `scale(${scale}) rotate(${label.rotate}deg)`,
              borderRadius: 18,
              padding: "14px 18px",
              background: label.color,
              border: "4px solid #111827",
              boxShadow: "8px 8px 0 rgba(17, 24, 39, 0.14)",
              fontSize: 31,
              fontWeight: 950,
              color: INK,
            }}
          >
            {label.text}
          </div>
        );
      })}
    </>
  );
};

const DemoBadge: React.FC = () => {
  const frame = useCurrentFrame();
  const beat = beatForFrame(frame);
  const visible = beat.tone === "demo";
  const local = frame - beat.from;
  const opacity = visible ? interpolate(local, [0, 10, beat.to - beat.from - 8], [0, 1, 1], clamp) : 0;
  const label =
    beat.from === 165 ? "dashboard" : beat.from === 255 ? "reminders" : beat.from === 345 ? "diary" : "profile";

  return (
    <div
      style={{
        position: "absolute",
        top: 352,
        left: "50%",
        opacity,
        transform: "translateX(-50%) rotate(-1deg)",
        borderRadius: 999,
        padding: "12px 22px",
        background: beat.color,
        color: beat.color === LIME ? INK : "#fff",
        border: "4px solid #111827",
        boxShadow: "7px 7px 0 rgba(17, 24, 39, 0.14)",
        fontSize: 30,
        fontWeight: 950,
        textTransform: "uppercase",
      }}
    >
      {label}
    </div>
  );
};

const PhoneDemo: React.FC = () => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  const accentWindows = [
    [166, 194],
    [258, 286],
    [348, 376],
    [438, 466],
  ];
  const accentZoom = accentWindows.reduce((zoom, [start, end]) => {
    const peak = (start + end) / 2;
    const value = interpolate(frame, [start, peak, end], [1, promoConfig.maxZoomScale, 1], clamp);
    return Math.max(zoom, value);
  }, 1);
  const intro = interpolate(frame, [0, sec(0.45, fps)], [0.94, 1], {
    ...clamp,
    easing: Easing.bezier(0.16, 1, 0.3, 1),
  });
  const floatY = Math.sin(frame / 24) * 5;

  return (
    <div
      style={{
        position: "absolute",
        top: 414 + floatY,
        left: "50%",
        width: 720,
        height: 1460,
        padding: 18,
        borderRadius: 58,
        background: "#101827",
        boxShadow: "0 36px 74px rgba(17, 24, 39, 0.28)",
        transform: `translateX(-50%) scale(${promoConfig.phoneScale * intro * accentZoom}) rotate(${interpolate(
          frame,
          [0, 630],
          [-0.7, 0.7],
          clamp,
        )}deg)`,
        transformOrigin: "top center",
      }}
    >
      <div
        style={{
          position: "relative",
          width: "100%",
          height: "100%",
          borderRadius: 42,
          overflow: "hidden",
          background: "#fff",
        }}
      >
        {demoScenes.map((scene) => (
          <Sequence key={`${scene.from}-${scene.trim}`} from={scene.from} durationInFrames={scene.duration}>
            <Video
              src={staticFile("caretail-recording.mp4")}
              muted
              playbackRate={1.08}
              trimBefore={scene.trim * fps}
              style={{
                width: "100%",
                height: "100%",
                objectFit: "contain",
                background: "#fff",
              }}
            />
          </Sequence>
        ))}
        <div
          style={{
            position: "absolute",
            inset: 0,
            boxShadow: "inset 0 0 0 5px rgba(17, 24, 39, 0.05)",
            pointerEvents: "none",
          }}
        />
      </div>
    </div>
  );
};

const BottomCta: React.FC = () => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  const show = frame >= 525;
  const entry = spring({
    frame: frame - 525,
    fps,
    config: { damping: 14, stiffness: 130, mass: 0.75 },
  });
  const opacity = show ? interpolate(frame, [525, 540], [0, 1], clamp) : 0;

  return (
    <div
      style={{
        position: "absolute",
        left: promoConfig.safeMargin,
        right: promoConfig.safeMargin,
        bottom: 88,
        opacity,
        transform: `translateY(${interpolate(entry, [0, 1], [34, 0], clamp)}px)`,
        borderRadius: 28,
        padding: "20px 22px",
        background: "#111827",
        color: "#fff",
        display: "flex",
        alignItems: "center",
        gap: 18,
        border: "4px solid #fff",
        boxShadow: "0 18px 46px rgba(17, 24, 39, 0.28)",
      }}
    >
      <Img
        src={staticFile("caretail-icon.png")}
        style={{
          width: 78,
          height: 78,
          borderRadius: 20,
          background: "#fff",
        }}
      />
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 26, fontWeight: 950, color: TEAL }}>CareTail</div>
        <div style={{ fontSize: 39, fontWeight: 950, lineHeight: 1 }}>Download on Google Play</div>
      </div>
      <div
        style={{
          background: TEAL,
          color: "#fff",
          borderRadius: 999,
          padding: "17px 22px",
          fontSize: 27,
          fontWeight: 950,
          whiteSpace: "nowrap",
        }}
      >
        Try it free
      </div>
    </div>
  );
};

const ProgressBar: React.FC = () => {
  const frame = useCurrentFrame();
  const progress = interpolate(frame, [0, 629], [0, 1], clamp);

  return (
    <div
      style={{
        position: "absolute",
        left: promoConfig.safeMargin,
        right: promoConfig.safeMargin,
        bottom: 36,
        height: 10,
        borderRadius: 999,
        background: "rgba(17, 24, 39, 0.12)",
        overflow: "hidden",
      }}
    >
      <div
        style={{
          width: `${progress * 100}%`,
          height: "100%",
          borderRadius: 999,
          background: `linear-gradient(90deg, ${CORAL}, ${LIME}, ${TEAL})`,
        }}
      />
    </div>
  );
};

const Background: React.FC = () => {
  const frame = useCurrentFrame();

  return (
    <AbsoluteFill style={{ background: PAPER, overflow: "hidden" }}>
      <div
        style={{
          position: "absolute",
          inset: 0,
          background:
            "radial-gradient(circle at 20% 18%, rgba(255, 107, 87, 0.22), transparent 30%), radial-gradient(circle at 86% 38%, rgba(19, 184, 166, 0.22), transparent 30%), linear-gradient(180deg, #FFF8EA 0%, #FFFFFF 62%, #EAF8F5 100%)",
        }}
      />
      <div
        style={{
          position: "absolute",
          top: 272,
          left: -116,
          width: 1180,
          height: 1180,
          borderRadius: 999,
          background: "rgba(255, 255, 255, 0.52)",
          border: "6px dashed rgba(17, 24, 39, 0.08)",
          transform: `rotate(${frame / 32}deg)`,
        }}
      />
      <div
        style={{
          position: "absolute",
          top: 212,
          right: 78,
          width: 96,
          height: 96,
          borderRadius: 26,
          background: TEAL,
          opacity: 0.16,
          transform: `rotate(${12 + frame / 7}deg)`,
        }}
      />
    </AbsoluteFill>
  );
};

export const CareTailTikTokPromo = () => {
  return (
    <AbsoluteFill>
      <Background />
      <PhoneDemo />
      <TikTokCaption />
      <DemoBadge />
      <MessyLabels />
      <BottomCta />
      <ProgressBar />
    </AbsoluteFill>
  );
};
