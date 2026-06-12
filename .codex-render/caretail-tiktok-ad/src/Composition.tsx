import {
  AbsoluteFill,
  Easing,
  Img,
  Video,
  interpolate,
  spring,
  staticFile,
  useCurrentFrame,
  useVideoConfig,
} from "remotion";

const ACCENT = "#13B8A6";
const CORAL = "#FF6B57";
const INK = "#14213D";
const CREAM = "#FFF7E8";
const YELLOW = "#FFD166";
const BLUE = "#4C7DFF";

const adConfig = {
  phoneScale: 0.78,
  maxZoomScale: 1.1,
  showFullDevice: true,
  safeMargin: 72,
};

const beats = [
  {
    from: 0,
    duration: 66,
    eyebrow: "NEW PET PARENT HACK",
    title: "Track every tail-wagging detail",
    subtitle: "Profiles, care, and routines in one app.",
    tag: "CareTail",
    color: CORAL,
  },
  {
    from: 66,
    duration: 78,
    eyebrow: "PET PROFILES",
    title: "Keep each pet's info ready",
    subtitle: "Age, breed, notes, and the little things that matter.",
    tag: "Profiles",
    color: ACCENT,
  },
  {
    from: 144,
    duration: 84,
    eyebrow: "REMINDERS",
    title: "Never miss meds, grooming, or checkups",
    subtitle: "Set routines once. Stay ahead daily.",
    tag: "Reminders",
    color: YELLOW,
  },
  {
    from: 228,
    duration: 84,
    eyebrow: "CARE DIARY",
    title: "Log symptoms, meals, and moments fast",
    subtitle: "A timeline for everything you want to remember.",
    tag: "Diary",
    color: BLUE,
  },
  {
    from: 312,
    duration: 84,
    eyebrow: "HEALTH TRACKING",
    title: "Spot patterns before they become problems",
    subtitle: "Turn scattered notes into clear history.",
    tag: "Health",
    color: ACCENT,
  },
  {
    from: 396,
    duration: 72,
    eyebrow: "FOR CATS + DOGS",
    title: "One calm home base for every pet",
    subtitle: "Simple enough for daily use. Detailed when you need it.",
    tag: "Cats + Dogs",
    color: CORAL,
  },
  {
    from: 468,
    duration: 132,
    eyebrow: "DOWNLOAD ON GOOGLE PLAY",
    title: "Give their care a smarter routine",
    subtitle: "Install CareTail today.",
    tag: "Get the app",
    color: INK,
  },
];

const features = ["Profiles", "Reminders", "Care diary", "Health"];

const featureForTag: Record<string, string> = {
  Profiles: "Profiles",
  Reminders: "Reminders",
  Diary: "Care diary",
  Health: "Health",
};

const clamp = {
  extrapolateLeft: "clamp" as const,
  extrapolateRight: "clamp" as const,
};

const beatForFrame = (frame: number) =>
  beats.find((beat) => frame >= beat.from && frame < beat.from + beat.duration) ??
  beats[beats.length - 1];

const seconds = (value: number, fps: number) => Math.round(value * fps);

const PopText: React.FC<{
  children: React.ReactNode;
  delay?: number;
  style?: React.CSSProperties;
}> = ({ children, delay = 0, style }) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  const local = Math.max(0, frame - delay);
  const entry = spring({
    frame: local,
    fps,
    config: { damping: 15, stiffness: 150, mass: 0.75 },
  });
  const opacity = interpolate(local, [0, seconds(0.2, fps)], [0, 1], clamp);
  const y = interpolate(entry, [0, 1], [36, 0], clamp);

  return (
    <div
      style={{
        opacity,
        transform: `translateY(${y}px) scale(${0.96 + entry * 0.04})`,
        ...style,
      }}
    >
      {children}
    </div>
  );
};

const PhoneRecording: React.FC = () => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  const beat = beatForFrame(frame);
  const accentWindows = [
    [78, 108],
    [158, 192],
    [244, 278],
    [326, 360],
    [414, 444],
  ];
  const accentZoom = accentWindows.reduce((zoom, [start, end]) => {
    const peak = (start + end) / 2;
    const windowZoom = interpolate(frame, [start, peak, end], [1, adConfig.maxZoomScale, 1], clamp);
    return Math.max(zoom, windowZoom);
  }, 1);
  const introScale = interpolate(frame, [0, seconds(0.7, fps)], [0.9, 1], {
    ...clamp,
    easing: Easing.bezier(0.16, 1, 0.3, 1),
  });
  const phoneY = interpolate(frame, [0, 600], [8, -8], clamp);
  const phoneScale = adConfig.showFullDevice
    ? adConfig.phoneScale * introScale * accentZoom
    : introScale * accentZoom;

  return (
    <div
      style={{
        position: "absolute",
        top: 462 + phoneY,
        left: "50%",
        width: 720,
        height: 1460,
        borderRadius: 58,
        background: "#101827",
        boxShadow: "0 44px 90px rgba(20, 33, 61, 0.3)",
        padding: 18,
        transform: `translateX(-50%) scale(${phoneScale}) rotate(${interpolate(
          frame,
          [0, 600],
          [-0.8, 0.8],
          clamp,
        )}deg)`,
        transformOrigin: "top center",
      }}
    >
      <div
        style={{
          width: "100%",
          height: "100%",
          overflow: "hidden",
          borderRadius: 42,
          background: "#ffffff",
          position: "relative",
        }}
      >
        <Video
          src={staticFile("caretail-recording.mp4")}
          muted
          loop
          playbackRate={1.06}
          style={{
            width: "100%",
            height: "100%",
            objectFit: "contain",
            background: "#ffffff",
          }}
        />
        <div
          style={{
            position: "absolute",
            inset: 0,
            boxShadow: `inset 0 0 0 6px ${beat.color}33`,
            pointerEvents: "none",
          }}
        />
      </div>
    </div>
  );
};

const FeatureRail: React.FC = () => {
  const frame = useCurrentFrame();
  const beat = beatForFrame(frame);

  return (
    <div
      style={{
        position: "absolute",
        left: adConfig.safeMargin,
        right: adConfig.safeMargin,
        top: 374,
        display: "flex",
        gap: 14,
        justifyContent: "center",
      }}
    >
      {features.map((feature, index) => {
        const active = featureForTag[beat.tag] === feature;
        const y = interpolate(frame, [index * 4, index * 4 + 16], [18, 0], clamp);
        return (
          <div
            key={feature}
            style={{
              transform: `translateY(${y}px)`,
              borderRadius: 999,
              padding: "14px 20px",
              background: active ? beat.color : "rgba(255, 255, 255, 0.72)",
              color: active && beat.color !== YELLOW ? "#fff" : INK,
              fontSize: 28,
              fontWeight: 800,
              boxShadow: "0 16px 34px rgba(20, 33, 61, 0.14)",
              border: "2px solid rgba(255, 255, 255, 0.72)",
              whiteSpace: "nowrap",
            }}
          >
            {feature}
          </div>
        );
      })}
    </div>
  );
};

const CaptionLayer: React.FC = () => {
  const frame = useCurrentFrame();
  const beat = beatForFrame(frame);
  const local = frame - beat.from;
  const exitOpacity = interpolate(local, [beat.duration - 12, beat.duration], [1, 0], clamp);
  const highlightWidth = interpolate(local, [8, 26], [0, 1], {
    ...clamp,
    easing: Easing.bezier(0.16, 1, 0.3, 1),
  });

  return (
    <div
      style={{
        position: "absolute",
        top: 62,
        left: adConfig.safeMargin,
        right: adConfig.safeMargin,
        opacity: exitOpacity,
      }}
    >
      <PopText delay={beat.from}>
        <div
          style={{
            display: "inline-flex",
            alignItems: "center",
            gap: 14,
            borderRadius: 999,
            padding: "12px 18px",
            color: "#fff",
            background: INK,
            fontSize: 25,
            fontWeight: 900,
            letterSpacing: 0,
            textTransform: "uppercase",
          }}
        >
          <span
            style={{
              width: 18,
              height: 18,
              borderRadius: 999,
              background: beat.color,
              display: "inline-block",
            }}
          />
          {beat.eyebrow}
        </div>
      </PopText>

      <PopText delay={beat.from + 5}>
        <div
          style={{
            position: "relative",
            marginTop: 18,
            color: INK,
            fontSize: beat.title.length > 38 ? 58 : 66,
            lineHeight: 0.95,
            fontWeight: 950,
            letterSpacing: 0,
            textWrap: "balance",
          }}
        >
          <span
            style={{
              position: "absolute",
              left: -10,
              bottom: 8,
              width: `${highlightWidth * 72}%`,
              height: 28,
              background: beat.color,
              opacity: 0.35,
              borderRadius: 999,
              zIndex: -1,
            }}
          />
          {beat.title}
        </div>
      </PopText>

      <PopText delay={beat.from + 13}>
        <div
          style={{
            marginTop: 18,
            maxWidth: 820,
            color: "#425066",
            fontSize: 35,
            lineHeight: 1.14,
            fontWeight: 750,
            letterSpacing: 0,
          }}
        >
          {beat.subtitle}
        </div>
      </PopText>
    </div>
  );
};

const FloatingCallout: React.FC = () => {
  const frame = useCurrentFrame();
  const beat = beatForFrame(frame);
  const local = frame - beat.from;
  const entry = spring({
    frame: local - 18,
    fps: 30,
    config: { damping: 16, stiffness: 130, mass: 0.8 },
  });
  const opacity =
    frame >= 468
      ? 0
      : interpolate(local, [12, 24, beat.duration - 8, beat.duration], [0, 1, 1, 0], clamp);

  return (
    <div
      style={{
        position: "absolute",
        left: adConfig.safeMargin,
        bottom: 168,
        width: 300,
        borderRadius: 26,
        padding: "20px 22px",
        background: "#ffffff",
        color: INK,
        boxShadow: "0 26px 56px rgba(20, 33, 61, 0.24)",
        border: `5px solid ${beat.color}`,
        opacity,
        transform: `translateX(${interpolate(entry, [0, 1], [96, 0], clamp)}px) rotate(-2deg)`,
      }}
    >
      <div style={{ fontSize: 24, fontWeight: 900, color: beat.color }}>{beat.tag}</div>
      <div style={{ marginTop: 8, fontSize: 28, lineHeight: 1.05, fontWeight: 950 }}>
        Clear care, all in one place.
      </div>
    </div>
  );
};

const ProgressBar: React.FC = () => {
  const frame = useCurrentFrame();
  const progress = interpolate(frame, [0, 599], [0, 1], clamp);

  return (
    <div
      style={{
        position: "absolute",
        left: 54,
        right: 54,
        bottom: 42,
        height: 12,
        borderRadius: 999,
        background: "rgba(20, 33, 61, 0.15)",
        overflow: "hidden",
      }}
    >
      <div
        style={{
          width: `${progress * 100}%`,
          height: "100%",
          borderRadius: 999,
          background: `linear-gradient(90deg, ${CORAL}, ${ACCENT}, ${BLUE})`,
        }}
      />
    </div>
  );
};

const FinalCta: React.FC = () => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  const visible = frame >= 468;
  const local = frame - 468;
  const scale = spring({
    frame: local,
    fps,
    config: { damping: 14, stiffness: 120, mass: 0.8 },
  });

  if (!visible) {
    return null;
  }

  return (
    <div
      style={{
        position: "absolute",
        left: 72,
        right: 72,
        bottom: 88,
        borderRadius: 34,
        padding: "26px 30px",
        background: INK,
        color: "#fff",
        display: "flex",
        alignItems: "center",
        gap: 24,
        boxShadow: "0 24px 70px rgba(20, 33, 61, 0.32)",
        transform: `scale(${0.94 + scale * 0.06})`,
      }}
    >
      <Img
        src={staticFile("caretail-icon.png")}
        style={{
          width: 94,
          height: 94,
          borderRadius: 24,
          background: "#fff",
          flex: "0 0 auto",
        }}
      />
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 30, color: YELLOW, fontWeight: 900 }}>CareTail</div>
        <div style={{ fontSize: 43, lineHeight: 1.02, fontWeight: 950 }}>Install on Google Play</div>
      </div>
      <div
        style={{
          borderRadius: 999,
          background: ACCENT,
          color: "#fff",
          fontSize: 30,
          fontWeight: 950,
          padding: "22px 26px",
          whiteSpace: "nowrap",
        }}
      >
        Get it
      </div>
    </div>
  );
};

const Background: React.FC = () => {
  const frame = useCurrentFrame();
  const beat = beatForFrame(frame);
  const drift = interpolate(frame, [0, 600], [0, 80], clamp);

  return (
    <AbsoluteFill style={{ background: CREAM, overflow: "hidden" }}>
      <div
        style={{
          position: "absolute",
          inset: 0,
          background:
            "radial-gradient(circle at 18% 10%, rgba(255, 107, 87, 0.28), transparent 29%), radial-gradient(circle at 88% 25%, rgba(19, 184, 166, 0.24), transparent 32%), linear-gradient(180deg, #FFF7E8 0%, #FFFFFF 68%, #EAF8F5 100%)",
        }}
      />
      <div
        style={{
          position: "absolute",
          width: 1300,
          height: 1300,
          left: -220 + drift,
          top: 600,
          borderRadius: 999,
          background: `${beat.color}1F`,
          transform: "rotate(-10deg)",
        }}
      />
      <div
        style={{
          position: "absolute",
          top: 132,
          right: 54,
          width: 120,
          height: 120,
          borderRadius: 36,
          background: beat.color,
          opacity: 0.18,
          transform: `rotate(${frame / 5}deg)`,
        }}
      />
    </AbsoluteFill>
  );
};

export const CareTailTikTokAd = () => {
  return (
    <AbsoluteFill>
      <Background />
      <PhoneRecording />
      <CaptionLayer />
      <FeatureRail />
      <FloatingCallout />
      <FinalCta />
      <ProgressBar />
    </AbsoluteFill>
  );
};
