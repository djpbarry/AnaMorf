@echo off
rem Run AnaMorf headlessly over the regression images (Phase C4).
rem
rem Usage:   run-anamorf.cmd [imagesDir] [workDir]
rem Defaults: src\test\resources\images, C:\anamorf-live (fixed for a stable baseline)
rem
rem Requires a prior `mvnw.cmd -DskipTests package` so target\classes and the
rem dependency jars in target\ are present. The compiled plugin classes and all
rem dependencies are on the classpath, so no Fiji install is needed for the
rem headless run.

setlocal

rem Load machine-specific JAVA_HOME if present.
call "%~dp0local-env.cmd" 2>nul

if "%JAVA_HOME%"=="" (
    echo [run-anamorf] ERROR: JAVA_HOME not set. Create bin\local-env.cmd.
    exit /b 1
)

set "JAVAC=%JAVA_HOME%\bin\javac.exe"
set "JAVA=%JAVA_HOME%\bin\java.exe"
set "OUT=%~dp0..\target\anamorf-runner"

set "IMAGES=%~dp0..\src\test\resources\images"
set "WORK=C:\anamorf-live"
if not "%~1"=="" set "IMAGES=%~1"
if not "%~2"=="" set "WORK=%~2"

set "CP=%~dp0..\target\classes;%~dp0..\target\*"

if not exist "%OUT%" mkdir "%OUT%"
"%JAVAC%" -cp "%CP%" -d "%OUT%" "%~dp0RunAnamorf.java"
if errorlevel 1 (
    echo [run-anamorf] ERROR: failed to compile RunAnamorf.java
    exit /b 1
)

"%JAVA%" -cp "%OUT%;%CP%" RunAnamorf "%IMAGES%" "%WORK%"
endlocal
