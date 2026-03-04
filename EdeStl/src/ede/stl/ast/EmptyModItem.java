package ede.stl.ast;

import ede.stl.common.Position;
import ede.stl.ast.AstNode;
import ede.stl.passes.ModuleVisitor;

public class EmptyModItem extends AstNode implements ModuleItem {

    public EmptyModItem(Position position) { super(position); }

    /**
     * The ast node visitor will allow the user to pass down data through the argument
     * vector. The accept method is needed to know which visit method to run.
     * 
     * @author Jacob Bauer
     */
    public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
        return modVisitor.visit(this, argv);
    }

    @Override
    public String toString(){ // TODO Auto-generated method stub
        return "";
    }
}


























































