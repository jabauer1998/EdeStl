package ede.stl.circuit;

import ede.stl.circuit.*;
/**
 * The NorGate class is an instance of the the Gate class that is used to simulate and
 * AndGate. These classes will be usefull when trying to create a graph in the
 * Interpreter phase of the compiler. The interpreter is importanct because it will help
 * validate if the code generator actually works and we retrieve the expected results.
 * 
 * @author Jacob Bauer
 */

public class NotGate extends Gate {

  private Web input;

  /**
   * The and gate constructor creates a new and gate. It can take in a variable number of
   * inputs with a minimum of two inputs
   * 
   * @param  input1:   the first input into the norgate
   * @param  input2:   the second input into the norgate
   * @param  optional: these are optional inputs to morph the andgate into a multiple
   *                   input and gate
   * @author           Jacob Bauer
   */

  public NotGate(Web output, Web input) {
    super(output);

    this.input = input;
    this.input.addOutput(this);

    this.update(); // update the output
  }

  /**
   * The update method samples the inputs and updates the output of the gate.
   * 
   * @param  None
   * @author      Jacob Bauer
   */

  public void update(){
    this.stateSignal = !input.getStateSignal();
    super.updateOutput();
  }

  public String toString(){
		return "NotGate with 1 input";
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


























































