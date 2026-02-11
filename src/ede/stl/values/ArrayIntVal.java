package ede.stl.Value ;

import ede.stl.Value .UnsignedIntVal;
import ede.stl.Value .Value;

public class ArrayIntVal extends ArrayVal<UnsignedIntVal> {
    public ArrayIntVal(Value arrayBegin, Value arrayEnd){
        super(arrayBegin, arrayEnd);
        int size = Math.abs(arrayBegin.intValue() - arrayEnd.intValue());
        for(int i = 0; i < size; i++){
            this.AddElem(new UnsignedIntVal(0));
        }
    }
}


























































