#!/usr/bin/env python3
"""Generate deterministic, transparent dark-fantasy PNG sheets for LibGDX."""
from pathlib import Path
from PIL import Image, ImageDraw
import json, math

ROOT = Path("core/assets")
for folder in ("characters", "enemies", "bosses", "weapons", "vfx", "items", "tiles", "ui"):
    (ROOT / folder).mkdir(parents=True, exist_ok=True)

def canvas(w, h): return Image.new("RGBA", (w, h), (0, 0, 0, 0))
def rect(d, xy, c): d.rectangle(xy, fill=c)
def ell(d, xy, c): d.ellipse(xy, fill=c)
def poly(d, xy, c): d.polygon(xy, fill=c)

def player_frame(frame, action="idle"):
    im=canvas(64,64); d=ImageDraw.Draw(im)
    bob = [0,1,0,-1][frame%4] if action in ("idle","move") else 0
    poly(d,[(24,25+bob),(40,25+bob),(46,48+bob),(18,48+bob)],(37,28,52,255))
    poly(d,[(26,29+bob),(38,29+bob),(41,45+bob),(23,45+bob)],(89,35,63,255))
    rect(d,(23,45+bob,29,53+bob),(25,24,34,255)); rect(d,(35,45+bob,41,53+bob),(25,24,34,255))
    ell(d,(22,8+bob,42,30+bob),(30,27,42,255))
    poly(d,[(24,15+bob),(40,15+bob),(37,27+bob),(27,27+bob)],(190,151,120,255))
    rect(d,(28,19+bob,31,21+bob),(232,65,77,255)); rect(d,(35,19+bob,38,21+bob),(232,65,77,255))
    rect(d,(18,27+bob,25,33+bob),(65,74,91,255)); rect(d,(39,27+bob,46,33+bob),(65,74,91,255))
    rect(d,(25,30+bob,39,34+bob),(112,94,77,255))
    if action=="attack":
        poly(d,[(42,30),(49,23),(53,17),(55,19),(51,27),(45,35)],(176,190,205,255)); rect(d,(47,27,50,30),(194,139,79,255))
    elif action=="skill":
        ell(d,(44,18,53,27),(126,63,255,180)); ell(d,(47,20,50,23),(225,204,255,255))
    elif action=="move": rect(d,(18,34,23,39),(132,43,78,255))
    return im

def creature_frame(kind, frame):
    im=canvas(64,64); d=ImageDraw.Draw(im)
    def R(a,b,c,e,col): rect(d,(a,b,c,e),col)
    def E(a,b,c,e,col): ell(d,(a,b,c,e),col)
    def P(points,col): poly(d,points,col)
    skin={"zombie":(73,111,74,255),"goblin":(78,139,61,255),"orc":(100,111,66,255),"skeleton":(193,184,155,255)}
    if kind in skin:
        P([(20,27),(42,27),(46,46),(38,49),(24,49),(17,42)],(43,39,48,255)); R(24,25,40,39,skin[kind]); E(24,12,40,29,skin[kind])
        R(25,17,28,19,(255,66,57,255)); R(35,17,38,19,(255,66,57,255)); R(22,39,28,52,(51,45,54,255)); R(36,39,42,52,(51,45,54,255))
        R(17,29,23,34,skin[kind]); R(41,29,47,34,skin[kind])
        if kind=="skeleton":
            for yy in (30,34,38): R(26,yy,38,yy+1,(77,70,75,255))
            R(28,23,36,25,(45,42,48,255))
        if kind=="orc":
            P([(26,12),(29,7),(31,14)],(225,205,164,255)); P([(34,14),(37,7),(39,12)],(225,205,164,255))
        if kind=="goblin":
            P([(23,15),(14,10),(20,21)],skin[kind]); P([(41,15),(50,10),(44,21)],skin[kind])
    elif kind=="wolf":
        P([(10,31),(20,23),(36,23),(46,29),(54,31),(48,36),(33,37),(23,43),(15,39)],(76,87,111,255))
        P([(18,27),(19,17),(27,26)],(76,87,111,255)); P([(30,26),(33,17),(38,27)],(76,87,111,255))
        R(45,29,50,31,(236,66,73,255)); R(17,38,20,47,(55,61,77,255)); R(37,37,40,47,(55,61,77,255))
    elif kind=="spider":
        E(22,25,42,42,(105,41,52,255)); E(27,17,37,29,(142,49,57,255))
        for a in range(4):
            y=26+a*3; P([(25,y),(16,y-6),(10,y-2)],(105,41,52,255)); P([(39,y),(48,y-6),(54,y-2)],(105,41,52,255))
        R(29,21,31,23,(255,79,57,255)); R(34,21,36,23,(255,79,57,255))
    elif kind=="bat":
        P([(27,25),(18,17),(8,15),(12,27),(6,34),(23,35),(29,31)],(91,39,58,255)); P([(37,25),(46,17),(56,15),(52,27),(58,34),(41,35),(35,31)],(91,39,58,255))
        E(27,22,37,35,(53,42,69,255)); R(29,26,31,28,(255,59,70,255)); R(34,26,36,28,(255,59,70,255))
    return im

def boss_frame(kind, frame):
    im=canvas(128,128); d=ImageDraw.Draw(im)
    if kind=="necromancer":
        poly(d,[(39,38),(89,38),(105,104),(23,104)],(40,27,66,255)); poly(d,[(44,43),(84,43),(91,96),(37,96)],(94,37,92,255))
        ell(d,(42,12,86,56),(29,26,43,255)); poly(d,[(43,30),(85,30),(75,57),(53,57)],(180,145,119,255))
        rect(d,(51,34,59,38),(178,53,255,255)); rect(d,(69,34,77,38),(178,53,255,255)); rect(d,(94,29,99,81),(112,91,67,255)); ell(d,(88,19,105,36),(157,68,255,255))
    elif kind=="lich":
        poly(d,[(40,38),(88,38),(103,107),(25,107)],(32,54,83,255)); ell(d,(43,11,85,55),(35,47,66,255))
        poly(d,[(48,27),(80,27),(75,47),(53,47)],(215,218,199,255)); rect(d,(51,31,60,34),(74,225,255,255)); rect(d,(69,31,78,34),(74,225,255,255))
        poly(d,[(46,13),(38,3),(53,10)],(201,205,213,255)); poly(d,[(82,13),(90,3),(76,10)],(201,205,213,255)); ell(d,(88,58,112,82),(55,186,255,200))
    else:
        poly(d,[(35,42),(92,42),(108,103),(20,103)],(56,24,35,255)); poly(d,[(39,37),(18,14),(8,25),(26,54)],(99,25,43,255)); poly(d,[(89,37),(110,14),(120,25),(102,54)],(99,25,43,255))
        ell(d,(40,10,88,58),(49,34,43,255)); poly(d,[(44,22),(84,22),(74,52),(54,52)],(125,54,54,255))
        rect(d,(51,29,61,33),(255,71,43,255)); rect(d,(70,29,80,33),(255,71,43,255)); poly(d,[(47,10),(35,0),(43,19)],(213,171,99,255)); poly(d,[(82,10),(94,0),(86,19)],(213,171,99,255))
        poly(d,[(35,58),(14,73),(28,80)],(136,44,57,255)); poly(d,[(93,58),(114,73),(100,80)],(136,44,57,255))
    return im

def save_sheet(images, path, cols, cell):
    w,h=cell; out=canvas(cols*w, math.ceil(len(images)/cols)*h)
    for i,img in enumerate(images): out.alpha_composite(img,(i%cols*w,i//cols*h))
    out.save(path,optimize=True)

manifest={"format":"RGBA PNG","frame_sizes":{"characters":[64,64],"enemies":[64,64],"bosses":[128,128],"weapons":[64,64],"vfx":[64,64],"items":[32,32],"tiles":[32,32]},"sheets":[]}
def record(path,frame,cols): manifest["sheets"].append({"path":str(path).replace("\\","/"),"frame":frame,"columns":cols})

for action in ("idle","move","attack","skill","hurt","death"):
    p=ROOT/"characters"/f"player_{action}_64.png"; save_sheet([player_frame(i,action) for i in range(4)],p,4,(64,64)); record(p,(64,64),4)
for kind in ("zombie","skeleton","goblin","orc","wolf","spider","bat"):
    p=ROOT/"enemies"/f"{kind}_64.png"; save_sheet([creature_frame(kind,i) for i in range(4)],p,4,(64,64)); record(p,(64,64),4)
for kind in ("necromancer","lich","demon_lord"):
    p=ROOT/"bosses"/f"{kind}_128.png"; save_sheet([boss_frame(kind,i) for i in range(4)],p,4,(128,128)); record(p,(128,128),4)

for name,color in (("swords",(193,205,220,255)),("bows",(156,107,68,255)),("magic",(153,75,255,255)),("daggers",(185,198,214,255)),("projectiles",(72,191,255,255))):
    frames=[]
    for i in range(6):
        im=canvas(64,64); d=ImageDraw.Draw(im)
        if name=="bows":
            d.arc((15,9,48,55),270,90,fill=color,width=4); rect(d,(29,27,43,29),(195,163,112,255))
        elif name in ("magic","projectiles"):
            ell(d,(22+i%3,22+i%2,42+i%3,42+i%2),color); ell(d,(28,28,36,36),(235,229,255,255)); poly(d,[(32,7),(36,25),(32,32),(28,25)],(218,202,255,255))
        else:
            poly(d,[(31,6),(36,14),(34,42),(31,51),(28,42),(27,14)],color); rect(d,(24,40,39,43),(166,112,67,255)); rect(d,(29,43,34,53),(110,71,45,255))
        frames.append(im)
    p=ROOT/"weapons"/f"{name}_64.png"; save_sheet(frames,p,6,(64,64)); record(p,(64,64),6)

for name,color in (("explosion",(255,133,43,255)),("magic",(158,70,255,255)),("blood",(192,32,54,255)),("heal",(63,255,137,255)),("level_up",(255,213,90,255)),("dust",(154,157,169,200))):
    frames=[]
    for i in range(6):
        im=canvas(64,64); d=ImageDraw.Draw(im); radius=5+i*3
        if name=="heal": rect(d,(29,10,35,54),color); rect(d,(10,29,54,35),color)
        else:
            for a in range(8):
                ang=a*math.pi/4; x=32+int(math.cos(ang)*radius); y=32+int(math.sin(ang)*radius); ell(d,(x-3,y-3,x+3,y+3),color)
            ell(d,(32-radius//2,32-radius//2,32+radius//2,32+radius//2),color)
        frames.append(im)
    p=ROOT/"vfx"/f"{name}_64.png"; save_sheet(frames,p,6,(64,64)); record(p,(64,64),6)

for name,color in (("xp",(71,195,255,255)),("hp_potion",(238,52,69,255)),("mana_potion",(47,131,255,255)),("gems",(190,70,255,255)),("coins",(255,194,55,255)),("keys",(218,173,90,255)),("chest",(145,85,47,255))):
    frames=[]
    for i in range(4):
        im=canvas(32,32); d=ImageDraw.Draw(im)
        if name.endswith("potion"):
            rect(d,(11,7,20,10),(223,213,187,255)); rect(d,(9,10,22,26),(76,47,55,255)); rect(d,(11,12,20,24),color); rect(d,(12,13,14,17),(255,255,255,220))
        elif name=="chest":
            rect(d,(4,10,27,25),(89,47,35,255)); rect(d,(4,10,27,15),(181,117,54,255)); rect(d,(14,15,17,19),(255,211,91,255))
        elif name=="coins":
            ell(d,(5,5,26,26),color); ell(d,(9,9,22,22),(255,229,143,255))
        elif name=="keys":
            ell(d,(5,6,17,18),color); ell(d,(9,10,13,14),(0,0,0,0)); rect(d,(14,11,27,14),color); rect(d,(22,14,25,19),color)
        else:
            poly(d,[(16,2),(27,13),(16,29),(5,13)],color); poly(d,[(16,2),(16,29),(5,13)],(255,255,255,90))
        frames.append(im)
    p=ROOT/"items"/f"{name}_32.png"; save_sheet(frames,p,4,(32,32)); record(p,(32,32),4)

tiles=canvas(256,256); d=ImageDraw.Draw(tiles)
for ty in range(8):
    for tx in range(8):
        x,y=tx*32,ty*32; base=[(37,40,48,255),(48,52,61,255),(33,52,42,255),(43,60,47,255)][(tx+ty)%4]
        rect(d,(x,y,x+31,y+31),base)
        for k in range(7):
            px=x+(k*11+tx*3)%29; py=y+(k*7+ty*5)%29; rect(d,(px,py,px+2,py+2),[(70,74,84,255),(54,80,59,255),(88,73,65,255)][(tx+ty+k)%3])
        if (tx+ty)%7==0:
            rect(d,(x+5,y+5,x+26,y+26),(58,62,72,255)); rect(d,(x+7,y+7,x+24,y+24),(36,40,49,255))
tiles.save(ROOT/"tiles"/"tileset_32.png",optimize=True); manifest["sheets"].append({"path":"core/assets/tiles/tileset_32.png","frame":[32,32],"columns":8,"rows":8})

ui=canvas(256,128); d=ImageDraw.Draw(ui)
for i in range(32):
    x=(i%8)*32; y=(i//8)*32; rect(d,(x+1,y+1,x+30,y+30),(23,27,39,255)); rect(d,(x+2,y+2,x+29,y+29),(65,55,76,255))
    c=[(168,69,255,255),(255,185,63,255),(70,173,255,255),(225,65,79,255)][i%4]
    if i%4==0: ell(d,(x+8,y+8,x+24,y+24),c)
    elif i%4==1: poly(d,[(x+16,y+5),(x+25,y+16),(x+16,y+27),(x+7,y+16)],c)
    elif i%4==2: rect(d,(x+8,y+8,x+23,y+23),c); rect(d,(x+13,y+4,x+18,y+27),(240,241,255,255))
    else: poly(d,[(x+16,y+5),(x+26,y+25),(x+6,y+25)],c)
ui.save(ROOT/"ui"/"icons_32.png",optimize=True); manifest["sheets"].append({"path":"core/assets/ui/icons_32.png","frame":[32,32],"columns":8,"rows":4})

(ROOT/"manifest.json").write_text(json.dumps(manifest,indent=2),encoding="utf-8")
(ROOT/"README.md").write_text("Dark Fantasy Survivor RPG PNG assets generated for LibGDX.\n\n- characters: player actions, 4 frames per sheet, 64x64\n- enemies: enemy sheets, 4 frames per sheet, 64x64\n- bosses: 4 frames per sheet, 128x128\n- weapons and vfx: 6 frames per sheet, 64x64\n- items: 4 frames per sheet, 32x32\n- tiles/tileset_32.png: 8x8 atlas of 32x32 tiles\n- ui/icons_32.png: 8x4 atlas of 32x32 icons\n\nThe Pages workflow runs this generator before the TeaVM build so generated PNGs are copied from core/assets into the browser bundle.\n",encoding="utf-8")
print(f"Generated {len(manifest['sheets'])} sheets under {ROOT}")
