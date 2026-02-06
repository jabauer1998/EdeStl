package io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.array;

import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.RegVal;

public class ArrayRegVal extends ArrayVal<RegVal> {
    public ArrayRegVal(Value arrayBegin, Value arrayEnd){
        super(arrayBegin, arrayEnd);
        int size = Math.abs(arrayBegin.intValue() - arrayEnd.intValue());
        for(int i = 0; i < size; i++){
            this.AddElem(new RegVal(false));
        }
    }
}
