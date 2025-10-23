#!/bin/bash
# ===============================================
# SPLAT Compiler Runner Script
# For Java 17 — compiles and runs SplatTester.java
# ===============================================

set -e  # exit immediately on error
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$ROOT_DIR/src"
OUT_DIR="$ROOT_DIR/out"

echo "🔨 Building SPLAT compiler project..."
echo "--------------------------------------"

# Clean old compiled files
if [ -d "$OUT_DIR" ]; then
  echo "🧹 Cleaning previous build..."
  rm -rf "$OUT_DIR"
fi

mkdir -p "$OUT_DIR"

# Compile all Java sources recursively
echo "📦 Compiling sources..."
find "$SRC_DIR" -name "*.java" > sources.txt
javac -d "$OUT_DIR" @sources.txt
rm sources.txt

echo "✅ Compilation successful!"
echo

# Run the SplatTester main class
echo "🚀 Running SplatTester..."
java -cp "$OUT_DIR" splat.SplatTester

# Optional cleanup (uncomment if you want auto-clean after running)
echo "🧽 Cleaning up compiled files..."
rm -rf "$OUT_DIR"
