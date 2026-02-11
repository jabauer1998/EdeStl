package ede.stl.ast;

import ede.stl.common.Position;
import ede.stl.ast.AstNode;
import ede.stl.ast.Expression;
import ede.stl.passes.ExpressionVisitor;
import java.lang.String;

/**
 * The St.Value .Ast node class is used to Par.E Strings
 * 
 * @author Jacob Bauer
 */
public class StringNode extends AstNode implements Expression {
    public final String lexeme; // token to hold the string.Value .

    /**
     * The St.Value .constructor when provided a token produces the exprVisitorected string
     *.Value .ast node
     * 
     * @param string token representing the string.Value .to be created
     */
    public StringNode(Position start, String lexeme) {
        super(start);
        this.lexeme = lexeme.substring(1, lexeme.length() - 1); //take away the \" marks around the string lexeme
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
    public String toString(){ // TODO Auto-generated method stub
        return lexeme;
    }
}


























































