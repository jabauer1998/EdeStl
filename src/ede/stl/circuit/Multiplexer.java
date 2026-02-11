package ede.stl.circuit;

import ede.stl.circuit.NandGate;
import ede.stl.circuit.NotGate;


public class Multiplexer {
    public Multiplexer(WireVal FinalOutput, WireVal Input1, WireVal Input2, WireVal Select){
        WireVal OutputInputSelect = new WireVal();
        WireVal OutputInputNotSelect = new WireVal();
        
        WireVal NotSelect = new WireVal();
        new NotGate(NotSelect, Select);

        new NandGate(OutputInputSelect, Input1, Select);
        new NandGate(OutputInputNotSelect, Input2, NotSelect);
        new NandGate(FinalOutput, OutputInputSelect, OutputInputNotSelect);
    }
}


























































