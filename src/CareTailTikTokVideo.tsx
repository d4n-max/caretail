import React from 'react';
import {
  AbsoluteFill,
  Easing,
  Img,
  interpolate,
  spring,
  staticFile,
  useCurrentFrame,
  useVideoConfig,
} from 'remotion';
import {CareTailSlide, slides} from './slides';

export const VIDEO_WIDTH = 1080;
export const VIDEO_HEIGHT = 1920;
export const FPS = 30;
export const SLIDE_SECONDS = 2.5;
export const SLIDE_FRAMES = FPS * SLIDE_SECONDS;

const TRANSITION_FRAMES = 10;
const SAFE_X = 76;
const SAFE_TOP = 126;
const SAFE_BOTTOM = 280;

const colors = {
  cream: '#fff4e5',
  aqua: '#d7fbf4',
  teal: '#0f766e',
  deepTeal: '#0a4f4a',
  coral: '#f16f5b',
  coralSoft: '#ffd7ca',
  ink: '#1f3d3a',
  white: '#fffdf8',
};

type FrameStyle = {
  left: number;
  top: number;
  width: number;
  height: number;
  rotate?: number;
  cropY?: number;
  cropScale?: number;
  shadow?: string;
};

export const slowScaleIn = (frame: number, duration: number, amount = 0.035) =>
  interpolate(frame, [0, duration], [1, 1 + amount], {
    easing: Easing.bezier(0.45, 0, 0.55, 1),
    extrapolateLeft: 'clamp',
    extrapolateRight: 'clamp',
  });

export const fadeInUp = (frame: number, fps: number, delay = 0) => {
  const progress = spring({
    frame: Math.max(0, frame - delay),
    fps,
    config: {damping: 34, stiffness: 110, mass: 0.8},
  });

  return {
    opacity: interpolate(progress, [0, 1], [0, 1], {
      extrapolateLeft: 'clamp',
      extrapolateRight: 'clamp',
    }),
    transform: `translateY(${interpolate(progress, [0, 1], [28, 0], {
      extrapolateLeft: 'clamp',
      extrapolateRight: 'clamp',
    })}px)`,
  };
};

export const slideTransition = (localFrame: number) => {
  const enter = interpolate(localFrame, [-TRANSITION_FRAMES, 0], [0, 1], {
    easing: Easing.bezier(0.16, 1, 0.3, 1),
    extrapolateLeft: 'clamp',
    extrapolateRight: 'clamp',
  });
  const exit = interpolate(
    localFrame,
    [SLIDE_FRAMES - TRANSITION_FRAMES, SLIDE_FRAMES],
    [1, 0],
    {
      easing: Easing.in(Easing.cubic),
      extrapolateLeft: 'clamp',
      extrapolateRight: 'clamp',
    },
  );
  const opacity = Math.min(enter, exit);
  const x = interpolate(enter, [0, 1], [54, 0]) + interpolate(exit, [0, 1], [-38, 0]);

  return {
    opacity,
    transform: `translateX(${x}px)`,
  };
};

const layoutFor = (slide: CareTailSlide): FrameStyle => {
  switch (slide.layoutVariant) {
    case 'dashboard-zoom':
      return {left: 62, top: 372, width: 956, height: 1398, cropY: -18, cropScale: 1.02};
    case 'profile-center':
      return {left: 168, top: 350, width: 744, height: 1348, cropY: -34, cropScale: 1.04};
    case 'reminders-full':
      return {left: 126, top: 338, width: 828, height: 1418, cropY: -12, cropScale: 1.01};
    case 'split-right':
      return {left: 392, top: 270, width: 640, height: 1424, cropY: -10, cropScale: 1.01};
    case 'diary-bubble':
      return {left: 120, top: 318, width: 840, height: 1438, cropY: -26, cropScale: 1.03};
    case 'documents-tilt':
      return {
        left: 170,
        top: 340,
        width: 740,
        height: 1340,
        rotate: -1.4,
        cropY: -16,
        cropScale: 1.02,
        shadow: '0 34px 76px rgba(10, 79, 74, 0.24)',
      };
    case 'premium-list':
      return {left: 160, top: 320, width: 760, height: 1368, cropY: -12, cropScale: 1.01};
  }
};

const Sticker = ({
  children,
  left,
  top,
  rotate = 0,
}: {
  children: React.ReactNode;
  left: number;
  top: number;
  rotate?: number;
}) => (
  <div
    style={{
      position: 'absolute',
      left,
      top,
      width: 58,
      height: 58,
      borderRadius: 18,
      background: colors.white,
      boxShadow: '0 12px 28px rgba(10, 79, 74, 0.14)',
      color: colors.coral,
      display: 'grid',
      placeItems: 'center',
      fontSize: 30,
      fontWeight: 900,
      transform: `rotate(${rotate}deg)`,
    }}
  >
    {children}
  </div>
);

const Background = ({variant}: {variant: CareTailSlide['layoutVariant']}) => {
  const isAqua = variant === 'split-right' || variant === 'reminders-full';

  return (
    <AbsoluteFill
      style={{
        background: isAqua
          ? `linear-gradient(155deg, ${colors.aqua} 0%, #fffaf0 62%, ${colors.cream} 100%)`
          : `linear-gradient(155deg, ${colors.cream} 0%, #fffaf0 54%, ${colors.aqua} 100%)`,
      }}
    >
      <div
        style={{
          position: 'absolute',
          width: 560,
          height: 560,
          left: -180,
          top: -120,
          borderRadius: 999,
          background: 'rgba(15, 118, 110, 0.10)',
          filter: 'blur(2px)',
        }}
      />
      <div
        style={{
          position: 'absolute',
          width: 620,
          height: 620,
          right: -240,
          bottom: 160,
          borderRadius: 999,
          background: 'rgba(241, 111, 91, 0.12)',
        }}
      />
      <div
        style={{
          position: 'absolute',
          left: SAFE_X,
          right: SAFE_X,
          bottom: SAFE_BOTTOM - 78,
          height: 2,
          background: 'rgba(15, 118, 110, 0.08)',
        }}
      />
    </AbsoluteFill>
  );
};

const HookText = ({
  slide,
  localFrame,
}: {
  slide: CareTailSlide;
  localFrame: number;
}) => {
  const {fps} = useVideoConfig();
  const entrance = fadeInUp(localFrame, fps, 3);
  const isSplit = slide.layoutVariant === 'split-right';
  const isBubble = slide.layoutVariant === 'diary-bubble';
  const isDashboard = slide.layoutVariant === 'dashboard-zoom';
  const isReminders = slide.layoutVariant === 'reminders-full';
  const isPremium = slide.layoutVariant === 'premium-list';
  const top = isBubble ? 190 : isReminders ? 112 : SAFE_TOP;
  const width = isSplit ? 310 : isDashboard ? 710 : 900;
  const left = isSplit || isDashboard ? SAFE_X : 90;
  const align = isSplit || isDashboard ? 'left' : 'center';
  const headlineSize = isSplit ? 62 : isDashboard ? 58 : isReminders ? 56 : isPremium ? 64 : 68;
  const subtextSize = isSplit ? 36 : isDashboard || isReminders ? 28 : 34;

  return (
    <div
      style={{
        position: 'absolute',
        left,
        top,
        width,
        textAlign: align,
        ...entrance,
      }}
    >
      <div
        style={{
          display: 'inline-block',
          borderRadius: isBubble ? 34 : 0,
          background: isBubble ? colors.coral : 'transparent',
          color: isBubble ? colors.white : colors.teal,
          padding: isBubble ? '24px 28px' : 0,
          boxShadow: isBubble ? '0 18px 38px rgba(241, 111, 91, 0.22)' : undefined,
          fontSize: headlineSize,
          lineHeight: 1.02,
          fontWeight: 900,
          letterSpacing: 0,
        }}
      >
        {slide.hook}
      </div>
      <div
        style={{
          marginTop: isBubble || isDashboard ? 28 : 24,
          color: colors.ink,
          fontSize: subtextSize,
          lineHeight: 1.22,
          fontWeight: 650,
        }}
      >
        {slide.subtext}
      </div>
    </div>
  );
};

const ScreenshotFrame = ({
  slide,
  localFrame,
}: {
  slide: CareTailSlide;
  localFrame: number;
}) => {
  const frameStyle = layoutFor(slide);
  const scale = slowScaleIn(localFrame, SLIDE_FRAMES, 0.032);

  return (
    <div
      style={{
        position: 'absolute',
        left: frameStyle.left,
        top: frameStyle.top,
        width: frameStyle.width,
        height: frameStyle.height,
        borderRadius: 58,
        padding: 14,
        background: 'rgba(255, 253, 248, 0.72)',
        boxShadow: frameStyle.shadow ?? '0 30px 70px rgba(10, 79, 74, 0.20)',
        transform: `rotate(${frameStyle.rotate ?? 0}deg)`,
      }}
    >
      <div
        style={{
          position: 'absolute',
          inset: 0,
          borderRadius: 64,
          border: '2px solid rgba(15, 118, 110, 0.12)',
        }}
      />
      <div
        style={{
          position: 'relative',
          width: '100%',
          height: '100%',
          borderRadius: 46,
          overflow: 'hidden',
          background: colors.white,
        }}
      >
        <Img
          src={staticFile(slide.screenshot)}
          style={{
            width: '100%',
            height: '100%',
            objectFit: 'cover',
            objectPosition: 'center top',
            transform: `translateY(${frameStyle.cropY ?? 0}px) scale(${
              (frameStyle.cropScale ?? 1) * scale
            })`,
          }}
        />
      </div>
    </div>
  );
};

const AccentLayer = ({
  slide,
  localFrame,
  layer,
}: {
  slide: CareTailSlide;
  localFrame: number;
  layer: 'back' | 'front';
}) => {
  const {fps} = useVideoConfig();
  const accent = fadeInUp(localFrame, fps, 12);

  if (slide.layoutVariant === 'dashboard-zoom') {
    if (layer === 'back') {
      return null;
    }

    return (
      <>
        <div
          style={{
            position: 'absolute',
            left: 106,
            top: 258,
            width: 300,
            height: 12,
            borderRadius: 999,
            background: colors.coral,
            transform: `${accent.transform} rotate(-2deg)`,
            opacity: accent.opacity,
          }}
        />
        <Sticker left={826} top={192} rotate={8}>
          ✓
        </Sticker>
      </>
    );
  }

  if (slide.layoutVariant === 'profile-center') {
    if (layer === 'front') {
      return (
        <Sticker left={812} top={306} rotate={-7}>
          ♡
        </Sticker>
      );
    }

    return (
      <div
        style={{
          position: 'absolute',
          left: 336,
          top: 500,
          width: 410,
          height: 410,
          borderRadius: 999,
          background: 'rgba(15, 118, 110, 0.16)',
          filter: 'blur(22px)',
          opacity: 0.85,
        }}
      />
    );
  }

  if (slide.layoutVariant === 'reminders-full') {
    if (layer === 'back') {
      return null;
    }

    return (
      <div
        style={{
          position: 'absolute',
          left: 178,
          top: 574,
          width: 318,
          height: 72,
          borderRadius: 39,
          border: `6px solid ${colors.coral}`,
          opacity: accent.opacity * 0.9,
          boxShadow: '0 0 0 10px rgba(241, 111, 91, 0.12)',
        }}
      />
    );
  }

  if (slide.layoutVariant === 'split-right') {
    if (layer === 'back') {
      return null;
    }

    return (
      <>
        {[512, 690, 876].map((top, index) => (
          <div
            key={top}
            style={{
              position: 'absolute',
              left: 330,
              top,
              width: 70,
              height: 4,
              borderRadius: 999,
              background: index === 1 ? colors.teal : colors.coral,
              opacity: accent.opacity * 0.8,
              transform: `${accent.transform} rotate(${index === 1 ? -8 : 8}deg)`,
            }}
          />
        ))}
        <Sticker left={92} top={952} rotate={-6}>
          □
        </Sticker>
      </>
    );
  }

  if (slide.layoutVariant === 'documents-tilt') {
    if (layer === 'front') {
      return (
        <Sticker left={834} top={234} rotate={8}>
          ≡
        </Sticker>
      );
    }

    return (
      <div
        style={{
          position: 'absolute',
          left: 214,
          top: 392,
          width: 690,
          height: 1240,
          borderRadius: 62,
          background: 'rgba(255, 255, 255, 0.42)',
          transform: 'rotate(2.2deg)',
          boxShadow: '0 22px 52px rgba(10, 79, 74, 0.10)',
        }}
      />
    );
  }

  if (slide.layoutVariant === 'premium-list') {
    if (layer === 'front') {
      return null;
    }

    return (
      <div
        style={{
          position: 'absolute',
          left: 206,
          top: 468,
          width: 668,
          height: 780,
          borderRadius: 220,
          background: 'rgba(15, 118, 110, 0.15)',
          filter: 'blur(28px)',
        }}
      />
    );
  }

  return null;
};

const Slide = ({slide, index}: {slide: CareTailSlide; index: number}) => {
  const frame = useCurrentFrame();
  const localFrame = frame - index * SLIDE_FRAMES;
  const transition = slideTransition(localFrame);

  if (localFrame < -TRANSITION_FRAMES || localFrame > SLIDE_FRAMES) {
    return null;
  }

  return (
    <AbsoluteFill
      style={{
        opacity: transition.opacity,
        transform: transition.transform,
        zIndex: index + 1,
      }}
    >
      <Background variant={slide.layoutVariant} />
      <AccentLayer slide={slide} localFrame={localFrame} layer="back" />
      <ScreenshotFrame slide={slide} localFrame={localFrame} />
      <AccentLayer slide={slide} localFrame={localFrame} layer="front" />
      <HookText slide={slide} localFrame={localFrame} />
    </AbsoluteFill>
  );
};

export const CareTailTikTokVideo = () => {
  return (
    <AbsoluteFill
      style={{
        width: VIDEO_WIDTH,
        height: VIDEO_HEIGHT,
        background: colors.cream,
        fontFamily:
          'Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif',
        overflow: 'hidden',
      }}
    >
      {slides.map((slide, index) => (
        <Slide key={slide.id} slide={slide} index={index} />
      ))}
    </AbsoluteFill>
  );
};
