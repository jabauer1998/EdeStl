#!/bin/bash

location=$(pwd)

echo "Checking if Java Exists..."
javaExists=$(java -version 2>&1 | head -1)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/.."

OPENJFX_DIR="/nix/store/frrwkgpk8srrqfhz0wccnklsyb3pbvrq-openjfx-modular-sdk-17.0.11+3"

if [ ! -d "$OPENJFX_DIR" ]; then
    OPENJFX_DIR=$(dirname "$(echo /nix/store/*openjfx-modular-sdk-17*/modules 2>/dev/null | tr ' ' '\n' | head -1)")
fi

if [ -n "$javaExists" ]; then
    echo "Java Found..."
    echo "Searching for Command..."
    if [ $# -eq 0 ]; then
        echo "No command found, expected 1 argument..."
        echo "Example of command can be 'build', 'publish' or 'clean'"
    else
        command=$1
        echo "Command is $command"
        if [ "$command" = "build" ]; then
            srcRoot=$(pwd)
            if [ -f build/BuildList.txt ]; then
                rm -f build/BuildList.txt
            fi
            touch build/BuildList.txt
            find "$srcRoot/src" -type f ! -name "#*" ! -name "*~" > build/BuildList.txt
            for line in $(cat build/BuildList.txt); do
                if [ -f "$line" ]; then
                    sed -i '1s/^\xEF\xBB\xBF//' "$line"
                fi
            done
            cat "build/BuildList.txt"
            javac "@build/BuildList.txt" -sourcepath "./src" --module-path "$OPENJFX_DIR/modules" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -encoding "UTF-8"
        elif [ "$command" = "clean" ]; then
            rm -rf bin/*
        elif [ "$command" = "publish" ]; then
            :
        else
            echo "Unknown command '$command'"
        fi
    fi
fi

cd "$location"
