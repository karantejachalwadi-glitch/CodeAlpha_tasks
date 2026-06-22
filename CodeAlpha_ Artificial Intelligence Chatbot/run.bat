@echo off
title AI Chatbot - Build and Run
color 0B
chcp 65001 >nul

echo.
echo  ╔══════════════════════════════════════════════════╗
echo  ║     AI CHATBOT  -  Java NLP Chat Application    ║
echo  ║         CodeAlpha Internship - Task 3            ║
echo  ╚══════════════════════════════════════════════════╝
echo.

:: ── Check Java ────────────────────────────────────────────
java -version >nul 2>&1
if errorlevel 1 (
    echo  [ERROR] Java is NOT installed or not found in PATH.
    echo  Please install JDK 11+ from: https://adoptium.net/
    pause
    exit /b 1
)
for /f "tokens=3" %%v in ('java -version 2^>^&1 ^| find "version"') do (
    echo  [OK] Java found: %%v
)
echo.

:: ── Resolve short path to handle non-ASCII folder names ───
for %%I in ("%~dp0.") do set "SHORT=%~sI"
echo  [INFO] Working directory: %SHORT%
echo.

:: ── Create output directories ────────────────────────────
if not exist "%SHORT%bin"  mkdir "%SHORT%bin"
if not exist "%SHORT%dist" mkdir "%SHORT%dist"

:: ── Collect .java files ──────────────────────────────────
echo  [STEP 1/3] Compiling source files...
dir /s /b "%SHORT%src\main\java\*.java" > "%SHORT%sources.txt" 2>nul

if not exist "%SHORT%sources.txt" (
    echo  [ERROR] No Java source files found in src\main\java\
    pause
    exit /b 1
)

javac -encoding UTF-8 -d "%SHORT%bin" "@%SHORT%sources.txt"

del "%SHORT%sources.txt" >nul 2>&1

if errorlevel 1 (
    echo.
    echo  [ERROR] Compilation FAILED. See messages above.
    pause
    exit /b 1
)
echo  [OK] Compilation successful!
echo.

:: ── Package into JAR ─────────────────────────────────────
echo  [STEP 2/3] Packaging into executable JAR...
echo Main-Class: com.chatbot.Main> "%SHORT%manifest.txt"
jar cfm "%SHORT%dist\chatbot.jar" "%SHORT%manifest.txt" -C "%SHORT%bin" .
del "%SHORT%manifest.txt" >nul 2>&1

if errorlevel 1 (
    echo  [ERROR] JAR packaging FAILED.
    pause
    exit /b 1
)
echo  [OK] JAR created: dist\chatbot.jar
echo.

:: ── Launch ───────────────────────────────────────────────
echo  [STEP 3/3] Launching AI Chatbot GUI...
echo  ─────────────────────────────────────────────────────
echo.
java -jar "%SHORT%dist\chatbot.jar"

pause
