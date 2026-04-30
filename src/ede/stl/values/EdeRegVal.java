package ede.stl.values;

import ede.stl.gui.GuiEde;
import ede.stl.values.Value;

public class EdeRegVal implements Value{
    private String regString;
    private GuiEde gui;

    public EdeRegVal(String regValueString, GuiEde edeInstance){
        this.gui = edeInstance;
        this.regString = regValueString;
    }

    public void setAllBits(int value){
        gui.setRegisterValue(regString, new LongVal((long) value));
    }

    public void setBitAtIndex(int index, int value){
        VectorVal vec = gui.getRegisterVector(regString);
        if(index < 0 || index >= vec.getSize()) return;
        vec.setValue(index, new RegVal(value != 0));
        gui.setRegisterValue(regString, vec);
    }

    public long getBitAtIndex(int index){
        VectorVal vec = gui.getRegisterVector(regString);
        if(index < 0 || index >= vec.getSize()) return 0L;
        return vec.getValue(index).getStateSignal() ? 1L : 0L;
    }

    public void setBitsAtIndex(int maxIndex, int minIndex, int value){
        VectorVal vec = gui.getRegisterVector(regString);
        int width = vec.getSize();
        if(minIndex < maxIndex){
            int index = minIndex;
            int size = maxIndex - minIndex + 1;
            int numIndex = 0;
            while(index <= maxIndex && numIndex < size){
                if(index >= 0 && index < width){
                    boolean isSet = ((value >> numIndex) & 1) != 0;
                    vec.setValue(index, new RegVal(isSet));
                }
                index++;
                numIndex++;
            }
        } else {
            int index = minIndex;
            int size = minIndex - maxIndex + 1;
            int numIndex = 0;
            while(index >= maxIndex && numIndex < size){
                if(index >= 0 && index < width){
                    boolean isSet = ((value >> numIndex) & 1) != 0;
                    vec.setValue(index, new RegVal(isSet));
                }
                index--;
                numIndex++;
            }
        }
        gui.setRegisterValue(regString, vec);
    }

    public long getBitsInRange(int begin, int end){
        VectorVal vec = gui.getRegisterVector(regString);
        int width = vec.getSize();
        int lo = (begin < end) ? begin : end;
        int hi = (begin < end) ? end : begin;
        int rangeWidth = hi - lo + 1;
        if(rangeWidth > 64) rangeWidth = 64;
        long result = 0L;
        for(int k = 0; k < rangeWidth; k++){
            int srcIdx = lo + k;
            if(srcIdx < 0 || srcIdx >= width) continue;
            if(vec.getValue(srcIdx).getStateSignal()){
                result |= (1L << k);
            }
        }
        return result;
    }

    @Override
    public double realValue(){
        return gui.getRegisterValue(regString).realValue();
    }

    @Override
    public long longValue(){
        return gui.getRegisterValue(regString).longValue();
    }
    @Override
    public int intValue(){
        return gui.getRegisterValue(regString).intValue();
    }
    @Override
    public short shortValue(){
        return gui.getRegisterValue(regString).shortValue();
    }

    @Override
    public byte byteValue(){
        return gui.getRegisterValue(regString).byteValue();
    }

    @Override
    public String toString(){
        return gui.getRegisterVector(regString).toString();
    }
    
    @Override
    public boolean boolValue(){
        return gui.getRegisterValue(regString).boolValue();
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
        this.gui.setRegisterValue(this.regString, val);
    }

    @Override
    public VectorVal asVector(){
        return this.gui.getRegisterVector(this.regString);
    }
}
