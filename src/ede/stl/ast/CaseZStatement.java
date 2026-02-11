package ede.stl.ast;

import java.util.List;
import ede.stl.common.Position;
import ede.stl.ast.Expression;
import ede.stl.ast.CaseItem;
import ede.stl.ast.DefCaseItem;
import ede.stl.ast.ExprCaseItem;
import ede.stl.passes.StatementVisitor;

public class CaseZStatement extends CaseStatement {

    public CaseZStatement(Position start, Expression exp, List<CaseItem> itemList) {
        super(start, exp, itemList);
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
        sb.append("casez(");
        sb.append(exp.toString());
        sb.append(")\n");
        for(CaseItem item: itemList){
            sb.append(item.toString());
            sb.append(";\n");
        }
        sb.append("endcase\n");
        return sb.toString();
    }
}


























































