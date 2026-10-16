# -*- coding: utf-8 -*-
"""生成 docs/dev-slides.pptx —— 代码工坊比赛 PPT"""
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR
from pptx.enum.shapes import MSO_SHAPE
from pptx.oxml.ns import qn
from PIL import Image, ImageDraw, ImageFont
import os

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
POSTER = os.path.join(SCRIPT_DIR, ".dev-demo-poster.png")


def make_poster():
    """视频封面：暖白底 + 标题 + 播放按钮 + 副标题（1920x1080，与视频同比例）"""
    W, H = 1920, 1080
    img = Image.new("RGB", (W, H), (0xFA, 0xF4, 0xEA))
    dr = ImageDraw.Draw(img)
    DFONTS = "/Applications/Microsoft PowerPoint.app/Contents/Resources/DFonts"
    f_title = ImageFont.truetype(os.path.join(DFONTS, "msyhbd.ttc"), 110)
    f_sub = ImageFont.truetype(os.path.join(DFONTS, "Deng.ttf"), 44)
    t = "实跑演示"
    bb = dr.textbbox((0, 0), t, font=f_title)
    dr.text(((W - (bb[2] - bb[0])) / 2, 280), t, font=f_title, fill=(0x1F, 0x24, 0x2B))
    cx, cy, r = W // 2, 590, 100
    dr.ellipse((cx - r, cy - r, cx + r, cy + r), fill=(0xC2, 0x41, 0x0C))
    dr.polygon([(cx - 24, cy - 46), (cx - 24, cy + 46), (cx + 52, cy)], fill=(255, 255, 255))
    s2 = "代码工坊 · 平台总览 / 项目接入 / 手动触发 / 夜间调度 / PR 合并（约 2 分 23 秒）"
    bb = dr.textbbox((0, 0), s2, font=f_sub)
    dr.text(((W - (bb[2] - bb[0])) / 2, 780), s2, font=f_sub, fill=(0x66, 0x66, 0x66))
    img.save(POSTER)


make_poster()

FONT_HEAD = "Microsoft YaHei"   # 标题/加粗：微软雅黑，压得住场
FONT_BODY = "DengXian"          # 正文：等线，笔画细更现代（Win10+ 自带）
INK = RGBColor(0x1F, 0x24, 0x2B)      # 近黑墨色（标题/强调）
NAVY = INK                            # 兼容旧引用，统一近黑
BLUE = RGBColor(0xC2, 0x41, 0x0C)     # 深橙点缀（亮厅投影安全）
LBLUE = RGBColor(0xFA, 0xF4, 0xEA)    # 暖白卡片底
GRAY = RGBColor(0x66, 0x66, 0x66)
DARK = RGBColor(0x33, 0x33, 0x33)
ORANGE = RGBColor(0xD9, 0x6A, 0x00)
LORANGE = RGBColor(0xFD, 0xF3, 0xE4)
WHITE = RGBColor(0xFF, 0xFF, 0xFF)
BG = RGBColor(0xFF, 0xFF, 0xFF)
GREEN = BLUE                          # 安全页三卡统一深橙系
LGREEN = LBLUE
LINE = RGBColor(0xE4, 0xDE, 0xD4)     # 暖灰描边

DOCS = SCRIPT_DIR

prs = Presentation()
prs.slide_width = Inches(13.333)
prs.slide_height = Inches(7.5)
BLANK = prs.slide_layouts[6]


def style_run(run, size=14, bold=False, color=DARK, font=None):
    # 未显式指定时：加粗用标题字体（雅黑），正文用等线
    name = font or (FONT_HEAD if bold else FONT_BODY)
    f = run.font
    f.size = Pt(size)
    f.bold = bold
    f.color.rgb = color
    f.name = name
    rPr = run._r.get_or_add_rPr()
    ea = rPr.find(qn("a:ea"))
    if ea is None:
        ea = rPr.makeelement(qn("a:ea"), {})
        rPr.append(ea)
    ea.set("typeface", name)


def add_text(slide, x, y, w, h, lines, anchor=MSO_ANCHOR.TOP):
    """lines: list of (text, dict) — dict keys: size,bold,color,align,space_before,space_after,line_spacing"""
    tb = slide.shapes.add_textbox(Inches(x), Inches(y), Inches(w), Inches(h))
    tf = tb.text_frame
    tf.word_wrap = True
    tf.vertical_anchor = anchor
    for i, item in enumerate(lines):
        text, kw = item
        p = tf.paragraphs[0] if i == 0 else tf.add_paragraph()
        p.alignment = kw.get("align", PP_ALIGN.LEFT)
        p.space_before = Pt(kw.get("space_before", 0))
        p.space_after = Pt(kw.get("space_after", 4))
        p.line_spacing = kw.get("line_spacing", 1.12)
        r = p.add_run()
        r.text = text
        style_run(r, kw.get("size", 14), kw.get("bold", False), kw.get("color", DARK), kw.get("font"))
    return tb


def fill_tf(shp, lines, anchor=MSO_ANCHOR.TOP, margin=0.12):
    tf = shp.text_frame
    tf.word_wrap = True
    tf.vertical_anchor = anchor
    tf.margin_left = Inches(margin)
    tf.margin_right = Inches(margin)
    tf.margin_top = Inches(margin)
    tf.margin_bottom = Inches(margin)
    for i, item in enumerate(lines):
        text, kw = item
        p = tf.paragraphs[0] if i == 0 else tf.add_paragraph()
        p.alignment = kw.get("align", PP_ALIGN.LEFT)
        p.space_before = Pt(kw.get("space_before", 0))
        p.space_after = Pt(kw.get("space_after", 4))
        p.line_spacing = kw.get("line_spacing", 1.12)
        r = p.add_run()
        r.text = text
        style_run(r, kw.get("size", 14), kw.get("bold", False), kw.get("color", DARK), kw.get("font"))


def card(slide, x, y, w, h, fill=WHITE, line=LINE, shape=MSO_SHAPE.ROUNDED_RECTANGLE):
    shp = slide.shapes.add_shape(shape, Inches(x), Inches(y), Inches(w), Inches(h))
    if shape == MSO_SHAPE.ROUNDED_RECTANGLE:
        shp.adjustments[0] = 0.07
    shp.fill.solid()
    shp.fill.fore_color.rgb = fill
    if line:
        shp.line.color.rgb = line
        shp.line.width = Pt(1)
    else:
        shp.line.fill.background()
    shp.shadow.inherit = False
    return shp


def rect(slide, x, y, w, h, fill, line=None):
    shp = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(x), Inches(y), Inches(w), Inches(h))
    shp.fill.solid()
    shp.fill.fore_color.rgb = fill
    if line:
        shp.line.color.rgb = line
    else:
        shp.line.fill.background()
    shp.shadow.inherit = False
    return shp


PAGE = [0]


def content_slide(title, kicker=None):
    s = prs.slides.add_slide(BLANK)
    s.background.fill.solid()
    s.background.fill.fore_color.rgb = BG
    rect(s, 0, 0, 13.333, 0.06, BLUE)
    rect(s, 0.55, 0.46, 0.09, 0.52, ORANGE if kicker else BLUE)
    lines = []
    if kicker:
        lines.append((kicker, dict(size=12, bold=True, color=BLUE, space_after=1)))
    lines.append((title, dict(size=25, bold=True, color=NAVY, space_after=0)))
    add_text(s, 0.78, 0.28, 12.0, 1.0, lines)
    PAGE[0] += 1
    add_text(s, 0.55, 7.12, 9, 0.3,
             [("代码工坊 · AI 自动值守的代码质量优化流水线", dict(size=9, color=GRAY, space_after=0))])
    add_text(s, 12.2, 7.12, 0.6, 0.3,
             [(str(PAGE[0]), dict(size=9, color=GRAY, align=PP_ALIGN.RIGHT, space_after=0))])
    return s


# ============ S1 封面 ============
s = prs.slides.add_slide(BLANK)
s.background.fill.solid()
s.background.fill.fore_color.rgb = WHITE
rect(s, 1.0, 3.62, 2.2, 0.06, ORANGE)
add_text(s, 1.0, 1.7, 11.3, 1.9, [
    ("代码工坊", dict(size=54, bold=True, color=INK, space_after=10)),
    ("AI 自动值守的代码质量优化流水线", dict(size=24, bold=True, color=BLUE, space_after=0)),
])
add_text(s, 1.0, 4.0, 11.3, 1.6, [
    ("晚上 AI 修代码，早上人工审 PR ——「AI 自主 + 人工门」", dict(size=17, color=DARK, space_after=8)),
])
add_text(s, 1.0, 5.75, 11.3, 0.5, [
    ("汇报人：吉普    团队：赛博管道工    汇报时间：2026/10/17",
     dict(size=14, color=DARK, space_after=0)),
])
add_text(s, 1.0, 6.55, 11.3, 0.7, [
    ("演示环境：https://morningstar369.com/dev/about", dict(size=11, color=GRAY, space_after=2)),
    ("演示账号：SpiderMan / xxxxxxxx", dict(size=11, color=GRAY, space_after=0)),
])

# ============ S2 痛点问题 ============
s = content_slide("痛点：技术债只增不减，为什么治不好", "01 · 为什么做")
c = card(s, 0.55, 1.75, 4.6, 3.95, LORANGE, None)
fill_tf(c, [
    ("痛点", dict(size=13, bold=True, color=BLUE, space_after=6)),
    ("技术债只增不减", dict(size=20, bold=True, color=NAVY, space_after=8)),
    ("白天为赶进度只保核心业务逻辑跑通，留下一堆不影响运行、但存在隐患的技术债。",
     dict(size=13.5, color=DARK, line_spacing=1.3, space_after=0)),
], margin=0.3)
add_text(s, 5.45, 1.45, 7.3, 0.4, [
    ("为什么只增不减？卡在两个原因上", dict(size=14, bold=True, color=NAVY, space_after=0)),
])
reasons = [
    ("原因一 · 人力无暇消化",
     "需求对齐、跨团队对接、联调沟通——只有人能做的事已占满人力；同样工时投业务需求产出明确，投在还技术债上短期产出不直观。"),
    ("原因二 · 算力供需错配",
     "白天行内模型资源被人机协同任务占满，到了夜间算力又大量空转，能用来还技术债的算力没有被利用。"),
]
for i, (t, d) in enumerate(reasons):
    c = card(s, 5.45, 1.95 + i * 1.98, 7.33, 1.83, WHITE)
    fill_tf(c, [
        (t, dict(size=15, bold=True, color=NAVY, space_after=4)),
        (d, dict(size=12.5, color=DARK, line_spacing=1.22, space_after=0)),
    ], margin=0.22)

# ============ S3 解决思路 ============
s = content_slide("解决思路：晚上 AI 修问题 + 早上人工审 PR", "02 · 怎么做")
c = card(s, 0.55, 1.7, 6.0, 3.5, LBLUE, None)
fill_tf(c, [
    ("🌙 晚上 · AI 修问题", dict(size=19, bold=True, color=NAVY, space_after=8)),
    ("夜间平台定时调度自动拉起流水线", dict(size=14, bold=True, color=BLUE, space_after=4)),
    ("逐步执行扫描、修复、验证、提交 PR", dict(size=14, color=DARK, space_after=4)),
    ("全程无人值守，重活归 AI", dict(size=14, color=DARK, space_after=0)),
], margin=0.3)
c = card(s, 6.78, 1.7, 6.0, 3.5, LORANGE, None)
fill_tf(c, [
    ("☀️ 早上 · 人工审 PR", dict(size=19, bold=True, color=NAVY, space_after=8)),
    ("开发人员评审 PR、决定合不合并", dict(size=14, bold=True, color=ORANGE, space_after=4)),
    ("裁决权在人，AI 没有主干写权限", dict(size=14, color=DARK, space_after=0)),
], margin=0.3)
bar = card(s, 0.55, 5.55, 12.23, 1.0, LORANGE, None)
fill_tf(bar, [
    ("利用夜间闲时算力，几乎不新增硬件成本；让 AI 在人划定的边界内，把人从重复劳动中解放出来。",
     dict(size=14.5, bold=True, color=INK, align=PP_ALIGN.CENTER, space_after=0)),
], anchor=MSO_ANCHOR.MIDDLE, margin=0.2)

# ============ S-demo 实跑演示（嵌入视频） ============
s = content_slide("实跑演示：平台总览 → 项目接入 → 手动触发 → 夜间调度 → PR 合并", "02 · 怎么做 · 演示")
mv_w = 9.42
mv_h = mv_w * 9 / 16  # ≈ 5.30，与视频 1920x1080 同比例
s.shapes.add_movie(f"{DOCS}/dev-demo.mp4", Inches((13.333 - mv_w) / 2), Inches(1.5),
                   Inches(mv_w), Inches(mv_h),
                   poster_frame_image=POSTER, mime_type="video/mp4")

# ============ S4 核心流程总览 ============
s = content_slide("核心流程：扫描 → 修复 → 验证 → 提交", "03 · 核心流程")
steps = [
    ("双通道扫描", "规则引擎 + 语义理解\n问题统一模型落库", LORANGE, INK),
    ("AI 修复", "统一 prompt 模板\nMCP 边修边查", LORANGE, INK),
    ("两道验证", "客观重扫比对\n+ AI 语义评审", LORANGE, INK),
    ("提交 PR", "结构化修复报告\n一轮一 PR", BLUE, WHITE),
]
for i, (t, d, color, tc) in enumerate(steps):
    x = 0.55 + i * 3.22
    shp = s.shapes.add_shape(MSO_SHAPE.CHEVRON, Inches(x), Inches(2.55), Inches(3.0), Inches(2.5))
    shp.adjustments[0] = 0.28
    shp.fill.solid()
    shp.fill.fore_color.rgb = color
    if color == LORANGE:
        shp.line.color.rgb = ORANGE
        shp.line.width = Pt(1)
    else:
        shp.line.fill.background()
    shp.shadow.inherit = False
    fill_tf(shp, [
        (t, dict(size=18, bold=True, color=tc, align=PP_ALIGN.CENTER, space_after=8)),
        *[(seg, dict(size=13, color=tc, align=PP_ALIGN.CENTER, space_after=2)) for seg in d.split("\n")],
    ], anchor=MSO_ANCHOR.MIDDLE, margin=0.1)

# ============ S5 双通道扫描 ============
s = content_slide("双通道扫描：规则引擎 + 语义理解", "03 · 核心流程 · 扫描")
c = card(s, 0.55, 1.6, 6.0, 3.3, WHITE)
fill_tf(c, [
    ("规则引擎（SonarQube）", dict(size=17, bold=True, color=BLUE, space_after=8)),
    ("已知模式识别：空指针、SQL 注入、资源泄漏等规则化问题", dict(size=13.5, space_after=6)),
    ("每条 issue 附带规则文档（是什么、怎么修）", dict(size=13.5, space_after=6)),
    ("规则文档直接成为修复环节的诊断上下文", dict(size=13.5, space_after=0)),
], margin=0.28)
c = card(s, 6.78, 1.6, 6.0, 3.3, WHITE)
fill_tf(c, [
    ("语义理解（Claude Code）", dict(size=17, bold=True, color=ORANGE, space_after=8)),
    ("要读懂代码才知道的问题：上帝类、竞态条件、N+1 查询、语义重复", dict(size=13.5, space_after=6)),
    ("规则引擎抓不到的问题，由它补齐", dict(size=13.5, space_after=6)),
    ("按约定 schema 输出：问题描述、修复建议、类型分类", dict(size=13.5, space_after=0)),
], margin=0.28)
bar = card(s, 0.55, 5.15, 12.23, 1.55, LBLUE, None)
fill_tf(bar, [
    ("每个问题按三个质量维度打标：Security 安全漏洞 / Reliability 逻辑缺陷 / Maintainability 代码异味",
     dict(size=13.5, bold=True, color=NAVY, space_after=6)),
    ("两通道互补而非替代；问题合并为统一模型落库，走同一套修复与验证流程，PR 报告逐条标注来源（SONAR / AI）",
     dict(size=13.5, color=DARK, space_after=6)),
    ("扫描不设目录边界：上帝类、语义重复等问题天然跨目录，预设边界等于先行排除；边界由沙盒容器承担",
     dict(size=13, color=GRAY, space_after=0)),
], anchor=MSO_ANCHOR.MIDDLE, margin=0.28)

# ============ S6 AI 修复 ============
s = content_slide("AI 修复：统一 prompt + MCP 边修边查", "03 · 核心流程 · 修复")
c = card(s, 0.55, 1.7, 6.0, 3.6, WHITE)
fill_tf(c, [
    ("统一 prompt 模板", dict(size=17, bold=True, color=NAVY, space_after=8)),
    ("扫描阶段已把每个问题的诊断信息入库", dict(size=13.5, space_after=6)),
    ("修复时所有 issue 共用一个 prompt 模板", dict(size=13.5, space_after=6)),
    ("AI 从标题、代码片段、元数据中获取上下文", dict(size=13.5, space_after=0)),
], margin=0.28)
c = card(s, 6.78, 1.7, 6.0, 3.6, WHITE)
fill_tf(c, [
    ("边修边查，查到干净为止", dict(size=17, bold=True, color=NAVY, space_after=8)),
    ("修复过程中逐个文件调用 SonarQube MCP analyze_code_snippet 检查，有新增 issue 就继续修，直到干净",
     dict(size=13, space_after=6, line_spacing=1.15)),
    ("改动涉及被引用的声明时，连带检查直接引用文件——只查直接引用，不扩大范围",
     dict(size=13, space_after=6, line_spacing=1.15)),
    ("Maven 项目修复后必须编译通过，不通过继续修", dict(size=13, space_after=0, line_spacing=1.15)),
], margin=0.28)
bar = card(s, 0.55, 5.6, 12.23, 1.1, LORANGE, None)
fill_tf(bar, [
    ("修复产出克制：只输出「一句话总结 + 修复思路」作为 commit message —— 不让 AI 自述验证、自评风险，验证是下一环节的职责。",
     dict(size=13.5, bold=True, color=NAVY, align=PP_ALIGN.CENTER, space_after=0)),
], anchor=MSO_ANCHOR.MIDDLE, margin=0.25)

# ============ S7 两道防线验证 ============
s = content_slide("两道防线验证：客观门禁 + 语义评审", "03 · 核心流程 · 验证")
c = card(s, 0.55, 1.65, 6.0, 3.5, WHITE)
fill_tf(c, [
    ("防线一 · 规则引擎重扫", dict(size=17, bold=True, color=BLUE, space_after=6)),
    ("原来报的问题都消了吗？有没有引入新问题？", dict(size=13.5, bold=True, color=NAVY, space_after=8)),
    ("修复完成后重新扫描、逐条比对", dict(size=13.5, space_after=4)),
    ("本轮修的问题必须全部关闭", dict(size=13.5, space_after=4)),
    ("多出一个新问题即整轮失败", dict(size=13.5, color=ORANGE, bold=True, space_after=0)),
], margin=0.28)
c = card(s, 6.78, 1.65, 6.0, 3.5, WHITE)
fill_tf(c, [
    ("防线二 · AI 语义评审", dict(size=17, bold=True, color=BLUE, space_after=6)),
    ("修复思路对不对？代码真的按思路改到位了吗？", dict(size=13.5, bold=True, color=NAVY, space_after=8)),
    ("不给 diff，直接读修复后的代码", dict(size=13.5, space_after=4)),
    ("结合 issue 诊断 + commit message 做判断", dict(size=13.5, space_after=4)),
    ("思路是 commit message 描述的，改法是读代码验的", dict(size=13.5, space_after=0)),
], margin=0.28)
bar = card(s, 0.55, 5.45, 12.23, 1.25, LORANGE, None)
fill_tf(bar, [
    ("两道缺一不可，两道都过才算通过：", dict(size=14, bold=True, color=INK, align=PP_ALIGN.CENTER, space_after=3)),
    ("只有重扫，防不住「改错方向」；只有评审，防不住「悄悄冒出的新问题」。宁可整轮重修，也不放行可疑修复。",
     dict(size=13, color=DARK, align=PP_ALIGN.CENTER, space_after=0)),
], anchor=MSO_ANCHOR.MIDDLE, margin=0.25)

# ============ S8 提交 PR + 串行修复 ============
s = content_slide("提交 PR：结构化修复报告；一轮一 PR", "03 · 核心流程 · 提交")
c = card(s, 0.55, 1.65, 6.0, 4.9, WHITE)
fill_tf(c, [
    ("PR = 可直接评审的修复报告", dict(size=16.5, bold=True, color=NAVY, space_after=8)),
    ("每条问题包含：", dict(size=13.5, bold=True, space_after=4)),
    ("· 标题 + 三维严重度", dict(size=13.5, space_after=3)),
    ("· 代码片段链接（直达源码对应行）", dict(size=13.5, space_after=3)),
    ("· 修改记录链接（直达对应 commit）", dict(size=13.5, space_after=3)),
    ("· AI 问题额外附类型与诊断说明", dict(size=13.5, space_after=8)),
    ("评审者不用打开 SonarQube、不翻原始扫描数据",
     dict(size=13.5, bold=True, color=BLUE, space_after=0)),
], margin=0.28)
c = card(s, 6.78, 1.65, 6.0, 4.9, WHITE)
fill_tf(c, [
    ("串行修复：一轮一 PR", dict(size=16.5, bold=True, color=NAVY, space_after=8)),
    ("每修一个 issue，代码基线就变一次", dict(size=13.5, space_after=4)),
    ("基于同一基线并行修出多个 PR，合并时容易互相冲突", dict(size=13.5, space_after=8)),
    ("· 逐 issue commit 叠加在同一分支", dict(size=13.5, space_after=3)),
    ("· 夜间每仓库跑一轮，只形成一个 PR", dict(size=13.5, space_after=3)),
    ("· 跨晚续修先同步最新远端：已合并的进入新基线，未合并的继续等待人工处置", dict(size=13.5, space_after=0)),
], margin=0.28)

# ============ S9 18 态状态机（图） ============
s = content_slide("可靠性：18 态状态机驱动", "04 · 可靠性")
img_w, img_h = 5884, 3724
disp_h = 5.3
disp_w = disp_h * img_w / img_h  # ≈ 8.37
if disp_w > 8.4:
    disp_w = 8.4
    disp_h = disp_w * img_h / img_w
pic = s.shapes.add_picture(f"{DOCS}/dev-状态转移图-演示.drawio.png",
                           Inches(0.5), Inches(1.55), Inches(disp_w), Inches(disp_h))
pic.line.color.rgb = LINE
pic.line.width = Pt(1)
add_text(s, 9.2, 1.7, 3.7, 5.2, [
    ("一条主链", dict(size=15, bold=True, color=NAVY, space_after=3)),
    ("每两态构成一个执行阶段（进行中 → 完成），阶段间自动接力", dict(size=12.5, space_after=10, line_spacing=1.2)),
    ("两种重试", dict(size=15, bold=True, color=NAVY, space_after=3)),
    ("没动代码的环节原地重试；动了代码的先回滚到修复前现场再重试，重试耗尽才判失败",
     dict(size=12.5, space_after=10, line_spacing=1.2)),
    ("职责分明", dict(size=15, bold=True, color=NAVY, space_after=3)),
    ("状态 / 动作 / 触发 / 转移四要素分离，无嵌套状态机、无隐式行为", dict(size=12.5, space_after=0, line_spacing=1.2)),
])

# ============ S10 回退与兜底 ============
s = content_slide("可靠性：整轮回退、沙盒隔离与故障兜底", "04 · 可靠性")
c = card(s, 0.55, 1.6, 6.0, 2.5, LORANGE, None)
fill_tf(c, [
    ("整轮回退：要么全过，要么重来", dict(size=15.5, bold=True, color=NAVY, space_after=6)),
    ("任一环节失败回到修复前干净基线，全部重修——不同基线上的修复拼在一起没有兼容验证；夜间算力不稀缺，重修代价远小于带回归的 PR 合进主干。",
     dict(size=12.5, line_spacing=1.2, space_after=0)),
], margin=0.22)
c = card(s, 6.78, 1.6, 6.0, 2.5, WHITE)
fill_tf(c, [
    ("回退牵连面可配置", dict(size=15.5, bold=True, color=NAVY, space_after=6)),
    ("每轮修复的 issue 数量按 Sonar / AI 两通道分别设上限；上限调低，一次失败拖累的重修范围就小——行为靠调参覆盖，架构不用动。",
     dict(size=12.5, line_spacing=1.2, space_after=0)),
], margin=0.22)
c = card(s, 0.55, 4.35, 6.0, 2.5, WHITE)
fill_tf(c, [
    ("沙盒隔离", dict(size=15.5, bold=True, color=NAVY, space_after=6)),
    ("每轮任务起独立沙盒容器，代码卷按项目缓存复用，项目删除时连卷回收。",
     dict(size=12.5, line_spacing=1.2, space_after=0)),
], margin=0.22)
c = card(s, 6.78, 4.35, 6.0, 2.5, WHITE)
fill_tf(c, [
    ("故障兜底", dict(size=15.5, bold=True, color=NAVY, space_after=6)),
    ("统一失败态记录原因，清理防循环；卡死超时强制清理，过夜任务一律取消；后端重启后执行中任务逐个接回状态机，不丢任务。",
     dict(size=12.5, line_spacing=1.2, space_after=0)),
], margin=0.22)

# ============ S11 整体架构（图） ============
s = content_slide("整体架构：自研重心在指挥，不在执行", "05 · 架构")
img_w, img_h = 2728, 2336
disp_h = 5.3
disp_w = disp_h * img_w / img_h  # ≈ 6.19
pic = s.shapes.add_picture(f"{DOCS}/dev-项目架构图.drawio.png",
                           Inches(0.5), Inches(1.55), Inches(disp_w), Inches(disp_h))
pic.line.color.rgb = LINE
pic.line.width = Pt(1)
add_text(s, 7.2, 1.7, 5.7, 5.2, [
    ("四类角色", dict(size=15, bold=True, color=NAVY, space_after=3)),
    ("自研组件 / 三方工具 / 外部服务 / 数据存储", dict(size=12.5, space_after=10, line_spacing=1.2)),
    ("自研发力在指挥", dict(size=15, bold=True, color=NAVY, space_after=3)),
    ("动作何时触发、失败怎么重试回退、凭证怎么注入、结果怎么统计，全由自研后端掌控",
     dict(size=12.5, space_after=10, line_spacing=1.2)),
    ("执行交给成熟工具", dict(size=15, bold=True, color=NAVY, space_after=3)),
    ("步骤里调用 Git、Sonar Scanner、Claude Code；前端把每轮任务状态实时可视化",
     dict(size=12.5, space_after=0, line_spacing=1.2)),
])

# ============ S12 安全模型 ============
s = content_slide("安全模型：能改仓库代码的凭证，绝不进 AI 容器", "06 · 安全")
secs = [
    ("碰不到 · 凭证隔离", "Git 凭证只留后端，用时注入临时容器、当次生效用完即毁；AI 容器内零 Git 凭证，只有可独立轮换的模型 API key 与 SonarQube token。", LBLUE, BLUE),
    ("权不大 · 最小权限", "双 token 各司其职：admin token 仅在接入/改配置时使用，流水线运行全程零 admin 凭证；bot token 按仓库收放，接入时授权、删除时回收。", LGREEN, GREEN),
    ("留不下 · 凭证脱敏", "命令日志与失败异常中的凭证值统一打码——凭证不进日志、不落库。", LORANGE, ORANGE),
]
for i, (t, d, fill, tc) in enumerate(secs):
    x = 0.55 + i * 4.18
    c = card(s, x, 1.7, 3.9, 3.3, fill, None)
    fill_tf(c, [
        (t, dict(size=16, bold=True, color=tc, space_after=8)),
        (d, dict(size=12.5, color=DARK, line_spacing=1.22, space_after=0)),
    ], margin=0.25)
bar = card(s, 0.55, 5.3, 12.23, 1.4, LORANGE, None)
fill_tf(bar, [
    ("身份分离：项目管理员 / 流水线机器人 / 平台管理员各司其职", dict(size=14, bold=True, color=INK, align=PP_ALIGN.CENTER, space_after=3)),
    ("平台管理员只有熔断权与调度权；任一身份被攻破或滥用，爆炸半径都被限制在自己的职责内。",
     dict(size=12.5, color=DARK, align=PP_ALIGN.CENTER, space_after=0)),
], anchor=MSO_ANCHOR.MIDDLE, margin=0.25)

# ============ S13 KPI ============
s = content_slide("平台运维 KPI：规模 → 实时 → 产出 → 质量 → 价值", "07 · 运营")
kpis = [
    ("接入仓库", "规模", "平台服务的项目面有多大"),
    ("并发任务", "实时", "此刻并发槽占用"),
    ("累计交付修复", "产出", "AI 实际修掉并通过验证的 issue 数"),
    ("PR 合并率", "质量", "人工评审通过的 PR 比例"),
    ("累计节约人天", "价值", "已采纳 issue 估算工时之和"),
]
for i, (t, tag, d) in enumerate(kpis):
    x = 0.55 + i * 2.52
    c = card(s, x, 2.1, 2.32, 3.1, WHITE)
    fill_tf(c, [
        (tag, dict(size=12, bold=True, color=ORANGE, space_after=6)),
        (t, dict(size=16, bold=True, color=NAVY, space_after=8)),
        (d, dict(size=11.5, color=DARK, line_spacing=1.2, space_after=0)),
    ], margin=0.18)
add_text(s, 0.55, 5.7, 12.2, 0.9, [
    ("一条链路看清平台价值：接了多少项目 → 此刻跑多满 → 修掉多少问题 → 人认可多少 → 省下多少人天。",
     dict(size=14, bold=True, color=NAVY, align=PP_ALIGN.CENTER, space_after=0)),
], anchor=MSO_ANCHOR.MIDDLE)

# ============ S14 开发过程 ============
s = content_slide("开发过程：人主导 + AI 辅助", "08 · 开发")
c = card(s, 0.55, 1.6, 6.0, 4.9, WHITE)
fill_tf(c, [
    ("分工原则", dict(size=16.5, bold=True, color=NAVY, space_after=6)),
    ("不可外包的判断人把住，可快速修正的表达交给 AI", dict(size=13.5, bold=True, color=BLUE, space_after=10)),
    ("后端 · 人牢牢握住", dict(size=14.5, bold=True, color=NAVY, space_after=4)),
    ("状态机、动作边界、安全模型、权限、数据一致性——出错代价是越权、写坏仓库、死锁；AI 只承担样板代码、单测骨架、调试辅助。",
     dict(size=12.5, line_spacing=1.2, space_after=10)),
    ("前端 · AI 多承担", dict(size=14.5, bold=True, color=NAVY, space_after=4)),
    ("迭代快、回滚成本低：布局、组件、样式交给 AI；人负责信息架构定稿与关键交互拍板。",
     dict(size=12.5, line_spacing=1.2, space_after=0)),
], margin=0.28)
c = card(s, 6.78, 1.6, 6.0, 4.9, WHITE)
evo = [
    ("1", "跑通基础状态机", "最简主链 + 假实现，只验证状态机本身正确"),
    ("2", "加入重试与回退环", "真实修复一定会失败，回退环先于真实动作定稿"),
    ("3", "按依赖顺序填真实动作", "每换一个真实实现就跑端到端验证，避免多变量同时爆炸"),
    ("4", "PR 反馈独立成环", "人工裁决不污染状态机，定时任务轮询回写"),
]
tf_lines = [("后端实现演进四步", dict(size=16.5, bold=True, color=NAVY, space_after=10))]
for n, t, d in evo:
    tf_lines.append((f"第 {n} 步 · {t}", dict(size=13.5, bold=True, color=BLUE, space_after=2, space_before=6)))
    tf_lines.append((d, dict(size=12.5, color=DARK, space_after=4, line_spacing=1.2)))
fill_tf(c, tf_lines, margin=0.28)

# ============ S15 竞品对标 ============
s = content_slide("为什么自研：对标 SonarQube AI CodeFix / Copilot Autofix", "09 · 差异")
add_text(s, 0.55, 1.35, 12.2, 0.55, [
    ("大厂也在做，恰恰说明「AI 修复静态分析发现的问题」是被验证的真实需求；差异不在单点修复能力，而在以下几个维度：",
     dict(size=13, color=GRAY, space_after=0)),
])
rows = [
    ("维度", "SonarQube AI CodeFix", "GitHub Copilot Autofix", "代码工坊"),
    ("产品定位", "per-issue 辅助：生成建议、人手动 apply，不 commit 不开 PR", "逐条 suggestion；agentic 模式自主修复开 PR（公测中）", "平台级流水线：扫描→修复→验证→提交全自动"),
    ("部署方式", "自托管需 Enterprise 付费版 + 自建 LLM 网关", "只在 GitHub 云上运行，进不了银行内网", "内网私有化部署，模型可插拔（国产/行内模型）"),
    ("调度方式", "无调度", "逐条指派", "夜间定时、多仓库、并发槽排队分发，无人值守"),
    ("验证深度", "无验证闭环", "仅重跑 CodeQL 一道门禁", "规则引擎重扫门禁 + 独立语义评审两道防线，失败整轮回退"),
    ("扫描通道", "SonarQube 规则", "CodeQL 规则", "规则引擎 + 语义理解双通道，覆盖竞态、N+1、上帝类等规则外问题"),
]
tw, th = 12.23, 4.35
tbl_shape = s.shapes.add_table(len(rows), 4, Inches(0.55), Inches(2.0), Inches(tw), Inches(th))
tbl = tbl_shape.table
tbl.columns[0].width = Inches(1.35)
for ci in (1, 2, 3):
    tbl.columns[ci].width = Inches((tw - 1.35) / 3)
for ri, row in enumerate(rows):
    for ci, text in enumerate(row):
        cell = tbl.cell(ri, ci)
        cell.fill.solid()
        if ri == 0:
            cell.fill.fore_color.rgb = NAVY if ci < 3 else ORANGE
        elif ci == 3:
            cell.fill.fore_color.rgb = LORANGE
        elif ci == 0:
            cell.fill.fore_color.rgb = LBLUE
        else:
            cell.fill.fore_color.rgb = WHITE
        cell.margin_left = Inches(0.08)
        cell.margin_right = Inches(0.08)
        cell.margin_top = Inches(0.04)
        cell.margin_bottom = Inches(0.04)
        cell.vertical_anchor = MSO_ANCHOR.MIDDLE
        tfc = cell.text_frame
        tfc.word_wrap = True
        p = tfc.paragraphs[0]
        r = p.add_run()
        r.text = text
        if ri == 0:
            style_run(r, 12, True, WHITE)
        elif ci == 0:
            style_run(r, 11.5, True, NAVY)
        elif ci == 3:
            style_run(r, 10.5, True, DARK)
        else:
            style_run(r, 10.5, False, DARK)
add_text(s, 0.55, 6.5, 12.2, 0.5, [
    ("单点修复能力大家都会有，差异在于能否部署进内网环境、以无人值守的方式跑完整条流水线。",
     dict(size=13.5, bold=True, color=NAVY, align=PP_ALIGN.CENTER, space_after=0)),
])

# ============ S16 价值与规划 ============
s = content_slide("项目价值与未来规划", "10 · 价值")
c = card(s, 0.55, 1.6, 6.0, 4.95, LBLUE, None)
vals = [
    ("自动化闭环，解放人力", "扫描 → 修复 → 验证转化为后台无感进程"),
    ("专注业务，治理技术债", "AI 后台治债，开发人员聚焦业务研发"),
    ("极低成本，极致利用", "充分利用行内既有闲时算力，几乎不新增硬件成本"),
    ("严控风险，守住合规底线", "绝不赋予 AI 生产发布或主干合并权限"),
    ("渐进式演进，远景可期", "架构可扩展，条件成熟时可接入完整研发流水线"),
]
vl = [("项目价值", dict(size=16.5, bold=True, color=NAVY, space_after=8))]
for t, d in vals:
    vl.append(("· " + t, dict(size=13.5, bold=True, color=BLUE, space_after=1, space_before=5)))
    vl.append(("  " + d, dict(size=12, color=DARK, space_after=2)))
fill_tf(c, vl, margin=0.28)
c = card(s, 6.78, 1.6, 6.0, 4.95, WHITE)
plans = [
    ("高频缺陷分析反哺研发培训", "长期积累大量 issue 数据后再做聚合分析"),
    ("项目优先级排序", "资源紧张时再配置，当前先到先修"),
    ("GitLab 适配", "内网部署时替换代码托管平台实现"),
    ("管理员操作审计表", "出现争议时从日志留痕升级"),
    ("容器网络出站白名单", "出现外发风险或合规要求时启用"),
]
pl = [("未来规划（条件成熟再做）", dict(size=16.5, bold=True, color=NAVY, space_after=8))]
for t, d in plans:
    pl.append(("· " + t, dict(size=13.5, bold=True, color=NAVY, space_after=1, space_before=5)))
    pl.append(("  " + d, dict(size=12, color=GRAY, space_after=2)))
fill_tf(c, pl, margin=0.28)

# ============ S17 结尾 ============
s = prs.slides.add_slide(BLANK)
s.background.fill.solid()
s.background.fill.fore_color.rgb = WHITE
rect(s, 5.57, 4.12, 2.2, 0.06, ORANGE)
add_text(s, 0, 2.5, 13.333, 1.6, [
    ("谢谢聆听", dict(size=54, bold=True, color=INK, align=PP_ALIGN.CENTER, space_after=10)),
    ("恳请各位评委批评指正", dict(size=18, color=GRAY, align=PP_ALIGN.CENTER, space_after=0)),
])
add_text(s, 0, 4.6, 13.333, 1.2, [
    ("演示环境：https://morningstar369.com/dev/about", dict(size=13, color=GRAY, align=PP_ALIGN.CENTER, space_after=3)),
    ("演示账号：SpiderMan / xxxxxxxx", dict(size=13, color=GRAY, align=PP_ALIGN.CENTER, space_after=0)),
])

# ============ 演讲者备注（演示者视图用） ============
NOTES = [
    # P1 封面（15 秒）
    "【15秒】各位评委，大家好。我是吉普，来自赛博管道工团队。今天汇报的作品是代码工坊，一套 AI 自动值守的代码质量优化流水线。【翻页】",
    # P2 痛点（45 秒）
    "【45秒】先讲为什么做这个平台。我们日常开发中有一个痛点问题：白天为了赶进度只能保证核心业务逻辑跑通，留下一堆不影响运行、但存在隐患的技术债。主要是两方面原因，一是人力无暇消化：需求对齐、跨团队对接、联调沟通，这些只有人能做的事已经占满人力；同样工时投业务需求产出明确，投在还技术债上短期产出不直观。二是算力供需错配：白天模型资源被占满，夜间又大量空转，能用来还债的算力没有被利用。【翻页】",
    # P3 解决思路（40 秒）
    "【40秒】针对这两个原因，我们的解决思路是：搭建一个平台夜间定时调度自动拉起流水线，逐步执行扫描、修复、验证、提交 PR，全程无人值守，重活归 AI；早上开发人员评审 PR、决定合不合并，AI 没有主干写权限。整个过程利用夜间闲时算力，几乎不新增硬件成本。【翻页】",
    # P4 演示（视频 2 分 23 秒）
    "【开场白10秒】接下来请大家看一段实跑演示：【播放期间安静站侧位，不用讲解。播完补一句再翻页：】下面我来展开讲一下实现细节。【翻页】",
    # P5 流程总览（30 秒）
    "【15秒】核心流程一共四步：双通道扫描、AI 修复、两道验证、提交 PR。【翻页】",
    # P6 双通道扫描（30 秒）
    "【30秒】先从扫描讲起，它分两个通道。规则引擎用 SonarQube，抓空指针、SQL 注入这类「规则写得出来」的问题，每条 issue 自带规则文档，直接成为修复环节的诊断上下文。语义理解用 Claude Code，抓上帝类、竞态条件、N+1 查询这类「要读懂代码才知道」的问题。两个通道合并为统一模型落库，走同一套修复与验证流程。【翻页】",
    # P7 AI 修复（30 秒）
    "【30秒】扫描发现的问题带着诊断信息入库，接下来交给修复。修复环节三个要点：所有 issue 共用一个 prompt 模板；修复过程中用 SonarQube MCP 逐个文件检查，有新问题就继续修，直到干净，Maven 项目还必须编译通过；产出很克制，commit message 只有一句话总结加修复思路，不让 AI 自述验证、自评风险，那是下一环节的事。【翻页】",
    # P8 两道验证（30 秒）
    "【30秒】修复完成后进入验证环节，这里设了两道防线。第一道，规则引擎重扫：原来报的问题必须全部关闭，多出一个新问题整轮失败。第二道，AI 语义评审：不给 diff，直接读修复后的代码，判断修复思路对不对、代码真的按思路改到位了吗。只有重扫防不住改错方向，只有评审防不住悄悄冒出的新问题，所以宁可整轮重修，也不放行可疑修复。【翻页】",
    # P9 提交 PR（25 秒）
    "【25秒】两道防线都过了，就推送分支、创建 PR。PR 本身就是一份可以直接评审的修复报告：每条问题有标题、三维严重度、代码片段链接和修改记录链接，评审者不用打开 SonarQube。修复必须串行，因为每修一个 issue，代码基线就变一次，一轮只产出一个 PR，避免并行修复互相冲突。【翻页】",
    # P10 状态机（25 秒）
    "【25秒】流程讲完了。它是夜里无人值守跑的，稳定性靠的是一台 18 态状态机：每两个状态构成一个执行阶段，阶段间自动接力。没动代码的环节原地重试；动了代码的先回滚到修复前现场再重试，重试耗尽才判失败。状态、动作、触发、转移四要素分离，没有嵌套、没有隐式行为。【翻页】",
    # P11 回退与兜底（20 秒）
    "【20秒】状态机之外，可靠性还有几层兜底：任一环节失败，整轮回退到干净基线全部重修；每轮修复数量可配置，控制回退牵连面；卡死超时强制清理，夜间的活不过夜；后端重启后，执行中的任务逐个接回状态机，不丢任务。【翻页】",
    # P12 架构（20 秒）
    "【20秒】这些能力都落在整体架构上。自研的重心在指挥、不在执行。动作何时触发、失败怎么重试回退、凭证怎么注入、结果怎么统计，全由自研后端掌控；执行交给 Git、Sonar Scanner、Claude Code 这些成熟工具；前端把每轮任务状态实时可视化。【翻页】",
    # P13 安全（30 秒）
    "【30秒】AI 能改代码，安全是这条流水线的底线。原则是：能改仓库代码的凭证，不进 AI 容器。三条防线逐层收紧：凭证隔离，Git 凭证只留后端，用时注入、用完即毁；最小权限，admin token 运行全程不出现，bot token 按仓库收放；凭证脱敏，不进日志、不落库。再加上身份分离，任一身份出问题，爆炸半径都被限制在自己的职责内。【翻页】",
    # P14 KPI（20 秒）
    "【20秒】平台跑起来之后，用五个指标衡量运营效果：接入仓库看规模，并发任务看实时水位，累计交付修复看产出，PR 合并率看质量，累计节约人天看价值，五个指标对应五个维度。【翻页】",
    # P15 开发过程（20 秒）
    "【20秒】作品本身介绍完了，再说说它是怎么被开发出来的。开发过程本身也是人机协作：不可外包的判断人把住，可快速修正的表达交给 AI。状态机、安全模型、数据一致性人握住，前端布局和样式交给 AI。后端分四步走：先跑通基础状态机，再定重试与回退环，按依赖顺序填真实动作，最后 PR 反馈独立成环。【翻页】",
    # P16 对标（25 秒）
    "【25秒】这里也回应一个大家可能会想到的问题：SonarQube 有 AI CodeFix，GitHub 有 Copilot Autofix，为什么还要自研？大厂也在做，恰恰说明需求真实。差异在部署、调度、验证、扫描四个维度，都列在这张表里。核心是我们能进内网，能以无人值守的方式跑完整条流水线。【翻页】",
    # P17 价值与规划（20 秒）
    "【20秒】最后总结价值：自动化闭环解放人力；AI 后台治债，人聚焦核心业务研发；利用闲时算力，几乎不新增硬件成本；绝不赋予 AI 生产发布或主干合并权限，风险可控。未来还有缺陷分析反哺培训、GitLab 适配等规划，条件成熟再启动。【翻页】",
    # P18 致谢（8 秒）
    "【8秒】我的汇报到此结束。恳请各位评委批评指正。",
]
assert len(NOTES) == len(prs.slides._sldIdLst), "备注条数与页数不一致"
for slide, note in zip(prs.slides, NOTES):
    slide.notes_slide.notes_text_frame.text = note

out = f"{DOCS}/dev-slides.pptx"
prs.save(out)
print("saved:", out, "slides:", len(prs.slides.__iter__.__self__._sldIdLst))
