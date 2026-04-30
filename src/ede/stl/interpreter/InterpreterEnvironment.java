package ede.stl.interpreter;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import ede.stl.common.SymbolTable;
import ede.stl.common.FormattedScanner;
import ede.stl.gui.GuiEde;
import ede.stl.values.Value;
import ede.stl.ast.ModuleDeclaration;
import ede.stl.ast.FunctionDeclaration;
import ede.stl.ast.TaskDeclaration;
import ede.stl.ast.ProcessBase;
import ede.stl.common.Environment;

/**
 * The environment class is what is supplied to the interpreter in order to Interpret the code
 * This includes several SymbolTables for each Type of Entry as well as a call stack
 */

public class InterpreterEnvironment extends Environment{
    private SymbolTable<ModuleDeclaration>  moduleTable;
        private SymbolTable<TaskDeclaration> taskTable;
        private SymbolTable<FunctionDeclaration> functionTable;
        private SymbolTable<Value> variableTable;

    private Stack<String> callStack;
        private Stack<Boolean> exitStack;

    private boolean InParamaterSequence;
    private boolean InFunctionBody;

    private List<ProcessBase> processList;

    public InterpreterEnvironment(){
        super();
        moduleTable = new SymbolTable<>();
        taskTable = new SymbolTable<>();
        functionTable = new SymbolTable<>();
        variableTable = new SymbolTable<>();
        callStack = new Stack<String>();
        exitStack = new Stack<Boolean>();
        processList = new LinkedList<ProcessBase>();
        moduleTable.addScope();
        taskTable.addScope();
        functionTable.addScope();
        variableTable.addScope();
    }

    /**
     * the module exists function is supposed to determine if a module exists ot not
     * @param symbol
     * @return boolean rerpresenting whether the module exists ot not
     */

    public boolean moduleExists(String symbol){
        return moduleTable.entryExists(symbol);
    }

    /**
     * the task Exists function is supposed to determine if a task exists ot not
     * @param symbol
     * @return boolean rerpresenting whether the task Exists ot not
     */

    public boolean taskExists(String symbol){
        return taskTable.entryExists(symbol);
    }

    /**
     * the functionExists function is supposed to determine if a functionExists or not
     * @param symbol
     * @return boolean rerpresenting whether the function exists ot not
     */

    public boolean functionExists(String symbol){
        return functionTable.entryExists(symbol);
    }

    /**
     * the localVariableExists function is supposed to determine if a variableExists or not
     * @param symbol
     * @return boolean rerpresenting whether the variable exists ot not
     */


    public boolean localVariableExists(String symbol){
        return variableTable.inScope(symbol);
    }

    /**
     * the variableExists function is supposed to determine if a variableExists or not
     * @param symbol
     * @return boolean rerpresenting whether the variable exists ot not
     */


    public boolean variableExists(String symbol){
        return variableTable.entryExists(symbol);
    }

    /**
     * 
     */

    public FunctionDeclaration lookupFunction(String symbol){
        return functionTable.getEntry(symbol);
    }

    public Value lookupVariable(String symbol){
        return variableTable.getEntry(symbol);
    }

    public TaskDeclaration lookupTask(String symbol){
        return taskTable.getEntry(symbol);
    }

    public ModuleDeclaration lookupModule(String symbol){
        return moduleTable.getEntry(symbol);
    }

    public void addModule(String symbol, ModuleDeclaration decl){
        moduleTable.addEntry(symbol, decl);
    }

    public void addTask(String symbol, TaskDeclaration decl){
        taskTable.addEntry(symbol, decl);
    }

    public void addFunction(String symbol, FunctionDeclaration decl){
        functionTable.addEntry(symbol, decl);
    }

    public void addProcess(ProcessBase process){
        this.processList.add(process);
    }

    public int getNumberOfProcesses(){
        return this.processList.size();    
    }

    public ProcessBase getProcess(int index){
        return this.processList.get(index);
    }

    public void addVariable(String symbol, Value value){
        variableTable.addEntry(symbol, value);
    }

    public void addStackFrame(String ScopeName){
        exitStack.push(false);
        callStack.push(ScopeName);
        variableTable.addScope();
    }

    public String removeStackFrame(){
        if(!exitStack.isEmpty())
            exitStack.pop();

        variableTable.removeScope();
        
        if(!callStack.isEmpty())
            return callStack.pop();

        return null;
    }

    public String stackFrameTitle(){
        if(callStack.isEmpty()) 
            return null;
        return callStack.peek();
    }

    public boolean stackFrameInExit(){
        if(exitStack.isEmpty())
            return false;

        return exitStack.peek();
    }

    public void setFunctionExit(){
        if(!exitStack.isEmpty()) 
            exitStack.pop();
        
        exitStack.push(true);
    }

    public void BeginParamaterDeclarations(){
        InParamaterSequence = true;
    }

    public void EndParamaterDeclarations(){
        InParamaterSequence = false;
    }

    public boolean DeclarationIsParamater(){
        return InParamaterSequence;
    }

    public void BeginFunctionBody(){
        InFunctionBody = true;
    }

    public void EndFunctionBody(){
        InFunctionBody = false;
    }

    public boolean InFunctionBody(){
        return InFunctionBody;
    }
}
