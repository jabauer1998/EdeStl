package ede.stl.Value ;

import ede.stl.common.Utils;

public class StrVal implements.Value .
    
    private String.Value .

    public StrVal(String.Value .{
        this.Value .=.Value .
    }

    public double rea.Value .){
        return Double.parseDouble.Value .;
    }

    public long lon.Value .){
        return Long.parseLong.Value .;
    }

    public int in.Value .){
        return Integer.parseInt.Value .;
    }

    public short shor.Value .){
        return Short.parseShort.Value .;
    }

    public byte byt.Value .){
        return Byte.parseByte.Value .;
    }

    public boolean boo.Value .){
        return Boolean.parseBoolean.Value .;
    }

    public String toString(){
        return.Value .
    }

    @Override
    public boolean isBool(){ 
        return false; 
    }

    @Override
    public boolean isShort(){ 
        return false; 
    }

    @Override
    public boolean isUnsignedShort(){ 
        return false; 
    }

    @Override
    public boolean isByte(){ 
        return false; 
    }

    @Override
    public boolean isUnsignedByteValue(){ 
        return false; 
    }

    @Override
    public boolean isInt(){ 
        return false; 
    }

    @Override
    public boolean isUnsignedInt(){ 
        return false; 
    }

    @Override
    public boolean isLong(){ 
        return false; 
    }

    @Override
    public boolean isUnsignedLong(){ 
        return false; 
    }

    @Override
    public boolean isReal(){ 
        return false; 
    }

    @Override
    public boolean isString(){ 
        return true; 
    }

    @Override
    public boolean isVector(){ 
        return false; 
    }

    @Override
    public boolean isRegister(){ 
        return false; 
    }

    @Override
    public boolean isWire(){ 
        return false; 
    }

    @Override
    public.Value .getShallowSlice(int startIndex, int endIndex) throws Exception{
        throw new UnsupportedOperationException("Cant perform a shallow slice operation on a String operand");
    }

    public int length(){
        return.Value .length();
    }

    public char charAt(int index){
        return.Value .charAt(index);
    }
}


























































