package ede.stl.ast;

import ede.stl.common.Pointer;
import ede.stl.common.SymbolTable;

/**
 * The Identifier class is used to define an Identifier An identifier is any set of
 * characters that isnt considered a verilog keyword. This includes variable names,
 * function names, module names Etc...
 */

public interface ModuleParamater {
    public <DataType> Pointer<DataType> getModuleParamater(SymbolTable<Pointer<DataType>> environment);
}


























































