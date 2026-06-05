@echo off
chcp 65001 >nul


echo ðŸš€ Running Demo...
cd examples
call mvn compile exec:java -Dexec.mainClass="fastdwm.Demo" -q
cd ..\..
pause