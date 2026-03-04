package ede.stl.circuit;

import javax.naming.OperationNotSupportedException;
import ede.stl.values.Value;
/**
 * The generic class for Circuit Objects
 * 
 * @author Jacob Bauer
 */
public abstract class CircuitElem implements Value {

    /**
     * method to update the output of the component
     * 
     * @param  None
     * @author      Jacob Bauer
     */

    public abstract void update(); // the update state of the component

    /**
     * All circuit elements must have some method of getting the state of an object that is
     * attached to it
     * 
     * @param  None
     * @author      Jacob Bauer
     */

    public abstract boolean getStateSignal();

    public abstract String toString();

    public double realValue(){
        return getStateSignal() ? 1.0 : 0.0;
    }

    public long longValue(){
        return getStateSignal() ? 1 : 0;
    }

    public int intValue(){
        return getStateSignal() ? 1 : 0;
    }

    public short shortValue(){
        return (short)(getStateSignal() ? 1 : 0);
    }

    public byte byteValue(){
        return (byte)(getStateSignal() ? 1 : 0);
    }

    public boolean boolValue(){
        return getStateSignal();
    }

    public boolean isBoolValue(){ return false; }
    public boolean isShortValue(){ return false; }
    public boolean isUnsignedShortValue(){ return false; }
    public boolean isByteValue(){ return false; }
    public boolean isUnsignedByteValue(){ return false; }
    public boolean isIntValue(){ return false; }
    public boolean isUnsignedIntValue(){ return false; }
    public boolean isLongValue(){ return false; }
    public boolean isUnsignedLongValue(){ return false; }
    public boolean isRealValue(){ return false; }
    public boolean isStringValue(){ return false; }
    public boolean isVector(){ return false; }
    public boolean isRegister(){ return false; }
    public boolean isWire(){ return true; }

    public Value getShallowSlice(int index1, int index2){
        throw new UnsupportedOperationException("Error no slice operation found for CirsuitElem");
    }

}


























































