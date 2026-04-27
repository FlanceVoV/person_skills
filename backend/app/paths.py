"""解析 fixtures 目录与前端静态导出目录（dist / web/out）。"""

from __future__ import annotations

import os
from pathlib import Path


def backend_root() -> Path:
    return Path(__file__).resolve().parent.parent


def repo_root() -> Path:
    """仓库根或打包目录 build（backend 的上一级）。"""
    return backend_root().parent


def fixtures_dir() -> Path:
    """优先：repo/fixtures；打包：build/fixtures 或 backend/fixtures。"""
    rr = repo_root()
    br = backend_root()
    for p in (rr / "fixtures", br / "fixtures"):
        if p.is_dir():
            return p
    return rr / "fixtures"


def static_export_dir() -> Path | None:
    """
    静态站点根目录（内含 index.html）。
    - 开发构建：web/out
    - 打包：dist（与 backend 同级）
    """
    rr = repo_root()
    for p in (rr / "dist", rr / "web" / "out"):
        if p.is_dir() and (p / "index.html").is_file():
            return p
    env = os.environ.get("FRONTEND_STATIC_DIR", "").strip()
    if env:
        pe = Path(env)
        if pe.is_dir() and (pe / "index.html").is_file():
            return pe
    return None
