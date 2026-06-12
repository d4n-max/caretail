from pathlib import Path
from PIL import Image, ImageDraw, ImageFont, ImageFilter

ROOT = Path(__file__).resolve().parents[5]
RAW = ROOT / "content" / "tiktok" / "caretail" / "day-02" / "raw-screenshots"
OUT = ROOT / "content" / "tiktok" / "caretail" / "day-02" / "final-slides"
W, H = 1080, 1920

INK = "#f8f3e8"
MUTED = "#c9bfae"
SOFT = "#8f8373"
GOLD = "#d9ad63"
GOLD2 = "#f1d9a5"
BG = "#10100f"
PANEL = "#24211d"
LINE = "#5a4d3b"


def font(size, bold=False):
    names = ["segoeuib.ttf", "segoeui.ttf"] if bold else ["segoeui.ttf", "arial.ttf"]
    for name in names:
        path = Path("C:/Windows/Fonts") / name
        if path.exists():
            return ImageFont.truetype(str(path), size)
    return ImageFont.load_default()


F_LABEL = font(24)
F_NUM = font(24, True)
F_TITLE = font(86, True)
F_TITLE_LONG = font(74, True)
F_SUB = font(36)
F_SMALL = font(22)
F_BODY = font(28)


SLIDES = [
    {
        "num": 1,
        "title": "What most pet owners forget to track",
        "subtitle": "Pet care is more than food and walks.",
        "screens": ["01-home.png"],
        "caption": "Real CareTail home screen",
        "layout": "single",
        "long": True,
    },
    {
        "num": 2,
        "title": "Medications and reminders",
        "subtitle": "Dates, times, and repeated care tasks are easy to miss.",
        "screens": ["04-add-reminder.png"],
        "caption": "Reminder fields: medication, vet visit, grooming, repeat, notes",
        "layout": "single",
        "long": True,
    },
    {
        "num": 3,
        "title": "Vet visits and follow-ups",
        "subtitle": "Notes matter when you need to remember what changed.",
        "screens": ["02-pet-profile.png"],
        "caption": "Profile sections include reminders, health diary, documents, export",
        "layout": "single",
        "long": True,
    },
    {
        "num": 4,
        "title": "Symptoms and behavior changes",
        "subtitle": "Small details make more sense when they stay in one place.",
        "screens": ["05-diary.png"],
        "caption": "Health Diary tracks mood, appetite, energy, and symptoms",
        "layout": "single",
        "long": True,
    },
    {
        "num": 5,
        "title": "Grooming and routines",
        "subtitle": "Repeated tasks need a simple system.",
        "screens": ["03-reminders.png", "04-add-reminder.png"],
        "caption": "Real reminders list plus the add-reminder routine fields",
        "layout": "pair",
        "long": True,
    },
    {
        "num": 6,
        "title": "Keep care history together",
        "subtitle": "One pet profile. Clearer care records.",
        "screens": ["06-profile-history.png"],
        "caption": "Luna profile with reminders, diary, documents, and report access",
        "layout": "single",
        "long": True,
    },
    {
        "num": 7,
        "title": "Meet CareTail",
        "subtitle": "A calmer way to organize pet care.",
        "cta": "Visit CareTail",
        "screens": ["01-home.png", "05-diary.png"],
        "caption": "Real home and diary screens",
        "layout": "cta",
        "long": False,
    },
]


def text_wrap(draw, text, fnt, max_width):
    words = text.split()
    lines, line = [], ""
    for word in words:
        trial = (line + " " + word).strip()
        if draw.textbbox((0, 0), trial, font=fnt)[2] <= max_width:
            line = trial
        else:
            if line:
                lines.append(line)
            line = word
    if line:
        lines.append(line)
    return lines


def draw_multiline(draw, xy, text, fnt, fill, max_width, leading=1.08):
    x, y = xy
    for line in text_wrap(draw, text, fnt, max_width):
        draw.text((x, y), line, font=fnt, fill=fill)
        bbox = draw.textbbox((0, 0), line, font=fnt)
        y += int((bbox[3] - bbox[1]) * leading)
    return y


def background():
    img = Image.new("RGB", (W, H), BG).convert("RGBA")
    layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    d = ImageDraw.Draw(layer)
    d.ellipse((-220, -220, 520, 520), fill=(217, 173, 99, 34))
    d.ellipse((720, 1280, 1400, 2040), fill=(108, 139, 104, 25))
    for x in range(0, W, 72):
        d.line((x, 0, x, H), fill=(255, 255, 255, 7), width=1)
    for y in range(0, H, 72):
        d.line((0, y, W, y), fill=(255, 255, 255, 7), width=1)
    return Image.alpha_composite(img, layer)


def rounded_panel(img, box, radius=30, fill=PANEL, outline=LINE):
    d = ImageDraw.Draw(img)
    d.rounded_rectangle(box, radius=radius, fill=fill, outline=outline, width=1)


def paste_rounded(base, shot_path, box, radius=42):
    shot = Image.open(shot_path).convert("RGB")
    target_w = box[2] - box[0]
    target_h = box[3] - box[1]
    scale = min(target_w / shot.width, target_h / shot.height)
    size = (int(shot.width * scale), int(shot.height * scale))
    resized = shot.resize(size, Image.Resampling.LANCZOS).convert("RGBA")

    x = box[0] + (target_w - size[0]) // 2
    y = box[1] + (target_h - size[1]) // 2
    shadow = Image.new("RGBA", (size[0] + 44, size[1] + 44), (0, 0, 0, 0))
    sd = ImageDraw.Draw(shadow)
    sd.rounded_rectangle((22, 22, size[0] + 22, size[1] + 22), radius=radius, fill=(0, 0, 0, 120))
    shadow = shadow.filter(ImageFilter.GaussianBlur(20))
    base.alpha_composite(shadow, (x - 22, y - 22))

    frame = Image.new("RGBA", (size[0] + 28, size[1] + 28), (0, 0, 0, 0))
    fd = ImageDraw.Draw(frame)
    fd.rounded_rectangle((0, 0, size[0] + 28, size[1] + 28), radius=radius + 16, fill="#171411", outline=GOLD, width=2)
    fd.rounded_rectangle((14, 14, size[0] + 14, size[1] + 14), radius=radius, fill="#ffffff")
    base.alpha_composite(frame, (x - 14, y - 14))

    mask = Image.new("L", size, 0)
    ImageDraw.Draw(mask).rounded_rectangle((0, 0, size[0], size[1]), radius=radius, fill=255)
    base.paste(resized, (x, y), mask)
    return (x - 14, y - 14, x + size[0] + 14, y + size[1] + 14)


def header(img, item):
    d = ImageDraw.Draw(img)
    d.text((86, 104), "DCP LABS", font=F_LABEL, fill=MUTED)
    d.text((86, 138), "CareTail", font=F_LABEL, fill=GOLD2)
    d.text((918, 104), f"{item['num']:02d}/07", font=F_NUM, fill=GOLD)
    title_font = F_TITLE_LONG if item.get("long") else F_TITLE
    y = draw_multiline(d, (86, 292), item["title"], title_font, INK, 880, 1.02)
    draw_multiline(d, (86, y + 30), item["subtitle"], F_SUB, MUTED, 790, 1.18)


def source_tag(img, text, y=1762):
    d = ImageDraw.Draw(img)
    d.rounded_rectangle((86, y, 994, y + 62), radius=31, fill="#1b1814", outline="#4a3f31", width=1)
    d.text((118, y + 16), text, font=F_SMALL, fill=SOFT)


def render_single(item):
    img = background()
    header(img, item)
    paste_rounded(img, RAW / item["screens"][0], (456, 700, 994, 1740), 44)
    source_tag(img, item["caption"])
    return img


def render_pair(item):
    img = background()
    header(img, item)
    paste_rounded(img, RAW / item["screens"][0], (100, 800, 508, 1700), 36)
    paste_rounded(img, RAW / item["screens"][1], (562, 710, 982, 1700), 36)
    source_tag(img, item["caption"])
    return img


def render_cta(item):
    img = background()
    header(img, item)
    paste_rounded(img, RAW / item["screens"][0], (116, 780, 508, 1600), 34)
    paste_rounded(img, RAW / item["screens"][1], (574, 780, 966, 1600), 34)
    d = ImageDraw.Draw(img)
    d.rounded_rectangle((350, 1682, 730, 1772), radius=45, fill="#f1d9a5")
    d.text((435, 1708), item["cta"], font=F_BODY, fill="#171411")
    source_tag(img, item["caption"], y=1810)
    return img


def main():
    OUT.mkdir(parents=True, exist_ok=True)
    for item in SLIDES:
        if item["layout"] == "pair":
            img = render_pair(item)
        elif item["layout"] == "cta":
            img = render_cta(item)
        else:
            img = render_single(item)
        img.convert("RGB").save(OUT / f"caretail-day-02-slide-{item['num']:02d}.png", "PNG", optimize=True)


if __name__ == "__main__":
    main()
