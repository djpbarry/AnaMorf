@echo off
rem Launch Fiji using the machine-specific paths from local-env.cmd.

setlocal

call "%~dp0local-env.cmd" 2>nul

if "%FIJI_DIR%"=="" (
    echo [launch-fiji] ERROR: FIJI_DIR not set. Create bin\local-env.cmd or edit it.
    exit /b 1
)

if not "%JAVA_HOME%"=="" set "PATH=%JAVA_HOME%\bin;%PATH%"

set "LAUNCHER=%FIJI_DIR%\fiji-windows-x64.exe"
if not exist "%LAUNCHER%" (
    echo [launch-fiji] ERROR: Fiji launcher not found: "%LAUNCHER%"
    exit /b 1
)

echo [launch-fiji] Starting Fiji from "%FIJI_DIR%"
start "" "%LAUNCHER%"

endlocal
