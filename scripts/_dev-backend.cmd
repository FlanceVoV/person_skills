@echo off
chcp 65001 >nul
cd /d "%~dp0..\backend"
if not exist ".venv\Scripts\python.exe" (
  echo [.venv missing] Run scripts\dev-setup.bat first.
  pause
  exit /b 1
)
"%CD%\.venv\Scripts\python.exe" -m uvicorn app.main:app --host 127.0.0.1 --port 8000
