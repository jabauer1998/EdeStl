package ede.stl.ast;

import java.util.List;
import ede.stl.common.Position;
import ede.stl.ast.AstNode;
import ede.stl.ast.Expression;
import ede.stl.ast.ModuleItem;
import ede.stl.passes.ModuleVisitor;

public class ModuleInstance extends AstNode implements ModuleItem {

    public final String     instanceName;
    public final List<Expression> expList;

    public ModuleInstance(Position start, String instanceName, List<Expression> expList) {
        super(start);
        this.instanceName = instanceName;
        this.expList = expList;
    }

    /**
     * The ast node visitor will allow the user to pass down data through the argument
     * vector. The accept method is needed to know which visit method to run.
     * 
     * @author Jacob Bauer
     */
    public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
        return modVisitor.visit(this, argv);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(instanceName);
        sb.append('(');
        int size = expList.size();
        for(int i = 0; i < size; i++){
            Expression exp = expList.get(i);
            sb.append(exp.toString());
            if(i < size - 1){
                sb.append(", ");
            }
        }
        sb.append(')');
        return sb.toString();
    }
}


























































