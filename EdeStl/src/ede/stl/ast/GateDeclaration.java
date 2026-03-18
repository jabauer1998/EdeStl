package ede.stl.ast;

import ede.stl.common.Position;
import ede.stl.ast.AstNode;
import ede.stl.ast.ModuleItem;
import ede.stl.passes.ModuleVisitor;

public abstract class GateDeclaration <OutputType> extends AstNode implements ModuleItem {
    public final OutputType gateConnections;

    protected GateDeclaration(Position start, OutputType gateConnections) {
        super(start);
        this.gateConnections = gateConnections;
    }

    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);
}


























































