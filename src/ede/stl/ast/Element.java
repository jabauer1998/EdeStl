package ede.stl.ast;

import ede.stl.common.Pointer;
import ede.stl.common.Position;
import ede.stl.common.SymbolTable;
import ede.stl.ast.Expression;
import ede.stl.passes.ExpressionVisitor;

/**
 * The VectorElement class is used to par.E a call to an array cell
 * 
 * @author Jacob Bauer
 */

public class Element extends Label {

    public final Expression       index1; // initial index to grap from the array

    /**
     * The VectorElement constructor takes an identifier with up to twoindex to s Ecify the sub
     * array that is desired
     * 
     * @param ident  name of the array
     * @param index1 min index of the array
     * @param index2 max index of the array
     */

    public Element(Position start, String ident, Expression index1) {
        super(start, ident);
        this.index1 = index1;
    }

    public <DataType> Pointer<DataType> get.Value .SymbolTable<Pointer<DataType>> table){
        return null;
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
        sb.append(super.toString());
        sb.append('[');
        sb.append(index1.toString());
        sb.append(']');
        return sb.toString();
    }
}


























































