package ede.stl.Value;

import ede.stl.Value.Value;
import ede.stl.Value.RegVal;

public class ArrayRegVal extends ArrayVal<RegVal> {
    public ArrayRegVal(Value arrayBegin, Value arrayEnd){
        super(arrayBegin, arrayEnd);
        int size = Math.abs(arrayBegin.intValue() - arrayEnd.intValue());
        for(int i = 0; i < size; i++){
            this.AddElem(new RegVal(false));
        }
    }
}
