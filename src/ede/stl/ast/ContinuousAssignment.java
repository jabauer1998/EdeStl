package ede.stl.ast;

import java.util.List;
import ede.stl.common.Position;
import ede.stl.ast.AstNode;
import ede.stl.ast.BlockingAssignment;
import ede.stl.passes.ModuleVisitor;

public class ContinuousAssignment extends AstNode implements ModuleItem {
    public final List<BlockingAssignment> assignmentList;

    public ContinuousAssignment(Position start, List<BlockingAssignment> assignmentList) {
        super(start);
        this.assignmentList = assignmentList;
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
        sb.append("assign ");
        int assignListSize = assignmentList.size();
        for(int i = 0; i < assignListSize; i++){
            BlockingAssignment assign = assignmentList.get(i);
            sb.append(assign.toString());
            if(i < assignListSize - 1){
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}


























































