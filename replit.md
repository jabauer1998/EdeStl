# EDE STL - Emulator Debug Environment

## Overview
A Java-based Emulator Debug Environment (EDE) inspired by PEP9. It allows users to create emulators with Verilog HDL and provides a JavaFX GUI for visualization. The project is a library/framework - it has no main entry point.

## Current State
- The source code has widespread corruption from a bad find-and-replace operation (`.Value .` pattern throughout many files)
- The project compiles with errors due to the above corruption (2400+ errors)
- Build infrastructure is set up and working

## Project Architecture
- **Language**: Java (GraalVM 22.3 / Java 19)
- **GUI Framework**: JavaFX (OpenJFX 17 via Nix)
- **Build System**: Custom bash build script (`build.sh`)
- **No main method**: This is a library, not a standalone application

### Source Structure
```
src/ede/stl/
├── ast/          - Abstract Syntax Tree nodes for Verilog parsing
├── circuit/      - Circuit simulation classes (gates, adders, etc.)
├── common/       - Shared utilities (Position, Source, ErrorLog, Utils)
├── compiler/     - Verilog to Java code generator
├── gui/          - JavaFX GUI components (GuiEde, GuiRam, etc.)
├── interpreter/  - Verilog interpreter
├── parser/       - Lexer, Parser, Preprocessor for Verilog
├── passes/       - Compiler passes (TypeChecker, Indexer, visitors)
└── Value/        - Value type classes (IntVal, RegVal, WireVal, etc.)
```

### Build
Run `bash build.sh` to compile. Output goes to `bin/` directory.

## Recent Changes
- 2026-02-11: Initial Replit setup
  - Installed Java (GraalVM 22.3) and OpenJFX
  - Renamed `values/` directory to `Value/` to match Java package declarations
  - Created `build.sh` build script
  - Removed stale Emacs lock file (.#Utils.java)
  - Configured Build workflow
