@echo off


echo ðŸš€ Running Demo...
cd examples
call mvn compile exec:java -Dexec.mainClass="fastdwm.Demo"
cd ..\..
pause
