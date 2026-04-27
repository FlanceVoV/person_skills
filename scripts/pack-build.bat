@echo off
REM 打包到仓库根目录 build\：前台静态 dist（无 node_modules）+ 后台 + 内嵌 Python + start.bat
chcp 65001 >nul
setlocal
pushd "%~dp0.."
set "ROOT=%CD%"

echo === pack-build — output "%ROOT%\build" ===

where node >nul 2>&1
if errorlevel 1 (
  echo ERROR: Node.js not in PATH.
  popd & endlocal & exit /b 1
)
where npm >nul 2>&1
if errorlevel 1 (
  echo ERROR: npm not in PATH.
  popd & endlocal & exit /b 1
)

echo [1/5] Frontend: next build ^(static export to web\out^) ...
cd web
if not exist "node_modules\next\package.json" (
  call npm install
  if errorlevel 1 ( cd .. & popd & endlocal & exit /b 1 )
)
REM 直接调用 node next build，避免 npm script-shell 劫持到 WSL/bash 导致失败
REM 与 FastAPI 同机同端口，构建进包时清空 API 基址（走同源）；开发机勿用本脚本时可在 cmd 中自行设 NEXT_PUBLIC_API_BASE
set "NEXT_PUBLIC_API_BASE="
call node node_modules\next\dist\bin\next build
if errorlevel 1 (
  cd ..
  popd
  endlocal
  exit /b 1
)
cd ..

if not exist "web\out\index.html" (
  echo ERROR: web\out\index.html missing.
  echo Fix: web\next.config.mjs must use output: 'export'
  popd
  endlocal
  exit /b 1
)

echo [2/5] Clean build folder ...
if exist "build" rmdir /s /q "build"
mkdir "build" 2>nul
mkdir "build\dist" 2>nul
mkdir "build\backend" 2>nul

echo [3/5] Copy static site dist + fixtures + backend ...
robocopy "web\out" "build\dist" /E /MIR /NFL /NDL /NJH /NJS /nc /ns /np
set "RC=%ERRORLEVEL%"
if %RC% GEQ 8 (
  echo robocopy out to dist failed, code=%RC%
  popd & endlocal & exit /b 1
)

if exist "fixtures" (
  robocopy "fixtures" "build\fixtures" /E /MIR /NFL /NDL /NJH /NJS /nc /ns /np
) else (
  echo WARNING: no folder fixtures\ — 示例列表可能为空
)

xcopy "backend\app" "build\backend\app\" /E /I /Y /Q >nul
copy /Y "backend\requirements.txt" "build\backend\" >nul

echo [4/5] 内嵌 Python 3.12 x64 ^(embeddable^) + pip 安装到 build\python-embed\ ...
if "%PACK_SKIP_PYTHON_EMBED%"=="1" (
  echo PACK_SKIP_PYTHON_EMBED=1 — 跳过内嵌解释器; 目标机需自备 Python 建 venv
) else (
  powershell -NoProfile -ExecutionPolicy Bypass -File "scripts\pack\_embed_python.ps1" -BuildRoot "%ROOT%\build"
  if errorlevel 1 (
    echo ERROR: 内嵌 Python 失败. 可联网重试, 或设置 PACK_SKIP_PYTHON_EMBED=1 后重打^(目标机自装 Python^).
    popd
    endlocal
    exit /b 1
  )
)

echo [5/5] Launcher scripts ...
copy /Y "scripts\pack\start.bat" "build\start.bat" >nul
copy /Y "scripts\pack\stop.bat" "build\stop.bat" >nul
copy /Y "scripts\pack\restart.bat" "build\restart.bat" >nul
copy /Y "scripts\pack\_run-backend.cmd" "build\_run-backend.cmd" >nul

> "build\README-Pack.txt" (
  echo person-skills — 打包目录（仅静态 + Python 后端，无 node_modules^）
  echo.
  echo 启动:  双击 start.bat
  echo 停止:  关 person-skills 窗口 或 双击 stop.bat
  echo 重启:  双击 restart.bat
  echo 提示: 可整体移动本 build 目录；脚本以自身所在目录为根^，勿只拷单个文件。
  echo 浏览器: http://127.0.0.1:8000 （页面与 API 同端口^）
  echo 健康检查: /api/health
  echo.
  echo 目标机**不需要**安装 Node；前台为 build\dist 静态资源，由 FastAPI 挂载。
  echo 若含 python-embed: 目标机**无需**安装 Python ^(x64^); 无 embed 时首次会尝试建 backend\.venv^（需本机有 Python^）
  echo 跳过内嵌: 打包前 set PACK_SKIP_PYTHON_EMBED=1
)

echo.
echo OK: "%ROOT%\build"
echo Run: "%ROOT%\build\start.bat"
echo.

popd
endlocal
exit /b 0
