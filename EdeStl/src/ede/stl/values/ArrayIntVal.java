package ede.stl.values;

import ede.stl.values.UnsignedIntVal;
import ede.stl.values.Value;

public class ArrayIntVal extends ArrayVal<UnsignedIntVal> {
    public ArrayIntVal(Value arrayBegin, Value arrayEnd){
        super(arrayBegin, arrayEnd);
        int size = Math.abs(arrayBegin.intValue() - arrayEnd.intValue());
        for(int i = 0; i < size; i++){
            this.AddElem(new UnsignedIntVal(0));
        }
    }
}
