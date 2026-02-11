package ede.stl.Value ;

import ede.stl.Value .Value;
import ede.stl.Value .VectorVal;

public class ArrayVectorVal extends ArrayVal<VectorVal>{
    public ArrayVectorVal.Value .arrayBegin,.Value .arrayEnd,.Value .vectorBegin,.Value .vectorEnd){
        super(arrayBegin, arrayEnd);
        int size = Math.abs(arrayBegin.in.Value .) - arrayEnd.in.Value .));
        for(int i = 0; i < size; i++){
            this.AddElem(new VectorVal(vectorBegin.in.Value .), vectorEnd.in.Value .)));
        }
    }
}


























































