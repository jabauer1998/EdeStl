package ede.stl.values;

import ede.stl.gui.GuiEde;
import ede.stl.values.Value;

public class EdeMemVal implements Value{
    private GuiEde gui;
<<<<<<< HEAD
    private String name;

    public EdeMemVal(GuiEde edeInstance, String name){
=======

    public EdeMemVal(GuiEde edeInstance){
>>>>>>> 43413bcba0463d045023f18674670b136c8b6020
        this.gui = edeInstance;
	this.name = name;
    }

    public long elemAtIndex(int index){
        return this.gui.getMemoryValue(name, index);
    }

    public void setElemAtIndex(int index, int value){
        this.gui.setMemoryValue(name, index, value);
    }

    @Override
    public double realValue(){
        return -1;    
    }

    @Override
    public long longValue(){
        return -1;
    }
    @Override
    public int intValue(){
        return -1;
    }
    @Override
    public short shortValue(){
        return -1;
    }

    @Override
    public byte byteValue(){
        return -1;
    }
    
    @Override
    public boolean boolValue(){
        return false;
    }

    @Override
    public boolean isBoolValue(){
        return false;
    }
    @Override
    public boolean isShortValue(){
        return false;
    }
    @Override
    public boolean isUnsignedShortValue(){
        return false;
    }
    @Override
    public boolean isByteValue(){
        return false;
    }
    @Override
    public boolean isUnsignedByteValue(){
        return false;
    }
    @Override
    public boolean isIntValue(){
        return false;
    }

    @Override
    public boolean isUnsignedIntValue(){
        return false;
    }
    @Override
    public boolean isLongValue(){
        return false;
    }
    @Override
    public boolean isUnsignedLongValue(){
        return false;
    }
    @Override
    public boolean isRealValue(){
        return false;
    }
    @Override
    public boolean isStringValue(){
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
    public Value getShallowSlice(int startIndex, int endIndex) throws Exception{ // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getShallowSlice'"); 
    }

    @Override
    public void setValue(Value val){
        //do nothing 
    }

    @Override
    public VectorVal asVector(){
        throw new UnsupportedOperationException("Cannot convert EdeMemVal (memory array) to a vector");
    }
}
