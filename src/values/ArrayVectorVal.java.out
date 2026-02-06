package io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.array;

import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.VectorVal;

public class ArrayVectorVal extends ArrayVal<VectorVal>{
    public ArrayVectorVal(Value arrayBegin, Value arrayEnd, Value vectorBegin, Value vectorEnd){
        super(arrayBegin, arrayEnd);
        int size = Math.abs(arrayBegin.intValue() - arrayEnd.intValue());
        for(int i = 0; i < size; i++){
            this.AddElem(new VectorVal(vectorBegin.intValue(), vectorEnd.intValue()));
        }
    }
}
