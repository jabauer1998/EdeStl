package ede.stl.ast;

import java.util.List;
import ede.stl.common.Position;
import ede.stl.ast.ModuleItem;
import ede.stl.ast.Statement;
import ede.stl.passes.ModuleVisitor;

public class TaskDeclaration extends ProcedureDeclaration {
    public final String      taskName;

    public TaskDeclaration(Position start, String taskName, List<ModuleItem> paramaters, Statement stat) {
        super(start, paramaters, stat);
        this.taskName = taskName;
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
        sb.append("task ");
        sb.append(taskName);
        sb.append('\n');
        sb.append(super.toString());
        sb.append("endtask\n");
        return sb.toString();
    }
}


























































