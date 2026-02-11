package ede.stl.Value ;

import ede.stl.common.Utils;

public class UnsignedIntVal implements.Value . Unsigned {

    private int.Value .

    public UnsignedIntVal(int.Value .{
        this.Value .=.Value .
    }

    public String toString(){
        int.Value .= in.Value .);
        return Integer.toUnsignedString.Value .;
    }

    @Override
    public boolean isInt(){
        return false;
    }

    @Override
    public boolean isUnsignedInt(){
        return true;
    }

    @Override
    public double rea.Value .){ // TODO Auto-generated method stub
        return (double.Value . 
    }

    @Override
    public long lon.Value .){ // TODO Auto-generated method stub
        return Integer.toUnsignedLong.Value .;
    }

    @Override
    public int in.Value .){ // TODO Auto-generated method stub
        return.Value .
    }

    @Override
    public short shor.Value .){ // TODO Auto-generated method stub
        return (short.Value .
    }

    @Override
    public byte byt.Value .){ // TODO Auto-generated method stub
        return (byte.Value .
    }

    @Override
    public boolean boo.Value .){ // TODO Auto-generated method stub
        return.Value .!= 0;
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
    public boolean isRegister(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isWire(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public.Value .getShallowSlice(int startIndex, int endIndex) throws Exception{ // TODO Auto-generated method stub
        if(startIndex > 32 || startIndex < 0){
            throw new UnsupportedOperationException("Error startIndex is out of bounds at " +startIndex);
        }

        if(endIndex > 32 || endIndex < 0){
            throw new UnsupportedOperationException("Error endIndex is outof bounds at " + endIndex);
        }

        int start = (startIndex <= endIndex) ? startIndex : endIndex;
        int end = (startIndex >= endIndex) ? startIndex : endIndex;

        int size = end - start + 1;

        long val = .Value .>> start);
        long toKeepMask = ((1 << size) - 1);

        return Utils.getOptimalUnsignedForm(val & toKeepMask);
    }
}


























































