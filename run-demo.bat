@echo off
chcp 65001 >nul

echo ⚡ Building Main Project...
call mvn -q clean install -DskipTests
if %ERRORLEVEL% NEQ 0 ( pause & exit /b )

echo 🚀 Running Demo...
cd examples
call mvn compile exec:java -Dexec.mainClass="fastdwm.Demo" -q
cd ..\..
pause