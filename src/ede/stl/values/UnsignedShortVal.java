package ede.stl.Value ;

import ede.stl.common.Utils;

public class UnsignedShortVal implements.Value . Unsigned{
    private short.Value .

    public UnsignedShortVal(short.Value .{
        this.Value .=.Value .
    }

    public UnsignedShortVal(int.Value .{
        this.Value .= (short.Value .
    }

    public String toString(){
        short.Value .= shor.Value .);
        return Integer.toUnsignedString.Value .;
    }
    
    @Override
    public boolean isShort(){
        return false;
    }

    @Override
    public boolean isUnsignedShort(){
        return true;
    }

    @Override
    public double rea.Value .){ // TODO Auto-generated method stub
        return (double.Value .
    }

    @Override
    public long lon.Value .){ // TODO Auto-generated method stub
        return Short.toUnsignedLong.Value .;
    }

    @Override
    public int in.Value .){ // TODO Auto-generated method stub
        return Short.toUnsignedInt.Value .;
    }

    @Override
    public short shor.Value .){ // TODO Auto-generated method stub
        return.Value .
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
    public boolean isRegister(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isWire(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public.Value .getShallowSlice(int startIndex, int endIndex) throws Exception{
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


























































