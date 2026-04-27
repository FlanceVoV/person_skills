@echo off
REM 一键启动：仅后端 + 同端口页面；**路径相对本文件**（可整体移动 build 目录^）
chcp 65001 >nul
cd /d "%~dp0"

echo Starting backend and UI on :8000 ...
start "person-skills" "%~dp0_run-backend.cmd"
timeout /t 2 /nobreak >nul

start "" "http://127.0.0.1:8000"
echo.
echo 页面与 API:  http://127.0.0.1:8000
echo 健康检查:    http://127.0.0.1:8000/api/health
echo 停止: 关「person-skills」窗口 或 在此目录运行 stop.bat
echo 重启: 双击 restart.bat
pause
