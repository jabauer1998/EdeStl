package ede.stl.compiler;


import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import ede.stl.common.Pointer;
import ede.stl.common.SymbolTable;
import ede.stl.common.ErrorLog;
import ede.stl.common.ErrorItem;
import ede.stl.common.Utils;
import ede.stl.interpreter.Environment;
import ede.stl.interpreter.VerilogInterpreter;
import ede.stl.values.IntVal;
import ede.stl.values.Value;
import ede.stl.values.VectorVal;
import ede.stl.values.ArrayRegVal;
import ede.stl.ast.ModuleDeclaration;
import ede.stl.ast.VerilogFile;
import ede.stl.ast.ConstantExpression;
import ede.stl.ast.Expression;
import ede.stl.ast.FunctionCall;
import ede.stl.ast.BinaryOperation;
import ede.stl.ast.Concatenation;
import ede.stl.ast.TernaryOperation;
import ede.stl.ast.UnaryOperation;
import ede.stl.ast.BinaryNode;
import ede.stl.ast.DecimalNode;
import ede.stl.ast.HexadecimalNode;
import ede.stl.ast.OctalNode;
import ede.stl.ast.StringNode;
import ede.stl.ast.Element;
import ede.stl.ast.Identifier;
import ede.stl.ast.LValue;
import ede.stl.ast.Slice;
import ede.stl.ast.ContinuousAssignment;
import ede.stl.ast.EmptyModItem;
import ede.stl.ast.ModuleItem;
import ede.stl.ast.AndGateDeclaration;
import ede.stl.ast.GateDeclaration;
import ede.stl.ast.NandGateDeclaration;
import ede.stl.ast.NorGateDeclaration;
import ede.stl.ast.NotGateDeclaration;
import ede.stl.ast.OrGateDeclaration;
import ede.stl.ast.XnorGateDeclaration;
import ede.stl.ast.XorGateDeclaration;
import ede.stl.ast.ModuleInstantiation;
import ede.stl.ast.FunctionDeclaration;
import ede.stl.ast.ProcedureDeclaration;
import ede.stl.ast.TaskDeclaration;
import ede.stl.ast.ProcessBase;
import ede.stl.ast.InitialProcess;
import ede.stl.ast.AllwaysProcess;
import ede.stl.ast.ArrayDeclaration;
import ede.stl.ast.IdentDeclaration;
import ede.stl.ast.Input;
import ede.stl.ast.Int;
import ede.stl.ast.Output;
import ede.stl.ast.Real;
import ede.stl.ast.Reg;
import ede.stl.ast.Wire;
import ede.stl.ast.SeqBlockStatement;
import ede.stl.ast.Statement;
import ede.stl.ast.CaseStatement;
import ede.stl.ast.CaseXStatement;
import ede.stl.ast.CaseZStatement;
import ede.stl.ast.CaseItem;
import ede.stl.ast.DefCaseItem;
import ede.stl.ast.ExprCaseItem;
import ede.stl.ast.Assignment;
import ede.stl.ast.BlockingAssignment;
import ede.stl.ast.NonBlockingAssignment;
import ede.stl.ast.ForStatement;
import ede.stl.ast.ForeverStatement;
import ede.stl.ast.RepeatStatement;
import ede.stl.ast.WhileStatement;
import ede.stl.ast.IfElseStatement;
import ede.stl.ast.IfStatement;
import ede.stl.ast.SystemTaskStatement;
import ede.stl.ast.TaskStatement;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;

public class VerilogToJavaGen {
        private ClassWriter mainWriter;
        private int         javaVersion;
        private ErrorLog    errLog;
        private Stack<SymbolTable<Integer>> scopedTable;
        private Stack<HashSet<String>> scopedFields;
        private SymbolTable<String> funcTypes;
        private int processNumber;
        private int localVariableNumber;
        
        public VerilogToJavaGen(int javaVersion) {
                this.javaVersion = javaVersion;
                this.errLog = new ErrorLog();
                this.processNumber = 0;
                this.scopedTable = new Stack<>();
                this.scopedFields = new Stack<>();
                this.funcTypes = new SymbolTable<>();
		this.localVariableNumber = 0;
        }
        
        boolean localInScope(String name){
                SymbolTable<Integer> scope = scopedTable.peek();
                return scope.entryExists(name);
        }
        
        boolean fieldInScope(String field) {
                HashSet<String> scope = scopedFields.peek();
                return scope.contains(field);
        }
        
        int getFromScope(String elem) {
                SymbolTable<Integer> scope = scopedTable.peek();
                return scope.getEntry(elem);
        }
        
        public void addElem(String elem) {
                SymbolTable<Integer> scope = scopedTable.peek();
                scope.addEntry(elem, localVariableNumber);
		localVariableNumber++;
        }
        
        void pushScope(boolean inModule){
                scopedTable.add(new SymbolTable<>());
		if(inModule)
		    scopedFields.add(new HashSet<String>());
		funcTypes.addScope();
		this.localVariableNumber = 0;
        }
        
        void popScope(boolean inModule) {
                scopedTable.pop();
		if(inModule)
		    scopedFields.pop();
		funcTypes.removeScope();
		this.localVariableNumber = 0;
        }

        public void codeGenVerilogFile(VerilogFile file) throws Exception{
                new File("ede/instance/mods").mkdirs();

                ClassWriter processWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                processWriter.visit(this.javaVersion,
                        Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER,
                        "Processes",
                        null,
                        "java/lang/Object",
                        null);

                for (ModuleDeclaration module : file.modules) {
		        pushScope(true);
                        ClassWriter moduleWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                        codeGenModule(module, moduleWriter, processWriter);
                        moduleWriter.visitEnd();
                        byte[] moduleBytes = moduleWriter.toByteArray();
                        try (FileOutputStream fos = new FileOutputStream("ede/instance/mods/" + module.moduleName + ".class")) {
                                fos.write(moduleBytes);
                        }
			popScope(true);
                }

                processWriter.visitEnd();
                byte[] processBytes = processWriter.toByteArray();
                try (FileOutputStream fos = new FileOutputStream("ede/instance/Processes.class")) {
                        fos.write(processBytes);
                }
        }

        private static void pushString(String val, MethodVisitor main){
                // 1. Instantiate the object
                main.visitTypeInsn(Opcodes.NEW, "java/lang/String");
                // 2. Duplicate the reference
                main.visitInsn(Opcodes.DUP);
                main.visitLdcInsn(val);
        }

        private static void pushDouble(double val, MethodVisitor main){
                // 1. Instantiate the object
                main.visitTypeInsn(Opcodes.NEW, "java/lang/Double");
                // 2. Duplicate the reference
                main.visitInsn(Opcodes.DUP);
                // 3. Load the double constant value 123.45 onto the stack
                double value = val;
                main.visitLdcInsn(value);
        }

        private static void pushBool(boolean bool, MethodVisitor main){
                main.visitTypeInsn(Opcodes.NEW, "java/lang/Boolean");
                // 2. Duplicate the reference
                main.visitInsn(Opcodes.DUP);
                // 3. Load the double constant value 123.45 onto the stack
                main.visitLdcInsn(bool);
        }

        private static void pushInt(int val, MethodVisitor main){
                // 1. Instantiate the object
                main.visitTypeInsn(Opcodes.NEW, "java/lang/Integer");
                // 2. Duplicate the reference
                main.visitInsn(Opcodes.DUP);
                // 3. Load the double constant value 123.45 onto the stack
                main.visitLdcInsn(val);
        }

        private static void pushEnum(String enumName, String specificName, MethodVisitor main){
                main.visitFieldInsn(Opcodes.GETSTATIC, specificName, enumName, "L" + specificName + ";");
        }

        private String getTypes(List<ModuleItem> args) throws Exception{
                StringBuilder typeStr = new StringBuilder();
                for(ModuleItem item: args) {
                        typeStr.append(typeOf(item));
                }
                return typeStr.toString();
        }

        private void codeGenModule(ModuleDeclaration mod, ClassWriter moduleWriter, ClassWriter processWriter) throws Exception{
                moduleWriter.visit(this.javaVersion, // Java version (e.g., V1_8 for Java 8)
                        Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, // Access flags (public class)
                        mod.moduleName, // Internal class name
                        null, // Signature (null for non-generic classes)
                        "java/lang/Object", // Superclass
                        null); // Interfaces (null if none

                String typeStr = getTypes(mod.args);
                
                MethodVisitor moduleConstructor = moduleWriter.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_VARARGS, // Access flags: public
                        "<init>", // Method name: <init> for constructor
                        "(" + typeStr + ")V", // Descriptor: no arguments, void return
                        null, // Signature: generic signature (null for non-generic)
                        null // Exceptions: no exceptions thrown
                );

                for(ModuleItem item : mod.moduleItemList) { codeGenField(item, moduleConstructor, mod.moduleName, moduleWriter); }

                for(ModuleItem item : mod.moduleItemList) { codeGenPossibleProcedure(item, moduleConstructor, mod.moduleName, moduleWriter); }
                
                for(ModuleItem item : mod.moduleItemList) { codeGenRestModuleItem(item, moduleConstructor, mod.moduleName, moduleWriter, processWriter); }
        }
        
        private void codeGenRestModuleItem(ModuleItem item, MethodVisitor moduleConstructor, String modName, ClassWriter moduleWriter, ClassWriter processWriter) throws Exception {
                if(item instanceof GateDeclaration) codeGenGateDeclaration(item, moduleConstructor, modName, moduleWriter);
                else if(item instanceof ContinuousAssignment) codeGenContinuousAssignment((ContinuousAssignment)item, moduleConstructor, modName, moduleWriter);
                else if(item instanceof EmptyModItem) codeGenEmptyModItem();
                else if(item instanceof ModuleInstantiation) codeGenModuleInstantiation((ModuleInstantiation)item, moduleConstructor, modName, moduleWriter);
                else if(item instanceof ProcessBase) codeGenProcess((ProcessBase)item, modName, processWriter);
        }

       private void codeGenProcess(ProcessBase process, String modName, ClassWriter processWriter) throws Exception{
           if(process instanceof InitialProcess) codeGenInitialProcess((InitialProcess)process, modName, processWriter);
           else if(process instanceof AllwaysProcess) codeGenAllwaysProcess((AllwaysProcess)process, modName, processWriter);
       }

       private void codeGenInitialProcess(InitialProcess process, String modName, ClassWriter processWriter) throws Exception{
	   pushScope(false);
           MethodVisitor methodVisit = processWriter.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "process" + this.processNumber, "(Lede/stl/gui/GuiEde;)V", null, null);
           methodVisit.visitCode();
           codeGenShallowStatement(process.statement, "process" + this.processNumber, methodVisit, modName, processWriter);
           methodVisit.visitInsn(Opcodes.RETURN);
           methodVisit.visitMaxs(0, 0);
           methodVisit.visitEnd();
           this.processNumber++;
	   popScope(false);
       }

      private void codeGenAllwaysProcess(AllwaysProcess process, String modName, ClassWriter processWriter) throws Exception{
	   pushScope(false);
           MethodVisitor methodVisit = processWriter.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "process" + this.processNumber, "(Lede/stl/compiler/CompiledEnvironment;)V", null, null);
           methodVisit.visitCode();
           Label begin = new Label();
           methodVisit.visitLabel(begin);
           codeGenShallowStatement(process.statement, "process" + this.processNumber, methodVisit, modName, processWriter);
           methodVisit.visitJumpInsn(Opcodes.GOTO, begin);
           methodVisit.visitInsn(Opcodes.RETURN);
           methodVisit.visitMaxs(0, 0);
           methodVisit.visitEnd();
           this.processNumber++;
	   popScope(false);
       }
        
        private void codeGenGateDeclaration(ModuleItem item, MethodVisitor moduleConstructor, String modName, ClassWriter writer) throws Exception{
                if(item instanceof AndGateDeclaration) codeGenAndGateDeclaration((AndGateDeclaration)item, moduleConstructor, modName, writer);
                else if(item instanceof NandGateDeclaration) codeGenNandGateDeclaration((NandGateDeclaration)item, moduleConstructor, modName, writer);
                else if(item instanceof OrGateDeclaration) codeGenOrGateDeclaration((OrGateDeclaration)item, moduleConstructor, modName, writer);
                else if(item instanceof NotGateDeclaration) codeGenNotGateDeclaration((NotGateDeclaration)item, moduleConstructor, modName, writer);
                else if(item instanceof NorGateDeclaration) codeGenNorGateDeclaration((NorGateDeclaration)item, moduleConstructor, modName, writer);
                else if(item instanceof XnorGateDeclaration) codeGenXnorGateDeclaration((XnorGateDeclaration)item, moduleConstructor, modName, writer);
                else if(item instanceof XorGateDeclaration) codeGenXorGateDeclaration((XorGateDeclaration)item, moduleConstructor, modName, writer);
                else Utils.errorAndExit("Unknown Gate Declaration " + item.getClass().toString());
        }
        
        private void codeGenEmptyModItem(){
                //Do nothing this is just a placeholder
        }
        
        private void codeGenModuleInstantiation(ModuleInstantiation instant, MethodVisitor constructor, String modName, ClassWriter moduleWriter){
                
        }
        
        private void codeGenContinuousAssignment(ContinuousAssignment item, MethodVisitor moduleConstructor, String modName, ClassWriter moduleWriter) throws Exception{
                for(BlockingAssignment assign: item.assignmentList) {
                        codeGenDeepAssignment(assign, moduleConstructor, modName, moduleWriter);
                }
        }
        
        private void codeGenDeepAssignment(BlockingAssignment assign, MethodVisitor constructor, String modName, ClassWriter moduleWriter) throws Exception {
                codeGenDeepExpression(assign.rightHandSide, constructor, modName, moduleWriter);
                
                if(assign.rightHandSide instanceof Element){
                        Element elem = (Element)assign.rightHandSide;
                        codeGenShallowExpression(elem.index1, constructor, modName, moduleWriter);
                        if(this.localInScope(elem.labelIdentifier)) {
                                int val = this.getFromScope(elem.labelIdentifier);
                                constructor.visitVarInsn(Opcodes.ALOAD, val);
                        } else if(this.fieldInScope(elem.labelIdentifier)) {
                                constructor.visitFieldInsn(Opcodes.GETFIELD, 
                modName, // Owner class internal name
                elem.labelIdentifier,           // Field name
                "Lede/stl/values/Value;");
                        } else {
                                Utils.errorAndExit("Error cant find local or field in deep assignment");
                        }
                        constructor.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "assignDeepElem", "(Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;)V", false);
                } else if (assign.rightHandSide instanceof Slice){
                        Slice slice = (Slice)assign.rightHandSide;
                        codeGenShallowExpression(slice.index1, constructor, modName, moduleWriter);
                        codeGenShallowExpression(slice.index2, constructor, modName, moduleWriter);
                        
                        if(this.localInScope(slice.labelIdentifier)) {
                                int val = this.getFromScope(slice.labelIdentifier);
                                constructor.visitVarInsn(Opcodes.ALOAD, val);
                        } else if(this.fieldInScope(slice.labelIdentifier)) {
                                constructor.visitFieldInsn(Opcodes.GETFIELD, 
                modName, // Owner class internal name
                slice.labelIdentifier,           // Field name
                "Lede/stl/values/Value;");
                        } else {
                                Utils.errorAndExit("Error cant find local or field in deep assignment");
                        }
                        constructor.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "assignDeepSlice", "(Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;)V", false);
                } else if(assign.rightHandSide instanceof Identifier) {
                        Identifier ident = (Identifier)assign.rightHandSide;
                        
                        if(this.localInScope(ident.labelIdentifier)) {
                                int place = this.getFromScope(ident.labelIdentifier);
                                constructor.visitVarInsn(Opcodes.ASTORE, place);
                        } else if(this.fieldInScope(ident.labelIdentifier)) {
                                constructor.visitFieldInsn(Opcodes.PUTFIELD, 
							   modName, // Owner class internal name
							   ident.labelIdentifier,           // Field name
							   "Lede/stl/values/Value;");
                        } else {
                                Utils.errorAndExit("Error no ident found for deep assignment with " + ident.labelIdentifier + " as its name");
                        }
                } else {
                        Utils.errorAndExit("Error unexpected type for continuous assignment");
                }
        }
        
        private void codeGenAndGateDeclaration(AndGateDeclaration item, MethodVisitor moduleConstructor, String modName, ClassWriter writer) throws Exception {
                int count = item.gateConnections.size() - 3;
                
                for(int i = 0; i < count; i++) {
                        Expression exp = item.gateConnections.get(i);
                        codeGenDeepExpression(exp, moduleConstructor, modName, writer);
                }
                
                pushInt(item.gateConnections.size() - count, moduleConstructor);
                moduleConstructor.visitTypeInsn(Opcodes.ANEWARRAY, "ede/stl/circuit/Web");
                for(int i = count; i < item.gateConnections.size(); i++) {
                        moduleConstructor.visitInsn(Opcodes.DUP);
                        pushInt(i, moduleConstructor); // Array index
                        Expression exp = item.gateConnections.get(i);
                        codeGenDeepExpression(exp, moduleConstructor, modName, writer);   // Value
                        moduleConstructor.visitInsn(Opcodes.AASTORE);
                }
                moduleConstructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/AndGate", "<Init>", "(Lede/stl/circuit/Web;Lede/stl/circuit/Web;Lede/stl/circuit/Web;[Lede/stl/circuit/Web;)V", false);
        }
        
        private void codeGenNandGateDeclaration(NandGateDeclaration item, MethodVisitor moduleConstructor, String modName, ClassWriter writer) throws Exception {
                int count = item.gateConnections.size() - 3;
                
                for(int i = 0; i < count; i++) {
                        Expression exp = item.gateConnections.get(i);
                        codeGenDeepExpression(exp, moduleConstructor, modName, writer);
                }
                
                pushInt(item.gateConnections.size() - count, moduleConstructor);
                moduleConstructor.visitTypeInsn(Opcodes.ANEWARRAY, "ede/stl/circuit/Web");
                for(int i = count; i < item.gateConnections.size(); i++) {
                        moduleConstructor.visitInsn(Opcodes.DUP);
                        pushInt(i, moduleConstructor); // Array index
                        Expression exp = item.gateConnections.get(i);
                        codeGenDeepExpression(exp, moduleConstructor, modName, writer);   // Value
                        moduleConstructor.visitInsn(Opcodes.AASTORE);
                }
                moduleConstructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/NandGate", "<Init>", "(Lede/stl/circuit/Web;Lede/stl/circuit/Web;Lede/stl/circuit/Web;[Lede/stl/circuit/Web;)V", false);
        }
        
        private void codeGenNorGateDeclaration(NorGateDeclaration item, MethodVisitor moduleConstructor, String modName, ClassWriter writer) throws Exception {
                int count = item.gateConnections.size() - 3;
                
                for(int i = 0; i < count; i++) {
                        Expression exp = item.gateConnections.get(i);
                        codeGenDeepExpression(exp, moduleConstructor, modName, writer);
                }
                
                pushInt(item.gateConnections.size() - count, moduleConstructor);
                moduleConstructor.visitTypeInsn(Opcodes.ANEWARRAY, "ede/stl/circuit/Web");
                for(int i = count; i < item.gateConnections.size(); i++) {
                        moduleConstructor.visitInsn(Opcodes.DUP);
                        pushInt(i, moduleConstructor); // Array index
                        Expression exp = item.gateConnections.get(i);
                        codeGenDeepExpression(exp, moduleConstructor, modName, writer);   // Value
                        moduleConstructor.visitInsn(Opcodes.AASTORE);
                }
                moduleConstructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/NorGate", "<Init>", "(Lede/stl/circuit/Web;Lede/stl/circuit/Web;Lede/stl/circuit/Web;[Lede/stl/circuit/Web;)V", false);
        }
        
        private void codeGenNotGateDeclaration(NotGateDeclaration item, MethodVisitor moduleConstructor, String modName, ClassWriter writer) throws Exception {
                codeGenDeepExpression(item.gateConnections.get(0), moduleConstructor, modName, writer);
                codeGenDeepExpression(item.gateConnections.get(1), moduleConstructor, modName, writer);
                moduleConstructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/NandGate", "<Init>", "(Lede/stl/circuit/Web;Lede/stl/circuit/Web;)V", false);
        }
        
        private void codeGenOrGateDeclaration(OrGateDeclaration item, MethodVisitor moduleConstructor, String modName, ClassWriter writer) throws Exception {
                int count = item.gateConnections.size() - 3;
                
                for(int i = 0; i < count; i++) {
                        Expression exp = item.gateConnections.get(i);
                        codeGenDeepExpression(exp, moduleConstructor, modName, writer);
                }
                
                pushInt(item.gateConnections.size() - count, moduleConstructor);
                moduleConstructor.visitTypeInsn(Opcodes.ANEWARRAY, "ede/stl/circuit/Web");
                for(int i = count; i < item.gateConnections.size(); i++) {
                        moduleConstructor.visitInsn(Opcodes.DUP);
                        pushInt(i, moduleConstructor); // Array index
                        Expression exp = item.gateConnections.get(i);
                        codeGenDeepExpression(exp, moduleConstructor, modName, writer);   // Value
                        moduleConstructor.visitInsn(Opcodes.AASTORE);
                }
                moduleConstructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/NorGate", "<Init>", "(Lede/stl/circuit/Web;Lede/stl/circuit/Web;Lede/stl/circuit/Web;[Lede/stl/circuit/Web;)V", false);
        }
        
        private void codeGenXnorGateDeclaration(XnorGateDeclaration item, MethodVisitor moduleConstructor, String modName, ClassWriter writer) throws Exception {
                int count = item.gateConnections.size() - 3;
                
                for(int i = 0; i < count; i++) {
                        Expression exp = item.gateConnections.get(i);
                        codeGenDeepExpression(exp, moduleConstructor, modName, writer);
                }
                
                pushInt(item.gateConnections.size() - count, moduleConstructor);
                moduleConstructor.visitTypeInsn(Opcodes.ANEWARRAY, "ede/stl/circuit/Web");
                for(int i = count; i < item.gateConnections.size(); i++) {
                        moduleConstructor.visitInsn(Opcodes.DUP);
                        pushInt(i, moduleConstructor); // Array index
                        Expression exp = item.gateConnections.get(i);
                        codeGenDeepExpression(exp, moduleConstructor, modName, writer);   // Value
                        moduleConstructor.visitInsn(Opcodes.AASTORE);
                }
                moduleConstructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/XnorGate", "<Init>", "(Lede/stl/circuit/Web;Lede/stl/circuit/Web;Lede/stl/circuit/Web;[Lede/stl/circuit/Web;)V", false);
        }
        
        private void codeGenXorGateDeclaration(XorGateDeclaration item, MethodVisitor moduleConstructor, String modName, ClassWriter writer) throws Exception {
                int count = item.gateConnections.size() - 3;
                
                for(int i = 0; i < count; i++) {
                        Expression exp = item.gateConnections.get(i);
                        codeGenDeepExpression(exp, moduleConstructor, modName, writer);
                }
                
                pushInt(item.gateConnections.size() - count, moduleConstructor);
                moduleConstructor.visitTypeInsn(Opcodes.ANEWARRAY, "ede/stl/circuit/Web");
                for(int i = count; i < item.gateConnections.size(); i++) {
                        moduleConstructor.visitInsn(Opcodes.DUP);
                        pushInt(i, moduleConstructor); // Array index
                        Expression exp = item.gateConnections.get(i);
                        codeGenDeepExpression(exp, moduleConstructor, modName, writer);   // Value
                        moduleConstructor.visitInsn(Opcodes.AASTORE);
                }
                moduleConstructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/XnorGate", "<Init>", "(Lede/stl/circuit/Web;Lede/stl/circuit/Web;Lede/stl/circuit/Web;[Lede/stl/circuit/Web;)V", false);
        }

        private String nameOf(ModuleItem declaration) throws Exception {
                if (declaration instanceof Input.Wire.Vector.Ident)
                        return ((Input.Wire.Vector.Ident)declaration).declarationIdentifier;
                else if (declaration instanceof Input.Reg.Vector.Ident)
                        return ((Input.Reg.Vector.Ident)declaration).declarationIdentifier;
                else if (declaration instanceof Input.Wire.Scalar.Ident)
                        return ((Input.Wire.Scalar.Ident)declaration).declarationIdentifier;
                else if (declaration instanceof Input.Reg.Scalar.Ident)
                        return ((Input.Reg.Scalar.Ident)declaration).declarationIdentifier;
                else if (declaration instanceof Output.Wire.Vector.Ident)
                        return ((Output.Wire.Vector.Ident)declaration).declarationIdentifier;
                else if (declaration instanceof Output.Reg.Vector.Ident)
                        return ((Output.Reg.Vector.Ident)declaration).declarationIdentifier;
                else if (declaration instanceof Output.Wire.Scalar.Ident)
                        return ((Output.Wire.Scalar.Ident)declaration).declarationIdentifier;
                else if (declaration instanceof Output.Reg.Scalar.Ident)
                        return ((Output.Reg.Scalar.Ident)declaration).declarationIdentifier;
                else if (declaration instanceof Wire.Vector.Ident)
                        return ((Wire.Vector.Ident)declaration).declarationIdentifier;
                else if (declaration instanceof Reg.Vector.Ident)
                        return ((Reg.Vector.Ident)declaration).declarationIdentifier;
                else if (declaration instanceof Wire.Scalar.Ident)
                        return ((Wire.Scalar.Ident)declaration).declarationIdentifier;
                else if (declaration instanceof Reg.Scalar.Ident)
                        return ((Reg.Scalar.Ident)declaration).declarationIdentifier;
                else if (declaration instanceof Int.Ident)
                        return ((Int.Ident)declaration).declarationIdentifier;
                else if (declaration instanceof Real.Ident)
                        return ((Real.Ident)declaration).declarationIdentifier;
                else {
                        Utils.errorAndExit("Error Could not find Ident Declaration with the following type " + declaration.getClass().getName());
                        return null;
                }
        }

        private String typeOf(ModuleItem declaration) throws Exception {
                if (declaration instanceof Input.Wire.Vector.Ident)
                        return "Lede/stl/values/VectorVal;";
                else if (declaration instanceof Input.Reg.Vector.Ident)
                        return "Lede/stl/values/VectorVal";
                else if (declaration instanceof Input.Wire.Scalar.Ident)
                        return "Lede/stl/circuit/WireVal;";
                else if (declaration instanceof Input.Reg.Scalar.Ident)
                        return "Lede/stl/values/RegVal;";
                else if (declaration instanceof Output.Wire.Vector.Ident)
                        return "Lede/stl/values/VectorVal;";
                else if (declaration instanceof Output.Reg.Vector.Ident)
                        return "Lede/stl/values/VectorVal;";
                else if (declaration instanceof Output.Wire.Scalar.Ident)
                        return "Lede/stl/circuit/WireVal;";
                else if (declaration instanceof Output.Reg.Scalar.Ident)
                        return "Lede/stl/values/RegVal;";
                else if (declaration instanceof Wire.Vector.Ident)
                        return "Lede/stl/values/VectorVal;";
                else if (declaration instanceof Reg.Vector.Ident)
                        return "Lede/stl/values/VectorVal;";
                else if (declaration instanceof Wire.Scalar.Ident)
                        return "Lede/stl/circuit/WireVal;";
                else if (declaration instanceof Reg.Scalar.Ident)
                        return "Lede/stl/values/RegVal;";
                else if (declaration instanceof Int.Ident)
                        return "Lede/stl/values/IntVal;";
                else if (declaration instanceof Real.Ident)
                        return "Lede/stl/values/RealVal;";
                else {
                        Utils.errorAndExit("Error Could not find Ident Declaration with the following type " + declaration.getClass().getName());
                        return "";
                }
        }
        
        private void codeGenDeepFunction(FunctionDeclaration decl, String modName, ClassWriter moduleWriter) throws Exception{
	        pushScope(false);
                StringBuilder methodType = new StringBuilder();
                methodType.append('(');

                for (ModuleItem param : decl.paramaters) {
                        String str = typeOf(param);
                        methodType.append(str);
                }

                methodType.append(')');
                methodType.append(typeOf(decl.functionName));
                
                String methodName = nameOf(decl.functionName);
                funcTypes.addEntry(methodName + "Deep", methodType.toString());

                MethodVisitor methodVisit = moduleWriter.visitMethod(Opcodes.ACC_PUBLIC, // Access: public static
                        methodName + "Deep", // Name: main
                        methodType.toString(), null, // Signature (generics related, null for simple types)
                        null // Exceptions thrown (null for none)
                );

                methodVisit.visitCode();
                codeGenDeepStatement(decl.stat, methodName + "Deep", methodVisit, modName, moduleWriter);
                methodVisit.visitInsn(Opcodes.RETURN);
                methodVisit.visitMaxs(0, 0); // COMPUTE_MAXS and COMPUTE_FRAMES handle these automatically
                methodVisit.visitEnd();
		popScope(false);
        }

        private void codeGenShallowFunction(FunctionDeclaration decl, String modName, ClassWriter moduleWriter) throws Exception{
	        pushScope(false);
                StringBuilder methodType = new StringBuilder();
                methodType.append('(');

                for (ModuleItem param : decl.paramaters) {
                        String str = typeOf(param);
                        methodType.append(str);
                }

                methodType.append(')');
                methodType.append(typeOf(decl.functionName));
                
                String methodName = nameOf(decl.functionName);
                funcTypes.addEntry(methodName + "Shallow", methodType.toString());

                MethodVisitor methodVisit = moduleWriter.visitMethod(Opcodes.ACC_PUBLIC, // Access: public static
                        methodName + "Shallow", // Name: main
                        methodType.toString(), null, // Signature (generics related, null for simple types)
                        null // Exceptions thrown (null for none)
                );

                methodVisit.visitCode();
                codeGenShallowStatement(decl.stat, methodName + "Shallow", methodVisit, modName, moduleWriter);
                methodVisit.visitInsn(Opcodes.RETURN);
                methodVisit.visitMaxs(0, 0); // COMPUTE_MAXS and COMPUTE_FRAMES handle these automatically
                methodVisit.visitEnd();
		popScope(false);
        }
        
        private void codeGenDeepTask(TaskDeclaration decl, String modName, ClassWriter moduleWriter) throws Exception {
	        pushScope(false);
                StringBuilder methodType = new StringBuilder();
                methodType.append('(');
		this.localVariableNumber = 1;
                for (ModuleItem param : decl.paramaters) {
                        String str = typeOf(param);
			String name = nameOf(param);
                        methodType.append(str);
			addElem(name);
                }

                methodType.append(')');
                methodType.append('V');
                
                String methodName = decl.taskName + "Deep";
                funcTypes.addEntry(methodName, methodType.toString());
                
                MethodVisitor methodVisit = moduleWriter.visitMethod(Opcodes.ACC_PUBLIC, // Access: public static
                        methodName, // Name: main
                        methodType.toString(), null, // Signature (generics related, null for simple types)
                        null // Exceptions thrown (null for none)
                );
                
                methodVisit.visitCode();
                codeGenDeepStatement(decl.stat, methodName, methodVisit, modName, moduleWriter);
                methodVisit.visitInsn(Opcodes.RETURN);
                methodVisit.visitMaxs(0, 0); // COMPUTE_MAXS and COMPUTE_FRAMES handle these automatically
                methodVisit.visitEnd();
		popScope(false);
        }
        
        private void codeGenShallowTask(TaskDeclaration decl, String modName, ClassWriter moduleWriter) throws Exception {
	        pushScope(false);
	        StringBuilder methodType = new StringBuilder();
                methodType.append('(');
		this.localVariableNumber = 1;
                for (ModuleItem param : decl.paramaters) {
                        String str = typeOf(param);
			String name = nameOf(param);
                        methodType.append(str);
			addElem(name);
                }

                methodType.append(')');
                methodType.append('V');
                 
                String methodName = decl.taskName + "Shallow";
                funcTypes.addEntry(methodName, methodType.toString());

                MethodVisitor methodVisit = moduleWriter.visitMethod(Opcodes.ACC_PUBLIC, // Access: public static
                        methodName + "Shallow", // Name: main
                        methodType.toString(), null, // Signature (generics related, null for simple types)
                        null // Exceptions thrown (null for none)
                );

                methodVisit.visitCode();
                codeGenShallowStatement(decl.stat, methodName, methodVisit, modName, moduleWriter);
                methodVisit.visitInsn(Opcodes.RETURN);
                methodVisit.visitMaxs(0, 0); // COMPUTE_MAXS and COMPUTE_FRAMES handle these automatically
                methodVisit.visitEnd();
		popScope(false);
        }
        
        private void codeGenPossibleProcedure(ModuleItem item, MethodVisitor moduleConstructor, String modName, ClassWriter writer) throws Exception{
                if(item instanceof ProcedureDeclaration) codeGenProcedure((ProcedureDeclaration)item, modName, writer);
        }

        private void codeGenProcedure(ProcedureDeclaration decl, String modName, ClassWriter moduleWriter) throws Exception{
                if(decl instanceof TaskDeclaration) codeGenTask((TaskDeclaration)decl, modName, moduleWriter);
                else if(decl instanceof FunctionDeclaration) codeGenFunction((FunctionDeclaration)decl, modName, moduleWriter);
                else Utils.errorAndExit("Error invalid procGen");
        }
        
        private void codeGenTask(TaskDeclaration task, String modName, ClassWriter moduleWriter) throws Exception {
                codeGenShallowTask(task, modName, moduleWriter);
                codeGenDeepTask(task, modName, moduleWriter);
        }
        
        private void codeGenFunction(FunctionDeclaration myDecl, String modName, ClassWriter moduleWriter) throws Exception {
                codeGenShallowFunction(myDecl, modName, moduleWriter);
                codeGenDeepFunction(myDecl, modName, moduleWriter);
        }

        private void codeGenShallowStatement(Statement stat, String methodName, MethodVisitor method, String modName, ClassWriter modWriter) throws Exception{
                if (stat instanceof CaseStatement)
                        codeGenCaseShallowStatement((CaseStatement)stat, methodName, method, modName, modWriter);
                else if (stat instanceof Assignment)
                        codeGenShallowAssignment((Assignment)stat, methodName, method, modName, modWriter);
                else if (stat instanceof IfStatement)
                        codeGenShallowIfStatement((IfStatement)stat, methodName, method, modName, modWriter);
                else if (stat instanceof ForeverStatement)
                        codeGenShallowForeverLoop((ForeverStatement)stat, methodName, method, modName, modWriter);
                else if (stat instanceof ForStatement)
                        codeGenShallowForLoop((ForStatement)stat, methodName, method, modName, modWriter);
                else if (stat instanceof RepeatStatement)
                        codeGenShallowRepeatLoop((RepeatStatement)stat, methodName, method, modName, modWriter);
                else if (stat instanceof WhileStatement)
                        codeGenShallowWhileLoop((WhileStatement)stat, methodName, method, modName, modWriter);
                else if (stat instanceof TaskStatement)
                        codeGenShallowTaskCall((TaskStatement)stat, method, modName, modWriter);
                else if (stat instanceof SeqBlockStatement)
                        codeGenShallowBlockOfStatements((SeqBlockStatement)stat, methodName, method, modName, modWriter);
                else {
                        Utils.errorAndExit("Error: Invalid Statement Node Found");
                }
        }

        private void codeGenCaseShallowStatement(CaseStatement stat, String methodName, MethodVisitor method, String modName, ClassWriter module) throws Exception{
                if (stat instanceof CaseXStatement)
                        codeGenShallowCaseXStatement((CaseXStatement)stat, methodName, method, modName, module);
                else if (stat instanceof CaseZStatement)
                        codeGenCaseZStatement((CaseZStatement)stat, methodName, method, modName, module);
                else {
                        codeGenShallowExpression(stat.exp, method, modName, module);
                        method.visitVarInsn(Opcodes.ASTORE, 0);

                        for (CaseItem statement : stat.itemList) {
                                if (statement instanceof ExprCaseItem) {
                                        ExprCaseItem item = (ExprCaseItem)statement;
                                        Label equalLabel = new Label();
                                        Label endStatLabel = new Label();

                                        for (Expression exp : item.expList) {
                                                        codeGenShallowExpression(exp, method, modName, module);
                                                        method.visitVarInsn(Opcodes.ALOAD, 0);
                                                        method.visitMethodInsn(Opcodes.INVOKESTATIC,
                      "ede/stl/common/Utils", // Internal name of the class
                      "caseBoolean",           // Name of the static method
                      "(Lede/stl/values/Value;Lede/stl/values/Value;)B",                        // Method descriptor (void return, no args)
                      false);
                                                        method.visitJumpInsn(Opcodes.IFNE, equalLabel);
                                        }

                                        method.visitJumpInsn(Opcodes.GOTO, endStatLabel);
                                        method.visitLabel(equalLabel);
                                        codeGenShallowStatement(item.statement, methodName, method, modName, module);
                                        method.visitLabel(endStatLabel);
                                } else {
                                        codeGenShallowStatement(statement.statement, methodName, method, modName, module);
                                }
                        }

                }
        }
        
        private void codeGenShallowCaseXStatement(CaseXStatement stat, String methodName, MethodVisitor method, String modName, ClassWriter module) throws Exception{
                codeGenShallowExpression(stat.exp, method, modName, module);
                method.visitVarInsn(Opcodes.ASTORE, 0);
                
                for(CaseItem statement: stat.itemList) {
                        if(statement instanceof ExprCaseItem) {
                                ExprCaseItem item = (ExprCaseItem)statement;
                                
                                Label equalLabel = new Label();
                                Label endStatLabel = new Label();

                                for (Expression exp : item.expList) {
                                                codeGenShallowExpression(exp, method, modName, module);
                                                method.visitVarInsn(Opcodes.ALOAD, 0);
                                                method.visitMethodInsn(Opcodes.INVOKESTATIC,
              "ede/stl/common/Utils", // Internal name of the class
              "caseBoolean",           // Name of the static method
              "(Lede/stl/values/Value;Lede/stl/values/Value;)B",                        // Method descriptor (void return, no args)
              false);
                                                method.visitJumpInsn(Opcodes.IFNE, equalLabel);
                                }
                                
                                method.visitJumpInsn(Opcodes.GOTO, endStatLabel);
                                method.visitLabel(equalLabel);
                                codeGenShallowStatement(item.statement, methodName, method, modName, module);
                                method.visitLabel(endStatLabel);
                        } else {
                                codeGenShallowStatement(statement.statement, methodName, method, modName, module);
                        }
                }
        }
        
        private void codeGenCaseZStatement(CaseZStatement stat, String methodName, MethodVisitor method, String modName, ClassWriter module) throws Exception {
                codeGenShallowExpression(stat.exp, method, modName, module);
                method.visitVarInsn(Opcodes.ASTORE, 0);
                
                for(CaseItem statement: stat.itemList) {
                        if(statement instanceof ExprCaseItem) {
                                ExprCaseItem item = (ExprCaseItem)statement;
                                
                                Label equalLabel = new Label();
                                Label endStatLabel = new Label();

                                for (Expression exp : item.expList) {
                                                codeGenShallowExpression(exp, method, modName, module);
                                                method.visitVarInsn(Opcodes.ALOAD, 0);
                                                method.visitMethodInsn(Opcodes.INVOKESTATIC,
              "ede/stl/common/Utils", // Internal name of the class
              "caseBoolean",           // Name of the static method
              "(Lede/stl/values/Value;Lede/stl/values/Value;)B",                        // Method descriptor (void return, no args)
              false);
                                                method.visitJumpInsn(Opcodes.IFNE, equalLabel);
                                }
                                
                                method.visitJumpInsn(Opcodes.GOTO, endStatLabel);
                                method.visitLabel(equalLabel);
                                codeGenShallowStatement(item.statement, methodName, method, modName, module);
                                method.visitLabel(endStatLabel);
                        } else {
                                codeGenShallowStatement(statement.statement, methodName, method, modName, module);
                        }
                }
        }
        
        private void codeGenShallowIfStatement(IfStatement stat, String methodName, MethodVisitor method, String modName, ClassWriter module) throws Exception{
                if(stat instanceof IfElseStatement) {
                        codeGenShallowIfElseStatement((IfElseStatement)stat, methodName, method, modName, module);
                } else {
                        codeGenShallowExpression(stat.condition, method, modName, module);
                        Label endLabel = new Label();
                        method.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
              "ede/stl/values/Value", // Internal name of the class
              "boolValue",           // Name of the static method
              "(Lede/stl/values/Value;)B",                        // Method descriptor (void return, no args)
              false);
                        method.visitJumpInsn(Opcodes.IFEQ, endLabel);
                        codeGenShallowStatement(stat.trueStatement, methodName, method, modName, module);
                        method.visitLabel(endLabel);
                }
        }
        
        private void codeGenShallowIfElseStatement(IfElseStatement stat, String methodName, MethodVisitor method, String modName, ClassWriter module) throws Exception {
                codeGenShallowExpression(stat.condition, method, modName, module);
                Label endLabel = new Label();
                Label elseLabel = new Label();
                method.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
      "ede/stl/values/Value", // Internal name of the class
      "boolValue",           // Name of the static method
      "(Lede/stl/values/Value;)B",                        // Method descriptor (void return, no args)
      false);
                method.visitJumpInsn(Opcodes.IFEQ, elseLabel);
                codeGenShallowStatement(stat.trueStatement, methodName, method, modName, module);
                method.visitJumpInsn(Opcodes.GOTO, endLabel);
                method.visitLabel(elseLabel);
                codeGenShallowStatement(stat.falseStatement, methodName, method, modName, module);
                method.visitLabel(endLabel);
        }
        
        private void codeGenShallowForeverLoop(ForeverStatement loop, String methodName, MethodVisitor method, String modName, ClassWriter writer) throws Exception {
                Label begin = new Label();
                method.visitLabel(begin);
                codeGenShallowStatement(loop.stat, methodName, method, modName, writer);
                method.visitJumpInsn(Opcodes.GOTO, begin);
        }
        
        private void codeGenShallowForLoop(ForStatement stat, String methodName, MethodVisitor method, String modName, ClassWriter module) throws Exception{
                codeGenShallowStatement(stat.init, methodName, method, modName, module);
                
                Label loopBegin = new Label();
                method.visitJumpInsn(Opcodes.GOTO, loopBegin);
                
                Label loopBody = new Label();
                method.visitLabel(loopBody);
                codeGenShallowStatement(stat.stat, methodName, method, modName, module);
                codeGenShallowStatement(stat.change, methodName, method, modName, module);
                
                method.visitLabel(loopBegin);
                codeGenShallowExpression(stat.exp, method, modName, module);
                method.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
      "ede/stl/values/Value", // Internal name of the class
      "boolValue",           // Name of the static method
      "(Lede/stl/values/Value;)B",                        // Method descriptor (void return, no args)
      false);
                method.visitJumpInsn(Opcodes.IFNE, loopBody);
        }
        
        private void codeGenShallowRepeatLoop(RepeatStatement loop, String methodName, MethodVisitor method, String modName, ClassWriter module) throws Exception{
                 pushInt(0, method);
                 method.visitVarInsn(Opcodes.ISTORE, 0);
                 codeGenShallowExpression(loop.exp, method, modName, module);
                 method.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
              "ede/stl/values/Value", // Internal name of the class
              "intValue",           // Name of the static method
              "(Lede/stl/values/Value;)I",                        // Method descriptor (void return, no args)
              false);
                 method.visitVarInsn(Opcodes.ISTORE, 1);
                 
                 Label loopBegin = new Label();
                 method.visitJumpInsn(Opcodes.GOTO, loopBegin);
                 
                 Label loopBody = new Label();
                 method.visitLabel(loopBody);
                 codeGenShallowStatement(loop.stat, methodName, method, modName, module);
                 method.visitIincInsn(0, 1);
                 
                 method.visitLabel(loopBegin);
                 method.visitVarInsn(Opcodes.ILOAD, 0);
                 method.visitVarInsn(Opcodes.ILOAD, 1);
                 method.visitJumpInsn(Opcodes.IF_ICMPLT, loopBody);
        }
        
        private void codeGenShallowWhileLoop(WhileStatement loop, String methodName, MethodVisitor method, String modName, ClassWriter module) throws Exception {
                Label loopBegin = new Label();
                method.visitJumpInsn(Opcodes.GOTO, loopBegin);
                
                Label loopBody = new Label();
                method.visitLabel(loopBody);
                codeGenShallowStatement(loop.stat, methodName, method, modName, module);
                
                method.visitLabel(loopBegin);
                codeGenShallowExpression(loop.exp, method, modName, module);
                method.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
      "ede/stl/values/Value", // Internal name of the class
      "boolValue",           // Name of the static method
      "(Lede/stl/values/Value;)B",                        // Method descriptor (void return, no args)
      false);
                method.visitJumpInsn(Opcodes.IFNE, loopBody);
        }

        private void codeGenShallowAssignment(Assignment assign, String methodName, MethodVisitor method, String moduleName, ClassWriter module) throws Exception {
                if(assign instanceof BlockingAssignment) codeGenShallowBlockingAssign((BlockingAssignment)assign, methodName, method, moduleName, module);
                else if(assign instanceof NonBlockingAssignment) codeGenShallowNonBlockingAssign((NonBlockingAssignment)assign, method, moduleName, module);
                else Utils.errorAndExit("Invalid Assignment found");
        }
        
        private void codeGenShallowBlockingAssign(BlockingAssignment assign, String funcName, MethodVisitor method, String modName, ClassWriter module) throws Exception {
                 codeGenShallowExpression(assign.rightHandSide, method, modName, module);
                 if(assign.leftHandSide instanceof Element) {
                          Element leftHandElement = (Element)assign.leftHandSide;
                          codeGenShallowExpression(leftHandElement.index1, method, modName, module);
                          if(this.localInScope(leftHandElement.labelIdentifier)){
                                int ptr = this.getFromScope(leftHandElement.labelIdentifier);
                                  method.visitVarInsn(Opcodes.ALOAD, ptr);
                          } else if(this.fieldInScope(leftHandElement.labelIdentifier)) {
                                method.visitFieldInsn(Opcodes.GETFIELD, 
                        modName, // Owner class internal name
                        leftHandElement.labelIdentifier,           // Field name
                        "Lede/stl/values/Value;");
                          } else {
                                Utils.errorAndExit("Error can not find left hand side of assignment" + leftHandElement.labelIdentifier);
                          }
                          method.visitMethodInsn(Opcodes.INVOKESTATIC,
          "ede/stl/common/Utils", // Internal name of the class
          "shallowAssignElem",           // Name of the static method
          "(Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;)V",                        // Method descriptor (void return, no args)
          false);      
                 } else if(assign.leftHandSide instanceof Slice) {
                         Slice slice = (Slice)assign.leftHandSide;
                         codeGenShallowExpression(slice.index2, method, modName, module);
                         codeGenShallowExpression(slice.index1, method, modName, module);
                         
                         if(this.localInScope(slice.labelIdentifier)) {
                                 int ptr = this.getFromScope(slice.labelIdentifier);
                                 method.visitVarInsn(Opcodes.ALOAD, ptr);
                         } else if(this.fieldInScope(slice.labelIdentifier)) {
                                 method.visitFieldInsn(Opcodes.GETFIELD, 
                        modName, // Owner class internal name
                        slice.labelIdentifier,           // Field name
                        "Lede/stl/values/Value;");
                         } else {
                                 Utils.errorAndExit("Error cant find ident " + slice.labelIdentifier);
                         }
                         method.visitMethodInsn(Opcodes.INVOKESTATIC,
         "ede/stl/common/Utils", // Internal name of the class
         "shallowAssignSlice",           // Name of the static method
         "(Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;)V",                        // Method descriptor (void return, no args)
         false);      
                 } else if(assign.leftHandSide instanceof Identifier) {
                         Identifier ident = (Identifier)assign.leftHandSide;
                         if(ident.labelIdentifier.equals(funcName)) {
                                 method.visitInsn(Opcodes.RETURN);
                         } else {
                                 if(localInScope(ident.labelIdentifier)) {
                                         int ptr = this.getFromScope(ident.labelIdentifier);
                                         method.visitVarInsn(Opcodes.ASTORE, ptr);
                                 } else if(fieldInScope(ident.labelIdentifier)){
                                         method.visitFieldInsn(Opcodes.PUTFIELD, 
             "ede/stl/values/Value", // internal name of the owner class
             ident.labelIdentifier,                   // field name
             "Lede/stl/values/Value;");
                                 } else {
                                         Utils.errorAndExit("Error cant find left hand side of assign " + ident);
                                 }
                         }
                 } else {
                         Utils.errorAndExit("Error invalid type for LValue in assignment");
                 }
        }
        
        private void codeGenShallowNonBlockingAssign(NonBlockingAssignment assign, MethodVisitor method, String modName, ClassWriter module) throws Exception {
                for(Expression exp: assign.rightHandSide){
                        codeGenShallowExpression(exp, method, modName, module);
                }
                
                for(int i = assign.leftHandSide.size(); i >= 0; i--){
                        LValue val = assign.leftHandSide.get(i);
                        if(val instanceof Element) {
                          Element leftHandElement = (Element)val;
                          codeGenShallowExpression(leftHandElement.index1, method, modName, module);
                          if(localInScope(leftHandElement.labelIdentifier)) {
                                int ptr = this.getFromScope(leftHandElement.labelIdentifier);
                                  method.visitVarInsn(Opcodes.ALOAD, ptr);
                          } else if(fieldInScope(leftHandElement.labelIdentifier)) {
                                method.visitFieldInsn(Opcodes.GETFIELD, 
                        modName, // Owner class internal name
                        leftHandElement.labelIdentifier,           // Field name
                        "Lede/stl/values/Value;");
                          } else {
                                Utils.errorAndExit("Error ident" + leftHandElement.labelIdentifier + " doesnt exist in module " + modName);
                          }
                          method.visitMethodInsn(Opcodes.INVOKESTATIC,
          "ede/stl/common/Utils", // Internal name of the class
          "shallowAssignElem",           // Name of the static method
          "(Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;)V",                        // Method descriptor (void return, no args)
          false);      
                 } else if(val instanceof Slice) {
                         Slice slice = (Slice)val;
                         codeGenShallowExpression(slice.index2, method, modName, module);
                         codeGenShallowExpression(slice.index1, method, modName, module);
                         if(localInScope(slice.labelIdentifier)) {
                                 int ptr = this.getFromScope(slice.labelIdentifier);
                                 method.visitVarInsn(Opcodes.ALOAD, ptr);
                         } else if(fieldInScope(slice.labelIdentifier)) {
                                 method.visitFieldInsn(Opcodes.GETFIELD, 
                        modName, // Owner class internal name
                        slice.labelIdentifier,           // Field name
                        "Lede/stl/values/Value;");
                         } else {
                                 Utils.errorAndExit("Error ident " + slice.labelIdentifier + " not found in module " + modName);
                         }
                         method.visitMethodInsn(Opcodes.INVOKESTATIC,
         "ede/stl/common/Utils", // Internal name of the class
         "shallowAssignSlice",           // Name of the static method
         "(Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;)V",                        // Method descriptor (void return, no args)
         false);      
                 } else if(val instanceof Identifier) {
                         Identifier ident = (Identifier)val;
                         if(localInScope(ident.labelIdentifier)){
                                 int ptr = this.getFromScope(ident.labelIdentifier);
                                 method.visitVarInsn(Opcodes.ASTORE, ptr);
                         } else if(fieldInScope(ident.labelIdentifier)) {
                                 method.visitFieldInsn(Opcodes.PUTFIELD, modName, ident.labelIdentifier, "Lede/stl/values/Value;");
                         } else {
                                 Utils.errorAndExit("Error field or local variable for ident " + ident + " was not found!!!");
                         }
                 } else {
                         Utils.errorAndExit("Error invalid type for LValue in assignment");
                 }
                }
        }
        
        private void codeGenShallowTaskCall(TaskStatement stat, MethodVisitor method, String modName, ClassWriter module) throws Exception {
                if(stat instanceof SystemTaskStatement) {
                        codeGenShallowSystemTaskCall((SystemTaskStatement)stat, method, modName, module);
                } else {
                        String typeStr = funcTypes.getEntry(stat.taskName);
                        for(Expression exp: stat.argumentList) {
                                codeGenShallowExpression(exp, method, modName, module);
                        }
                        method.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
					       modName, // Internal name of the class
					       stat.taskName,           // Name of the static method
					       typeStr,                        // Method descriptor (void return, no args)
					       false);
                }
        }
        
        private void codeGenShallowSystemTaskCall(SystemTaskStatement stat, MethodVisitor method, String modName, ClassWriter module) {
                if(stat.taskName.equals("fclose")){
                        
                }
        }
        
        private void codeGenShallowBlockOfStatements(SeqBlockStatement stat, String methodName, MethodVisitor method, String modName, ClassWriter module) throws Exception{
                for(Statement myStat: stat.statementList){
                        codeGenShallowStatement(myStat, methodName, method, modName, module);
                }
        }

        private void codeGenField(ModuleItem declaration, MethodVisitor constructor, String modName, ClassWriter moduleWriter)
                throws Exception{
                if (declaration instanceof ArrayDeclaration)
                        codeGenFieldArray((ArrayDeclaration)declaration, constructor, modName, moduleWriter);
                else if (declaration instanceof Input.Wire.Vector.Ident)
                        codeGenFieldInputWireVectorIdent((Input.Wire.Vector.Ident)declaration, constructor, modName, moduleWriter);
                else if (declaration instanceof Input.Reg.Vector.Ident)
                        codeGenFieldInputRegVectorIdent((Input.Reg.Vector.Ident)declaration, constructor, modName, moduleWriter);
                else if (declaration instanceof Input.Wire.Scalar.Ident)
                        codeGenFieldInputWireScalarIdent((Input.Wire.Scalar.Ident)declaration, constructor, modName, moduleWriter);
                else if (declaration instanceof Input.Reg.Scalar.Ident)
                        codeGenFieldInputRegScalarIdent((Input.Reg.Scalar.Ident)declaration, constructor, modName, moduleWriter);
                else if (declaration instanceof Output.Wire.Vector.Ident)
                        codeGenFieldOutputWireVectorIdent((Output.Wire.Vector.Ident)declaration, constructor, modName, moduleWriter);
                else if (declaration instanceof Output.Reg.Vector.Ident)
                        codeGenFieldOutputRegVectorIdent((Output.Reg.Vector.Ident)declaration, constructor, modName, moduleWriter);
                else if (declaration instanceof Output.Wire.Scalar.Ident)
                        codeGenFieldOutputWireScalarIdent((Output.Wire.Scalar.Ident)declaration, constructor, modName, moduleWriter);
                else if (declaration instanceof Output.Reg.Scalar.Ident)
                        codeGenFieldOutputRegScalarIdent((Output.Reg.Scalar.Ident)declaration, constructor, modName, moduleWriter);
                else if (declaration instanceof Wire.Vector.Ident)
                        codeGenFieldWireVectorIdent((Wire.Vector.Ident)declaration, constructor, modName, moduleWriter);
                else if (declaration instanceof Reg.Vector.Ident)
                        codeGenFieldRegVectorIdent((Reg.Vector.Ident)declaration, constructor, modName, moduleWriter);
                else if (declaration instanceof Wire.Scalar.Ident)
                        codeGenFieldWireScalarIdent((Wire.Scalar.Ident)declaration, constructor, modName, moduleWriter);
                else if (declaration instanceof Reg.Scalar.Ident)
                        codeGenFieldRegScalarIdent((Reg.Scalar.Ident)declaration, constructor, modName, moduleWriter);
                else if (declaration instanceof Int.Ident)
                        codeGenFieldIntIdent((Int.Ident)declaration, constructor, modName, moduleWriter);
                else if (declaration instanceof Real.Ident)
                        codeGenFieldRealIdent((Real.Ident)declaration, constructor, modName, moduleWriter);
        }

        private void codeGenFieldArray(ArrayDeclaration declaration, MethodVisitor constructor, String modName, ClassWriter modWriter) throws Exception{
                if (declaration instanceof Reg.Scalar.Array)
                        codeGenFieldRegScalarArray((Reg.Scalar.Array)declaration, constructor, modName, modWriter);
                else if (declaration instanceof Reg.Vector.Array)
                        codeGenFieldRegVectorArray((Reg.Vector.Array)declaration, constructor, modName, modWriter);
                else if (declaration instanceof Int.Array)
                        codeGenFieldIntArray((Int.Array)declaration, constructor, modName, modWriter);
                else {
                        Utils.errorAndExit("No Array Type found of type " + declaration.getClass().getName());
                }
        }

        private void codeGenFieldRegScalarArray(Reg.Scalar.Array arr, MethodVisitor constructor, String modName, ClassWriter modWriter) throws Exception{

                if (arr.annotationLexeme != "@Memory") {
                        modWriter.visitField(Opcodes.ACC_PRIVATE, arr.declarationIdentifier, "Lede/stl/values/ArrayRegVal;", null, null);
                        
                        codeGenShallowExpression(arr.arrayIndex1, constructor, modName, modWriter);
                        codeGenShallowExpression(arr.arrayIndex2, constructor, modName, modWriter);

                        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/ArrayRegVal", "<init>", "(Lede/stl/values/Value;Lede/stl/values/Value;)V", false);
                        constructor.visitFieldInsn(Opcodes.PUTFIELD, "ede/stl/values/ArrayRegVal", arr.declarationIdentifier, "Lede/stl/values/ArrayRegVal;"); // Store in field
                        
                }

        }

        private void codeGenFieldRegVectorArray(Reg.Vector.Array arr, MethodVisitor constructor, String modName, ClassWriter modWriter) throws Exception{

                if (arr.annotationLexeme != "@Memory") {
                        modWriter.visitField(Opcodes.ACC_PRIVATE, arr.declarationIdentifier, "Lede/stl/values/ArrayVectorVal;", null, null);

                        codeGenShallowExpression(arr.arrayIndex1, constructor, modName, modWriter);
                        codeGenShallowExpression(arr.arrayIndex2, constructor, modName, modWriter);

                        codeGenShallowExpression(arr.GetIndex1(), constructor, modName, modWriter);
                        codeGenShallowExpression(arr.GetIndex2(), constructor, modName, modWriter);

                        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/ArrayVectorVal", "<init>", "(Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;)V", false);
                        constructor.visitFieldInsn(Opcodes.PUTFIELD, "ede/stl/values/ArrayVectorVal", arr.declarationIdentifier, "Lede/stl/values/ArrayVectorVal;");
                }

        }

        private void codeGenFieldIntArray(Int.Array arr, MethodVisitor constructor, String modName, ClassWriter modWriter) throws Exception{
                modWriter.visitField(Opcodes.ACC_PRIVATE, arr.declarationIdentifier, "Lede/stl/values/ArrayIntVal;", null, null);

                codeGenShallowExpression(arr.arrayIndex1, constructor, modName, modWriter);
                constructor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "ede/stl/values/Value", "intValue", "(Lede/stl/values/Value;)I", false);
                codeGenShallowExpression(arr.arrayIndex2, constructor, modName, modWriter);
                constructor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "ede/stl/values/Value", "intValue", "(Lede/stl/values/Value;)I", false);
                
                constructor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "abs", "(II)I", false);

                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/ArrayIntVal", "<init>", "(I)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, "ede/stl/values/ArrayIntVal", arr.declarationIdentifier, "Lede/stl/values/ArrayIntVal;"); // Store in
        }

        private void codeGenFieldInputWireVectorIdent(Input.Wire.Vector.Ident declaration, MethodVisitor constructor, String modName, ClassWriter modWriter) throws Exception{
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/VectorVal;", null, null);

                codeGenShallowExpression(declaration.GetIndex1(), constructor, modName, modWriter);
                constructor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "ede/stl/values/Value", "intValue", "(Lede/stl/values/Value;)I", false);
                codeGenShallowExpression(declaration.GetIndex2(), constructor, modName, modWriter);
                constructor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "ede/stl/values/Value", "intValue", "(Lede/stl/values/Value;)I", false);
                
                constructor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "abs", "(II)I", false);

                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/VectorVal", "<init>", "(II)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, "ede/stl/values/VectorVal", declaration.declarationIdentifier, "Lede/stl/values/VectorVal;");
        }

        private void codeGenFieldInputRegVectorIdent(Input.Reg.Vector.Ident declaration, MethodVisitor constructor, String modName, ClassWriter modWriter) throws Exception{
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/VectorVal;", null, null);

                codeGenShallowExpression(declaration.GetIndex1(), constructor, modName, modWriter);
                codeGenShallowExpression(declaration.GetIndex2(), constructor, modName, modWriter);

                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/VectorVal", "<init>", "(II)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, "ede/stl/values/VectorVal", declaration.declarationIdentifier, "Lede/stl/values/VectorVal;");
        }

        private void codeGenFieldInputWireScalarIdent(Input.Wire.Scalar.Ident declaration, MethodVisitor constructor, String modName, ClassWriter modWriter){
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/circuit/WireVal;", null, null);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/WireVal", "<init>", "()V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, "ede/stl/circuit/WireVal", declaration.declarationIdentifier, "Lede/stl/circuit/WireVal;");
        }

        private void codeGenFieldInputRegScalarIdent(Input.Reg.Scalar.Ident declaration, MethodVisitor constructor, String modName, ClassWriter modWriter){
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/RegVal;", null, null);
                pushBool(false, constructor);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/RegVal", "<init>", "(B)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, "ede/stl/values/RegVal", declaration.declarationIdentifier, "Lede/stl/values/RegVal;");
        }

        private void codeGenFieldOutputWireVectorIdent(Output.Wire.Vector.Ident declaration, MethodVisitor constructor, String modName, ClassWriter modWriter) throws Exception{
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/VectorVal;", null, null);

    codeGenShallowExpression(declaration.GetIndex1(), constructor, modName, modWriter);
    constructor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "ede/stl/values/Value", "intValue", "(Lede/stl/values/Value;)I", false);
                codeGenShallowExpression(declaration.GetIndex2(), constructor, modName, modWriter);
                constructor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "ede/stl/values/Value", "intValue", "(Lede/stl/values/Value;)I", false);

                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/VectorVal", "<init>", "(II)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, "ede/stl/values/VectorVal", declaration.declarationIdentifier, "Lede/stl/values/VectorVal;");
        }

        private void codeGenFieldOutputRegVectorIdent(Output.Reg.Vector.Ident declaration, MethodVisitor constructor, String moduleName, ClassWriter modWriter) throws Exception{
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/VectorVal;", null, null);

                codeGenShallowExpression(declaration.GetIndex1(), constructor, moduleName, modWriter);
                constructor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "ede/stl/values/Value", "intValue", "(Lede/stl/values/Value;)I", false);
                codeGenShallowExpression(declaration.GetIndex2(), constructor, moduleName, modWriter);
                constructor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "ede/stl/values/Value", "intValue", "(Lede/stl/values/Value;)I", false);

                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/VectorVal", "<init>", "(II)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, "ede/stl/values/VectorVal", declaration.declarationIdentifier, "Lede/stl/values/VectorVal;");
        }

        private void codeGenFieldOutputWireScalarIdent(Output.Wire.Scalar.Ident declaration, MethodVisitor constructor, String modName, ClassWriter modWriter){
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/circuit/WireVal;", null, null);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/WireVal", "<init>", "()V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, "ede/stl/circuit/WireVal", declaration.declarationIdentifier, "Lede/stl/circuit/WireVal;");
        }

        private void codeGenFieldOutputRegScalarIdent(Output.Reg.Scalar.Ident declaration, MethodVisitor constructor, String modName, ClassWriter modWriter){
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/RegVal;", null, null);
                pushBool(false, constructor);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/RegVal", "<init>", "(B)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, "ede/stl/values/RegVal", declaration.declarationIdentifier, "Lede/stl/values/RegVal;");
        }

        private void codeGenFieldWireVectorIdent(Wire.Vector.Ident declaration, MethodVisitor constructor, String moduleName, ClassWriter modWriter)
                throws Exception{
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/VectorVal;", null, null);

                codeGenShallowExpression(declaration.GetIndex1(), constructor, moduleName, modWriter);
                constructor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "ede/stl/values/Value", "intValue", "(Lede/stl/values/Value;)I", false);
                codeGenShallowExpression(declaration.GetIndex2(), constructor, moduleName, modWriter);
                constructor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "ede/stl/values/Value", "intValue", "(Lede/stl/values/Value;)I", false);

                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/VectorVal", "<init>", "(II)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, "ede/stl/values/VectorVal", declaration.declarationIdentifier, "Lede/stl/values/VectorVal;");
        }

        private void codeGenFieldRegVectorIdent(Reg.Vector.Ident declaration, MethodVisitor constructor, String moduleName, ClassWriter modWriter)
                throws Exception{
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/VectorVal;", null, null);

                codeGenShallowExpression(declaration.GetIndex1(), constructor, moduleName, modWriter);
                constructor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "ede/stl/values/Value", "intValue", "(Lede/stl/values/Value;)I", false);
                codeGenShallowExpression(declaration.GetIndex2(), constructor, moduleName, modWriter);
                constructor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "ede/stl/values/Value", "intValue", "(Lede/stl/values/Value;)I", false);

                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/VectorVal", "<init>", "(II)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, "ede/stl/values/VectorVal", declaration.declarationIdentifier, "Lede/stl/values/VectorVal;");
        }

        private void codeGenFieldWireScalarIdent(Wire.Scalar.Ident declaration, MethodVisitor constructor, String modName, ClassWriter modWriter){
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/circuit/WireVal;", null, null);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/WireVal", "<init>", "()V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, "ede/stl/circuit/WireVal", declaration.declarationIdentifier, "Lede/stl/circuit/WireVal;");
        }

        private void codeGenFieldRegScalarIdent(Reg.Scalar.Ident declaration, MethodVisitor constructor, String modName, ClassWriter modWriter){
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/RegVal;", null, null);
                pushBool(false, constructor);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/RegVal", "<init>", "(B)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, "ede/stl/values/RegVal", declaration.declarationIdentifier, "Lede/stl/values/RegVal;");
        }

        private void codeGenFieldIntIdent(Int.Ident declaration, MethodVisitor constructor, String modName, ClassWriter modWriter){
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/IntVal;", null, null);
                pushInt(0, constructor);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/IntVal", "<init>", "(I)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, "ede/stl/values/IntVal", declaration.declarationIdentifier, "Lede/stl/values/IntVal;");
        }

        private void codeGenFieldRealIdent(Real.Ident declaration, MethodVisitor constructor, String modName, ClassWriter modWriter){
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/RealVal;", null, null);
                pushDouble(0.0, constructor);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/RealVal", "<init>", "(D)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, "ede/stl/values/RealVal", declaration.declarationIdentifier, "Lede/stl/values/RealVal;");
        }
        
        private void codeGenShallowExpression(Expression exp, MethodVisitor method, String moduleName, ClassWriter module) throws Exception {
                if (exp instanceof BinaryOperation)
                        codeGenShallowBinaryOperation((BinaryOperation)exp, method, moduleName, module);
                else if (exp instanceof UnaryOperation)
                        codeGenShallowUnaryOperation((UnaryOperation)exp, method, moduleName, module);
                else if (exp instanceof Concatenation)
                        codeGenShallowConcatenation((Concatenation)exp, method, moduleName, module);
                else if (exp instanceof FunctionCall)
                        codeGenShallowFunctionCall((FunctionCall)exp, method, moduleName, module);
                else if (exp instanceof TernaryOperation)
                        codeGenShallowTernaryOperation((TernaryOperation)exp, method, moduleName, module);
                else if (exp instanceof BinaryNode)
                        codeGenShallowBinaryNode((BinaryNode)exp, method, moduleName, module);
                else if (exp instanceof DecimalNode)
                        codeGenShallowDecimalNode((DecimalNode)exp, method, moduleName, module);
                else if (exp instanceof HexadecimalNode)
                        codeGenShallowHexadecimalNode((HexadecimalNode)exp, method, moduleName, module);
                else if (exp instanceof OctalNode)
                        codeGenShallowOctalNode((OctalNode)exp, method, moduleName, module);
                else if (exp instanceof StringNode)
                        codeGenShallowStringNode((StringNode)exp, method, moduleName, module);
                else if (exp instanceof ConstantExpression)
                        codeGenShallowConstantExpression((ConstantExpression)exp, method, moduleName, module);
                else if (exp instanceof Slice)
                        codeGenShallowSlice((Slice)exp, method, moduleName, module);
                else if (exp instanceof Element)
                        codeGenShallowElement((Element)exp, method, moduleName, module);
                else if (exp instanceof Identifier)
                        codeGenShallowIdentifier((Identifier)exp, method, moduleName, module);
                else {
                        Utils.errorAndExit("Error: Could not find an expression of type" + exp.getClass().getName());
                }
        }
        
        private void codeGenShallowBinaryOperation(BinaryOperation op, MethodVisitor method, String moduleName, ClassWriter module) throws Exception {
                codeGenShallowExpression(op.left, method, moduleName, module);
                codeGenShallowExpression(op.right, method, moduleName, module);
                
                switch(op.Op) {
                        case BAND:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "add", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case BOR:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "bitwiseOr", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case BXNOR:{
                                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "exclusiveNor", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                        break;
                        }
                        case BXOR:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "exclusiveOr", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case DIV:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "div", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case EQ2:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "lazyEquality", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case EQ3:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "strictEquality", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case GE:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "greaterThanOrEqualTo", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case GT:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "greaterThan", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case LAND:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "logicalAnd", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case LE:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "lessThenOrEqualTo", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case LOR:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "lessThenOrEqualTo", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case LSHIFT:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "leftShift", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case LT:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "lessThan", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case MINUS:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "minus", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case MOD:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "mod", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case NE1:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "lazyInequality", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case NE2:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "strictInequality", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case PLUS:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "add", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case RSHIFT:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "rightShift", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case TIMES:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "times", "(Lede/stl/values/Value;Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        default:
                                Utils.errorAndExit("Invalid operation found!!!");
                                break;
                }
        }
        
        private void codeGenShallowUnaryOperation(UnaryOperation op, MethodVisitor method, String moduleName, ClassWriter module) throws Exception{
                codeGenShallowExpression(op.rightHandSideExpression, method, moduleName, module);
                
                switch(op.Op){
                        case BNEG:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "bitwiseNegation", "(Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case LNEG:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "logicalNegation", "(Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case MINUS:{
                                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "negation", "(Lede/stl/values/Value;)Lede/stl/values/Value;", false);
                                break;
                        }
                        case PLUS:{
                                break;
                        }
                        default:
                                Utils.errorAndExit("Error invalid unary type");
                                break;
                }
        }
        
        private void codeGenShallowConcatenation(Concatenation concat, MethodVisitor method, String moduleName, ClassWriter module) throws Exception {
                pushInt(0, method);
                method.visitVarInsn(Opcodes.ISTORE, 0);
                
                for(Expression exp: concat.circuitElementExpressionList) {
                        codeGenShallowExpression(exp, method, moduleName, module);
                        method.visitVarInsn(Opcodes.ILOAD, 0);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "addVecSize", "(Lede/stl/values/Value;I)I", false);
                        method.visitVarInsn(Opcodes.ISTORE, 0);
                }
                
                method.visitIincInsn(0, -1);
                method.visitVarInsn(Opcodes.ILOAD, 0);
                method.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/VectorVal", "<Init>", "(Lede/stl/values/VectorVal;I)V", false);
                
                for(Expression exp: concat.circuitElementExpressionList) {
                        method.visitInsn(Opcodes.DUP);
                        codeGenShallowExpression(exp, method, moduleName, module);
                        method.visitVarInsn(Opcodes.ILOAD, 0);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "assignVectorInConcatenation", "(Lede/stl/values/VectorVal;Lede/stl/values/Value;I)I", false);
                        method.visitVarInsn(Opcodes.ISTORE, 0);
                }
        }
        
        private void codeGenShallowFunctionCall(FunctionCall call, MethodVisitor method, String moduleName, ClassWriter module) throws Exception{
                for(Expression exp: call.argumentList){
                        codeGenShallowExpression(exp, method, moduleName, module);
                }
                
                method.visitMethodInsn(Opcodes.INVOKEVIRTUAL, moduleName, call.functionName, this.funcTypes.getEntry(call.functionName), false);
        }
        
        private void codeGenShallowTernaryOperation(TernaryOperation call, MethodVisitor method, String moduleName, ClassWriter module) throws Exception {
                codeGenShallowExpression(call.condition, method, moduleName, module);
                Label elseLabel = new Label();
                Label endLabel = new Label();
                method.visitJumpInsn(Opcodes.IFEQ, elseLabel);
                codeGenShallowExpression(call.ifTrue, method, moduleName, module);
                method.visitJumpInsn(Opcodes.GOTO, endLabel);
                method.visitLabel(elseLabel);
                codeGenShallowExpression(call.ifFalse, method, moduleName, module);
                method.visitLabel(endLabel);
        }
        
        private void codeGenShallowBinaryNode(BinaryNode node, MethodVisitor method, String moduleName, ClassWriter module) throws Exception {
                int indexOfColon = node.lexeme.indexOf('\'');
                
                if(indexOfColon == -1)
                        Utils.errorAndExit("Error malformed BinaryNode!!!");
                
                String beforeIndex = node.lexeme.substring(0, indexOfColon);
                String afterIndex = node.lexeme.substring(indexOfColon + 2, node.lexeme.length());
                
                if(Utils.numberIsPattern(afterIndex)) {
                        pushString(afterIndex, method);
                        method.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/BinaryPattern", "<init>", "(I)Lede/stl/values/BinaryPattern;", false);
                } else {
                        long value = Long.parseUnsignedLong(afterIndex, 2);
                        pushInt((int)value, method);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "getOptimalUnsignedForm", "(I)Lede/stl/values/Value;", false);
                }
        }
        
        private void codeGenShallowHexadecimalNode(HexadecimalNode node, MethodVisitor method, String moduleName, ClassWriter module) throws Exception {
                int indexOfColon = node.lexeme.indexOf('\'');
                
                if(indexOfColon == -1)
                        Utils.errorAndExit("Error malformed BinaryNode!!!");
                
                String beforeIndex = node.lexeme.substring(0, indexOfColon);
                String afterIndex = node.lexeme.substring(indexOfColon + 2, node.lexeme.length());
                
                if(Utils.numberIsPattern(afterIndex)) {
                        pushString(afterIndex, method);
                        method.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/HexadecimalPattern", "<init>", "(I)Lede/stl/values/HexadecimalPattern;", false);
                } else {
                        long value = Long.parseUnsignedLong(afterIndex, 16);
                        pushInt((int)value, method);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "getOptimalUnsignedForm", "(I)Lede/stl/values/Value;", false);
                }
        }
        
        private void codeGenShallowDecimalNode(DecimalNode node, MethodVisitor method, String moduleName, ClassWriter module) {
                int indexOfColon = node.lexeme.indexOf('\'');
                
                if(indexOfColon == -1) {
                        long value = Long.parseUnsignedLong(node.lexeme);
                        pushInt((int)value, method);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "getOptimalUnsignedForm", "(I)Lede/stl/values/Value;", false);
                } else {
                        String beforeIndex = node.lexeme.substring(0, indexOfColon);
                        String afterIndex = node.lexeme.substring(indexOfColon + 2, node.lexeme.length());
                        long value = Long.parseUnsignedLong(afterIndex, 10);
                        pushInt((int)value, method);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "getOptimalUnsignedForm", "(I)Lede/stl/values/Value;", false);
                }
        }
        
        private void codeGenShallowOctalNode(OctalNode node, MethodVisitor method, String moduleName, ClassWriter module) throws Exception {
                int indexOfColon = node.lexeme.indexOf('\'');
                
                if(indexOfColon == -1)
                        Utils.errorAndExit("Error malformed BinaryNode!!!");
                
                String beforeIndex = node.lexeme.substring(0, indexOfColon);
                String afterIndex = node.lexeme.substring(indexOfColon + 2, node.lexeme.length());
                
                if(Utils.numberIsPattern(afterIndex)) {
                        pushString(afterIndex, method);
                        method.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/OctalPattern", "<init>", "(I)Lede/stl/values/OctalPattern;", false);
                } else {
                        long value = Long.parseUnsignedLong(afterIndex, 8);
                        pushInt((int)value, method);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "getOptimalUnsignedForm", "(I)Lede/stl/values/Value;", false);
                }
        }
        
        private void codeGenShallowStringNode(StringNode node, MethodVisitor method, String moduleName, ClassWriter module) {
                pushString(node.lexeme, method);
                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/values/StrVal", "<Init>", "(S)Lede/stl/values/Value;", false);
        }
        
        private void codeGenShallowConstantExpression(ConstantExpression exp, MethodVisitor method, String moduleName, ClassWriter module) throws Exception {
                codeGenShallowExpression(exp.expression, method, moduleName, module);
        }
        
        private void codeGenShallowElement(Element elem, MethodVisitor method, String moduleName, ClassWriter module) throws Exception{
                codeGenShallowExpression(elem.index1, method, moduleName, module);
                if(this.localInScope(elem.labelIdentifier)) {
                        int num = this.getFromScope(elem.labelIdentifier);
                        method.visitVarInsn(Opcodes.ALOAD, num);
                } else if(this.fieldInScope(elem.labelIdentifier)){
                        method.visitFieldInsn(Opcodes.GETFIELD, 
        moduleName, // Owner class internal name
        elem.labelIdentifier,           // Field name
        "Lede/stl/values/Value;");
                } else {
                        Utils.errorAndExit("Error cant find elem from identifier " + elem.labelIdentifier);
                }
                
                pushString(elem.labelIdentifier, method);
                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "getShallowElemFromIndex", "(Lede/stl/values/Value;Lede/stl/values/Value;S)Lede/stl/values/Value;", false);
        }
        
        private void codeGenShallowSlice(Slice slice, MethodVisitor method, String moduleName, ClassWriter module) throws Exception{
                codeGenShallowExpression(slice.index1, method, moduleName, module);
                codeGenShallowExpression(slice.index2, method, moduleName, module);
                if(localInScope(slice.labelIdentifier)) {
                        int num = this.getFromScope(slice.labelIdentifier);
                        method.visitVarInsn(Opcodes.ALOAD, num);
                } else if(fieldInScope(slice.labelIdentifier)) {
                        method.visitFieldInsn(Opcodes.GETFIELD, 
        moduleName, // Owner class internal name
        slice.labelIdentifier,           // Field name
        "Lede/stl/values/Value;");
                } else {
                        Utils.errorAndExit("Error variable is not found!!!");
                }
                
                pushString(slice.labelIdentifier, method);
                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "getShallowSliceFromIndecis", "(Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;S)Lede/stl/values/Value;", false);
        }
        
        private void codeGenFileDescriptor(MethodVisitor mainVisit){
                // TODO: stub - method lost during code corruption, needs reimplementation
        }

        private void codeGenDeepExpression(Expression exp, MethodVisitor method, String modName, ClassWriter writer) throws Exception{
                codeGenShallowExpression(exp, method, modName, writer);
        }

        private void codeGenDeepStatement(Statement stat, String methodName, MethodVisitor method, String modName, ClassWriter modWriter) throws Exception{
                codeGenShallowStatement(stat, methodName, method, modName, modWriter);
        }

        private void codeGenShallowIdentifier(Identifier ident, MethodVisitor method, String moduleName, ClassWriter module) throws Exception{
                if(this.localInScope(ident.labelIdentifier)) {
                        Integer num = this.getFromScope(ident.labelIdentifier);
                        method.visitVarInsn(Opcodes.ALOAD, num);
                } else if(this.fieldInScope(ident.labelIdentifier)){
                        method.visitFieldInsn(Opcodes.GETFIELD, 
        moduleName, // Owner class internal name
        ident.labelIdentifier,           // Field name
        "Lede/stl/values/Value;");
                } else {
                        Utils.errorAndExit("Error identifier " + ident.labelIdentifier + " not found in scope!!!");
                }
        }
}
