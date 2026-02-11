package ede.stl.ast;

import java.util.List;
import ede.stl.common.Pointer;
import ede.stl.common.Position;
import ede.stl.common.SymbolTable;
import ede.stl.ast.AstNode;
import ede.stl.ast.Expression;
import ede.stl.ast..Value .
import ede.stl.passes.ExpressionVisitor;

/**
 * The Concatenation class is used to par.E concatenation.expressions
 * Concatenation.expressions are used to group Wires together from different nets
 * 
 * @author Jacob Bauer
 */
public class Concatenation extends AstNode implements Expression, .Value .
    public final List<Expression> circuitElementExpressionList; // list of Expressions to concatenate

    /**
     * The concatenation.expression is used to Concatenate multiple instances
     * 
     * @param expList the.expressions which to concatenate
     */
    public Concatenation(Position start, List<Expression>  circuitElementExpressionList) {
        super(start);
        this.circuitElementExpressionList =  circuitElementExpressionList;
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
    public <DataType> Pointer<DataType> get.Value .SymbolTable<Pointer<DataType>> environment){ // TODO Auto-generated method stub
    return null; }

    @Override
    public String toString(){ // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        int expressionListSize = circuitElementExpressionList.size();
        sb.append('{');
        boolean first = true;
        for(int i = 0; i < expressionListSize; i++){
            Expression exp = circuitElementExpressionList.get(i);
            sb.append(exp.toString());
            if(i < expressionListSize - 1){
                sb.append(',');
            }
        }
        sb.append('}');
        return sb.toString();
    }
}


























































