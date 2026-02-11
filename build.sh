#!/bin/bash

OPENJFX_DIR="/nix/store/frrwkgpk8srrqfhz0wccnklsyb3pbvrq-openjfx-modular-sdk-17.0.11+3"

if [ ! -d "$OPENJFX_DIR" ]; then
    OPENJFX_DIR=$(echo /nix/store/*openjfx-modular-sdk-17*/modules 2>/dev/null | head -1)
    OPENJFX_DIR=$(dirname "$OPENJFX_DIR")
fi

JAVAFX_MODULES="$OPENJFX_DIR/modules"

echo "=== EDE STL Build ==="
echo "Using JavaFX from: $OPENJFX_DIR"
echo ""

mkdir -p bin

find src -name "*.java" -not -name "#*" -not -name "*~" > build/BuildList.txt

echo "Compiling $(wc -l < build/BuildList.txt) Java files..."
echo ""

javac \
    --module-path "$JAVAFX_MODULES" \
    --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base \
    -sourcepath "./src" \
    -d "./bin" \
    -encoding "UTF-8" \
    @build/BuildList.txt 2>&1

if [ $? -eq 0 ]; then
    echo ""
    echo "Build completed successfully!"
else
    echo ""
    echo "Build completed with errors (see above)."
fi
