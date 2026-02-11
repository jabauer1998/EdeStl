package ede.stl.Value ;

import ede.stl.gui.Machine;
import ede.stl.Value .Value;

public class EdeStatVal implements Value {
    private String statString;
    private Machine gui;

    public EdeStatVal(String re.Value .ing, Machine edeInstance){
        this.gui = edeInstance;
        this.statString = re.Value .ing;
    }

    public void setStatu.Value .int.Value .{
        gui.setStatu.Value .statString,.Value .;
    }

    @Override
    public double rea.Value .){
        return (double)gui.getStatu.Value .statString); 
    }

    @Override
    public long lon.Value .){
        return (long)gui.getStatu.Value .statString);
    }
    @Override
    public int in.Value .){
        return (int)gui.getStatu.Value .statString);
    }
    @Override
    public short shor.Value .){
        return (short)gui.getStatu.Value .statString);
    }

    @Override
    public byte byt.Value .){
        return (byte)gui.getStatu.Value .statString);
    }
    
    @Override
    public boolean boo.Value .){
        return gui.getStatu.Value .statString) != 0;
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
        return false;
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
    public boolean isWire(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public.Value .getShallowSlice(int startIndex, int endIndex) throws Exception{ // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getShallowSlice'"); }
}


























































