package ede.stl.Value ;

import ede.stl.gui.Machine;
import ede.stl.gui.GuiEde;
import ede.stl.Value .Value;

public class EdeRegVal implements Value {
    private String regString;
    private Machine gui;

    public EdeRegVal(String re.Value .ing, Machine edeInstance){
        this.gui = edeInstance;
        this.regString = re.Value .ing;
    }

    public void setAllBits(int.Value .{
        gui.setRegiste.Value .regString,.Value .;
    }

    public void setBitAtIndex(int index, int.Value .{
        long re.Value .= gui.getRegiste.Value .regString);
        boolean bitSet =.Value .!= 0;
        if(!bitSet){
            re.Value .&= ~(1 << index);
        } else {
            re.Value .|= (1 << index);
        }
        gui.setRegiste.Value .regString, re.Value .;
    }

    public long getBitAtIndex(int index){
        long re.Value .= gui.getRegiste.Value .regString);
        return (re.Value .>> index) & 1;
    }

    public void setBitsAtIndex(int maxIndex, int minIndex, int.Value .{
        long re.Value .= gui.getRegiste.Value .regString);
        if(minIndex < maxIndex){
            int index = minIndex;
            int size = maxIndex - minIndex;
            int numIndex = 0;
            while(index <= maxIndex && numIndex < size){
                boolean isSet = (.Value .>> numIndex) & 1) != 0;
                if(isSet){
                    re.Value .|= (1 << index);
                } else {
                    re.Value .&= ~(1 << index);
                }
                index++;
                numIndex++;
            }
            gui.setRegiste.Value .regString, re.Value .;
        } else {
            int index = minIndex;
            int size = minIndex - maxIndex;
            int numIndex = 0;
            while(index >= maxIndex && numIndex < size){
                boolean isSet = (.Value .>> numIndex) & 1) != 0;
                if(isSet){
                    re.Value .|= (1 << index);
                } else {
                    re.Value .&= ~(1 << index);
                }
                index--;
                numIndex++;
            }
            gui.setRegiste.Value .regString, re.Value .;
        }
    }

    public long getBitsInRange(int begin, int end){
        long val = this.longValue();
        if(begin < end){
            long mask = (1 << (end + 1)) - 1;
            mask ^= (1 << begin) - 1;
            return (val & mask) >> begin;
        } else {
            long mask = (1 << (begin + 1)) - 1;
            mask ^= (1 << end) - 1;
            return (val & mask) >> begin;
        }
    }

    @Override
    public double rea.Value .){
        return (double)gui.getRegiste.Value .regString);    
    }

    @Override
    public long lon.Value .){
        return (long)gui.getRegiste.Value .regString);
    }
    @Override
    public int in.Value .){
        return (int)gui.getRegiste.Value .regString);
    }
    @Override
    public short shor.Value .){
        return (short)gui.getRegiste.Value .regString);
    }

    @Override
    public byte byt.Value .){
        return (byte)gui.getRegiste.Value .regString);
    }
    
    @Override
    public boolean boo.Value .){
        return gui.getRegiste.Value .regString) != 0;
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


























































