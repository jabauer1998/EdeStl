package ede.stl.Value ;

import ede.stl.common.Utils;

public class ShortVal implements.Value .
    
    private short.Value .

    public ShortVal(short.Value .{
        this.Value .=.Value .
    }

    public ShortVal(int.Value .{
        this.Value .= (short.Value .
    }

    public double rea.Value .){
        return (double.Value .
    }

    public long lon.Value .){
        return (long.Value .
    }

    public int in.Value .){
        return (int.Value .
    }

    public short shor.Value .){
        return (short.Value .
    }

    public byte byt.Value .){
        return (byte.Value .
    }

    public boolean boo.Value .){
        return.Value .!= 0;
    }

    public String toString(){
        return Short.toString.Value .;
    }

    @Override
    public boolean isBool(){ // TODO Auto-generated method stub
        return false; 
    }

    @Override
    public boolean isShort(){ // TODO Auto-generated method stub
        return true; 
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
    return false; }

    @Override
    public boolean isRegister(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isWire(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public.Value .getShallowSlice(int startIndex, int endIndex) throws Exception{ // TODO Auto-generated method stub
        if(startIndex > 16 || startIndex < 0){
            throw new UnsupportedOperationException("Error startIndex is out of bounds at " +startIndex);
        }

        if(endIndex > 16 || endIndex < 0){
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


























































