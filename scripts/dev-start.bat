@echo off
REM 启动后端 + 前台（新窗口），并打开浏览器
chcp 65001 >nul
setlocal
pushd "%~dp0"

if not exist "_dev-backend.cmd" (
  echo Missing scripts\_dev-backend.cmd
  popd & exit /b 1
)

start "person-skills-backend" "%~dp0_dev-backend.cmd"
timeout /t 2 /nobreak >nul

start "person-skills-frontend" "%~dp0_dev-frontend.cmd"
timeout /t 4 /nobreak >nul

start "" "http://127.0.0.1:3000"
echo Opened browser: http://127.0.0.1:3000
echo Backend: http://127.0.0.1:8000

popd
endlocal
exit /b 0
