package ede.stl.circuit;

import ede.stl.circuit.CircuitElem;
import ede.stl.values.Value;

public abstract class Web extends CircuitElem {
    // For now this is just a place holder but this is to provide more specific support for
    // the CircuitElem class

    public abstract void addOutput(CircuitElem elem);

    public abstract boolean getStateSignal();

    public void setValue(Value val){
	//Do nothing
    }
}


























































