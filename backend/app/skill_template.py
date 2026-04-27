"""
按参考「SKILL.md 案例结构」生成人员蒸馏包的主文档（zip 根目录 SKILL.md）。
章节顺序与 `name` / `description` 头与参考一致；内容优先来自表单，缺省为可编辑占位。
"""

from __future__ import annotations

import json
from typing import Any


def _n(s: str, empty: str = "（未填写，可基于证据与调研补全。）") -> str:
    t = (s or "").strip()
    return t if t else empty


def _md_quote_block(text: str) -> str:
    t = (text or "").strip()
    if not t:
        return "> （可在此放置代表性引言或标语。）"
    return "\n".join(f"> {line}" for line in t.splitlines())


def _yaml_block(name: str, body: str) -> str:
    b = (body or "").rstrip() or " "
    lines = b.splitlines()
    inner = "\n".join("  " + (ln if ln.strip() else " ") for ln in lines)
    return f"{name}:\n  |\n{inner}"


def _build_description(form: Any, display_name: str) -> str:
    ei = (getattr(form, "explicit_identity", "") or "").strip()
    anchor = ""
    if ei:
        anchor = (
            f"**身份锚定（固定执行主体）：** {ei}\n\n"
            "本 skill 的全部环节（路径 A/B、Agentic 工作流、证据阅读与结论表述、输出语气）均**仅**在该身份语境下执行；"
            "不得切换为匿名/泛化顾问而不声明；禁止用「某人」「一位专家」等模糊指代替代本节已写明的身份。"
        )
    pd = (getattr(form, "purpose_description", "") or "").strip()
    if pd:
        parts = ([anchor] if anchor else []) + [pd]
    else:
        parts = ([anchor] if anchor else []) + [
            f"{display_name} 的思维/行为与表达逻辑（人员蒸馏包）。"
            f"由使用者提供的资料经工具生成骨架，**不用于冒充真人身份**；"
            f"适用于分析、风格化表达辅助与授权场景下的行为模式推演。",
        ]
    if getattr(form, "basic_info", None) and str(form.basic_info).strip():
        parts.append("【背景与调研摘要】\n" + _truncate(str(form.basic_info), 3000))
    if getattr(form, "in_scope", None) and str(form.in_scope).strip():
        parts.append("【适用/触发与任务方向】\n" + _truncate(str(form.in_scope), 2000))
    if getattr(form, "out_of_scope", None) and str(form.out_of_scope).strip():
        parts.append("【明确不覆盖】\n" + _truncate(str(form.out_of_scope), 1500))
    return "\n\n".join(parts)


def _truncate(s: str, max_len: int) -> str:
    s = (s or "").strip()
    if len(s) <= max_len:
        return s
    return s[: max_len - 3] + "…"


def _heuristics_block(form: Any) -> str:
    dh = (getattr(form, "decision_heuristics", "") or "").strip()
    if dh:
        return dh
    chunks: list[str] = []
    for label, val in (
        ("性格与习惯（Persona）", getattr(form, "persona", "") or ""),
        ("工作流/判断步骤", getattr(form, "workflow", "") or ""),
    ):
        v = (val or "").strip()
        if v:
            chunks.append(f"### {label}\n\n{v}")
    return "\n\n".join(chunks) if chunks else "（可在此整理编号启发式：场景 → 模式 → 反例/局限。）"


def render_skill_markdown(form: Any, skill_id: str, slug: str, created_utc: str) -> str:
    display_name = (getattr(form, "display_name", None) or "").strip() or "未命名技能"
    p_explicit = (getattr(form, "explicit_identity", "") or "").strip()
    name_key = slug
    desc_body = _build_description(form, display_name)

    header_lines = [
        "---",
        _yaml_block("name", name_key),
        _yaml_block("description", desc_body),
        'schema: "person-skill/1"',
        f"skill_id: {json.dumps(skill_id, ensure_ascii=False)}",
        f"display_name: {json.dumps(display_name, ensure_ascii=False)}",
        f"explicit_identity: {json.dumps(p_explicit or '（未填写）', ensure_ascii=False)}",
        f"version: {json.dumps(getattr(form, 'version', '1.0.0') or '1.0.0', ensure_ascii=False)}",
        f"generated_at_utc: {json.dumps(created_utc, ensure_ascii=False)}",
        "---",
    ]
    head = "\n".join(header_lines)

    first_line = (getattr(form, "basic_info", None) or "").strip().splitlines()[:1]
    epigraph = _md_quote_block(first_line[0] if first_line else "")

    p_in = (getattr(form, "in_scope", "") or "").strip()
    p_out = (getattr(form, "out_of_scope", "") or "").strip()
    p_persona = (getattr(form, "persona", "") or "").strip()
    p_chat = (getattr(form, "chat_records", "") or "").strip()
    p_work = (getattr(form, "work_achievements", "") or "").strip()
    p_flow = (getattr(form, "workflow", "") or "").strip()
    p_bio = (getattr(form, "basic_info", "") or "").strip()
    p_role_play = (getattr(form, "role_play_rules", "") or "").strip()
    p_analyst_rules = (getattr(form, "analyst_rules", "") or "").strip()
    p_activation = (getattr(form, "activation_hints", "") or "").strip()
    p_latest = (getattr(form, "latest_updates", "") or "").strip()
    p_timeline = (getattr(form, "timeline", "") or "").strip()
    p_values = (getattr(form, "values_antipatterns", "") or "").strip()
    p_intel = (getattr(form, "intellectual_lineage", "") or "").strip()
    p_honesty_extra = (getattr(form, "honesty_supplement", "") or "").strip()

    path_a_rules = p_role_play or p_flow or p_persona
    if not path_a_rules.strip():
        path_a_rules = "（可在此从性格、语气、禁忌、退出指令等方面补充角色扮演专用规则。）"

    analyst_persona = p_persona or "（从 Persona 与核心工作成果中整理 1–2 个母题，并各写适用情境。）"

    activation_extra = ""
    if p_activation:
        activation_extra = (
            "\n\n### 自定义触发与模式（来自表单）\n\n"
            + p_activation
        )

    timeline_body = (
        p_timeline
        if p_timeline
        else (
            "> 维护「时间 | 事件 | 对思维/行为的影响」；未提供时在表中自补。\n\n"
            "| 时间 | 事件 | 对思维/行为的影响 |\n"
            "|------|------|-------------------|\n"
            "| （待补） | （待补） | （待补） |"
        )
    )

    if p_values:
        values_section = p_values
    else:
        values_section = (
            f"- **优先追求/叙事优先级：** 可与 In scope 对齐并细化：{_n(p_in, '（从 persona 中归纳。）')}\n"
            f"- **拒绝/回避：** 与 Out of scope 对齐：{_n(p_out, '（自填。）')}\n"
            "- **内在张力（可选）：** （自填。）"
        )

    intellectual_body = p_intel if p_intel else "（待补。）"

    honesty_tail = ""
    if p_honesty_extra:
        honesty_tail = f"\n\n**补充说明（来自表单）：**\n\n{p_honesty_extra}"

    latest_block = (
        p_latest
        if p_latest
        else (
            "（未在表单中提供时，此处保持为占位；可按任务时期自 evidence 摘录可公开摘要。）"
        )
    )

    parts: list[str] = [
        head,
        "",
        f"# {display_name} · 思维与行为技能包",
        "",
        epigraph,
        "",
        "---",
        "",
        "## 身份锚定（唯一执行主体）",
        "",
        "**本节为全 skill 的固定锚点。** 下文「激活确认」「路径 A/B」「Agentic 工作流」「证据阅读」「身份卡/Persona」等所有步骤，均只能在此身份下理解与执行；不得默认匿名专家或随意切换人格。",
        "",
        "### 身份明确（具体、可核验）",
        "",
        (
            p_explicit
            if p_explicit
            else "**（生成错误：未填写身份明确字段；请回到表单补全后再导出。）**"
        ),
        "",
        "---",
        "",
        "## 证据与附件使用（必须先做）",
        "",
        "当用户上传了附件（或压缩包已被解压进包内）时，你**必须先阅读证据**再回答，避免凭空编造。理解材料、引用与结论**均须以本文件「身份锚定」中的具体身份为唯一视角**；不得脱离该身份作泛化解说。",
        "",
        "### 你应如何读取证据",
        "",
        "1. 先读取 `evidence/index.jsonl`，了解有哪些文件、各自路径（`path`）与来源名（`original_name`）。",
        "2. 再按索引逐个读取 `evidence/uploads/...` 中与你当前任务相关的文件内容。",
        "3. 如果需要引用依据：在输出中点名文件（例如「根据 `evidence/uploads/...`」）并说明你使用了哪些片段/要点。",
        "",
        "### 如果证据不足",
        "",
        "- 明确指出缺口（缺少哪些日志/截图/接口文档/表结构等）。",
        "- 给出**最小补充清单**与可执行的验证步骤；不要假装已经看过不存在的材料。",
        "",
        "---",
        "",
        "## 激活确认（优先执行）",
        "",
        f"执行主体固定为：**{p_explicit or '（须先完成身份明确）'}**。在此前提下，Skill 激活后**先判断使用模式**，再按路径响应。",
        "",
        "| 触发信号 | 模式 | 执行路径 |",
        "|---------|------|----------|",
        "| 风格/口吻/角色化对话（如「用 TA 的视角说」「切到该人格」等） | 角色扮演 | 路径 A |",
        "| 分析行为逻辑、预测倾向、第三人称解读 | 分析师 / 顾问 | 路径 B |",
        "| 未明确指定 | 默认路径 B，必要时可提示可切换到路径 A | 路径 B |",
        "",
        "**范围约束（In / Out of scope）**",
        "",
        f"- **In scope:** {_n(p_in, '（未填写：请根据任务与证据补充。）')}",
        f"- **Out of scope / 必须拒绝:** {_n(p_out, '（未填写：请从合规与风险角度补充。）')}",
        activation_extra,
        "",
        "---",
        "",
        "## 角色扮演规则（路径 A）",
        "",
        "**需在授权、脱敏、非冒充前提下使用。**",
        "",
        "- **从表单抽象的规则与步骤（可编辑）：**",
        "",
        path_a_rules,
        "",
        "**与性格、表达、习惯相关的材料：**",
        "",
        _n(p_persona, "（可对接「表达 DNA」与语料。）"),
        "",
        "**聊天记录与沟通摘录：** 全文统一放在文末独立章节「聊天记录与沟通摘录」，此处不重复贴出；需要句式/节奏/话题跳转参考时**只读该章节或 evidence**。",
        "",
        "---",
        "",
        "## 分析师规则（路径 B）",
        "",
        "**以第三人称**分析公开行为逻辑、决策母题与可预判的下一步，并标出**不确定度**与**需补充变量**。",
        "",
        "### 与 Persona 相关的分析维度",
        "",
        analyst_persona,
        "",
        "### 核心工作成果与可抽象「模型/套路」的素材",
        "",
        _n(p_work, "（将成果、项目、可重复打法整理为母题。）"),
        "",
        "### 分析师路径补充规则",
        "",
        _n(p_analyst_rules, "（可选：步骤、预判格式、置信度说明等。）"),
        "",
        "---",
        "",
        "## 回答工作流（Agentic Protocol）",
        "",
        f"**固定执行主体：** {_n(p_explicit, '（须与本节上方「身份锚定」一致。）')}。**原则：** 先搞清任务类型与可核查事实，再按该身份的风格与职责边界输出；不编造不可验证细节。",
        "",
        _n(p_flow, "（可写：分类 → 是否检索事实 → 组织答案 → 合规自检 等。）"),
        "",
        "#### 当需要外部事实时（可选）",
        "",
        "- 涉及**具体政策/数据/人物/事件**的，宜在有工具时检索更新；**不得用训练语料硬编「最新」事实**；信息不足时列出待补变量。",
        "",
        "---",
        "",
        "## 身份卡",
        "",
        _n(p_bio, "（姓名/起点/自我叙事/当前重心等。）"),
        "",
        "### 最新动态（任务相关）",
        "",
        "> 与参考案例**「某时期市场/政策动态」**同构；以下为表单填写内容。",
        "",
        latest_block,
        "",
        "---",
        "",
        "## 核心心智模型",
        "",
        "> 建议整理为 3–6 条：一句话模型、证据/出处要点、**应用**、**局限**；下接表单中「工作成果/抽象」为原始素材。",
        "",
        _n(p_work, "（将成果与重复出现的判断母题写在此处。）"),
        "",
        "---",
        "",
        "## 决策启发式",
        "",
        _heuristics_block(form),
        "",
        "---",
        "",
        "## 表达 DNA（风格与话语）",
        "",
        "### Persona 摘要",
        "",
        _n(p_persona, "（句式、词库、节奏、禁忌词等。）"),
        "",
        "**聊天记录与沟通摘录：** 见文末「聊天记录与沟通摘录」模块（集中收录，全文仅出现一次）。",
        "",
        "---",
        "",
        "## 人物时间线（关键节点）",
        "",
        timeline_body,
        "",
        "---",
        "",
        "## 价值观与反模式",
        "",
        values_section,
        "",
        "---",
        "",
        "## 智识谱系",
        "",
        "**影响来源、引用传统、对上下游的影响**（有则留，无则删本节。）",
        "",
        intellectual_body,
        "",
        "---",
        "",
        "## 诚实边界",
        "",
        "1. 本包基于**公开/授权资料**与使用者填写，**不声称**与真实个人思维 1:1 一致。",
        "2. **公开表达 ≠ 真实意图**；任何预测须标注**置信度**与**未知变量**。",
        "3. **不冒充**身份、职务或联系方式；不生成违法、欺诈或仇恨内容。",
        "4. 涉及高风险领域时**升级人类审核**或拒答。",
        honesty_tail,
        "",
        "---",
        "",
        "## 附录：资料与包内资源",
        "",
        "- 上传与索引：`evidence/index.jsonl`，`evidence/uploads/`。可与参考案例的 `references/research/` 同态使用，自行建立 `references/research/` 并放入调研原文。",
        "- 约定文件：`manifest.json`，`contract.json`；本文件为入口。",
        "",
        "---",
        "",
        "## 聊天记录与沟通摘录",
        "",
        "本节**集中收录**表单字段 `chat_records` 的全部内容，位于文档**最末**；前文章节（路径 A、表达 DNA 等）如需引用聊天/沟通样例，**只指向本节或 `evidence/uploads/`**，避免同一段正文在文档中多次出现。",
        "",
        _n(
            p_chat,
            "（未填写：可自 `evidence/uploads/` 中粘贴脱敏对话后手改本节。）",
        ),
        "",
    ]
    return "\n".join(parts) + "\n"
