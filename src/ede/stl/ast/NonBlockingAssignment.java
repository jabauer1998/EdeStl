package ede.stl.ast;

import java.util.List;
import ede.stl.common.Position;
import ede.stl.ast.Expression;
import ede.stl.ast.LValue;
import ede.stl.passes.StatementVisitor;

public class NonBlockingAssignment extends Assignment<List<.Value ., List<Expression>> {

    public NonBlockingAssignment(Position start, List<.Value . .Value . List<Expression> exp) {
        super(start, .Value . exp);
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
        List<.Value . lList = leftHandSide;
        List<Expression> rExp = rightHandSide;

        int size = lList.size();
        for(int i = 0; i < size; i++){
            (Value left = lList.get(i);
            Expression right = rExp.get(i);

            sb.append(left.toString());
            sb.append(" <= ");
            sb.append(right.toString());
            sb.append("\n");
        }

        return sb.toString();
    }
}


























































