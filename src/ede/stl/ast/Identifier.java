package ede.stl.ast;

import ede.stl.common.Pointer;
import ede.stl.common.Position;
import ede.stl.common.SymbolTable;
import ede.stl.passes.ExpressionVisitor;
import java.lang.String;

/**
 * The Identifier class is used to define an Identifier An identifier is any set of
 * characters that isnt considered a verilog keyword. This includes variable names,
 * function names, module names Etc...
 */

public class Identifier extends Label {
    /**
     * The identifier constructor produces an identifier given an Identifier Token
     * 
     * @param ident the ident token to base the token off of
     */
    public Identifier(Position start, String identifierLexeme) {
        super(start, identifierLexeme);
    }

    public <DataType> Pointer<DataType> getLValue(SymbolTable<Pointer<DataType>> table){
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

}


























































