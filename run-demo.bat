@echo off
chcp 65001 >nul
cd /d "%~dp0"

set MAVEN_OPTS=--enable-native-access=ALL-UNNAMED

echo ⚡ Building FastDWM...
call mvn clean install -DskipTests -q
if %ERRORLEVEL% NEQ 0 ( echo ❌ Build failed. & pause & exit /b %ERRORLEVEL% )

echo 🚀 Running FastDWM Demo...
cd examples
call mvn compile exec:java -Dexec.mainClass="fastdwm.Demo" -q
if %ERRORLEVEL% NEQ 0 ( echo ❌ Demo execution failed. & pause & exit /b %ERRORLEVEL% )

cd ..
pause
