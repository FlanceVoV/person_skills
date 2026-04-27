@echo off
REM 先停再起：相对路径，不依赖盘符^（与 start.bat 相同目录^）
chcp 65001 >nul
cd /d "%~dp0"

call "%~dp0stop.bat"
echo Waiting for port 8000 to release...
timeout /t 2 /nobreak >nul

echo Starting backend ^(new window^) ...
start "person-skills" "%~dp0_run-backend.cmd"
timeout /t 2 /nobreak >nul
start "" "http://127.0.0.1:8000"
echo.
echo 已重启。页面与 API: http://127.0.0.1:8000
echo 若需停服: 关「person-skills」窗口 或 双击 stop.bat
exit /b 0
