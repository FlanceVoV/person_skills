@echo off
REM 停止端口进程后重新 dev-start（需已执行过 dev-setup）
chcp 65001 >nul
call "%~dp0dev-stop.bat"
timeout /t 2 /nobreak >nul
call "%~dp0dev-start.bat"
exit /b 0
