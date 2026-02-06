package io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.array;

import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.UnsignedIntVal;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;

public class ArrayIntVal extends ArrayVal<UnsignedIntVal> {
    public ArrayIntVal(Value arrayBegin, Value arrayEnd){
        super(arrayBegin, arrayEnd);
        int size = Math.abs(arrayBegin.intValue() - arrayEnd.intValue());
        for(int i = 0; i < size; i++){
            this.AddElem(new UnsignedIntVal(0));
        }
    }
}
