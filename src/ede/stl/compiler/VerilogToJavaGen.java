package ede.stl.compiler;


import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Stack;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.CheckClassAdapter;
import ede.stl.common.Pointer;
import ede.stl.common.SymbolTable;
import ede.stl.common.ErrorLog;
import ede.stl.common.ErrorItem;
import ede.stl.common.Utils;
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
import ede.stl.ast.SystemFunctionCall;
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
        private int         javaVersion;
        private ErrorLog    errLog;
        private SymbolTable<Integer> scopedTable;
        private SymbolTable<String> scopedFields;
        private SymbolTable<String> funcTypes;
        private int processNumber;
        private int localAndArgNumber;
        
        public VerilogToJavaGen(int javaVersion) {
                this.javaVersion = javaVersion;
                this.errLog = new ErrorLog();
                this.processNumber = 0;
                this.localAndArgNumber = 3;
                this.scopedTable = new SymbolTable<Integer>();
                this.scopedFields = new SymbolTable<String>();
                this.funcTypes = new SymbolTable<String>();
        }
        
        protected boolean localInScope(String name){
            return scopedTable.inScope(name);
        }
        
        protected boolean fieldInScope(String field) {
            return scopedFields.inScope(field);
        }

        protected void printStringNow(String toPrint){
                System.out.println(toPrint);
        }
        
        protected int getFromScope(String elem) {
            return scopedTable.getEntry(elem);
        }

        protected String getTypeFromFieldScope(String scope){
            return scopedFields.getEntry(scope);
        }

        protected int getSmallestInScope(){
                Set<String> keys = scopedTable.getKeysInScope();
                int smallest = Integer.MAX_VALUE;
                for(String key: keys){
                        int val = scopedTable.getEntry(key);
                        if(val < smallest) smallest = val;
                }

                return smallest;
        }

        protected int getLargestInScope(){
                Set<String> keys = scopedTable.getKeysInScope();
                int largest = Integer.MIN_VALUE;
                for(String key: keys){
                        int val = scopedTable.getEntry(key);
                        if(val > largest) largest = val;
                }

                return largest;
        }
        
        public void addElem(String elem) {
                scopedTable.addEntry(elem, localAndArgNumber);
                localAndArgNumber++;
        }

        public void addField(String field, String type){
            scopedFields.addEntry(field, type);
        }

        public void addType(String funcName, String type){
                funcTypes.addEntry(funcName, type);
        }

        private void pushModule(){
            scopedFields.addScope();
            funcTypes.addScope();
        }

        private void popModule(){
            scopedFields.removeScope();
            funcTypes.removeScope();
        }
        
        private void pushScope(){
                scopedTable.addScope();
        }
        
        private void popScope() {
                scopedTable.removeScope();
        }

        public void codeGenVerilogFile(VerilogFile file) throws Exception{
                new File("ede/instance/mods").mkdirs();

                for (ModuleDeclaration module : file.modules) {
                        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES) {
                                @Override
                                protected String getCommonSuperClass(String type1, String type2) {
                                        return "java/lang/Object";
                                }
                        };
                        CheckClassAdapter moduleWriter = new CheckClassAdapter(cw);
                        HashSet<String> usedFunctions = new HashSet<String>();
                        calculateUsedFunctions(module, usedFunctions);
                        codeGenModule(module, moduleWriter, usedFunctions);
                        moduleWriter.visitEnd();
                        byte[] moduleBytes = cw.toByteArray();
                        try (FileOutputStream fos = new FileOutputStream("ede/instance/mods/" + module.moduleName + ".class")) {
                                fos.write(moduleBytes);
                        }
                }
        }

        protected static void pushString(String val, MethodVisitor main){
                main.visitLdcInsn(val);
        }

        private static void pushDouble(double val, MethodVisitor main){
                double value = val;
                main.visitLdcInsn(value);
        }

        private static void pushBool(boolean bool, MethodVisitor main){
                // 3. Load the double constant value 123.45 onto the stack
                main.visitLdcInsn(bool ? 1 : 0);
        }

        private static void pushInt(int val, MethodVisitor main){
                main.visitLdcInsn(val);
        }

        private static void pushLong(long val, MethodVisitor main){
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

        private void calculateUsedFunctions(ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                for(ModuleItem item: mod.moduleItemList){
                        if(item instanceof ContinuousAssignment) calculateUsedDeepFunctions((ContinuousAssignment)item, mod, usedFunctions);
                        else if(item instanceof ProcessBase) calculateUsedShallowFunctions((ProcessBase)item, mod, usedFunctions);
                }
        }

        private void calculateUsedShallowFunctions(ProcessBase process, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                calculateUsedShallowFunctions(process.statement, mod, usedFunctions);
        }

        private void calculateUsedShallowFunctions(Statement stat, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 if(stat instanceof CaseStatement) calculateUsedShallowFunctions((CaseStatement)stat, mod, usedFunctions);
                 else if(stat instanceof Assignment) calculateUsedShallowFunctions((Assignment)stat, mod, usedFunctions);
                 else if(stat instanceof IfStatement) calculateUsedShallowFunctions((IfStatement)stat, mod, usedFunctions);
                 else if(stat instanceof ForeverStatement) calculateUsedShallowFunctions((ForeverStatement)stat, mod, usedFunctions);
                 else if(stat instanceof ForStatement) calculateUsedShallowFunctions((ForStatement)stat, mod, usedFunctions);
                 else if(stat instanceof RepeatStatement) calculateUsedShallowFunctions((RepeatStatement)stat, mod, usedFunctions);
                 else if(stat instanceof WhileStatement) calculateUsedShallowFunctions((WhileStatement)stat, mod, usedFunctions);
                 else if(stat instanceof TaskStatement) calculateUsedShallowFunctions((TaskStatement)stat, mod, usedFunctions);
                 else if(stat instanceof SeqBlockStatement) calculateUsedShallowFunctions((SeqBlockStatement)stat, mod, usedFunctions);
                 else Utils.errorAndExit("Error invalid statement type " + stat.getClass().getName());
        }

        private void calculateUsedShallowFunctions(CaseStatement stat, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 calculateUsedShallowFunctions(stat.exp, mod, usedFunctions);
                 for(CaseItem item: stat.itemList){
                         if(item instanceof ExprCaseItem) calculateUsedShallowFunctions((ExprCaseItem)item, mod, usedFunctions);
                         else if(item instanceof DefCaseItem) calculateUsedShallowFunctions((DefCaseItem)item, mod, usedFunctions);
                         else Utils.errorAndExit("Error invalid case item type " + item.getClass().getName());
                 }
        }

        private void calculateUsedShallowFunctions(ExprCaseItem item, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 for(Expression exp: item.expList){
                         calculateUsedShallowFunctions(exp, mod, usedFunctions);
                 }
                 calculateUsedShallowFunctions(item.statement, mod, usedFunctions);
        }

        private void calculateUsedShallowFunctions(DefCaseItem item, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 calculateUsedShallowFunctions(item.statement, mod, usedFunctions);
        }

        private void calculateUsedShallowFunctions(Assignment assign, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 if(assign instanceof BlockingAssignment) calculateUsedShallowFunctions((BlockingAssignment)assign, mod, usedFunctions);
                 else if(assign instanceof NonBlockingAssignment) calculateUsedShallowFunctions((NonBlockingAssignment)assign, mod, usedFunctions);
                 else Utils.errorAndExit("Error invalid assignment type " + assign.getClass().getName());
        }

        private void calculateUsedShallowFunctions(BlockingAssignment assign, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 calculateUsedShallowFunctions(assign.rightHandSide, mod, usedFunctions);
        }

        private void calculateUsedShallowFunctions(NonBlockingAssignment assign, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 for(Expression exp: assign.rightHandSide){
                         calculateUsedShallowFunctions(exp, mod, usedFunctions);
                 }
        }

        private void calculateUsedShallowFunctions(IfStatement stat, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 calculateUsedShallowFunctions(stat.condition, mod, usedFunctions);
                 calculateUsedShallowFunctions(stat.trueStatement, mod, usedFunctions);
                 if(stat instanceof IfElseStatement) calculateUsedShallowFunctions(((IfElseStatement)stat).falseStatement, mod, usedFunctions);
        }

        private void calculateUsedShallowFunctions(ForeverStatement stat, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 calculateUsedShallowFunctions(stat.stat, mod, usedFunctions);
        }

        private void calculateUsedShallowFunctions(ForStatement stat, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 calculateUsedShallowFunctions(stat.init, mod, usedFunctions);
                 calculateUsedShallowFunctions(stat.exp, mod, usedFunctions);
                 calculateUsedShallowFunctions(stat.change, mod, usedFunctions);
                 calculateUsedShallowFunctions(stat.stat, mod, usedFunctions);
        }

        private void calculateUsedShallowFunctions(RepeatStatement stat, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 calculateUsedShallowFunctions(stat.exp, mod, usedFunctions);
                 calculateUsedShallowFunctions(stat.stat, mod, usedFunctions);
        }

        private void calculateUsedShallowFunctions(WhileStatement stat, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 calculateUsedShallowFunctions(stat.exp, mod, usedFunctions);
                 calculateUsedShallowFunctions(stat.stat, mod, usedFunctions);
        }

        private void calculateUsedShallowFunctions(TaskStatement stat, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 if(!usedFunctions.contains(stat.taskName + "Shallow")){
                         usedFunctions.add(stat.taskName + "Shallow");
                         for(ModuleItem item: mod.moduleItemList){
                                 if(item instanceof TaskDeclaration){
                                         if(((TaskDeclaration)item).taskName.equals(stat.taskName)){
                                                 calculateUsedShallowFunctions(((ProcedureDeclaration)item).stat, mod, usedFunctions);
                                         }
                                 }
                         }
                 }
        }

        private void calculateUsedShallowFunctions(SeqBlockStatement stat, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 for(Statement myStat: stat.statementList){
                         calculateUsedShallowFunctions(myStat, mod, usedFunctions);
                 }
        }

        private void calculateUsedShallowFunctions(Expression exp, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 if(exp instanceof FunctionCall) calculateUsedShallowFunctions((FunctionCall)exp, mod, usedFunctions);
                 else if(exp instanceof BinaryOperation) calculateUsedShallowFunctions((BinaryOperation)exp, mod, usedFunctions);
                 else if(exp instanceof Concatenation) calculateUsedShallowFunctions((Concatenation)exp, mod, usedFunctions);
                 else if(exp instanceof TernaryOperation) calculateUsedShallowFunctions((TernaryOperation)exp, mod, usedFunctions);
                 else if(exp instanceof UnaryOperation) calculateUsedShallowFunctions((UnaryOperation)exp, mod, usedFunctions);
                 else if(exp instanceof Element) calculateUsedShallowFunctions((Element)exp, mod, usedFunctions);
                 else if(exp instanceof Slice) calculateUsedShallowFunctions((Slice)exp, mod, usedFunctions);
        }

        private void calculateUsedShallowFunctions(FunctionCall call, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 if(!usedFunctions.contains(call.functionName + "Shallow")){
                         usedFunctions.add(call.functionName + "Shallow");
                         for(ModuleItem item: mod.moduleItemList){
                                 if(item instanceof FunctionDeclaration){
                                         if(nameOf(((FunctionDeclaration)item).functionName).equals(call.functionName))
                                                 calculateUsedShallowFunctions(((FunctionDeclaration)item).stat, mod, usedFunctions);
                                 }
                         }
                 }
        }

        private void calculateUsedShallowFunctions(BinaryOperation exp, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 calculateUsedShallowFunctions(exp.left, mod, usedFunctions);
                 calculateUsedShallowFunctions(exp.right, mod, usedFunctions);
        }

        private void calculateUsedShallowFunctions(Concatenation exp, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 for(Expression myExp: exp.circuitElementExpressionList){
                         calculateUsedShallowFunctions(myExp, mod, usedFunctions);
                 }
        }

        private void calculateUsedShallowFunctions(TernaryOperation exp, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 calculateUsedShallowFunctions(exp.condition, mod, usedFunctions);
                 calculateUsedShallowFunctions(exp.ifTrue, mod, usedFunctions);
                 calculateUsedShallowFunctions(exp.ifFalse, mod, usedFunctions);
        }

        private void calculateUsedShallowFunctions(UnaryOperation exp, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 calculateUsedShallowFunctions(exp.rightHandSideExpression, mod, usedFunctions);
        }

        private void calculateUsedShallowFunctions(Element exp, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 calculateUsedShallowFunctions(exp.index1, mod, usedFunctions);
        }

        private void calculateUsedShallowFunctions(Slice exp, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                 calculateUsedShallowFunctions(exp.index1, mod, usedFunctions);
                 calculateUsedShallowFunctions(exp.index2, mod, usedFunctions);
        }

        private void calculateUsedDeepFunctions(ContinuousAssignment decl, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                for(BlockingAssignment assign: decl.assignmentList){
                        calculateUsedDeepFunctions(assign.rightHandSide, mod, usedFunctions);
                }
        }

        private void calculateUsedDeepFunctions(Expression exp, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                if(exp instanceof FunctionCall) calculateUsedDeepFunctions((FunctionCall)exp, mod, usedFunctions);
                else if(exp instanceof BinaryOperation) calculateUsedDeepFunctions((BinaryOperation)exp, mod, usedFunctions);
                else if(exp instanceof Concatenation) calculateUsedDeepFunctions((Concatenation)exp, mod, usedFunctions);
                else if(exp instanceof TernaryOperation) calculateUsedDeepFunctions((TernaryOperation)exp, mod, usedFunctions);
                else if(exp instanceof UnaryOperation) calculateUsedDeepFunctions((UnaryOperation)exp, mod, usedFunctions);
                else if(exp instanceof Element) calculateUsedDeepFunctions((Element)exp, mod, usedFunctions);
                else if(exp instanceof Slice) calculateUsedDeepFunctions((Slice)exp, mod, usedFunctions);
        }

        private void calculateUsedDeepFunctions(BinaryOperation exp, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                calculateUsedDeepFunctions(exp.left, mod, usedFunctions);
                calculateUsedDeepFunctions(exp.right, mod, usedFunctions);
        }

        private void calculateUsedDeepFunctions(Concatenation exp, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                for(Expression myExp: exp.circuitElementExpressionList){
                        calculateUsedDeepFunctions(myExp, mod, usedFunctions);
                }
        }

        private void calculateUsedDeepFunctions(TernaryOperation exp, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                calculateUsedDeepFunctions(exp.condition, mod, usedFunctions);
                calculateUsedDeepFunctions(exp.ifTrue, mod, usedFunctions);
                calculateUsedDeepFunctions(exp.ifFalse, mod, usedFunctions);
        }

        private void calculateUsedDeepFunctions(UnaryOperation exp, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                calculateUsedDeepFunctions(exp.rightHandSideExpression, mod, usedFunctions);
        }

        private void calculateUsedDeepFunctions(Element exp, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                calculateUsedDeepFunctions(exp.index1, mod, usedFunctions);
        }

        private void calculateUsedDeepFunctions(Slice exp, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                calculateUsedDeepFunctions(exp.index1, mod, usedFunctions);
                calculateUsedDeepFunctions(exp.index2, mod, usedFunctions);
        }

        private void calculateUsedDeepFunctions(Statement stat, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                if(stat instanceof SeqBlockStatement) {
                        for(Statement s : ((SeqBlockStatement)stat).statementList)
                                calculateUsedDeepFunctions(s, mod, usedFunctions);
                } else if(stat instanceof IfElseStatement) {
                        IfElseStatement ies = (IfElseStatement)stat;
                        calculateUsedDeepFunctions(ies.condition, mod, usedFunctions);
                        calculateUsedDeepFunctions(ies.trueStatement, mod, usedFunctions);
                        calculateUsedDeepFunctions(ies.falseStatement, mod, usedFunctions);
                } else if(stat instanceof IfStatement) {
                        IfStatement is = (IfStatement)stat;
                        calculateUsedDeepFunctions(is.condition, mod, usedFunctions);
                        calculateUsedDeepFunctions(is.trueStatement, mod, usedFunctions);
                } else if(stat instanceof ForStatement) {
                        ForStatement fs = (ForStatement)stat;
                        calculateUsedDeepFunctions(fs.init, mod, usedFunctions);
                        calculateUsedDeepFunctions(fs.exp, mod, usedFunctions);
                        calculateUsedDeepFunctions(fs.change, mod, usedFunctions);
                        calculateUsedDeepFunctions(fs.stat, mod, usedFunctions);
                } else if(stat instanceof WhileStatement) {
                        WhileStatement ws = (WhileStatement)stat;
                        calculateUsedDeepFunctions(ws.exp, mod, usedFunctions);
                        calculateUsedDeepFunctions(ws.stat, mod, usedFunctions);
                } else if(stat instanceof RepeatStatement) {
                        RepeatStatement rs = (RepeatStatement)stat;
                        calculateUsedDeepFunctions(rs.exp, mod, usedFunctions);
                        calculateUsedDeepFunctions(rs.stat, mod, usedFunctions);
                } else if(stat instanceof ForeverStatement) {
                        calculateUsedDeepFunctions(((ForeverStatement)stat).stat, mod, usedFunctions);
                } else if(stat instanceof CaseStatement) {
                        CaseStatement cs = (CaseStatement)stat;
                        calculateUsedDeepFunctions(cs.exp, mod, usedFunctions);
                        for(CaseItem item : cs.itemList)
                                calculateUsedDeepFunctions(item.statement, mod, usedFunctions);
                } else if(stat instanceof BlockingAssignment) {
                        calculateUsedDeepFunctions(((BlockingAssignment)stat).rightHandSide, mod, usedFunctions);
                } else if(stat instanceof TaskStatement) {
                        for(Expression arg : ((TaskStatement)stat).argumentList)
                                calculateUsedDeepFunctions(arg, mod, usedFunctions);
                }
        }

        private void calculateUsedDeepFunctions(FunctionCall call, ModuleDeclaration mod, HashSet<String> usedFunctions) throws Exception{
                if(!usedFunctions.contains(call.functionName + "Deep")){
                        usedFunctions.add(call.functionName + "Deep");
                        for(ModuleItem item: mod.moduleItemList){
                                if(item instanceof FunctionDeclaration)
                                        if(nameOf(((FunctionDeclaration)item).functionName).equals(call.functionName))
                                                calculateUsedDeepFunctions(((FunctionDeclaration)item).stat, mod, usedFunctions);
                        }
                }
        }

        private void codeGenModule(ModuleDeclaration mod, ClassVisitor moduleWriter, HashSet<String> usedFunctions) throws Exception{
                pushModule();
                String modName = "ede/instance/mods/" + mod.moduleName;
                moduleWriter.visit(Opcodes.V1_6,
                        Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER,
                        modName,
                        null,
                        "ede/stl/compiler/VerilogAsJavaBase",
                        null);

                String typeStr = getTypes(mod.args);
                
                MethodVisitor moduleConstructor =                   moduleWriter.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_VARARGS,
                        "<init>",
                        "(Lede/stl/gui/GuiEde;" + typeStr + ")V",
                        null,
                        null
                );

                moduleConstructor.visitCode();
                moduleConstructor.visitVarInsn(Opcodes.ALOAD, 0);
                moduleConstructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/compiler/VerilogAsJavaBase", "<init>", "()V", false);
                for(ModuleItem item : mod.moduleItemList) { codeGenField(item, moduleConstructor, modName, moduleWriter); }

                for(ModuleItem item : mod.moduleItemList) { codeGenParamTypesForProcedure(item, moduleConstructor, modName, moduleWriter, usedFunctions); }

                for(ModuleItem item : mod.moduleItemList) { codeGenPossibleProcedure(item, moduleConstructor, modName, moduleWriter, usedFunctions); }
                
                for(ModuleItem item : mod.moduleItemList) { codeGenRestModuleItem(item, moduleConstructor, modName, moduleWriter); }
                moduleConstructor.visitInsn(Opcodes.RETURN);
                moduleConstructor.visitMaxs(0, 0);
                moduleConstructor.visitEnd();

                popModule();
        }
        
        private void codeGenRestModuleItem(ModuleItem item, MethodVisitor moduleConstructor, String modName, ClassVisitor moduleWriter) throws Exception {
                if(item instanceof GateDeclaration) codeGenGateDeclaration(item, moduleConstructor, modName, moduleWriter);
                else if(item instanceof ContinuousAssignment) codeGenContinuousAssignment((ContinuousAssignment)item, moduleConstructor, modName, moduleWriter);
                else if(item instanceof EmptyModItem) codeGenEmptyModItem();
                else if(item instanceof ModuleInstantiation) codeGenModuleInstantiation((ModuleInstantiation)item, moduleConstructor, modName, moduleWriter);
                else if(item instanceof ProcessBase) codeGenProcess((ProcessBase)item, modName, moduleWriter);
        }

       private void codeGenProcess(ProcessBase process, String modName, ClassVisitor moduleWriter) throws Exception{
           if(process instanceof InitialProcess) codeGenInitialProcess((InitialProcess)process, modName, moduleWriter);
           else if(process instanceof AllwaysProcess) codeGenAllwaysProcess((AllwaysProcess)process, modName, moduleWriter);
       }

       private void codeGenInitialProcess(InitialProcess process, String modName, ClassVisitor moduleWriter) throws Exception{
           MethodVisitor methodVisit = moduleWriter.visitMethod(Opcodes.ACC_PUBLIC, "process" + this.processNumber, "(Lede/stl/gui/GuiEde;Lede/stl/compiler/CompiledEnvironment;)V", null, null);
           methodVisit.visitCode();
           pushScope();
           this.localAndArgNumber = 3;
           codeGenShallowStatement(process.statement, "process" + this.processNumber, methodVisit, modName, moduleWriter);
           
           popScope();
           methodVisit.visitInsn(Opcodes.RETURN);
           methodVisit.visitMaxs(0, 0);
           methodVisit.visitEnd();
           this.processNumber++;
       }

      private void codeGenAllwaysProcess(AllwaysProcess process, String modName, ClassVisitor moduleWriter) throws Exception{
           MethodVisitor methodVisit = moduleWriter.visitMethod(Opcodes.ACC_PUBLIC, "process" + this.processNumber, "(Lede/stl/gui/GuiEde;Lede/stl/compiler/CompiledEnvironment;)V", null, null);
           methodVisit.visitCode();
           pushScope();
           this.localAndArgNumber = 3;
           Label begin = new Label();
           methodVisit.visitLabel(begin);
           codeGenShallowStatement(process.statement, "process" + this.processNumber, methodVisit, modName, moduleWriter);
           popScope();
           methodVisit.visitJumpInsn(Opcodes.GOTO, begin);
           methodVisit.visitInsn(Opcodes.RETURN);
           methodVisit.visitMaxs(0, 0);
           methodVisit.visitEnd();
           this.processNumber++;
       }
        
        private void codeGenGateDeclaration(ModuleItem item, MethodVisitor moduleConstructor, String modName, ClassVisitor writer) throws Exception{
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
        
        private void codeGenModuleInstantiation(ModuleInstantiation instant, MethodVisitor constructor, String modName, ClassVisitor moduleWriter){
                
        }
        
        private void codeGenContinuousAssignment(ContinuousAssignment item, MethodVisitor moduleConstructor, String modName, ClassVisitor moduleWriter) throws Exception{
                pushScope();
                this.localAndArgNumber = 3;
                for(BlockingAssignment assign: item.assignmentList) {
                        codeGenDeepAssignment(assign, moduleConstructor, modName, moduleWriter);
                }
                popScope();
        }
        
        private void codeGenDeepAssignment(BlockingAssignment assign, MethodVisitor constructor, String modName, ClassVisitor moduleWriter) throws Exception {
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
        
        private void codeGenAndGateDeclaration(AndGateDeclaration item, MethodVisitor moduleConstructor, String modName, ClassVisitor writer) throws Exception {
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
                moduleConstructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/AndGate", "<init>", "(Lede/stl/circuit/Web;Lede/stl/circuit/Web;Lede/stl/circuit/Web;[Lede/stl/circuit/Web;)V", false);
        }
        
        private void codeGenNandGateDeclaration(NandGateDeclaration item, MethodVisitor moduleConstructor, String modName, ClassVisitor writer) throws Exception {
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
                moduleConstructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/NandGate", "<init>", "(Lede/stl/circuit/Web;Lede/stl/circuit/Web;Lede/stl/circuit/Web;[Lede/stl/circuit/Web;)V", false);
        }
        
        private void codeGenNorGateDeclaration(NorGateDeclaration item, MethodVisitor moduleConstructor, String modName, ClassVisitor writer) throws Exception {
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
                moduleConstructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/NorGate", "<init>", "(Lede/stl/circuit/Web;Lede/stl/circuit/Web;Lede/stl/circuit/Web;[Lede/stl/circuit/Web;)V", false);
        }
        
        private void codeGenNotGateDeclaration(NotGateDeclaration item, MethodVisitor moduleConstructor, String modName, ClassVisitor writer) throws Exception {
                codeGenDeepExpression(item.gateConnections.get(0), moduleConstructor, modName, writer);
                codeGenDeepExpression(item.gateConnections.get(1), moduleConstructor, modName, writer);
                moduleConstructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/NandGate", "<init>", "(Lede/stl/circuit/Web;Lede/stl/circuit/Web;)V", false);
        }
        
        private void codeGenOrGateDeclaration(OrGateDeclaration item, MethodVisitor moduleConstructor, String modName, ClassVisitor writer) throws Exception {
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
                moduleConstructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/NorGate", "<init>", "(Lede/stl/circuit/Web;Lede/stl/circuit/Web;Lede/stl/circuit/Web;[Lede/stl/circuit/Web;)V", false);
        }
        
        private void codeGenXnorGateDeclaration(XnorGateDeclaration item, MethodVisitor moduleConstructor, String modName, ClassVisitor writer) throws Exception {
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
                moduleConstructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/XnorGate", "<init>", "(Lede/stl/circuit/Web;Lede/stl/circuit/Web;Lede/stl/circuit/Web;[Lede/stl/circuit/Web;)V", false);
        }
        
        private void codeGenXorGateDeclaration(XorGateDeclaration item, MethodVisitor moduleConstructor, String modName, ClassVisitor writer) throws Exception {
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
                moduleConstructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/XnorGate", "<init>", "(Lede/stl/circuit/Web;Lede/stl/circuit/Web;Lede/stl/circuit/Web;[Lede/stl/circuit/Web;)V", false);
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
                        return "Lede/stl/values/Value;";
                else if (declaration instanceof Input.Reg.Vector.Ident)
                        return "Lede/stl/values/Value;";
                else if (declaration instanceof Input.Wire.Scalar.Ident)
                        return "Lede/stl/values/Value;";
                else if (declaration instanceof Input.Reg.Scalar.Ident)
                        return "Lede/stl/values/Value;";
                else if (declaration instanceof Output.Wire.Vector.Ident)
                        return "Lede/stl/values/Value;";
                else if (declaration instanceof Output.Reg.Vector.Ident)
                        return "Lede/stl/values/Value;";
                else if (declaration instanceof Output.Wire.Scalar.Ident)
                        return "Lede/stl/values/Value;";
                else if (declaration instanceof Output.Reg.Scalar.Ident)
                        return "Lede/stl/values/Value;";
                else if (declaration instanceof Wire.Vector.Ident)
                        return "Lede/stl/values/Value;";
                else if (declaration instanceof Reg.Vector.Ident)
                        return "Lede/stl/values/Value;";
                else if (declaration instanceof Wire.Scalar.Ident)
                        return "Lede/stl/values/Value;";
                else if (declaration instanceof Reg.Scalar.Ident)
                        return "Lede/stl/values/Value;";
                else if (declaration instanceof Int.Ident)
                        return "Lede/stl/values/Value;";
                else if (declaration instanceof Real.Ident)
                        return "Lede/stl/values/Value;";
                else {
                        Utils.errorAndExit("Error Could not find Ident Declaration with the following type " + declaration.getClass().getName());
                        return "";
                }
        }
        
        private void codeGenDeepFunction(FunctionDeclaration decl, String modName, ClassVisitor moduleWriter) throws Exception{
                pushScope();

                this.localAndArgNumber = 3;
                for (ModuleItem param : decl.paramaters) {
                        if(isParam(param)){
                                addElem(nameOf(param));
                        }
                }

                String methodName = nameOf(decl.functionName);
                String methodType = funcTypes.getEntry(methodName + "Deep");

                MethodVisitor methodVisit = moduleWriter.visitMethod(Opcodes.ACC_PUBLIC, // Access: public static
                        methodName + "Deep", // Name: main
                        methodType.toString(), null, // Signature (generics related, null for simple types)
                        null // Exceptions thrown (null for none)
                );

                methodVisit.visitCode();

                for(ModuleItem param: decl.paramaters){
                        if(!isParam(param)){
                                addElem(nameOf(param));
                        }
                }

                codeGenDeepStatement(decl.stat, methodName + "Deep", methodVisit, modName, moduleWriter);
                int l = getLargestInScope();
                int s = getSmallestInScope();
                for(int i = l; i >= s; i--){
                        methodVisit.visitInsn(Opcodes.ACONST_NULL);
                        methodVisit.visitVarInsn(Opcodes.ASTORE, i);
                }
                methodVisit.visitInsn(Opcodes.ARETURN);
                methodVisit.visitMaxs(0, 0); // COMPUTE_MAXS and COMPUTE_FRAMES handle these automatically
                methodVisit.visitEnd();
                this.localAndArgNumber = s;
                popScope();
        }

        private void codeGenShallowFunction(FunctionDeclaration decl, String modName, ClassVisitor moduleWriter) throws Exception{
                printStringNow("Function is " + nameOf(decl.functionName));
                pushScope();

                this.localAndArgNumber = 3;
                for (ModuleItem param : decl.paramaters) {
                        if(isParam(param)){
                                addElem(nameOf(param));
                        }
                }

                String methodName = nameOf(decl.functionName);
                String methodType = funcTypes.getEntry(methodName + "Shallow");

                MethodVisitor methodVisit = moduleWriter.visitMethod(Opcodes.ACC_PUBLIC, // Access: public static
                        methodName + "Shallow", // Name: main
                        methodType.toString(), null, // Signature (generics related, null for simple types)
                        null // Exceptions thrown (null for none)
                );

                methodVisit.visitCode();
                
                for(ModuleItem param: decl.paramaters){
                        if(!isParam(param)){
                                addElem(nameOf(param));
                                codeGenLocalVariable(param, modName, methodVisit, moduleWriter);
                        }
                }
                
                codeGenShallowStatement(decl.stat, methodName + "Shallow", methodVisit, modName, moduleWriter);
                int l = getLargestInScope();
                int s = getSmallestInScope();
                for(int i = l; i >= s; i--){
                        methodVisit.visitInsn(Opcodes.ACONST_NULL);
                        methodVisit.visitVarInsn(Opcodes.ASTORE, i);
                }
                methodVisit.visitInsn(Opcodes.ARETURN);
                methodVisit.visitMaxs(0, 0);
                methodVisit.visitEnd();
                this.localAndArgNumber = s;
                popScope();
        }
        
        private void codeGenDeepTask(TaskDeclaration decl, String modName, ClassVisitor moduleWriter) throws Exception {
                pushScope();

                this.localAndArgNumber = 3;
                for (ModuleItem param : decl.paramaters) {
                        if(isParam(param)){
                                addElem(nameOf(param));
                        }
                }

                String methodName = decl.taskName + "Deep";
                String methodType = funcTypes.getEntry(methodName);

                MethodVisitor methodVisit = moduleWriter.visitMethod(Opcodes.ACC_PUBLIC, // Access: public static
                        methodName, // Name: main
                        methodType.toString(), null, // Signature (generics related, null for simple types)
                        null // Exceptions thrown (null for none)
                );

                methodVisit.visitCode();
                
                for(ModuleItem param: decl.paramaters){
                        if(!isParam(param)){
                                addElem(nameOf(param));
                        }
                }

                
                codeGenDeepStatement(decl.stat, methodName, methodVisit, modName, moduleWriter);
                int l = getLargestInScope();
                int s = getSmallestInScope();
                for(int i = l; i >= s; i--){
                        methodVisit.visitInsn(Opcodes.ACONST_NULL);
                        methodVisit.visitVarInsn(Opcodes.ASTORE, i);
                }
                methodVisit.visitInsn(Opcodes.RETURN);
                methodVisit.visitMaxs(0, 0); // COMPUTE_MAXS and COMPUTE_FRAMES handle these automatically
                methodVisit.visitEnd();
                this.localAndArgNumber = s;
                popScope();
        }

        private boolean isParam(ModuleItem item){
                if(item instanceof Input.Wire.Vector.Ident || item instanceof Input.Reg.Vector.Ident || item instanceof Input.Wire.Scalar.Ident || item instanceof Input.Reg.Scalar.Ident){
                        return true;
                } else {
                        return false;
                }
        }
        
        private void codeGenShallowTask(TaskDeclaration decl, String modName, ClassVisitor moduleWriter) throws Exception {
                printStringNow("Task is " + decl.taskName);
                pushScope();

                this.localAndArgNumber = 3;
                for (ModuleItem param : decl.paramaters) {
                        if(isParam(param)){
                                addElem(nameOf(param));
                        }
                }

                String methodName = decl.taskName + "Shallow";
                String methodType = funcTypes.getEntry(methodName);

                MethodVisitor methodVisit = moduleWriter.visitMethod(Opcodes.ACC_PUBLIC,
                        methodName,
                        methodType.toString(), null,
                        null
                );
                
                methodVisit.visitCode();
                
                for(ModuleItem param: decl.paramaters){
                        if(!isParam(param)){
                                addElem(nameOf(param));
                                codeGenLocalVariable(param, modName, methodVisit, moduleWriter);
                        }
                }

                codeGenShallowStatement(decl.stat, methodName, methodVisit, modName, moduleWriter);
                int l = getLargestInScope();
                int s = getSmallestInScope();
                for(int i = l; i >= s; i--){
                        methodVisit.visitInsn(Opcodes.ACONST_NULL);
                        methodVisit.visitVarInsn(Opcodes.ASTORE, i);
                }
                methodVisit.visitInsn(Opcodes.RETURN);
                methodVisit.visitMaxs(0, 0); // COMPUTE_MAXS and COMPUTE_FRAMES handle these automatically
                methodVisit.visitEnd();
                this.localAndArgNumber = s;
                popScope();
        }

        private void codeGenLocalVariable(ModuleItem item, String modName, MethodVisitor methodVisit, ClassVisitor moduleWriter) throws Exception{
                if(item instanceof Int.Ident){
                        codeGenIntIdentLocal((Int.Ident)item, modName, methodVisit, moduleWriter);
                } else if(item instanceof Real.Ident){
                        codeGenRealIdentLocal((Real.Ident)item, modName, methodVisit, moduleWriter);
                } else if(item instanceof Int.Array){
                        codeGenIntArrayLocal((Int.Array)item, modName, methodVisit, moduleWriter);
                } else if(item instanceof Reg.Scalar.Ident){
                        codeGenRegScalarIdentLocal((Reg.Scalar.Ident)item, modName, methodVisit, moduleWriter);
                } else if(item instanceof Reg.Vector.Ident){
                        codeGenRegVectorIdentLocal((Reg.Vector.Ident)item, modName, methodVisit, moduleWriter);
                } else if(item instanceof Reg.Scalar.Array){
                        codeGenRegScalarArrayLocal((Reg.Scalar.Array)item, modName, methodVisit, moduleWriter);
                } else if(item instanceof Reg.Vector.Array){
                        codeGenRegVectorArrayLocal((Reg.Vector.Array)item, modName, methodVisit, moduleWriter);
                } else {
                        Utils.errorAndExit("Error invalid local variable type" + item.getClass().getName());
                }
        }

        private void codeGenIntIdentLocal(Int.Ident item, String modName, MethodVisitor methodVisit, ClassVisitor moduleWriter) throws Exception{
                methodVisit.visitTypeInsn(Opcodes.NEW, "ede/stl/values/IntVal");
                methodVisit.visitInsn(Opcodes.DUP);
                pushInt(0, methodVisit);
                methodVisit.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/IntVal", "<init>", "(I)V", false);
                methodVisit.visitVarInsn(Opcodes.ASTORE, this.getFromScope(nameOf(item)));
        }

        private void codeGenRealIdentLocal(Real.Ident item, String modName, MethodVisitor methodVisit, ClassVisitor moduleWriter) throws Exception{
                methodVisit.visitTypeInsn(Opcodes.NEW, "ede/stl/values/RealVal");
                methodVisit.visitInsn(Opcodes.DUP);
                pushDouble(0.0, methodVisit);
                methodVisit.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/RealVal", "<init>", "(D)V", false);
                methodVisit.visitVarInsn(Opcodes.ASTORE, this.getFromScope(nameOf(item)));
        }

        private void codeGenIntArrayLocal(Int.Array arr, String modName, MethodVisitor methodVisit, ClassVisitor moduleWriter) throws Exception{
                methodVisit.visitTypeInsn(Opcodes.NEW, "ede/stl/values/ArrayIntVal");
                methodVisit.visitInsn(Opcodes.DUP);
                codeGenShallowExpression(arr.arrayIndex1, methodVisit, modName, moduleWriter);
                codeGenShallowExpression(arr.arrayIndex2, methodVisit, modName, moduleWriter);
                methodVisit.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/ArrayIntVal", "<init>", "(Lede/stl/values/Value;Lede/stl/values/Value;)V", false);
                methodVisit.visitVarInsn(Opcodes.ASTORE, this.getFromScope(nameOf(arr)));
        }

        private void codeGenRegScalarIdentLocal(Reg.Scalar.Ident item, String modName, MethodVisitor methodVisit, ClassVisitor moduleWriter) throws Exception{
                methodVisit.visitTypeInsn(Opcodes.NEW, "ede/stl/values/RegVal");
                methodVisit.visitInsn(Opcodes.DUP);
                pushInt(0, methodVisit);
                methodVisit.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/RegVal", "<init>", "(Z)V", false);
                methodVisit.visitVarInsn(Opcodes.ASTORE, this.getFromScope(nameOf(item)));
        }

        private void codeGenRegVectorIdentLocal(Reg.Vector.Ident item, String modName, MethodVisitor methodVisit, ClassVisitor moduleWriter) throws Exception{
                methodVisit.visitTypeInsn(Opcodes.NEW, "ede/stl/values/VectorVal");
                methodVisit.visitInsn(Opcodes.DUP);
                codeGenShallowExpression(item.GetIndex1(), methodVisit, modName, moduleWriter);
                codeGenShallowExpression(item.GetIndex2(), methodVisit, modName, moduleWriter);
                methodVisit.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/VectorVal", "<init>", "(Lede/stl/values/Value;Lede/stl/values/Value;)V", false);
                methodVisit.visitVarInsn(Opcodes.ASTORE, this.getFromScope(nameOf(item)));
        }

        private void codeGenRegScalarArrayLocal(Reg.Scalar.Array arr, String modName, MethodVisitor methodVisit, ClassVisitor moduleWriter) throws Exception{
                methodVisit.visitTypeInsn(Opcodes.NEW, "ede/stl/values/ArrayRegVal");
                methodVisit.visitInsn(Opcodes.DUP);
                codeGenShallowExpression(arr.arrayIndex1, methodVisit, modName, moduleWriter);
                codeGenShallowExpression(arr.arrayIndex2, methodVisit, modName, moduleWriter);
                methodVisit.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/ArrayRegVal", "<init>", "(Lede/stl/values/Value;Lede/stl/values/Value;)V", false);
                methodVisit.visitVarInsn(Opcodes.ASTORE, this.getFromScope(nameOf(arr)));
        }

        private void codeGenRegVectorArrayLocal(Reg.Vector.Array arr, String modName, MethodVisitor methodVisit, ClassVisitor moduleWriter) throws Exception{
                methodVisit.visitTypeInsn(Opcodes.NEW, "ede/stl/values/ArrayVectorVal");
                methodVisit.visitInsn(Opcodes.DUP);
                codeGenShallowExpression(arr.arrayIndex1, methodVisit, modName, moduleWriter);
                codeGenShallowExpression(arr.arrayIndex2, methodVisit, modName, moduleWriter);
                codeGenShallowExpression(arr.GetIndex1(), methodVisit, modName, moduleWriter);
                codeGenShallowExpression(arr.GetIndex2(), methodVisit, modName, moduleWriter);
                methodVisit.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/ArrayVectorVal", "<init>", "(Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;)V", false);
                methodVisit.visitVarInsn(Opcodes.ASTORE, this.getFromScope(nameOf(arr)));
        }

        private void codeGenParamTypesForProcedure(ModuleItem item, MethodVisitor moduleConstructor, String modName, ClassVisitor moduleWriter, HashSet<String> methods) throws Exception{
                if(item instanceof ProcedureDeclaration) codeGenParamTypesForProcedure((ProcedureDeclaration)item, moduleConstructor, modName, moduleWriter, methods);
        }
        
        private void codeGenPossibleProcedure(ModuleItem item, MethodVisitor moduleConstructor, String modName, ClassVisitor writer, HashSet<String> methods) throws Exception{
                if(item instanceof ProcedureDeclaration) codeGenProcedure((ProcedureDeclaration)item, modName, writer, methods);
        }

        private void codeGenParamTypesForProcedure(ProcedureDeclaration decl, MethodVisitor moduleConstructor, String modName, ClassVisitor moduleWriter, HashSet<String> methods) throws Exception{
                if(decl instanceof TaskDeclaration) codeGenParamTypesForTask((TaskDeclaration)decl, moduleConstructor, modName, moduleWriter, methods);
                else if(decl instanceof FunctionDeclaration) codeGenParamTypesForFunction((FunctionDeclaration)decl, moduleConstructor, modName, moduleWriter, methods);
        }

        private void codeGenParamTypesForTask(TaskDeclaration task, MethodVisitor moduleConstructor, String modName, ClassVisitor moduleWriter, HashSet<String> methods) throws Exception{
                StringBuilder sb = new StringBuilder();
                sb.append('(');
                sb.append("Lede/stl/gui/GuiEde;Lede/stl/compiler/CompiledEnvironment;");
                for(ModuleItem item: task.paramaters){
                        if(isParam(item)){
                                String type = typeOf(item);
                                sb.append(type);
                        }
                }
                sb.append(')');
                sb.append('V');

                if(methods.contains(task.taskName + "Shallow"))
                        addType(task.taskName + "Shallow", sb.toString());
                if(methods.contains(task.taskName + "Deep"))
                        addType(task.taskName + "Deep", sb.toString());
        }

        private void codeGenParamTypesForFunction(FunctionDeclaration func, MethodVisitor moduleConstructor, String modName, ClassVisitor moduleWriter, HashSet<String> methods) throws Exception{
                StringBuilder sb = new StringBuilder();
                sb.append('(');
                sb.append("Lede/stl/gui/GuiEde;Lede/stl/compiler/CompiledEnvironment;");
                for(ModuleItem item: func.paramaters){
                        if(isParam(item)){
                                String type = typeOf(item);
                                sb.append(type);
                        }
                }
                sb.append(')');
                sb.append(typeOf(func.functionName));
                String name = nameOf(func.functionName);

                if(methods.contains(name + "Shallow"))
                        addType(name + "Shallow", sb.toString());
                if(methods.contains(name + "Deep"))
                        addType(name + "Deep", sb.toString());
        }

        private void codeGenProcedure(ProcedureDeclaration decl, String modName, ClassVisitor moduleWriter, HashSet<String> methods) throws Exception{
                if(decl instanceof TaskDeclaration) codeGenTask((TaskDeclaration)decl, modName, moduleWriter, methods);
                else if(decl instanceof FunctionDeclaration) codeGenFunction((FunctionDeclaration)decl, modName, moduleWriter, methods);
                else Utils.errorAndExit("Error invalid procGen");
        }
        
        private void codeGenTask(TaskDeclaration task, String modName, ClassVisitor moduleWriter, HashSet<String> methods) throws Exception {
                if(methods.contains(task.taskName + "Shallow"))
                        codeGenShallowTask(task, modName, moduleWriter);
                if(methods.contains(task.taskName + "Deep"))
                        codeGenDeepTask(task, modName, moduleWriter);
        }
        
        private void codeGenFunction(FunctionDeclaration myDecl, String modName, ClassVisitor moduleWriter) throws Exception {
                codeGenShallowFunction(myDecl, modName, moduleWriter);
                codeGenDeepFunction(myDecl, modName, moduleWriter);
        }

        private void codeGenFunction(FunctionDeclaration myDecl, String modName, ClassVisitor moduleWriter, HashSet<String> methods) throws Exception {
                String name = nameOf(myDecl.functionName);
                if(methods.contains(name + "Shallow"))
                        codeGenShallowFunction(myDecl, modName, moduleWriter);
                if(methods.contains(name + "Deep"))
                        codeGenDeepFunction(myDecl, modName, moduleWriter);
        }

        private void codeGenShallowStatement(Statement stat, String methodName, MethodVisitor method, String modName, ClassVisitor modWriter) throws Exception{
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

        private void codeGenCaseShallowStatement(CaseStatement stat, String methodName, MethodVisitor method, String modName, ClassVisitor module) throws Exception{
                if (stat instanceof CaseXStatement)
                        codeGenShallowCaseXStatement((CaseXStatement)stat, methodName, method, modName, module);
                else if (stat instanceof CaseZStatement)
                        codeGenCaseZStatement((CaseZStatement)stat, methodName, method, modName, module);
                else {
                        codeGenShallowExpression(stat.exp, method, modName, module);
                        int exprResult = this.localAndArgNumber;
                        this.localAndArgNumber++;
                        method.visitVarInsn(Opcodes.ASTORE, exprResult);

                        for (CaseItem statement : stat.itemList) {
                                if (statement instanceof ExprCaseItem) {
                                        ExprCaseItem item = (ExprCaseItem)statement;
                                        Label equalLabel = new Label();
                                        Label endStatLabel = new Label();

                                        for (Expression exp : item.expList) {
                                                        codeGenShallowExpression(exp, method, modName, module);
                                                        method.visitVarInsn(Opcodes.ALOAD, exprResult);
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
        
        private void codeGenShallowCaseXStatement(CaseXStatement stat, String methodName, MethodVisitor method, String modName, ClassVisitor module) throws Exception{
                codeGenShallowExpression(stat.exp, method, modName, module);
                int exprResult = this.localAndArgNumber;
                this.localAndArgNumber++;
                method.visitVarInsn(Opcodes.ASTORE, exprResult);
                
                for(CaseItem statement: stat.itemList) {
                        if(statement instanceof ExprCaseItem) {
                                ExprCaseItem item = (ExprCaseItem)statement;
                                
                                Label equalLabel = new Label();
                                Label endStatLabel = new Label();

                                for (Expression exp : item.expList) {
                                                codeGenShallowExpression(exp, method, modName, module);
                                                method.visitVarInsn(Opcodes.ALOAD, exprResult);
                                                method.visitMethodInsn(Opcodes.INVOKESTATIC,
              "ede/stl/common/Utils", // Internal name of the class
              "caseBoolean",           // Name of the static method
              "(Lede/stl/values/Value;Lede/stl/values/Value;)Z",                        // Method descriptor (void return, no args)
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
        
        private void codeGenCaseZStatement(CaseZStatement stat, String methodName, MethodVisitor method, String modName, ClassVisitor module) throws Exception {
                codeGenShallowExpression(stat.exp, method, modName, module);
                int exprResult = this.localAndArgNumber;
                this.localAndArgNumber++;
                method.visitVarInsn(Opcodes.ASTORE, exprResult);
                
                for(CaseItem statement: stat.itemList) {
                        if(statement instanceof ExprCaseItem) {
                                ExprCaseItem item = (ExprCaseItem)statement;
                                
                                Label equalLabel = new Label();
                                Label endStatLabel = new Label();

                                for (Expression exp : item.expList) {
                                                codeGenShallowExpression(exp, method, modName, module);
                                                method.visitVarInsn(Opcodes.ALOAD, exprResult);
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
        
        private void codeGenShallowIfStatement(IfStatement stat, String methodName, MethodVisitor method, String modName, ClassVisitor module) throws Exception{
                if(stat instanceof IfElseStatement) {
                        codeGenShallowIfElseStatement((IfElseStatement)stat, methodName, method, modName, module);
                } else {
                        codeGenShallowExpression(stat.condition, method, modName, module);
                        Label endLabel = new Label();
                        method.visitMethodInsn(Opcodes.INVOKEINTERFACE,
                      "ede/stl/values/Value", // Internal name of the class
                      "boolValue",           // Name of the static method
                      "()Z",                        // Method descriptor (void return, no args)
                      true);
                        method.visitJumpInsn(Opcodes.IFEQ, endLabel);
                        codeGenShallowStatement(stat.trueStatement, methodName, method, modName, module);
                        method.visitLabel(endLabel);
                }
        }
        
        private void codeGenShallowIfElseStatement(IfElseStatement stat, String methodName, MethodVisitor method, String modName, ClassVisitor module) throws Exception {
                codeGenShallowExpression(stat.condition, method, modName, module);
                Label endLabel = new Label();
                Label elseLabel = new Label();
                method.visitMethodInsn(Opcodes.INVOKEINTERFACE,
              "ede/stl/values/Value", // Internal name of the class
              "boolValue",           // Name of the static method
              "()Z",                        // Method descriptor (void return, no args)
              true);
                method.visitJumpInsn(Opcodes.IFEQ, elseLabel);
                codeGenShallowStatement(stat.trueStatement, methodName, method, modName, module);
                method.visitJumpInsn(Opcodes.GOTO, endLabel);
                method.visitLabel(elseLabel);
                codeGenShallowStatement(stat.falseStatement, methodName, method, modName, module);
                method.visitLabel(endLabel);
        }
        
        private void codeGenShallowForeverLoop(ForeverStatement loop, String methodName, MethodVisitor method, String modName, ClassVisitor writer) throws Exception {
                Label begin = new Label();
                method.visitLabel(begin);
                codeGenShallowStatement(loop.stat, methodName, method, modName, writer);
                method.visitJumpInsn(Opcodes.GOTO, begin);
        }
        
        private void codeGenShallowForLoop(ForStatement stat, String methodName, MethodVisitor method, String modName, ClassVisitor module) throws Exception{
                codeGenShallowStatement(stat.init, methodName, method, modName, module);
                
                Label loopBegin = new Label();
                method.visitJumpInsn(Opcodes.GOTO, loopBegin);
                
                Label loopBody = new Label();
                method.visitLabel(loopBody);
                codeGenShallowStatement(stat.stat, methodName, method, modName, module);
                codeGenShallowStatement(stat.change, methodName, method, modName, module);
                
                method.visitLabel(loopBegin);
                codeGenShallowExpression(stat.exp, method, modName, module);
                method.visitMethodInsn(Opcodes.INVOKEINTERFACE,
      "ede/stl/values/Value", // Internal name of the class
      "boolValue",           // Name of the static method
      "()Z",                        // Method descriptor (void return, no args)
      true);
                method.visitJumpInsn(Opcodes.IFNE, loopBody);
        }
        
        private void codeGenShallowRepeatLoop(RepeatStatement loop, String methodName, MethodVisitor method, String modName, ClassVisitor module) throws Exception{
                 pushInt(0, method);
                 int localArg1 = this.localAndArgNumber;
                 method.visitVarInsn(Opcodes.ISTORE, localArg1);
                 this.localAndArgNumber++;
                 codeGenShallowExpression(loop.exp, method, modName, module);
                 method.visitMethodInsn(Opcodes.INVOKEINTERFACE,
              "ede/stl/values/Value", // Internal name of the class
              "intValue",           // Name of the static method
              "()I",                        // Method descriptor (void return, no args)
              true);
                 int localArg2 = this.localAndArgNumber;
                 method.visitVarInsn(Opcodes.ISTORE, localArg2);
                 this.localAndArgNumber++;
                 
                 Label loopBegin = new Label();
                 method.visitJumpInsn(Opcodes.GOTO, loopBegin);
                 
                 Label loopBody = new Label();
                 method.visitLabel(loopBody);
                 codeGenShallowStatement(loop.stat, methodName, method, modName, module);
                 method.visitIincInsn(localArg1, 1);
                 
                 method.visitLabel(loopBegin);
                 method.visitVarInsn(Opcodes.ILOAD, localArg1);
                 method.visitVarInsn(Opcodes.ILOAD, localArg2);
                 method.visitJumpInsn(Opcodes.IF_ICMPLT, loopBody);
        }
        
        private void codeGenShallowWhileLoop(WhileStatement loop, String methodName, MethodVisitor method, String modName, ClassVisitor module) throws Exception {
                Label loopBegin = new Label();
                method.visitJumpInsn(Opcodes.GOTO, loopBegin);
                
                Label loopBody = new Label();
                method.visitLabel(loopBody);
                codeGenShallowStatement(loop.stat, methodName, method, modName, module);
                
                method.visitLabel(loopBegin);
                codeGenShallowExpression(loop.exp, method, modName, module);
                method.visitMethodInsn(Opcodes.INVOKEINTERFACE,
                "ede/stl/values/Value", // Internal name of the class
                "boolValue",           // Name of the static method
                "()Z",                        // Method descriptor (void return, no args)
                true);
                method.visitJumpInsn(Opcodes.IFNE, loopBody);
        }

        private void codeGenShallowAssignment(Assignment assign, String methodName, MethodVisitor method, String moduleName, ClassVisitor module) throws Exception {
                if(assign instanceof BlockingAssignment) codeGenShallowBlockingAssign((BlockingAssignment)assign, methodName, method, moduleName, module);
                else if(assign instanceof NonBlockingAssignment) codeGenShallowNonBlockingAssign((NonBlockingAssignment)assign, method, moduleName, module);
                else Utils.errorAndExit("Invalid Assignment found");
        }
        
        protected void codeGenShallowBlockingAssign(BlockingAssignment assign, String funcName, MethodVisitor method, String modName, ClassVisitor module) throws Exception {
                 if(assign.leftHandSide instanceof Element) {
                          Element leftHandElement = (Element)assign.leftHandSide;
                          if(this.localInScope(leftHandElement.labelIdentifier)){
                                int ptr = this.getFromScope(leftHandElement.labelIdentifier);
                                 method.visitVarInsn(Opcodes.ALOAD, ptr);
                          } else if(this.fieldInScope(leftHandElement.labelIdentifier)) {
                                String getType = getTypeFromFieldScope(leftHandElement.labelIdentifier);
                                method.visitVarInsn(Opcodes.ALOAD, 0);
                                method.visitFieldInsn(Opcodes.GETFIELD, 
                                modName, // Owner class internal name
                                leftHandElement.labelIdentifier,           // Field name
                                getType);
                          } else {
                                Utils.errorAndExit("Error can not find left hand side of assignment" + leftHandElement.labelIdentifier);
                          }
                          codeGenShallowExpression(leftHandElement.index1, method, modName, module);
                          codeGenShallowExpression(assign.rightHandSide, method, modName, module);
                          method.visitMethodInsn(Opcodes.INVOKESTATIC,
                          "ede/stl/common/Utils", // Internal name of the class
                          "shallowAssignElem",           // Name of the static method
                          "(Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;)V",                        // Method descriptor (void return, no args)
                          false);      
                 } else if(assign.leftHandSide instanceof Slice) {
                         Slice slice = (Slice)assign.leftHandSide;
                         
                         if(this.localInScope(slice.labelIdentifier)) {
                                 int ptr = this.getFromScope(slice.labelIdentifier);
                                 method.visitVarInsn(Opcodes.ALOAD, ptr);
                         } else if(this.fieldInScope(slice.labelIdentifier)) {
                                 String typeSlice = getTypeFromFieldScope(slice.labelIdentifier);
                                 method.visitVarInsn(Opcodes.ALOAD, 0);
                                 method.visitFieldInsn(Opcodes.GETFIELD, 
                                 modName, // Owner class internal name
                                 slice.labelIdentifier,           // Field name
                                 typeSlice);
                         } else {
                                 Utils.errorAndExit("Error cant find ident " + slice.labelIdentifier);
                         }
                         codeGenShallowExpression(slice.index2, method, modName, module);
                         codeGenShallowExpression(slice.index1, method, modName, module);
                         codeGenShallowExpression(assign.rightHandSide, method, modName, module);
                         method.visitMethodInsn(Opcodes.INVOKESTATIC,
                         "ede/stl/common/Utils", // Internal name of the class
                         "shallowAssignSlice",           // Name of the static method
                         "(Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;)V",                        // Method descriptor (void return, no args)
                         false);      
                 } else if(assign.leftHandSide instanceof Identifier) {
                         Identifier ident = (Identifier)assign.leftHandSide;
                         if((ident.labelIdentifier + "Shallow").equals(funcName) || (ident.labelIdentifier + "Deep").equals(funcName)) {
                                 codeGenShallowExpression(assign.rightHandSide, method, modName, module);
                                 method.visitInsn(Opcodes.ARETURN);
                         } else {
                                 if(localInScope(ident.labelIdentifier)) {
                                         codeGenShallowExpression(assign.rightHandSide, method, modName, module);
                                         int ptr = this.getFromScope(ident.labelIdentifier);
                                         method.visitVarInsn(Opcodes.ASTORE, ptr);
                                 } else if(fieldInScope(ident.labelIdentifier)){
                                         String myType = getTypeFromFieldScope(ident.labelIdentifier);
                                         method.visitVarInsn(Opcodes.ALOAD, 0);
                                         method.visitFieldInsn(Opcodes.GETFIELD, 
                                         modName,
                                         ident.labelIdentifier,
                                         myType);
                                         codeGenShallowExpression(assign.rightHandSide, method, modName, module);
                                         method.visitMethodInsn(Opcodes.INVOKESTATIC,
                                                                "ede/stl/common/Utils", // Internal name of the class
                                                                "shallowAssignValue",           // Name of the static method
                                                                "(Lede/stl/values/Value;Lede/stl/values/Value;)V",                        // Method descriptor (void return, no args)
                                                                false); 
                                 } else {
                                         Utils.errorAndExit("Error cant find left hand side of assign " + ident + "\n in assignment " + assign.toString() + "\nin function" + funcName + "\nin module " + modName);
                                 }
                         }
                 } else {
                         Utils.errorAndExit("Error invalid type for LValue in assignment");
                 }
        }
        
        private void codeGenShallowNonBlockingAssign(NonBlockingAssignment assign, MethodVisitor method, String modName, ClassVisitor module) throws Exception {
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
                                String labelType = getTypeFromFieldScope(leftHandElement.labelIdentifier);
                                method.visitVarInsn(Opcodes.ALOAD, 0);
                                method.visitFieldInsn(Opcodes.GETFIELD, 
                                modName, // Owner class internal name
                                leftHandElement.labelIdentifier,           // Field name
                                labelType);
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
                                 String labelType = getTypeFromFieldScope(slice.labelIdentifier);
                                 method.visitVarInsn(Opcodes.ALOAD, 0);
                                 method.visitFieldInsn(Opcodes.GETFIELD, 
                        modName, // Owner class internal name
                        slice.labelIdentifier,           // Field name
                        labelType);
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
                                 String labelType = getTypeFromFieldScope(ident.labelIdentifier);
                                 // Value is already on stack from pre-computed RHS; ALOAD_0 goes on top.
                                 // PUTFIELD needs [objectref, value]; SWAP corrects [value, objectref] → [objectref, value].
                                 method.visitVarInsn(Opcodes.ALOAD, 0);
                                 method.visitInsn(Opcodes.SWAP);
                                 method.visitFieldInsn(Opcodes.GETFIELD, modName, ident.labelIdentifier, labelType);
                                 method.visitMethodInsn(Opcodes.INVOKESTATIC,
                                                        "ede/stl/common/Utils", // Internal name of the class
                                                        "shallowAssignValue",           // Name of the static method
                                                        "(Lede/stl/values/Value;Lede/stl/values/Value;)V",                        // Method descriptor (void return, no args)
                                                        false);      
                         } else {
                                 Utils.errorAndExit("Error field or local variable for ident " + ident + " was not found!!!");
                         }
                 } else {
                         Utils.errorAndExit("Error invalid type for LValue in assignment");
                 }
                }
        }
        
        protected void codeGenShallowTaskCall(TaskStatement stat, MethodVisitor method, String modName, ClassVisitor module) throws Exception {
                if(stat instanceof SystemTaskStatement) {
                        codeGenShallowSystemTaskCall((SystemTaskStatement)stat, method, modName, module);
                } else {
                        String typeStr = funcTypes.getEntry(stat.taskName + "Shallow");
                        method.visitVarInsn(Opcodes.ALOAD, 0);
                        method.visitVarInsn(Opcodes.ALOAD, 1);
                        method.visitVarInsn(Opcodes.ALOAD, 2);
                        
                        for(Expression exp: stat.argumentList) {
                                codeGenShallowExpression(exp, method, modName, module);
                        }
                        
                        method.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
        modName,
        stat.taskName + "Shallow",
        typeStr,
        false);
                }
        }
        
        protected void codeGenShallowSystemTaskCall(SystemTaskStatement stat, MethodVisitor method, String modName, ClassVisitor module) throws Exception {
                if(stat.taskName.equals("fclose")){
                        codeGenShallowExpression(stat.argumentList.get(0), method, modName, module);
                        method.visitVarInsn(Opcodes.ALOAD, 2);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "fClose", "(Lede/stl/values/Value;Lede/stl/common/Environment;)V", false);
                } else if(stat.taskName.equals("display")){
                        codeGenShallowExpression(stat.argumentList.get(0), method, modName, module);
                        method.visitIntInsn(Opcodes.BIPUSH, stat.argumentList.size() - 1);
                        method.visitTypeInsn(Opcodes.ANEWARRAY, "ede/stl/values/Value");
                        for(int i = 1; i < stat.argumentList.size(); i++){
                                method.visitInsn(Opcodes.DUP);
                                method.visitIntInsn(Opcodes.BIPUSH, i - 1);
                                codeGenShallowExpression(stat.argumentList.get(i), method, modName, module);
                                method.visitInsn(Opcodes.AASTORE); 
                        }
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "formatString", "(Lede/stl/values/Value;[Lede/stl/values/Value;)Ljava/lang/String;", false);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "display", "(Ljava/lang/String;)V", false);
                } else if(stat.taskName.equals("finish")){
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "finish", "()V", false);
                }
        }
        
        private void codeGenShallowBlockOfStatements(SeqBlockStatement stat, String methodName, MethodVisitor method, String modName, ClassVisitor module) throws Exception{
                for(Statement myStat: stat.statementList){
                        codeGenShallowStatement(myStat, methodName, method, modName, module);
                }
        }

        private void codeGenField(ModuleItem declaration, MethodVisitor constructor, String modName, ClassVisitor moduleWriter)
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

        private void codeGenFieldArray(ArrayDeclaration declaration, MethodVisitor constructor, String modName, ClassVisitor modWriter) throws Exception{
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

        private void codeGenFieldRegScalarArray(Reg.Scalar.Array arr, MethodVisitor constructor, String modName, ClassVisitor modWriter) throws Exception{
                        modWriter.visitField(Opcodes.ACC_PRIVATE, arr.declarationIdentifier, "Lede/stl/values/ArrayRegVal;", null, null);
                        constructor.visitVarInsn(Opcodes.ALOAD, 0);
                        constructor.visitTypeInsn(Opcodes.NEW, "ede/stl/values/ArrayRegVal");
                        constructor.visitInsn(Opcodes.DUP);
                        codeGenShallowExpression(arr.arrayIndex1, constructor, modName, modWriter);
                        codeGenShallowExpression(arr.arrayIndex2, constructor, modName, modWriter);
                        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/ArrayRegVal", "<init>", "(Lede/stl/values/Value;Lede/stl/values/Value;)V", false);
                        constructor.visitFieldInsn(Opcodes.PUTFIELD, modName, arr.declarationIdentifier, "Lede/stl/values/ArrayRegVal;");
                        addField(arr.declarationIdentifier, "Lede/stl/values/ArrayRegVal;");
        }

        protected void codeGenFieldRegVectorArray(Reg.Vector.Array arr, MethodVisitor constructor, String modName, ClassVisitor modWriter) throws Exception{
                        modWriter.visitField(Opcodes.ACC_PRIVATE, arr.declarationIdentifier, "Lede/stl/values/ArrayVectorVal;", null, null);
                        constructor.visitVarInsn(Opcodes.ALOAD, 0);
                        constructor.visitTypeInsn(Opcodes.NEW, "ede/stl/values/ArrayVectorVal");
                        constructor.visitInsn(Opcodes.DUP);
                        codeGenShallowExpression(arr.arrayIndex1, constructor, modName, modWriter);
                        codeGenShallowExpression(arr.arrayIndex2, constructor, modName, modWriter);
                        codeGenShallowExpression(arr.GetIndex1(), constructor, modName, modWriter);
                        codeGenShallowExpression(arr.GetIndex2(), constructor, modName, modWriter);
                        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/ArrayVectorVal", "<init>", "(Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;)V", false);
                        constructor.visitFieldInsn(Opcodes.PUTFIELD, modName, arr.declarationIdentifier, "Lede/stl/values/ArrayVectorVal;");
                        addField(arr.declarationIdentifier, "Lede/stl/values/ArrayVectorVal;");
        }

        private void codeGenFieldIntArray(Int.Array arr, MethodVisitor constructor, String modName, ClassVisitor modWriter) throws Exception{
                modWriter.visitField(Opcodes.ACC_PRIVATE, arr.declarationIdentifier, "Lede/stl/values/ArrayIntVal;", null, null);
                constructor.visitVarInsn(Opcodes.ALOAD, 0);
                constructor.visitTypeInsn(Opcodes.NEW, "ede/stl/values/ArrayIntVal");
                constructor.visitInsn(Opcodes.DUP);
                codeGenShallowExpression(arr.arrayIndex1, constructor, modName, modWriter);
                codeGenShallowExpression(arr.arrayIndex2, constructor, modName, modWriter);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/ArrayIntVal", "<init>", "(Lede/stl/values/Value;Lede/stl/values/Value;)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, modName, arr.declarationIdentifier, "Lede/stl/values/ArrayIntVal;");
                addField(arr.declarationIdentifier, "Lede/stl/values/ArrayIntVal;");
        }

        private void initVectorValField(String fieldName, MethodVisitor constructor, String modName, Expression idx1, Expression idx2, ClassVisitor modWriter) throws Exception {
                constructor.visitVarInsn(Opcodes.ALOAD, 0);
                constructor.visitTypeInsn(Opcodes.NEW, "ede/stl/values/VectorVal");
                constructor.visitInsn(Opcodes.DUP);
                codeGenShallowExpression(idx1, constructor, modName, modWriter);
                constructor.visitMethodInsn(Opcodes.INVOKEINTERFACE, "ede/stl/values/Value", "intValue", "()I", true);
                codeGenShallowExpression(idx2, constructor, modName, modWriter);
                constructor.visitMethodInsn(Opcodes.INVOKEINTERFACE, "ede/stl/values/Value", "intValue", "()I", true);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/VectorVal", "<init>", "(II)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, modName, fieldName, "Lede/stl/values/VectorVal;");
        }

        private void codeGenFieldInputWireVectorIdent(Input.Wire.Vector.Ident declaration, MethodVisitor constructor, String modName, ClassVisitor modWriter) throws Exception{
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/VectorVal;", null, null);
                initVectorValField(declaration.declarationIdentifier, constructor, modName, declaration.GetIndex1(), declaration.GetIndex2(), modWriter);
                addField(declaration.declarationIdentifier, "Lede/stl/values/VectorVal;");
        }

        private void codeGenFieldInputRegVectorIdent(Input.Reg.Vector.Ident declaration, MethodVisitor constructor, String modName, ClassVisitor modWriter) throws Exception{
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/VectorVal;", null, null);
                initVectorValField(declaration.declarationIdentifier, constructor, modName, declaration.GetIndex1(), declaration.GetIndex2(), modWriter);
                addField(declaration.declarationIdentifier, "Lede/stl/values/VectorVal;");
        }

        private void codeGenFieldInputWireScalarIdent(Input.Wire.Scalar.Ident declaration, MethodVisitor constructor, String modName, ClassVisitor modWriter){
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/circuit/WireVal;", null, null);
                constructor.visitVarInsn(Opcodes.ALOAD, 0);
                constructor.visitTypeInsn(Opcodes.NEW, "ede/stl/circuit/WireVal");
                constructor.visitInsn(Opcodes.DUP);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/WireVal", "<init>", "()V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, modName, declaration.declarationIdentifier, "Lede/stl/cirdfcuit/WireVal;");
                addField(declaration.declarationIdentifier, "Lede/stl/circuit/WireVal;");
        }

        private void codeGenFieldInputRegScalarIdent(Input.Reg.Scalar.Ident declaration, MethodVisitor constructor, String modName, ClassVisitor modWriter){
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/RegVal;", null, null);
                constructor.visitVarInsn(Opcodes.ALOAD, 0);
                constructor.visitTypeInsn(Opcodes.NEW, "ede/stl/values/RegVal");
                constructor.visitInsn(Opcodes.DUP);
                constructor.visitIntInsn(Opcodes.BIPUSH, 0);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/RegVal", "<init>", "(B)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, modName, declaration.declarationIdentifier, "Lede/stl/values/RegVal;");
                addField(declaration.declarationIdentifier, "Lede/stl/values/RegVal;");
        }

        private void codeGenFieldOutputWireVectorIdent(Output.Wire.Vector.Ident declaration, MethodVisitor constructor, String modName, ClassVisitor modWriter) throws Exception{
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/VectorVal;", null, null);
                initVectorValField(declaration.declarationIdentifier, constructor, modName, declaration.GetIndex1(), declaration.GetIndex2(), modWriter);
                addField(declaration.declarationIdentifier, "Lede/stl/values/VectorVal;");
        }

        private void codeGenFieldOutputRegVectorIdent(Output.Reg.Vector.Ident declaration, MethodVisitor constructor, String moduleName, ClassVisitor modWriter) throws Exception{
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/VectorVal;", null, null);
                initVectorValField(declaration.declarationIdentifier, constructor, moduleName, declaration.GetIndex1(), declaration.GetIndex2(), modWriter);
                addField(declaration.declarationIdentifier, "Lede/stl/values/VectorVal;");
        }

        private void codeGenFieldOutputWireScalarIdent(Output.Wire.Scalar.Ident declaration, MethodVisitor constructor, String modName, ClassVisitor modWriter){
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/circuit/WireVal;", null, null);
                constructor.visitVarInsn(Opcodes.ALOAD, 0);
                constructor.visitTypeInsn(Opcodes.NEW, "ede/stl/circuit/WireVal");
                constructor.visitInsn(Opcodes.DUP);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/WireVal", "<init>", "()V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, modName, declaration.declarationIdentifier, "Lede/stl/circuit/WireVal;");
                addField(declaration.declarationIdentifier, "Lede/stl/circuit/WireVal;");
        }

        private void codeGenFieldOutputRegScalarIdent(Output.Reg.Scalar.Ident declaration, MethodVisitor constructor, String modName, ClassVisitor modWriter){
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/RegVal;", null, null);
                constructor.visitVarInsn(Opcodes.ALOAD, 0);
                constructor.visitTypeInsn(Opcodes.NEW, "ede/stl/values/RegVal");
                constructor.visitInsn(Opcodes.DUP);
                constructor.visitIntInsn(Opcodes.BIPUSH, 0);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/RegVal", "<init>", "(Z)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, modName, declaration.declarationIdentifier, "Lede/stl/values/RegVal;");
                addField(declaration.declarationIdentifier, "Lede/stl/values/RegVal;");
        }

        private void codeGenFieldWireVectorIdent(Wire.Vector.Ident declaration, MethodVisitor constructor, String moduleName, ClassVisitor modWriter)
                throws Exception{
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/VectorVal;", null, null);
                initVectorValField(declaration.declarationIdentifier, constructor, moduleName, declaration.GetIndex1(), declaration.GetIndex2(), modWriter);
                addField(declaration.declarationIdentifier, "Lede/stl/values/VectorVal;");
        }

        protected void codeGenFieldRegVectorIdent(Reg.Vector.Ident declaration, MethodVisitor constructor, String moduleName, ClassVisitor modWriter)
                throws Exception{
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/VectorVal;", null, null);
                initVectorValField(declaration.declarationIdentifier, constructor, moduleName, declaration.GetIndex1(), declaration.GetIndex2(), modWriter);
                addField(declaration.declarationIdentifier, "Lede/stl/values/VectorVal;");
        }

        private void codeGenFieldWireScalarIdent(Wire.Scalar.Ident declaration, MethodVisitor constructor, String modName, ClassVisitor modWriter){
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/circuit/WireVal;", null, null);
                constructor.visitVarInsn(Opcodes.ALOAD, 0);
                constructor.visitTypeInsn(Opcodes.NEW, "ede/stl/circuit/WireVal");
                constructor.visitInsn(Opcodes.DUP);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/circuit/WireVal", "<init>", "()V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, modName, declaration.declarationIdentifier, "Lede/stl/circuit/WireVal;");
                addField(declaration.declarationIdentifier, "Lede/stl/circuit/WireVal;");
        }

        protected void codeGenFieldRegScalarIdent(Reg.Scalar.Ident declaration, MethodVisitor constructor, String modName, ClassVisitor modWriter){
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/RegVal;", null, null);
                constructor.visitVarInsn(Opcodes.ALOAD, 0);
                constructor.visitTypeInsn(Opcodes.NEW, "ede/stl/values/RegVal");
                constructor.visitInsn(Opcodes.DUP);
                constructor.visitIntInsn(Opcodes.BIPUSH, 0);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/RegVal", "<init>", "(Z)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, modName, declaration.declarationIdentifier, "Lede/stl/values/RegVal;");
                addField(declaration.declarationIdentifier, "Lede/stl/values/RegVal;");
        }

        private void codeGenFieldIntIdent(Int.Ident declaration, MethodVisitor constructor, String modName, ClassVisitor modWriter){
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/IntVal;", null, null);
                constructor.visitVarInsn(Opcodes.ALOAD, 0);
                constructor.visitTypeInsn(Opcodes.NEW, "ede/stl/values/IntVal");
                constructor.visitInsn(Opcodes.DUP);
                pushInt(0, constructor);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/IntVal", "<init>", "(I)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, modName, declaration.declarationIdentifier, "Lede/stl/values/IntVal;");
                addField(declaration.declarationIdentifier, "Lede/stl/values/IntVal;");
        }

        private void codeGenFieldRealIdent(Real.Ident declaration, MethodVisitor constructor, String modName, ClassVisitor modWriter){
                modWriter.visitField(Opcodes.ACC_PRIVATE, declaration.declarationIdentifier, "Lede/stl/values/RealVal;", null, null);
                constructor.visitVarInsn(Opcodes.ALOAD, 0);
                constructor.visitTypeInsn(Opcodes.NEW, "ede/stl/values/RealVal");
                constructor.visitInsn(Opcodes.DUP);
                pushDouble(0.0, constructor);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/RealVal", "<init>", "(D)V", false);
                constructor.visitFieldInsn(Opcodes.PUTFIELD, modName, declaration.declarationIdentifier, "Lede/stl/values/RealVal;");
                addField(declaration.declarationIdentifier, "Lede/stl/values/RealVal;");
        }
        
        protected void codeGenShallowExpression(Expression exp, MethodVisitor method, String moduleName, ClassVisitor module) throws Exception {
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
        
        private void codeGenShallowBinaryOperation(BinaryOperation op, MethodVisitor method, String moduleName, ClassVisitor module) throws Exception {
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
        
        private void codeGenShallowUnaryOperation(UnaryOperation op, MethodVisitor method, String moduleName, ClassVisitor module) throws Exception{
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
        
        private void codeGenShallowConcatenation(Concatenation concat, MethodVisitor method, String moduleName, ClassVisitor module) throws Exception {
                pushInt(0, method);
                int argNum = this.localAndArgNumber;
                method.visitVarInsn(Opcodes.ISTORE, argNum);
                this.localAndArgNumber++;
                
                for(Expression exp: concat.circuitElementExpressionList) {
                        codeGenShallowExpression(exp, method, moduleName, module);
                        method.visitVarInsn(Opcodes.ILOAD, argNum);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "addVecSize", "(Lede/stl/values/Value;I)I", false);
                        method.visitVarInsn(Opcodes.ISTORE, argNum);
                }

                method.visitIincInsn(argNum, -1);
                method.visitTypeInsn(Opcodes.NEW, "ede/stl/values/VectorVal");
                method.visitInsn(Opcodes.DUP);
                method.visitVarInsn(Opcodes.ILOAD, argNum);
                method.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/VectorVal", "<init>", "(I)V", false);
                
                for(Expression exp: concat.circuitElementExpressionList) {
                        method.visitInsn(Opcodes.DUP);
                        codeGenShallowExpression(exp, method, moduleName, module);
                        method.visitVarInsn(Opcodes.ILOAD, argNum);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "assignVectorInConcatenation", "(Lede/stl/values/VectorVal;Lede/stl/values/Value;I)I", false);
                        method.visitVarInsn(Opcodes.ISTORE, argNum);
                }
        }
        
        private void codeGenShallowFunctionCall(FunctionCall call, MethodVisitor method, String moduleName, ClassVisitor module) throws Exception{
                if(call instanceof SystemFunctionCall)
                        codeGenShallowSystemFunctionCall((SystemFunctionCall)call, method, moduleName, module);
                else {
                        method.visitVarInsn(Opcodes.ALOAD, 0);
                        method.visitVarInsn(Opcodes.ALOAD, 1);
                        method.visitVarInsn(Opcodes.ALOAD, 2);
                        for(Expression exp: call.argumentList){
                                codeGenShallowExpression(exp, method, moduleName, module);
                        }

                        if(!this.funcTypes.entryExists(call.functionName + "Shallow"))
                            Utils.errorAndExit("Error funcTypes does not exist for " + call.functionName + "Shallow");

                        method.visitMethodInsn(Opcodes.INVOKEVIRTUAL, moduleName, call.functionName + "Shallow", this.funcTypes.getEntry(call.functionName + "Shallow"), false);
                }
        }

        private void codeGenShallowSystemFunctionCall(SystemFunctionCall call, MethodVisitor method, String moduleName, ClassVisitor module) throws Exception {
                if(call.functionName.equals("fopen")){
                        codeGenShallowExpression(call.argumentList.get(0), method, moduleName, module);
                        codeGenShallowExpression(call.argumentList.get(1), method, moduleName, module);
                        method.visitVarInsn(Opcodes.ALOAD, 2);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "fOpen", "(Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/common/Environment;)Lede/stl/values/IntVal;", false);
                } else if(call.functionName.equals("feof")){
                        codeGenShallowExpression(call.argumentList.get(0), method, moduleName, module);
                        method.visitVarInsn(Opcodes.ALOAD, 2);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "fEof", "(Lede/stl/values/Value;Lede/stl/common/Environment;)Lede/stl/values/BoolVal;", false);     
                } else if(call.functionName.equals("fscanf")){
                        codeGenShallowExpression(call.argumentList.get(0), method, moduleName, module);
                        codeGenShallowExpression(call.argumentList.get(1), method, moduleName, module);
                        codeGenShallowExpression(call.argumentList.get(2), method, moduleName, module);
                        method.visitVarInsn(Opcodes.ALOAD, 2);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "fScanf", "(Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/common/Environment;)Lede/stl/values/Value;", false);
                }
        }
        
        private void codeGenShallowTernaryOperation(TernaryOperation call, MethodVisitor method, String moduleName, ClassVisitor module) throws Exception {
                codeGenShallowExpression(call.condition, method, moduleName, module);
                method.visitMethodInsn(Opcodes.INVOKEINTERFACE,
                        "ede/stl/values/Value", // Internal name of the class
                        "boolValue",           // Name of the static method
                        "()Z", true);
                Label elseLabel = new Label();
                Label endLabel = new Label();
                method.visitJumpInsn(Opcodes.IFEQ, elseLabel);
                codeGenShallowExpression(call.ifTrue, method, moduleName, module);
                method.visitJumpInsn(Opcodes.GOTO, endLabel);
                method.visitLabel(elseLabel);
                codeGenShallowExpression(call.ifFalse, method, moduleName, module);
                method.visitLabel(endLabel);
        }
        
        private void codeGenShallowBinaryNode(BinaryNode node, MethodVisitor method, String moduleName, ClassVisitor module) throws Exception {
                int indexOfColon = node.lexeme.indexOf('\'');
                
                if(indexOfColon == -1)
                        Utils.errorAndExit("Error malformed BinaryNode!!!");
                
                String beforeIndex = node.lexeme.substring(0, indexOfColon);
                String afterIndex = node.lexeme.substring(indexOfColon + 2, node.lexeme.length());
                
                if(Utils.numberIsPattern(afterIndex)) {
                        method.visitTypeInsn(Opcodes.NEW, "ede/stl/values/BinaryPattern");
                        method.visitInsn(Opcodes.DUP);
                        pushString(afterIndex, method);
                        method.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/BinaryPattern", "<init>", "(Ljava/lang/String;)V", false);
                } else {
                        long value = Long.parseUnsignedLong(afterIndex, 2);
                        pushLong(value, method);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "getOptimalUnsignedForm", "(J)Lede/stl/values/Value;", false);
                }
        }
        
        private void codeGenShallowHexadecimalNode(HexadecimalNode node, MethodVisitor method, String moduleName, ClassVisitor module) throws Exception {
                int indexOfColon = node.lexeme.indexOf('\'');
                
                if(indexOfColon == -1)
                        Utils.errorAndExit("Error malformed BinaryNode!!!");
                
                String beforeIndex = node.lexeme.substring(0, indexOfColon);
                String afterIndex = node.lexeme.substring(indexOfColon + 2, node.lexeme.length());
                
                if(Utils.numberIsPattern(afterIndex)) {
                        method.visitTypeInsn(Opcodes.NEW, "ede/stl/values/HexadecimalPattern");
                        method.visitInsn(Opcodes.DUP);
                        pushString(afterIndex, method);
                        method.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/HexadecimalPattern", "<init>", "(Ljava/lang/String;)V", false);
                } else {
                        long value = Long.parseUnsignedLong(afterIndex, 16);
                        pushLong(value, method);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "getOptimalUnsignedForm", "(J)Lede/stl/values/Value;", false);
                }
        }
        
        private void codeGenShallowDecimalNode(DecimalNode node, MethodVisitor method, String moduleName, ClassVisitor module) {
                int indexOfColon = node.lexeme.indexOf('\'');
                
                if(indexOfColon == -1) {
                        long value = Long.parseUnsignedLong(node.lexeme);
                        pushLong(value, method);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "getOptimalUnsignedForm", "(J)Lede/stl/values/Value;", false);
                } else {
                        String beforeIndex = node.lexeme.substring(0, indexOfColon);
                        String afterIndex = node.lexeme.substring(indexOfColon + 2, node.lexeme.length());
                        long value = Long.parseUnsignedLong(afterIndex, 10);
                        pushLong(value, method);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "getOptimalUnsignedForm", "(J)Lede/stl/values/Value;", false);
                }
        }
        
        private void codeGenShallowOctalNode(OctalNode node, MethodVisitor method, String moduleName, ClassVisitor module) throws Exception {
                int indexOfColon = node.lexeme.indexOf('\'');
                
                if(indexOfColon == -1)
                        Utils.errorAndExit("Error malformed BinaryNode!!!");
                
                String beforeIndex = node.lexeme.substring(0, indexOfColon);
                String afterIndex = node.lexeme.substring(indexOfColon + 2, node.lexeme.length());
                
                if(Utils.numberIsPattern(afterIndex)) {
                        method.visitTypeInsn(Opcodes.NEW, "ede/stl/values/OctalPattern");
                        method.visitInsn(Opcodes.DUP);
                        pushString(afterIndex, method);
                        method.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/OctalPattern", "<init>", "(Ljava/lang/String;)V", false);
                } else {
                        long value = Long.parseUnsignedLong(afterIndex, 8);
                        pushLong(value, method);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "getOptimalUnsignedForm", "(J)Lede/stl/values/Value;", false);
                }
        }
        
        private void codeGenShallowStringNode(StringNode node, MethodVisitor method, String moduleName, ClassVisitor module) {
                method.visitTypeInsn(Opcodes.NEW, "ede/stl/values/StrVal");
                method.visitInsn(Opcodes.DUP);
                pushString(node.lexeme, method);
                method.visitMethodInsn(Opcodes.INVOKESPECIAL, "ede/stl/values/StrVal", "<init>", "(Ljava/lang/String;)V", false);
        }
        
        private void codeGenShallowConstantExpression(ConstantExpression exp, MethodVisitor method, String moduleName, ClassVisitor module) throws Exception {
                codeGenShallowExpression(exp.expression, method, moduleName, module);
        }
        
        private void codeGenShallowElement(Element elem, MethodVisitor method, String moduleName, ClassVisitor module) throws Exception{
                if(this.localInScope(elem.labelIdentifier)) {
                        int num = this.getFromScope(elem.labelIdentifier);
                        method.visitVarInsn(Opcodes.ALOAD, num);
                } else if(this.fieldInScope(elem.labelIdentifier)){
                        String fieldType = getTypeFromFieldScope(elem.labelIdentifier);
                        method.visitVarInsn(Opcodes.ALOAD, 0);
                        method.visitFieldInsn(Opcodes.GETFIELD, 
                        moduleName, // Owner class internal name
                        elem.labelIdentifier,           // Field name
                        fieldType);
                } else {
                        Utils.errorAndExit("Error cant find elem from identifier " + elem.labelIdentifier);
                }

                codeGenShallowExpression(elem.index1, method, moduleName, module);
                
                pushString(elem.labelIdentifier, method);
                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "getShallowElemFromIndex", "(Lede/stl/values/Value;Lede/stl/values/Value;Ljava/lang/String;)Lede/stl/values/Value;", false);
        }
        
        private void codeGenShallowSlice(Slice slice, MethodVisitor method, String moduleName, ClassVisitor module) throws Exception{
                if(localInScope(slice.labelIdentifier)) {
                        int num = this.getFromScope(slice.labelIdentifier);
                        method.visitVarInsn(Opcodes.ALOAD, num);
                } else if(fieldInScope(slice.labelIdentifier)) {
                        String fieldType = getTypeFromFieldScope(slice.labelIdentifier);
                        method.visitVarInsn(Opcodes.ALOAD, 0);
                        method.visitFieldInsn(Opcodes.GETFIELD, 
                        moduleName, // Owner class internal name
                        slice.labelIdentifier,           // Field name
                        fieldType);
                } else {
                        Utils.errorAndExit("Error variable " + slice.labelIdentifier + " is not found!!!\nin module " + moduleName + "\nat position " + slice.position.toString());
                }

                codeGenShallowExpression(slice.index1, method, moduleName, module);
                codeGenShallowExpression(slice.index2, method, moduleName, module);
                
                pushString(slice.labelIdentifier, method);
                method.visitMethodInsn(Opcodes.INVOKESTATIC, "ede/stl/common/Utils", "getShallowSliceFromFromIndices", "(Lede/stl/values/Value;Lede/stl/values/Value;Lede/stl/values/Value;Ljava/lang/String;)Lede/stl/values/Value;", false);
        }

        private void codeGenDeepExpression(Expression exp, MethodVisitor method, String modName, ClassVisitor writer) throws Exception{
                codeGenShallowExpression(exp, method, modName, writer);
        }

        private void codeGenDeepStatement(Statement stat, String methodName, MethodVisitor method, String modName, ClassVisitor modWriter) throws Exception{
                codeGenShallowStatement(stat, methodName, method, modName, modWriter);
        }

        private void codeGenShallowIdentifier(Identifier ident, MethodVisitor method, String moduleName, ClassVisitor module) throws Exception{
                if(this.localInScope(ident.labelIdentifier)) {
                        Integer num = this.getFromScope(ident.labelIdentifier);
                        method.visitVarInsn(Opcodes.ALOAD, num);
                } else if(this.fieldInScope(ident.labelIdentifier)){
                        String fieldType = getTypeFromFieldScope(ident.labelIdentifier);
                        method.visitVarInsn(Opcodes.ALOAD, 0);
                        method.visitFieldInsn(Opcodes.GETFIELD, 
                        moduleName, // Owner class internal name
                        ident.labelIdentifier,           // Field name
                        fieldType);
                } else {
                        Utils.errorAndExit("Error identifier " + ident.labelIdentifier + " not found in scope!!!");
                }
        }
}
