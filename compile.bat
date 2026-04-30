@echo off
setlocal
cd /d "%~dp0"

echo ===========================================
echo FastDWM Builder (v0.1.0)
echo ===========================================
echo.

:: 1. Compile Native Library
echo [1/2] Compiling Native C++ (fastdwm.dll)...
if not exist build mkdir build

:: Locate MSVC (Standard Blueprint logic)
set "MSVC_PATH=C:\Program Files\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvars64.bat"
if not exist "%MSVC_PATH%" (
    set "MSVC_PATH=C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\VC\Auxiliary\Build\vcvars64.bat"
)

call "%MSVC_PATH%" > nul
cl /LD /O2 /I"%JAVA_HOME%\include" /I"%JAVA_HOME%\include\win32" native\fastdwm.cpp /Fe:build\fastdwm.dll /link /DLL > nul

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Native compilation failed.
    pause
    exit /b %errorlevel%
)

:: 2. Compile Java & Install to local maven
echo [2/2] Building Java Library...
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Java build failed.
    pause
    exit /b %errorlevel%
)

echo.
echo ===========================================
echo Build Successful! 
echo ===========================================
pause
