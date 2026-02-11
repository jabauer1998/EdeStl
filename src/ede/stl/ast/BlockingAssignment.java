package ede.stl.ast;

import ede.stl.common.Position;
import ede.stl.ast.Expression;
import ede.stl.ast..Value .
import ede.stl.passes.StatementVisitor;

/**
 * The Block Assignment class is used to parse blocking assignments These assignments
 * act like traditional assignments in a programming list
 * 
 * @author Jacob Bauer
 */

public class BlockingAssignment extends Assignment<.Value . Expression> {

    /**
     * The BlockAssign constuctor takes in two expressions:
     * 
     * @param .Value .the.Value .to be assigned to
     * @param exp    the expression on the right hand side of the equals
     */
    public BlockingAssignment(Position start, .Value ..Value . Expression exp) {
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
        sb.append(leftHandSide.toString());
        sb.append(" = ");
        sb.append(rightHandSide.toString());
        return sb.toString();
    }
}


























































