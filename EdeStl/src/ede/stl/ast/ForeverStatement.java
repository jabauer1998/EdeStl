package ede.stl.ast;

import ede.stl.common.Position;
import ede.stl.ast.AstNode;
import ede.stl.ast.Statement;
import ede.stl.passes.StatementVisitor;

public class ForeverStatement extends AstNode implements Statement {

    public final Statement stat; // statement

    public ForeverStatement(Position start, Statement stat) {
        super(start);
        this.stat = stat;
    }

    /**
     * The accept method makes it possible so that nodes know which visitor object to call
     * the visit method from. This is needed because the Visitor method is an interface not
     * a class. All of the classes implementing ASTnode visitor will not have the required
     * dependencies.
     * 
     * @author Jacob Bauer
     */
    public <StatVisitType> StatVisitType accept(StatementVisitor<StatVisitType> statVisitor, Object... argv){
        return statVisitor.visit(this, argv);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("forever\n");
        sb.append(stat.toString());
        return sb.toString();
    }
}


























































