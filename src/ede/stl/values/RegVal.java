package ede.stl.Value ;

/**
 * This is the register class it will be used to simulate regs in the verilog language
 * These are kind of like variables but they can be set and it will update the wires
 * accordingly
 * 
 * @author Jacob Bauer
 *
 */

public class RegVal extends Node {

    /**
     * The Register constructor creates a register object
     * 
     * @param  signal: initial signal you want to be sent out
     * @author         Jacob Bauer
     */
    public RegVal(boolean signal) {
        super(null);
        super.stateSignal = signal;
    }

    /**
     * The update method samples the inputs and updates the output of the gate.
     * 
     * @param  None
     * @author      Jacob Bauer
     */
    public void update(){

        if (super.output != null) { super.output.update(); }

    }

    /**
     * The set signal method is used to change the input signal of a register
     * 
     * @param  signal: a boolean with true representing HI and false representing low
     * @author         Jacob Bauer
     */

    public void setSignal(boolean signal){

        if (signal != super.stateSignal) {
            super.stateSignal = signal;
            this.update();
        }

    }

    public String toString(){
        return "Register with a " + ((super.stateSignal) ? "LOW":"HIGH") + " Signal";
    }

    @Override
    public boolean isBool(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isShort(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedShort(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isByte(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedByteValue(){ // TODO Auto-generated method stub
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
    public boolean isRegister(){
        return true; 
    }

    @Override
    public boolean isWire(){ // TODO Auto-generated method stub
    return false; }
}


























































