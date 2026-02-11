package ede.stl.ast;

import ede.stl.common.Position;
import ede.stl.ast.AstNode;
import ede.stl.ast.Statement;
import ede.stl.passes.StatementVisitor;

public abstract class CaseItem extends AstNode implements Statement {

    public final Statement statement;

    protected CaseItem(Position position, Statement statement) { 
        super(position);
        this.statement = statement; 
    }

    
    /**
     * The accept method makes it possible so that nodes know which visitor object to call
     * the visit method from. This is needed because the Visitor method is an interface not
     * a class. All of the classes implementing ASTnode visitor will not have the required
     * dependencies.
     * 
     * @author Jacob Bauer
     */
    public abstract <StatVisitType> StatVisitType accept(StatementVisitor<StatVisitType> statVisitor, Object... argv);

    @Override
    public String toString(){
        return statement.toString();
    }
}


























































