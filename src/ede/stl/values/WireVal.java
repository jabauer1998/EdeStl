package ede.stl.Value ;

import java.util.HashSet;
import ede.stl.circuit.CircuitElem;
import ede.stl.circuit.Node;

/**
 * The wire class is used to attach all of the CircuitElems together. The wire class
 * should work just like a Wire Net in Verilog
 * 
 * @author Jacob Bauer
 */

public class WireVal extends Web {

    private HashSet<CircuitElem> outputs; // Outputs of the wire object
    private CircuitElem          input;

    /**
     * This is the constructor of the wire class
     * 
     * @param  None
     * @author      Jacob Bauer
     */
    public WireVal(CircuitElem input) {
        this.outputs = new HashSet<>();
        this.input = input;
    }

    public WireVal() { this(null); }

    /**
     * The update method is used to update the wires outputs.
     * 
     * @param  None
     * @author      Jacob Bauer
     */
    public void update(){

        for (CircuitElem output : outputs) { output.update(); }

    }

    /**
     * The hasOutput is used to check if the output allready exists inside the Linkedlist
     * 
     * @param  output: the output you want to see if it exists
     * @author         Jacob Bauer
     */
    public boolean hasOutput(CircuitElem output){ return outputs.contains(output); }

    /**
     * Add output method adds non duplicate circuit elem to the hashset
     */
    public void addOutput(CircuitElem output){ outputs.add(output); }

    public boolean getStateSignal(){
        if (input == null) return false;

        return input.getStateSignal();
    }

    /**
     * The assign input method is used to attach an input to the Wire
     * @param input
     */

    public void assignInput(CircuitElem input){
        this.input = input;
        
        if(this.input instanceof Web){
            ((Web)this.input).addOutput(this);
        } else {
            ((Node)this.input).attachOutput(this);
        }
    }

    public String toString(){
		return "Wire with 1 input";
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
    public boolean isRegister(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isWire(){ // TODO Auto-generated method stub
    return true; }
}


























































