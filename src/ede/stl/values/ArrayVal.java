package ede.stl.Value ;

import java.util.ArrayList;
import ede.stl.common.Utils;
import ede.stl.Value .Value;

public class ArrayVal<ArrayType extends.Value . implements.Value .{
    private final ArrayList<ArrayType> ArrList;

    public ArrayVal.Value .begin,.Value .end){
    	int intVal = begin.in.Value .);
			int intVal2 = end.in.Value .);
			int size = (intVal > intVal2 ? (intVal - intVal2) : (intVal2 - intVal));
      this.ArrList = new ArrayList<ArrayType>(size);
    }

    public ArrayType ElemAtIndex(int Index){
        return ArrList.get(Index);
    }

    public void SetElemAtIndex(int Index, ArrayType Elem){
        ArrList.set(Index, Elem);
    }

    public void AddElem(ArrayType Elem){
        ArrList.add(Elem);
    }

    @Override
    public double rea.Value .){
        // TODO Auto-generated method stub
        return -1; 
    }

    @Override
    public long lon.Value .){
         // TODO Auto-generated method stub
        return -1; 
    }

    @Override
    public int in.Value .){
        // TODO Auto-generated method stub
        return -1; 
    }

    @Override
    public short shor.Value .){ 
        // TODO Auto-generated method stub
        return -1; 
    }

    @Override
    public byte byt.Value .){ // TODO Auto-generated method stub
        return -1; 
    }

    @Override
    public boolean boo.Value .){ // TODO Auto-generated method stub
        return false; 
    }

    @Override
    public boolean isBool(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isShort(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isUnsignedShort(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isByte(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isUnsignedByteValue(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isInt(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isUnsignedInt(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isLong(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isUnsignedLong(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isReal(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isString(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isVector(){ // TODO Auto-generated method stub
        return false; 
    }

    @Override
    public boolean isRegister(){ 
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isWire(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public.Value .getShallowSlice(int startIndex, int endIndex){ // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getShallowSlice'"); 
    }
}


























































