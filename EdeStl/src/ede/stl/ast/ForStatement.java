package ede.stl.ast;

import ede.stl.common.Position;
import ede.stl.ast.AstNode;
import ede.stl.ast.Expression;
import ede.stl.ast.Statement;
import ede.stl.ast.BlockingAssignment;
import ede.stl.passes.StatementVisitor;

public class ForStatement extends AstNode implements Statement {

    public final BlockingAssignment init;
    public final Expression       exp;
    public final BlockingAssignment change;
    public final Statement  stat;

    public ForStatement(Position start, BlockingAssignment init, Expression exp, BlockingAssignment change, Statement stat) {
        super(start);
        this.exp = exp;
        this.init = init;
        this.change = change;
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
        sb.append("for (");
        sb.append(init.toString());
        sb.append("; ");
        sb.append(exp.toString());
        sb.append("; ");
        sb.append(change.toString());
        sb.append(")\n");
        sb.append(stat.toString());
        sb.append("\n");
        return sb.toString();
    }
}


























































