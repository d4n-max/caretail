import {Composition} from 'remotion';
import {
  CareTailTikTokVideo,
  FPS,
  SLIDE_FRAMES,
  VIDEO_HEIGHT,
  VIDEO_WIDTH,
} from './CareTailTikTokVideo';
import {slides} from './slides';

export const RemotionRoot = () => {
  return (
    <Composition
      id="CareTailTikTokVideo"
      component={CareTailTikTokVideo}
      durationInFrames={slides.length * SLIDE_FRAMES}
      fps={FPS}
      width={VIDEO_WIDTH}
      height={VIDEO_HEIGHT}
    />
  );
};
