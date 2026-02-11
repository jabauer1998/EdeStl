# EDE STL - Emulator Debug Environment

## Overview
A Java-based Emulator Debug Environment (EDE) inspired by PEP9. It allows users to create emulators with Verilog HDL and provides a JavaFX GUI for visualization. The project is a library/framework - it has no main entry point.

## Current State
- The project compiles successfully with zero errors
- Build infrastructure is set up and working
- Three stub methods in VerilogToJavaGen.java (codeGenFileDescriptor, codeGenDeepExpression, codeGenDeepStatement) were lost during prior corruption and need reimplementation

## Project Architecture
- **Language**: Java (GraalVM 22.3 / Java 19)
- **GUI Framework**: JavaFX (OpenJFX 17 via Nix)
- **Build System**: Custom bash build scripts (`build/LinuxBuild.sh` for Linux, `build/WindowsBuild.ps1` for Windows)
- **No main method**: This is a library, not a standalone application

### Source Structure
```
src/ede/stl/
├── ast/          - Abstract Syntax Tree nodes for Verilog parsing
├── circuit/      - Circuit simulation classes (gates, adders, Node, Web, etc.)
├── common/       - Shared utilities (Position, Source, ErrorLog, Utils)
├── compiler/     - Verilog to Java code generator (VerilogToJavaGen)
├── gui/          - JavaFX GUI components (GuiEde, GuiRam, etc.)
├── interpreter/  - Verilog interpreter
├── parser/       - Lexer, Parser, Preprocessor for Verilog
├── passes/       - Compiler passes (TypeChecker, Indexer, visitors)
└── values/       - Value type classes (IntVal, RegVal, VectorVal, Pattern, etc.)
```

### Build
Run `bash build/LinuxBuild.sh build` to compile (or `bash build/LinuxBuild.sh clean` to clean).
On Windows use `build/WindowsBuild.ps1 build`. Output goes to `bin/` directory.

## Recent Changes
- 2026-02-11: Renamed `Value/` directory to `values/` and updated all package declarations/imports
- 2026-02-11: Fixed all compilation errors (2400+ → 0)
  - Fixed package declarations across 30+ files with wrong subpackage names
  - Fixed import statements across entire codebase
  - Added missing `import ede.stl.circuit.Node` to RegVal.java
  - Fixed VerilogToJavaGen package, method name mismatches, and missing throws declarations
  - Fixed TypeChecker package declaration
  - Fixed Interpreter type conversion issue
  - Created stub methods for lost code in VerilogToJavaGen
- 2026-02-11: Initial Replit setup
  - Installed Java (GraalVM 22.3) and OpenJFX
  - Created `build/LinuxBuild.sh` build script (equivalent to WindowsBuild.ps1)
  - Configured Build workflow (`bash build/LinuxBuild.sh build`)
