@echo off
echo ==================================================
echo   Metro Rail Ticket Vending System - Starter Script
echo ==================================================
echo.
echo [1/3] Cleaning old compiled files...
del /s /q *.class >nul 2>&1

echo [2/3] Compiling Java files...
javac Start.java Classes\*.java
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Please check your Java installation.
    pause
    exit /b %errorlevel%
)

echo [3/3] Running Metro Ticket System...
echo.
java Start
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Program exited with error code %errorlevel%.
    echo Check the stack trace above for details.
)
echo.
echo Press any key to exit...
pause
