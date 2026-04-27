@echo off
REM 打包根目录^ = 本脚本所在目录^（与 dist / backend / python-embed 同级^）
chcp 65001 >nul
set "ROOT=%~dp0"
cd /d "%ROOT%"

REM 1) 优先：打包包入的 embeddable Python（见 scripts\pack\_embed_python.ps1）
if exist "%ROOT%python-embed\python.exe" (
  cd /d "%ROOT%backend"
  set "PYTHONNOUSERSITE=1"
  "%ROOT%python-embed\python.exe" -m uvicorn app.main:app --host 127.0.0.1 --port 8000
  exit /b 0
)

REM 2) 回退：本机已安装 Python 时，在 backend 下建 .venv
cd /d "%ROOT%backend"
if not exist ".venv\Scripts\python.exe" (
  echo [pack 无内嵌解释器] 本机建 venv 并装依赖^（需已装 Python^）...
  where python >nul 2>&1
  if errorlevel 1 (
    echo 未找到: python-embed\python.exe，且本机无 python。
    echo 请用完整包（含 python-embed）或先安装 Python 3.10+ 再运行。
    pause
    exit /b 1
  )
  python -m venv .venv
  call .venv\Scripts\pip.exe install -U pip -q
  call .venv\Scripts\pip.exe install -r requirements.txt
  if errorlevel 1 ( pause & exit /b 1 )
)
"%CD%\.venv\Scripts\python.exe" -m uvicorn app.main:app --host 127.0.0.1 --port 8000
