package ede.stl.ast;

import ede.stl.common.Position;
import ede.stl.ast.Expression;
import ede.stl.passes.StatementVisitor;
import java.util.List;

public class SystemTaskStatement extends TaskStatement {

    public SystemTaskStatement(Position start, String taskName, List<Expression> argumentList) {
        super(start, taskName, argumentList);
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
        sb.append('$');
        sb.append(super.toString());
        return sb.toString();
    }
}


























































