package ede.stl.ast;

import ede.stl.common.Position;
import ede.stl.ast.AstNode;
import ede.stl.ast.ModuleItem;
import ede.stl.passes.ModuleVisitor;

public abstract class IdentDeclaration extends AstNode implements ModuleItem{
    public final String declarationIdentifier;

    protected IdentDeclaration(Position start, String declarationIdentifier){
        super(start);
        this.declarationIdentifier = declarationIdentifier;
    }

    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);

    @Override
    public String toString(){
        return declarationIdentifier;
    }
}


























































