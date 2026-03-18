package ede.stl.values;

import ede.stl.values.Value;
import ede.stl.values.VectorVal;

public class ArrayVectorVal extends ArrayVal<VectorVal>{
    public ArrayVectorVal(Value arrayBegin, Value arrayEnd, Value vectorBegin, Value vectorEnd){
        super(arrayBegin, arrayEnd);
        int size = Math.abs(arrayBegin.intValue() - arrayEnd.intValue());
        for(int i = 0; i < size; i++){
            this.AddElem(new VectorVal(vectorBegin.intValue(), vectorEnd.intValue()));
        }
    }
}
