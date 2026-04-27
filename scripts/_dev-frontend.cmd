@echo off
chcp 65001 >nul
cd /d "%~dp0..\web"
if not exist "node_modules\next\package.json" (
  echo [node_modules missing] Run scripts\dev-setup.bat first.
  pause
  exit /b 1
)
node node_modules\next\dist\bin\next dev -p 3000
