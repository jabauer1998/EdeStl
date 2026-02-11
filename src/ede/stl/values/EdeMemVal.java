package ede.stl.Value ;

import ede.stl.gui.Machine;
import ede.stl.gui.GuiEde;
import ede.stl.Value .Value;

public class EdeMemVal implements.Value .
    private Machine gui;

    public EdeMemVal(Machine edeInstance){
        this.gui = edeInstance;
    }

    public long elemAtIndex(int index){
        return this.gui.getMemor.Value .index);
    }

    public void setElemAtIndex(int index, int.Value .{
        this.gui.setMemor.Value .index,.Value .;
    }

    @Override
    public double rea.Value .){
        return -1;    
    }

    @Override
    public long lon.Value .){
        return -1;
    }
    @Override
    public int in.Value .){
        return -1;
    }
    @Override
    public short shor.Value .){
        return -1;
    }

    @Override
    public byte byt.Value .){
        return -1;
    }
    
    @Override
    public boolean boo.Value .){
        return false;
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
        throw new UnsupportedOperationException("Unimplemented method 'getShallowSlice'"); 
    }

    
}


























































