#!/bin/bash
# AI Chatbot - Build and Run Script (Linux/macOS)

echo "=============================================="
echo "  AI Chatbot - Java NLP Chatbot Project"
echo "  CodeAlpha Internship Task 3"
echo "=============================================="
echo

# Check Java
if ! command -v java &> /dev/null; then
    echo "[ERROR] Java not found! Install JDK 11+ and add to PATH."
    exit 1
fi

echo "[INFO] Java found: $(java -version 2>&1 | head -1)"
echo

# Create dirs
mkdir -p bin dist

# Compile
echo "[STEP 1] Compiling..."
find src/main/java -name "*.java" > sources.txt
javac -d bin -cp src/main/java @sources.txt
STATUS=$?
rm sources.txt

if [ $STATUS -ne 0 ]; then
    echo "[ERROR] Compilation failed!"
    exit 1
fi
echo "[OK] Compiled successfully!"
echo

# Create JAR
echo "[STEP 2] Creating JAR..."
echo "Main-Class: com.chatbot.Main" > manifest.txt
jar cfm dist/chatbot.jar manifest.txt -C bin .
rm manifest.txt
echo "[OK] JAR: dist/chatbot.jar"
echo

# Run
echo "[STEP 3] Launching AI Chatbot..."
java -jar dist/chatbot.jar
