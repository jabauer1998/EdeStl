package ede.stl.circuit;

import ede.stl.circuit.AndGate;
import ede.stl.circuit.OrGate;
import ede.stl.circuit.XorGate;


public class FullAdder {
    public FullAdder(WireVal CarryOut, WireVal Output, WireVal InputA, WireVal InputB, WireVal CarryIn){
       WireVal AXorBOutput = new WireVal();
       WireVal AAndBOutput = new WireVal();

       new XorGate(AXorBOutput, InputA, InputB);
       new AndGate(AAndBOutput, InputA, InputB);

       WireVal AXorBAndCinOutput = new WireVal();
       new XorGate(Output, AXorBOutput, CarryIn);
       new AndGate(AXorBAndCinOutput, AXorBOutput, CarryIn);

       new OrGate(CarryOut, AXorBAndCinOutput, AAndBOutput);
    }
}


























































