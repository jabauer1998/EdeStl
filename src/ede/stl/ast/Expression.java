package ede.stl.ast;
import ede.stl.common.ErrorLog;
import ede.stl.interpreter.Environment;
import ede.stl.Value.Value;
import ede.stl.passes.ExpressionVisitor;

/**
 * The.expression class is an extention of the AstNode class This class is used as a
 * supertype for all.expression objects
 * 
 * @author Jacob Bauer
 */
public interface Expression {

    /**
     * The accept method will make it so the visitor interface will work
     * 
     * @param astNodeVisitor the visitor object we want to use to visit another member of a
     *                       class
     */
    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv);

}
