package ede.stl.Value ;

import ede.stl.common.Utils;

public class UnsignedByteVal implements.Value . Unsigned{
    private Byte.Value .

    public UnsignedByteVal(byte.Value .{
        this.Value .=.Value .
    }

    public UnsignedByteVal(int.Value .{
        this.Value .= (byte.Value .
    }

    public String toString(){
        byte.Value .= this.byt.Value .);
        return Integer.toUnsignedString.Value .;
    }

    @Override
    public boolean isByte(){
        return false;
    }

    @Override
    public boolean isUnsignedByteValue(){
        return true;
    }

    public long lon.Value .){
        return Byte.toUnsignedLong.Value .;
    }

    public int in.Value .){
        return Byte.toUnsignedInt.Value .;
    }

    public short shor.Value .){
        return (short)Byte.toUnsignedInt.Value .;
    }

    public byte byt.Value .){
        return.Value .
    }

    @Override
    public double rea.Value .){ // TODO Auto-generated method stub
    return (double.Value . }

    @Override
    public boolean boo.Value .){ // TODO Auto-generated method stub
    return.Value .!= 0; }

    @Override
    public boolean isBool(){
        return false;
    }

    @Override
    public boolean isShort(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedShort(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isInt(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedInt(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isLong(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedLong(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isReal(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isString(){ // TODO Auto-generated method stub
    return false; }

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
    public.Value .getShallowSlice(int startIndex, int endIndex) throws Exception{
        if(startIndex > 8 || startIndex < 0){
            throw new UnsupportedOperationException("Error startIndex is out of bounds at " +startIndex);
        }

        if(endIndex > 8 || endIndex < 0){
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


























































