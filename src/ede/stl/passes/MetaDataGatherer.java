package ede.stl.passes;

import java.io.StringWriter;
import ede.stl.ast.*;
import ede.stl.interpreter.VerilogInterpreter;
import ede.stl.common.Destination;
import ede.stl.common.ErrorLog;
import ede.stl.gui.GuiEde;

public class MetaDataGatherer implements ModuleVisitor<Void> {
    private GuiEde edeInstance;
    private VerilogInterpreter constSolver;
  
    public MetaDataGatherer(GuiEde edeInstance, StringWriter str){
        Destination dest = new Destination(str);
        ErrorLog errLog = new ErrorLog(dest);
        this.edeInstance = edeInstance;
        this.constSolver = new VerilogInterpreter(errLog, dest);
    }
  
    public Void visit(ModuleDeclaration mod, Object... argv){
        for(ModuleItem item : mod.moduleItemList){
            item.accept(this);
        }
        return null;
    }

    public Void visit(AllwaysProcess stat, Object... argv){
        return null;
    }

    public Void visit(ContinuousAssignment assign, Object... argv){
        return null;
    }

    public Void visit(FunctionDeclaration function, Object... argv){
        return null;
    }

    public Void visit(InitialProcess stat, Object... argv){
        return null;
    }

    public Void visit(ModuleInstantiation mod, Object... argv){
        return null;
    }

    public Void visit(ModuleInstance mod, Object... argv){
        return null;
    }

    public Void visit(TaskDeclaration task, Object... argv){
        return null;
    }

    public Void visit(EmptyModItem modItem, Object... argv){
        return null;
    }

    public Void visit(Input.Wire.Scalar.Ident decl, Object... argv){
        return null;
    }

    public Void visit(Input.Reg.Scalar.Ident decl, Object... argv){
        return null;
    }

    public Void visit(Input.Wire.Vector.Ident decl, Object... argv){
        return null;
    }

    public Void visit(Input.Reg.Vector.Ident decl, Object... argv){
        return null;
    }

    public Void visit(Output.Reg.Scalar.Array decl, Object... argv){
        return null;
    }

    public Void visit(Reg.Scalar.Array decl, Object... argv){
        if(decl.annotationLexeme.equals("@Memory")){
            int numBytes = Integer.parseInt(decl.arrayIndex2.toString());
            edeInstance.setUpMemory(numBytes);
        }
        return null;
    }

    public Void visit(Reg.Vector.Array decl, Object... argv){
        return null;
    }

    public Void visit(Wire.Scalar.Ident decl, Object... argv){
        return null;
    }

    public Void visit(Wire.Vector.Ident decl, Object... argv){
        return null;
    }

    public Void visit(Reg.Scalar.Ident decl, Object... argv){
        return null;
    }

    public Void visit(Reg.Vector.Ident decl, Object... argv){
        return null;
    }

    public Void visit(Output.Wire.Scalar.Ident decl, Object... argv){
        return null;
    }

    public Void visit(Output.Reg.Scalar.Ident decl, Object... argv){
        return null;
    }

    public Void visit(Output.Wire.Vector.Ident decl, Object... argv){
        return null;
    }

    public Void visit(Output.Reg.Vector.Ident decl, Object... argv){
        return null;
    }

    public Void visit(Output.Reg.Vector.Array decl, Object... argv){
        return null;
    }

    public Void visit(Unidentified.Declaration decl, Object... argv){
        return null;
    }

    public Void visit(Int.Ident decl, Object... argv){
        return null;
    }

    public Void visit(Int.Array decl, Object... argv){
        return null;
    }

    public Void visit(Real.Ident decl, Object... argv){
        return null;
    }

    public Void visit(AndGateDeclaration decl, Object... argv){
        return null;
    }

    public Void visit(OrGateDeclaration decl, Object... argv){
        return null;
    }

    public Void visit(NandGateDeclaration decl, Object... argv){
        return null;
    }

    public Void visit(NorGateDeclaration decl, Object... argv){
        return null;
    }

    public Void visit(XorGateDeclaration decl, Object... argv){
        return null;
    }

    public Void visit(XnorGateDeclaration decl, Object... argv){
        return null;
    }

    public Void visit(NotGateDeclaration decl, Object... argv){
        return null;
    }
}
