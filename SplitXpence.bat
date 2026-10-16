@echo off
title SplitXpence Launcher

cd /d "C:\Users\artif\Documents\Projects\SplitXpence"

echo ==========================================
echo          Starting SplitXpence
echo ==========================================
echo.

set "JARFILE="

for %%F in (target\*.jar) do (
    echo %%~nxF | findstr /I /V "original" >nul
    if not errorlevel 1 (
        set "JARFILE=%%F"
        goto :found
    )
)

echo No runnable JAR was found inside the target folder.
echo.
echo First run Clean and Build in NetBeans, then launch this file again.
echo.
pause
exit /b 1

:found
echo Starting: %JARFILE%
echo.
echo Make sure MySQL Server is running.
echo.

start "SplitXpence Server" cmd /k java -jar "%JARFILE%"

timeout /t 6 /nobreak >nul

start "" "http://localhost:8080"

exit
