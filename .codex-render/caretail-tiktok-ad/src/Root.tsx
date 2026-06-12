import "./index.css";
import { Composition } from "remotion";
import { CareTailTikTokAd } from "./Composition";
import { CareTailTikTokPromo } from "./TikTokPromo";

export const RemotionRoot: React.FC = () => {
  return (
    <>
      <Composition
        id="CareTailTikTokAd"
        component={CareTailTikTokAd}
        durationInFrames={600}
        fps={30}
        width={1080}
        height={1920}
      />
      <Composition
        id="CareTailTikTokPromo"
        component={CareTailTikTokPromo}
        durationInFrames={630}
        fps={30}
        width={1080}
        height={1920}
      />
    </>
  );
};
