@echo off
REM 前台 + 后台依赖安装（venv / npm）
chcp 65001 >nul
setlocal
pushd "%~dp0.."
set "ROOT=%CD%"
echo === dev-setup : %ROOT% ===

echo [1/2] Backend: Python venv + pip install ...
pushd backend
where python >nul 2>&1
if errorlevel 1 (
  echo ERROR: Python not in PATH. Install Python 3.10+ and retry.
  popd & popd
  exit /b 1
)
if not exist ".venv\Scripts\python.exe" (
  python -m venv .venv
  if errorlevel 1 ( echo venv failed & popd & popd & exit /b 1 )
)
call .venv\Scripts\pip.exe install -U pip -q
call .venv\Scripts\pip.exe install -r requirements.txt
if errorlevel 1 ( echo pip failed & popd & popd & exit /b 1 )
popd

echo [2/2] Frontend: npm install ...
pushd web
where npm >nul 2>&1
if errorlevel 1 (
  echo ERROR: npm not in PATH. Install Node.js LTS and retry.
  popd & popd
  exit /b 1
)
call npm install
if errorlevel 1 ( echo npm failed & popd & popd & exit /b 1 )
popd

popd
echo === dev-setup OK ===
endlocal
exit /b 0
