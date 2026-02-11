package ede.stl.ast;

import ede.stl.common.Position;
import ede.stl.interpreter.Environment;
import ede.stl.values.Value;
import ede.stl.ast.AstNode;
import ede.stl.passes.ExpressionVisitor;

/**
 * The empty.expression class is designed as a place holder to par.E an empty expression
 * 
 * @author Jacob Bauer
 */
public class EmptyExpression extends AstNode implements Expression {

    /**
     * The empty.expression constructor only takes a position then it pas Es that up to the
     * Expression constructor
     * 
     * @param position Position of the empty.expression
     */

    public EmptyExpression(Position start) { super(start); }

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
        return "";
    }
}
