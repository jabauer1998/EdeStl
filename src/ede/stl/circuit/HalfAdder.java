package ede.stl.circuit;

import ede.stl.circuit.AndGate;
import ede.stl.circuit.XorGate;


public class HalfAdder {
    public HalfAdder(WireVal Output, WireVal Carry, WireVal Input1, WireVal Input2){
        new AndGate(Carry, Input1, Input2);
        new XorGate(Output, Input1, Input2);
    }
}


























































