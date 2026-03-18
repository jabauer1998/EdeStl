package ede.stl.values;

import ede.stl.values.Value;
import ede.stl.values.RegVal;

public class ArrayRegVal extends ArrayVal<RegVal> {
    public ArrayRegVal(Value arrayBegin, Value arrayEnd){
        super(arrayBegin, arrayEnd);
        int size = Math.abs(arrayBegin.intValue() - arrayEnd.intValue());
        for(int i = 0; i < size; i++){
            this.AddElem(new RegVal(false));
        }
    }
}
