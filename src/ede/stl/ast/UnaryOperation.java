package ede.stl.ast;

import java.lang.management.OperatingSystemMXBean;
import ede.stl.common.Position;
import ede.stl.common.ErrorLog;
import ede.stl.common.ErrorItem;
import ede.stl.common.Utils;
import ede.stl.interpreter.Environment;
import ede.stl.Value .Value;
import ede.stl.ast.AstNode;
import ede.stl.ast.Expression;
import ede.stl.passes.ExpressionVisitor;

/**
 * The Unary Operation class is used to par.E unary operations unary operations are
 * operations with one operator and an.expression ex: - (1 + 5) the.expression is 1 + 5
 * and the negation is the unary operation
 * 
 * @author Jacob Bauer
 */

public class UnaryOperation extends AstNode implements Expression {

    public final Expression rightHandSideExpression; // the right hand side of the equation
    public final Operator Op;
    public enum Operator{
        PLUS,
        MINUS,
        LNEG,
        BNEG
    }
    /**
     * The UnaryOperation constuctor is used to create a Unary operation
     * 
     * @param op               operator to use
     * @param right.expression to apply the operator to
     */

    public UnaryOperation(Position start, Operator Op, Expression rightHandSideExpression) {
        super(start);
        this.Op = Op;
        this.rightHandSideExpression = rightHandSideExpression;
    }

    /**
     * The accept method will make it so the visitor interface will work
     * 
     * @param astNodeVisitor the visitor object we want to use to visit another member of a
     *                       class
     */
    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv){
        return exprVisitor.visit(this, argv);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        switch(Op){
            case PLUS: sb.append("+");
            case MINUS: sb.append("-");
            case LNEG: sb.append("!");
            case BNEG: sb.append("~");
        }

        sb.append(rightHandSideExpression.toString());
        return sb.toString();
    }
}


























































