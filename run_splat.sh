#!/bin/bash
# ===============================================
# SPLAT Compiler Runner Script (Java 8 compatible)
# Compiles with Java 17 but targets Java 8
# ===============================================

set -e
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$ROOT_DIR/src"
OUT_DIR="$ROOT_DIR/out"

echo "Building SPLAT compiler project (target: Java 8)..."
echo "--------------------------------------"

# Clean previous build
if [ -d "$OUT_DIR" ]; then
  echo "Cleaning previous build..."
  rm -rf "$OUT_DIR"
fi
mkdir -p "$OUT_DIR"

# Compile all Java sources recursively
echo "Compiling sources (Java 8 target)..."
find "$SRC_DIR" -name "*.java" > sources.txt

# Prefer --release 8 when available
if javac --release 8 -d "$OUT_DIR" @sources.txt 2>/dev/null; then
  echo "Compilation successful (Java 8 target)."
else
  echo "Falling back to -source/-target 8..."
  javac -source 8 -target 8 -d "$OUT_DIR" @sources.txt
  echo "Compilation successful (Java 8 target)."
fi

rm sources.txt
echo

# Run the SplatTester main class
echo "Running SplatTester..."
java -cp "$OUT_DIR" splat.SplatTester
