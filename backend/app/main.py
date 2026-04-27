from __future__ import annotations

import io
import zipfile
from pathlib import PurePosixPath

from fastapi import FastAPI, File, Form, HTTPException, Response, UploadFile
from fastapi.middleware.cors import CORSMiddleware

from .package_builder import SkillFormPayload, build_skill_zip, slugify

app = FastAPI(
    title="Person Skills",
    version="0.1.0",
    description="从表单与上传资料生成 {slug}-skill.zip 技能包（FastAPI 后端）",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://127.0.0.1:3000", "http://localhost:3000", "*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/api/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/api/build-skill-zip")
async def build_skill_zip_ep(
    display_name: str = Form(..., description="展示名称，用于包内与 zip 文件名 slug"),
    basic_info: str = Form(""),
    persona: str = Form(""),
    chat_records: str = Form(""),
    work_achievements: str = Form(""),
    workflow: str = Form(""),
    skill_id: str = Form(""),
    version: str = Form("1.0.0"),
    in_scope: str = Form(""),
    out_of_scope: str = Form(""),
    purpose_description: str = Form(""),
    activation_hints: str = Form(""),
    role_play_rules: str = Form(""),
    analyst_rules: str = Form(""),
    latest_updates: str = Form(""),
    decision_heuristics: str = Form(""),
    timeline: str = Form(""),
    values_antipatterns: str = Form(""),
    intellectual_lineage: str = Form(""),
    honesty_supplement: str = Form(""),
    files: list[UploadFile] | None = File(None),
):
    if not (display_name or "").strip():
        raise HTTPException(status_code=400, detail="display_name 不能为空")

    def _zip_prefix(filename: str) -> str:
        base = (filename or "").strip().rsplit("/", 1)[-1].rsplit("\\", 1)[-1]
        if base.lower().endswith(".zip"):
            base = base[: -len(".zip")]
        return base or "archive"

    def _safe_member_path(p: str) -> str | None:
        # normalize to posix for zip members
        raw = (p or "").replace("\\", "/").strip().lstrip("/")
        if not raw or raw.endswith("/"):
            return None
        # reject absolute / traversal
        pp = PurePosixPath(raw)
        if any(part in ("..", "") for part in pp.parts):
            return None
        # keep only safe characters per segment, keep directories
        safe_parts: list[str] = []
        for part in pp.parts:
            cleaned = "".join(ch if ch.isalnum() or ch in "._- " else "_" for ch in part).strip()
            cleaned = cleaned.replace(" ", "_")
            if not cleaned or cleaned in (".", ".."):
                return None
            safe_parts.append(cleaned)
        return "/".join(safe_parts)

    uploaded: list[tuple[str, bytes]] = []
    extracted_total = 0
    extracted_files = 0
    EXTRACT_MAX_FILES = 200
    EXTRACT_MAX_TOTAL = 60 * 1024 * 1024  # 60MB total extracted payload
    if files:
        for f in files:
            if f is None or not f.filename:
                continue
            name = (f.filename or "").strip()
            data = await f.read()

            # Robust zip support: attempt to open as zip; if it succeeds and contains files,
            # we treat it as an archive regardless of filename suffix.
            extracted_any = False
            if len(data) > 0:
                try:
                    with zipfile.ZipFile(io.BytesIO(data)) as zf:
                        members = [i for i in zf.infolist() if not i.is_dir()]
                        if members:
                            extracted_any = True
                            prefix = _zip_prefix(name)
                            for info in members:
                                if extracted_files >= EXTRACT_MAX_FILES:
                                    raise HTTPException(status_code=400, detail=f"压缩包解压文件数超限（>{EXTRACT_MAX_FILES}）：{name}")
                                if info.file_size > 15 * 1024 * 1024:
                                    raise HTTPException(
                                        status_code=400,
                                        detail=f"压缩包内文件过大: {info.filename}（单文件 15MB 上限）",
                                    )
                                safe_member = _safe_member_path(info.filename)
                                if not safe_member:
                                    # skip unsafe members silently
                                    continue
                                raw_entry = zf.read(info)
                                extracted_total += len(raw_entry)
                                extracted_files += 1
                                if extracted_total > EXTRACT_MAX_TOTAL:
                                    raise HTTPException(
                                        status_code=400,
                                        detail=f"压缩包解压总大小超限（>{EXTRACT_MAX_TOTAL // (1024 * 1024)}MB）：{name}",
                                    )
                                uploaded.append((f"{prefix}/{safe_member}", raw_entry))
                except zipfile.BadZipFile:
                    # If user explicitly uploads a .zip, fail fast instead of silently storing raw.
                    if name.lower().endswith(".zip"):
                        magic = data[:8].hex()
                        raise HTTPException(status_code=400, detail=f"压缩包损坏或格式不支持：{name}（magic={magic}）")
                    extracted_any = False

            if not extracted_any:
                # Non-zip file: store raw.
                uploaded.append((name, data))

    payload = SkillFormPayload(
        display_name=display_name.strip(),
        basic_info=basic_info or "",
        persona=persona or "",
        chat_records=chat_records or "",
        work_achievements=work_achievements or "",
        workflow=workflow or "",
        skill_id=skill_id or "",
        version=(version or "1.0.0").strip() or "1.0.0",
        in_scope=in_scope or "",
        out_of_scope=out_of_scope or "",
        purpose_description=purpose_description or "",
        activation_hints=activation_hints or "",
        role_play_rules=role_play_rules or "",
        analyst_rules=analyst_rules or "",
        latest_updates=latest_updates or "",
        decision_heuristics=decision_heuristics or "",
        timeline=timeline or "",
        values_antipatterns=values_antipatterns or "",
        intellectual_lineage=intellectual_lineage or "",
        honesty_supplement=honesty_supplement or "",
    )
    if len(payload.resolved_skill_id) > 128:
        raise HTTPException(status_code=400, detail="skill_id 过长")
    for name, raw in uploaded:
        if len(raw) > 15 * 1024 * 1024:
            raise HTTPException(status_code=400, detail=f"文件过大: {name}（单文件 15MB 上限）")

    filename, zbytes = build_skill_zip(payload, uploaded)
    return Response(
        content=zbytes,
        media_type="application/zip",
        headers={
            "Content-Disposition": f'attachment; filename="{filename}"',
            "X-Skill-Slug": slugify(payload.display_name),
        },
    )
