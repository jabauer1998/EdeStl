package ede.stl.circuit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import ede.stl.circuit.CircuitElem;
import ede.stl.circuit.Web;

/**
 * The AndGate class is an instance of the the Gate class that is used to simulate and
 * AndGate. These classes will be usefull when trying to create a graph in the
 * Interpreter phase of the compiler. The interpreter is importanct because it will help
 * validate if the code generator actually works and we retrieve the expected results.
 * 
 * @author Jacob Bauer
 */
public class AndGate extends Gate {

	private HashSet<Web> inputs;

	/**
	 * The and gate constructor creates a new and gate. It can take in a variable number of
	 * inputs with a minimum of two inputs
	 * 
	 * @param  input1:   the first input into the andgate
	 * @param  input2:   the second input into the andgate
	 * @param  optional: these are optional inputs to morph the andgate into a multiple
	 *                   input and gate
	 * @author           Jacob Bauer
	 */

	public AndGate(Web output, Web input1, Web input2, Web... optional) {
		super(output); // Set the output of the gate

		this.inputs = new HashSet<Web>(); // initialize an array for inputs

		this.inputs.add(input1); //Add input 1 and 2 to the HashSet of Gates
		this.inputs.add(input2);

		List<Web> asList = Arrays.asList(optional);
		this.inputs.addAll(asList);

		for (Web input: inputs) { // Loop through all of newly created outputs and add this as an input
			input.addOutput(this);
		}

		this.update(); // update the output
	}

	/**
	 * The update method samples the inputs and updates the output of the gate.
	 * 
	 * @param  None
	 * @author      Jacob Bauer
	 */

	public void update(){

		if (super.stateSignal == false) {

			for (Web input : inputs) { if (input.getStateSignal() == false) return; }

			super.stateSignal = true;
			super.updateOutput();
		} else {

			for (CircuitElem input : inputs) {

				if (input.getStateSignal() == false) {
					super.stateSignal = false;
					super.updateOutput();
					break;
				}

			}

		}

	}

	public String toString(){
		return "AndGate with " + inputs.size() + " inputs";
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
	return false; }
}


























































