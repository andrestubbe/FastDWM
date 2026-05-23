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
:: Run with -q to hide Maven noise
call mvn compile exec:java -Dexec.mainClass="fastdwm.Demo" -q
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Demo failed to launch. 
    pause
)

cd ..
