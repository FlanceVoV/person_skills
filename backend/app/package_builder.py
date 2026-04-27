from __future__ import annotations

import io
import json
import re
import uuid
import zipfile
from dataclasses import dataclass
from datetime import datetime, timezone
from typing import Any

from .skill_template import render_skill_markdown


def slugify(name: str) -> str:
    s = (name or "").strip().lower()
    s = re.sub(r"[^a-z0-9._-]+", "-", s, flags=re.IGNORECASE)
    s = re.sub(r"-{2,}", "-", s).strip("-")
    if not s:
        s = f"skill-{uuid.uuid4().hex[:8]}"
    if len(s) > 64:
        s = s[:64].rstrip("-")
    return s


@dataclass
class SkillFormPayload:
    display_name: str
    explicit_identity: str = ""
    basic_info: str = ""
    persona: str = ""
    chat_records: str = ""
    work_achievements: str = ""
    workflow: str = ""
    skill_id: str = ""
    version: str = "1.0.0"
    in_scope: str = ""
    out_of_scope: str = ""
    # 与 SKILL.md 模板对齐的扩展字段（界面分区）
    purpose_description: str = ""
    activation_hints: str = ""
    role_play_rules: str = ""
    analyst_rules: str = ""
    latest_updates: str = ""
    decision_heuristics: str = ""
    timeline: str = ""
    values_antipatterns: str = ""
    intellectual_lineage: str = ""
    honesty_supplement: str = ""

    @property
    def resolved_skill_id(self) -> str:
        raw = (self.skill_id or "").strip()
        if raw:
            return raw
        return slugify(self.display_name)


def _utc_now_iso() -> str:
    return datetime.now(timezone.utc).replace(microsecond=0).isoformat()


def _write_json(zf: zipfile.ZipFile, path: str, data: Any) -> None:
    b = json.dumps(data, ensure_ascii=False, indent=2).encode("utf-8")
    zf.writestr(path, b)


def build_skill_zip(
    form: SkillFormPayload,
    uploaded_files: list[tuple[str, bytes]],
) -> tuple[str, bytes]:
    """
    Returns (filename, zip_bytes). Filename: ``{slug}-skill.zip`` where slug comes from
    display_name (or skill_id if provided and safe).
    """
    skill_id = form.resolved_skill_id
    slug = slugify(form.display_name) if form.display_name.strip() else slugify(skill_id)
    # 扁平包：{slug}-skill.zip 根目录直放约定文件、资源目录与 SKILL.md，无 skills/<id>/ 嵌套
    created = _utc_now_iso()
    manifest: dict[str, Any] = {
        "schema": "person-skill/1",
        "layout": "flat",
        "skill_id": skill_id,
        "display_name": form.display_name,
        "version": form.version,
        "created_at_utc": created,
        "name": slug,
        "entry": "SKILL.md",
        "package_paths": {
            "skill_md": "SKILL.md",
            "manifest": "manifest.json",
            "contract": "contract.json",
            "evidence_index": "evidence/index.jsonl",
            "evidence_uploads": "evidence/uploads/",
            "references_research": "references/research/",
        },
        "compliance": {
            "not_impersonation": "This package encodes style and work patterns only, not a real individual identity."
        },
    }

    skill_md = render_skill_markdown(form, skill_id, slug, created)

    contract = {
        "$schema": "https://json-schema.org/draft/2020-12/schema",
        "title": f"Skill: {skill_id}",
        "type": "object",
        "properties": {
            "task": {"type": "string", "description": "User task to execute in this skill's style."},
            "context": {
                "type": "string",
                "description": "Optional context; never include secrets or PII in production."
            }
        },
        "required": ["task"],
        "additionalProperties": False
    }

    evidence_index: list[dict[str, str]] = []
    buf = io.BytesIO()
    skill_bytes = skill_md.encode("utf-8")

    # Expand uploaded zip archives into individual evidence files so that the
    # final `{slug}-skill.zip` always contains extracted content rather than nested zips.
    def _zip_base(name: str) -> str:
        base = (name or "").strip().rsplit("/", 1)[-1].rsplit("\\", 1)[-1]
        if base.lower().endswith(".zip"):
            base = base[: -len(".zip")]
        return re.sub(r"[^A-Za-z0-9._-]+", "_", base) or "archive"

    def _safe_inner_path(p: str) -> str | None:
        raw = (p or "").replace("\\", "/").strip().lstrip("/")
        if not raw or raw.endswith("/"):
            return None
        parts = [x for x in raw.split("/") if x and x not in (".", "..")]
        if not parts:
            return None
        # sanitize each segment; preserve directories
        safe_parts: list[str] = []
        for seg in parts:
            seg2 = re.sub(r"[^A-Za-z0-9._-]+", "_", seg).strip("_")
            if not seg2 or seg2 in (".", ".."):
                return None
            safe_parts.append(seg2)
        return "/".join(safe_parts)

    EXTRACT_MAX_FILES = 200
    EXTRACT_MAX_TOTAL = 60 * 1024 * 1024  # 60MB total extracted payload
    EXTRACT_MAX_SINGLE = 15 * 1024 * 1024  # 15MB per extracted file

    expanded_files: list[tuple[str, bytes, str]] = []
    extracted_files = 0
    extracted_total = 0

    for filename, data in uploaded_files:
        name = (filename or "").strip()
        extracted_any = False
        if data:
            try:
                with zipfile.ZipFile(io.BytesIO(data)) as zf_in:
                    members = [i for i in zf_in.infolist() if not i.is_dir()]
                    if members:
                        extracted_any = True
                        base = _zip_base(name)
                        for info in members:
                            if extracted_files >= EXTRACT_MAX_FILES:
                                raise ValueError(f"zip extracted file count exceeds {EXTRACT_MAX_FILES}")
                            if info.file_size > EXTRACT_MAX_SINGLE:
                                raise ValueError(f"zip member too large: {info.filename}")
                            inner = _safe_inner_path(info.filename)
                            if not inner:
                                continue
                            raw_entry = zf_in.read(info)
                            extracted_files += 1
                            extracted_total += len(raw_entry)
                            if extracted_total > EXTRACT_MAX_TOTAL:
                                raise ValueError(f"zip extracted total exceeds {EXTRACT_MAX_TOTAL}")
                            expanded_files.append((f"{base}/{inner}", raw_entry, f"{name}::{info.filename}"))
            except zipfile.BadZipFile:
                extracted_any = False

        if not extracted_any:
            expanded_files.append((name, data, name))

    with zipfile.ZipFile(buf, "w", compression=zipfile.ZIP_DEFLATED) as zf:
        # 约定文件 → 资源（evidence）→ 主入口 SKILL.md
        _write_json(zf, "manifest.json", manifest)
        _write_json(zf, "contract.json", contract)
        for i, (filename, data, original_name) in enumerate(expanded_files, start=1):
            # keep directory structure when provided (e.g. from zip extraction)
            raw = (filename or "").replace("\\", "/").strip().lstrip("/")
            parts = [p for p in raw.split("/") if p and p not in (".", "..")]
            safe_parts = [re.sub(r"[^A-Za-z0-9._-]+", "_", p) or f"part_{k}" for k, p in enumerate(parts, start=1)]
            safe = "/".join(safe_parts) if safe_parts else (re.sub(r"[^A-Za-z0-9._-]+", "_", filename) or f"file_{i}")
            rel = f"evidence/uploads/{safe}"
            zf.writestr(rel, data)
            evidence_index.append(
                {
                    "id": f"e{i:04d}",
                    "path": rel,
                    "original_name": original_name,
                }
            )
        index_lines = "\n".join(json.dumps(row, ensure_ascii=False) for row in evidence_index)
        if not index_lines:
            index_lines = ""
        zf.writestr("evidence/index.jsonl", (index_lines + "\n" if index_lines else "").encode("utf-8"))
        _ref_readme = (
            "与参考 SKILL 案例的 `references/research/` 同构；可将调研原文、文献摘录放于此。\n"
            "本工具已上传的附件在 `evidence/uploads/` 并在 `evidence/index.jsonl` 中索引。\n"
        )
        zf.writestr("references/research/README.txt", _ref_readme.encode("utf-8"))
        zf.writestr("SKILL.md", skill_bytes)

    filename = f"{slug}-skill.zip"
    return filename, buf.getvalue()
