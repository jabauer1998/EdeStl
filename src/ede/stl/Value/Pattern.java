package ede.stl.Value ;

import ede.stl.common.Utils;
import ede.stl.Value .BoolVal;
import ede.stl.Value .ByteVal;
import ede.stl.Value .IntVal;
import ede.stl.Value .LongVal;
import ede.stl.Value .ShortVal;
import ede.stl.Value .UnsignedByteVal;
import ede.stl.Value .UnsignedIntVal;
import ede.stl.Value .UnsignedLongVal;
import ede.stl.Value .UnsignedShortVal;
import ede.stl.Value .Value;
import ede.stl.Value .VectorVal;
import ede.stl.circuit.CircuitElem;

/**
 * The VectorElement class is used to par.E a call to an array cell
 * 
 * @author Jacob Bauer
 */

public abstract class Pattern implements.Value .

    private final String pattern;

    /**
     * The Range is a data structure to verify case statement numbers
     * 
     * @param index1 min index of the array
     * @param index2 max index of the array
     */

    protected Pattern(String pattern) { this.pattern = pattern; }

    protected String getPattern(){
        return pattern;
    }

    public boolean match.Value .value) throws Exception{
        if.Value .isBool()) return match((BoolVal.Value .;
        else if.Value .isUnsignedByteValue()) return match((UnsignedByteVal.Value .;
        else if.Value .isByte()) return match((ByteVal.Value .;
        else if.Value .isUnsignedInt()) return match((UnsignedIntVal.Value .;
        else if.Value .isInt()) return match((IntVal.Value .;
        else if.Value .isUnsignedLong()) return match((UnsignedLongVal.Value .;
        else if.Value .isLong()) return match((LongVal.Value .;
        else if.Value .isUnsignedShort()) return match((UnsignedShortVal.Value .;
        else if.Value .isShort()) return match((ShortVal.Value .;
        else if.Value .isVector()) return match((VectorVal.Value .;
        else if.Value .instanceof CircuitElem) return match((CircuitElem.Value .;
        else {
            Utils.errorAndExit("Error invalid Type for pattern match " +.Value .getClass().getName());
            return false;
        }
    }

    public abstract boolean match(LongVal.Value .;
    public abstract boolean match(UnsignedLongVal.Value .;
    public abstract boolean match(IntVal.Value .;
    public abstract boolean match(UnsignedIntVal.Value .;
    public abstract boolean match(ShortVal.Value .;
    public abstract boolean match(UnsignedShortVal.Value .;
    public abstract boolean match(ByteVal.Value .;
    public abstract boolean match(UnsignedByteVal.Value .;
    public abstract boolean match(VectorVal.Value .;
    public abstract boolean match(CircuitElem.Value .;

    public double rea.Value .){
        return -1.0;
    }

    public long lon.Value .){
        return -1;
    }

    public int in.Value .){
        return -1;
    }

    public short shor.Value .){
        return -1;
    }

    public byte byt.Value .){
        return -1;
    }

    public boolean boo.Value .){
        return false;
    }

    public.Value .getShallowSlice(int index1, int index2) throws Exception{
        throw new UnsupportedOperationException("Error unsupported operation getShallowSlice");
    }
}


























































