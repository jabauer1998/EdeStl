package ede.stl.ast;

import ede.stl.passes.StatementVisitor;

/**
 * The Statement abstract class is used to represent Statement abstract syntax tree
 * nodes in the verilog language. This will come in handy when creating graphical user
 * interfaces.
 * 
 * @author Jacob Bauer
 */
public interface Statement {

    public abstract <StatVisitType> StatVisitType accept(StatementVisitor<StatVisitType> statVisitor, Object... argv);

}


























































