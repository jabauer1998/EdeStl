#!/bin/bash

location=$(pwd)

echo "Checking if Java Exists..."
javaExists=$(java -version 2>&1 | head -1)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/.."

if [ -n "$javaExists" ]; then
    echo "Java Found..."
    echo "Searching for Command..."
    if [ $# -eq 0 ]; then
        echo "No command found, expected 1 argument..."
        echo "Example of command can be 'build', 'publish', 'run' or 'clean'"
    else
        command=$1
        echo "Command is $command"
        if [ "$command" = "build" ]; then
            srcRoot=$(pwd)
            if [ -f build/BuildList.txt ]; then
                rm -f build/BuildList.txt
            fi
            touch build/BuildList.txt
            find "$srcRoot/src" -type f -name "*.java" ! -name "#*" ! -name "*~" > build/BuildList.txt
            for line in $(cat build/BuildList.txt); do
                if [ -f "$line" ]; then
                    sed -i '1s/^\xEF\xBB\xBF//' "$line"
                fi
            done
            cat "build/BuildList.txt"
            CLASSPATH=""
            for jar in lib/*.jar; do
                if [ -f "$jar" ]; then
                    if [ -n "$CLASSPATH" ]; then
                        CLASSPATH="$CLASSPATH:"
                    fi
                    CLASSPATH="$CLASSPATH$jar"
                fi
            done
            mkdir -p tmp
            mkdir -p bin
            echo "Building Src"
            if [ -n "$CLASSPATH" ]; then
                javac "@build/BuildList.txt" -sourcepath "./src" -classpath "$CLASSPATH" -d "tmp" -encoding "UTF-8"
            else
                javac "@build/BuildList.txt" -sourcepath "./src" -d "tmp" -encoding "UTF-8"
            fi
            if [ $? -ne 0 ]; then
                echo "Build failed!"
                cd "$location"
                exit 1
            fi
            if [ -f "./lib/asm-9.6.jar" ]; then
                echo "Extracting asm-9.6.jar into tmp"
                (cd tmp && jar xf "../lib/asm-9.6.jar")
            fi
            echo "Bundling into a Jar"
            jar cf "./bin/EdeStl.jar" -C "./tmp" "."
            echo "Deleting Tmp Directory"
            rm -rf tmp/*
            if [ -f "sample/ede/Processor.java" ]; then
                echo "Building Sample"
                javac "sample/ede/Processor.java" -d "./tmp" -sourcepath "./sample" -cp "./bin/EdeStl.jar" -encoding "UTF-8"
                if [ $? -eq 0 ]; then
                    echo "Bundling Sample into a Jar"
                    jar cfe "./bin/EdeSample.jar" "sample.ede.Processor" -C "./tmp" "."
                else
                    echo "Sample build failed (non-fatal), skipping sample jar"
                fi
                rm -rf tmp/*
            fi
        elif [ "$command" = "run" ]; then
            java -jar ./bin/EdeSample.jar
        elif [ "$command" = "clean" ]; then
            find ./src -name "*.class" -delete 2>/dev/null
            rm -rf bin/*
            rm -rf tmp/*
            find . -name "*~" -delete 2>/dev/null
            find . -name "*#" -delete 2>/dev/null
        elif [ "$command" = "publish" ]; then
            :
        else
            echo "Unknown command '$command'"
        fi
    fi
fi

cd "$location"
