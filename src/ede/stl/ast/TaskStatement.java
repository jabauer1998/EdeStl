package ede.stl.ast;

import ede.stl.common.Position;
import ede.stl.ast.AstNode;
import ede.stl.ast.Expression;
import ede.stl.ast.Statement;
import ede.stl.passes.StatementVisitor;
import java.util.List;

public class TaskStatement extends AstNode implements Statement {
    public final String taskName;
    public final List<Expression> argumentList;

    public TaskStatement(Position start, String taskName, List<Expression> argumentList) {
        super(start);
        this.taskName = taskName;
        this.argumentList = argumentList;
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
        sb.append(taskName);
        int argSize = argumentList.size();
        sb.append('(');
        for(int i = 0; i < argSize; i++){
            Expression exp = argumentList.get(i);
            sb.append(exp.toString());
            if(i < argSize - 1){
                sb.append(", ");
            }
        }
        sb.append(')');
        return sb.toString();
    }
}


























































