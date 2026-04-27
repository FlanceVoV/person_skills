@echo off
REM 一键：环境构建 + 启动后台 + 启动前台 + 打开浏览器
chcp 65001 >nul
call "%~dp0dev-setup.bat"
if errorlevel 1 exit /b 1
call "%~dp0dev-start.bat"
exit /b 0
