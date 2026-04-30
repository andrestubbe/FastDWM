@echo off
setlocal
cd /d "%~dp0"

echo ===========================================
echo FastDWM Demo (v0.1.0)
echo ===========================================
echo.
echo Launching: Native Timing Drift Test
echo.

cd examples
call mvn compile exec:java -Dexec.mainClass="fastdwm.Demo"
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Demo failed to launch. 
    echo Ensure you ran 'compile.bat' first to build the native DLL.
    pause
)

cd ..
