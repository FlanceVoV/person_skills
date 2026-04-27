@echo off
REM 停止占用本机 8000 端口的进程^（与 _run-backend 默认端口一致^），路径均相对本脚本所在目录
chcp 65001 >nul
cd /d "%~dp0"

echo Stopping process listening on 127.0.0.1:8000 ...
powershell -NoProfile -ExecutionPolicy Bypass -Command "Get-NetTCPConnection -LocalPort 8000 -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }"
echo Done.
exit /b 0
