package ede.stl.ast;

import java.util.List;
import ede.stl.common.Position;
import ede.stl.ast.AstNode;
import ede.stl.ast.ModuleItem;
import ede.stl.passes.ModuleVisitor;

public class ModuleInstantiation extends AstNode implements ModuleItem{
    public final String      moduleType;
    public final List<ModuleInstance> modList;

    public ModuleInstantiation(Position start, String moduleType, List<ModuleInstance> modList) {
        super(start);
        this.moduleType = moduleType;
        this.modList = modList;
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
        sb.append(moduleType);
        sb.append(' ');
        int size = modList.size();
        for(int i = 0; i < size; i++){
            ModuleInstance exp = modList.get(i);
            sb.append(exp.toString());
            if(i < size - 1){
                sb.append(", ");
            }
        }
        sb.append(";");
        return sb.toString();
    }
}


























































