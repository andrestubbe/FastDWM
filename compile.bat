@echo off
setlocal EnableDelayedExpansion
cd /d "%~dp0"

echo ===========================================
echo FastDWM Builder (v0.1.0)
echo ===========================================
echo.

:: 1. Setup Java Environment
if not defined JAVA_HOME (
    echo JAVA_HOME not defined. Searching for JDK...
    for /d %%i in ("C:\Program Files\Java\jdk-*") do (
        set "JAVA_HOME=%%i"
    )
)

if not defined JAVA_HOME (
    echo ERROR: Could not find a JDK in C:\Program Files\Java.
    echo Please set JAVA_HOME manually.
    pause
    exit /b 1
)

echo Using JDK: %JAVA_HOME%

:: 2. Setup VS Environment
set "VSWHERE=%ProgramFiles(x86)%\Microsoft Visual Studio\Installer\vswhere.exe"
if not exist "%VSWHERE%" (
    echo ERROR: vswhere.exe not found!
    pause
    exit /b 1
)

for /f "usebackq tokens=*" %%i in (`"%VSWHERE%" -latest -products * -requires Microsoft.VisualStudio.Component.VC.Tools.x86.x64 -property installationPath`) do (
    set "VS_INSTALL=%%i"
)

if not defined VS_INSTALL (
    echo ERROR: Visual Studio with C++ tools not found!
    pause
    exit /b 1
)

set "VCVARS=%VS_INSTALL%\VC\Auxiliary\Build\vcvars64.bat"
echo Found Visual Studio at: %VS_INSTALL%
call "%VCVARS%" > nul

:: 3. Compile Native Library
echo.
echo [1/2] Compiling Native C++ (fastdwm.dll)...
if not exist build mkdir build

cl /LD /O2 /I"%JAVA_HOME%\include" /I"%JAVA_HOME%\include\win32" ^
    native\fastdwm.cpp /Fe:build\fastdwm.dll ^
    user32.lib dwmapi.lib winmm.lib ^
    /link /DLL > nul

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Native compilation failed.
    pause
    exit /b %errorlevel%
)

:: 4. Compile Java & Install (Quiet Mode)
echo.
echo [2/2] Building Java Library...
call mvn clean install -DskipTests -q
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Java build failed.
    pause
    exit /b %errorlevel%
)

echo.
echo ===========================================
echo BUILD SUCCESSFUL! 
echo ===========================================
pause
