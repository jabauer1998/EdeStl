# EDE STL - Emulator Debug Environment

## Overview
A Java-based Emulator Debug Environment (EDE) inspired by PEP9. It allows users to create emulators with Verilog HDL and provides a Swing GUI for visualization. The project is a library/framework - it has no main entry point.

## Current State
- The project compiles successfully with zero errors
- Build infrastructure is set up and working
- GUI has been converted from JavaFX to Swing
- Three stub methods in VerilogToJavaGen.java (codeGenFileDescriptor, codeGenDeepExpression, codeGenDeepStatement) were lost during prior corruption and need reimplementation

## Project Architecture
- **Language**: Java (OpenJDK 25.0.2, installed at jre/)
- **GUI Framework**: Swing (javax.swing / java.awt)
- **Build System**: Custom bash build scripts (`build/LinuxBuild.sh` for Linux, `build/WindowsBuild.ps1` for Windows)
- **No main method**: This is a library, not a standalone application

### Source Structure
```
src/ede/stl/
├── ast/          - Abstract Syntax Tree nodes for Verilog parsing
├── circuit/      - Circuit simulation classes (gates, adders, Node, Web, etc.)
├── common/       - Shared utilities (Position, Source, ErrorLog, Utils, EdeCallable)
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
- 2026-03-01: Fixed breakpoint annotation parsing and GUI issues
  - Parser.parseAnnotationStatement() was missing LPAR match before expression list — added proper LPAR/RPAR handling
  - GuiEde "Take Step" button was setting size/listener on clearStatus instead of takeStep — fixed references
  - EdeInterpreter.interpretTaskCall() used == for string comparison and didn't null-check annotationLexeme — fixed with .equals() and null guard
  - New files added by user: CompiledEnvironment.java, RunnableThread.java (compiler package)
  - TaskStatement now supports annotationLexeme field for breakpoint annotations
  - ANNOTATION token type added to Lexer for //@Annotation syntax in Verilog comments
  - GuiVerilogJob supports both interpreted and compiled execution modes
  - RunJob() now executes on background thread to keep Swing EDT responsive during breakpoints
  - Breakpoint wait uses proper wait/notify synchronization (not busy-wait) with stepLock Object
  - All GUI-mutating methods in GuiEde wrapped with SwingUtilities.invokeLater for thread safety
  - GUI-reading methods use SwingUtilities.invokeAndWait with isEventDispatchThread guard
  - GuiJob.setText() wrapped with SwingUtilities.invokeLater
  - "Enable Debugger" checkbox added to GuiMachine between Flags and IO panels
  - EdeInterpreter checks isDebuggerEnabled() before pausing at @breakpoint annotations
- 2026-02-24: Refactored GUI class names and removed interfaces
  - Renamed job classes: ExeJob → GuiExeJob, JavaJob → GuiJavaJob, VerilogJob → GuiVerilogJob
  - Renamed LineNumberGutter → GuiLineNumberGutter (standalone file)
  - Removed interfaces: Machine, Flags, Memory, RegFile (classes now standalone, no implements)
  - Moved EdeCallable from gui/ to common/ package
  - Replaced Machine type references with GuiEde in values/ and interpreter/
  - Removed stale @Override annotations from interface methods
  - Added GuiLineNumberGutter: line number gutter component for text panes (row header on JScrollPane)
  - GuiJob supports TextAreaNumbered enum (IS_NUMBERED / IS_NOT_NUMBERED)
  - Fixed GuiRam memory row calculation: uses floating-point division to prevent IndexOutOfBoundsException
- 2026-02-24: Fixed VerilogJob file mismatch and state reset
  - VerilogJob inputFile was "OutputBinary.bin" but ARM7TDMIS.v's loadProgram reads from "default" via $fopen
  - Changed Processor.java to pass "default" as inputFile so assembler output reaches the interpreter
  - Added automatic state reset in VerilogJob.RunJob(): clears registers, memory, status flags, and IO panes before each execution
  - Prevents accumulating register/output values across repeated runs
- 2026-02-24: Refactored GuiJobs to eliminate file-based I/O between jobs
  - Created EdeCallable functional interface (takes String input, returns String output)
  - JavaJob and ExeJob now chain directly: output posts to next job's text pane via nextJob.setText()
  - Added getText()/setText() helpers to GuiJob base class
  - GuiJobs now stores GuiJob list (not JComponent), added linkJobs() to wire job chain
  - VerilogJob retains file I/O (writes pane text to inputFile for interpreter binary loading)
  - Removed Callable<Void> usage in favor of EdeCallable
  - Processor.java sample updated: assembler uses CharStreams.fromString() instead of file reading
- 2026-02-24: Fixed KEYWORD text pane editing
  - Replaced KeyListener with DocumentListener for keyword highlighting in GuiJob
  - Old keyTyped handler crashed on empty text and stale cursor positions, preventing typing
  - New highlightKeywords() method scans full text safely via SwingUtilities.invokeLater
- 2026-02-22: Fixed ErrorLog closing System.err and Verilog syntax error
  - ErrorLog.printLog() was calling output.close() which closed System.err, silencing all subsequent error output
  - Removed close() call, added infoLog.clear() to prevent duplicate error printing
  - Fixed syntax error in ARM7TDMIS.v line 291: missing opening parenthesis in shift expression
  - Removed debug logging from GuiEde, MetaDataGatherer, and GuiRam
  - Parser error recovery confirmed working: all 17 registers (R0-R15, CPSR) and 1001-byte memory correctly detected
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
