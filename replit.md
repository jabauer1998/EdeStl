# EDE STL - Emulator Debug Environment

## Overview
A Java-based Emulator Debug Environment (EDE) inspired by PEP9. It allows users to create emulators with Verilog HDL and provides a Swing GUI for visualization. The project is a library/framework - it has no main entry point.

## Current State
- The project compiles successfully with zero errors
- Build infrastructure is set up and working
- GUI has been converted from JavaFX to Swing
- Three stub methods in VerilogToJavaGen.java (codeGenFileDescriptor, codeGenDeepExpression, codeGenDeepStatement) were lost during prior corruption and need reimplementation

## Project Architecture
- **Language**: Java (OpenJDK 25.0.2, installed at tools/jdk-25.0.2)
- **GUI Framework**: Swing (javax.swing / java.awt)
- **Build System**: Custom bash build scripts (`build/LinuxBuild.sh` for Linux, `build/WindowsBuild.ps1` for Windows)
- **No main method**: This is a library, not a standalone application

### Source Structure
```
src/ede/stl/
├── ast/          - Abstract Syntax Tree nodes for Verilog parsing
├── circuit/      - Circuit simulation classes (gates, adders, Node, Web, etc.)
├── common/       - Shared utilities (Position, Source, ErrorLog, Utils)
├── compiler/     - Verilog to Java code generator (VerilogToJavaGen)
├── gui/          - Swing GUI components (GuiEde, GuiRam, etc.)
├── interpreter/  - Verilog interpreter
├── parser/       - Lexer, Parser, Preprocessor for Verilog
├── passes/       - Compiler passes (TypeChecker, Indexer, visitors)
└── values/       - Value type classes (IntVal, RegVal, VectorVal, Pattern, etc.)
```

### GUI Component Mapping (JavaFX → Swing)
- VBox → JPanel with BoxLayout(Y_AXIS)
- HBox → JPanel with BoxLayout(X_AXIS)
- Label → JLabel
- Button → JButton
- TextArea → JTextArea
- ScrollPane → JScrollPane
- TabPane → JTabbedPane
- InlineCssTextArea (RichTextFX) → JTextPane with StyledDocument
- Region → JComponent
- javafx.stage.Screen → java.awt.Toolkit

### Build
Run `bash build/LinuxBuild.sh build` to compile (or `bash build/LinuxBuild.sh clean` to clean).
On Windows use `build/WindowsBuild.ps1 build`.

Build steps (both platforms):
1. Compile all `.java` sources to `tmp/` directory
2. Extract `lib/asm-9.6.jar` into `tmp/` for bundling
3. Bundle everything into `bin/EdeStl.jar`
4. Clean `tmp/`, then compile `sample/ede/Processor.java` against the library jar (if dependencies available)
5. Bundle sample into `bin/EdeSample.jar`

Additional commands: `run` (runs EdeSample.jar), `clean` (removes bin/*, tmp/*, temp files)

## Recent Changes
- 2026-02-22: Upgraded to JDK 25 and added Declan.jar dependency
  - Downloaded OpenJDK 25.0.2 to tools/jdk-25.0.2 (needed for Declan.jar class version 69.0)
  - Build script auto-detects JDK 25 in tools/ directory
  - Fixed sample/ede/Processor.java imports (Callable, TextAreaType, GuiRegister, AssemblerVisitor, ProgramContext)
  - Both EdeStl.jar and EdeSample.jar now build successfully
- 2026-02-22: Synced Linux build script with Windows build
  - Added jar creation (EdeStl.jar), ASM extraction, sample compilation, EdeSample.jar bundling
  - Added `run` command, improved `clean` command
  - Sample classpath now includes all lib/*.jar files
- 2026-02-15: Converted GUI from JavaFX to Swing
  - Replaced all JavaFX components with Swing equivalents across 13 GUI files
  - Replaced InlineCssTextArea (RichTextFX) with JTextPane + StyledDocument for keyword highlighting
  - Updated VerilogToJavaGen bytecode generation to use java.awt.Toolkit/Dimension instead of JavaFX Screen/Rectangle2D
  - Updated build script to remove JavaFX classpath dependencies
  - Project compiles cleanly with zero errors
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
