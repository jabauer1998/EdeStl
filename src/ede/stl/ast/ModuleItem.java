package ede.stl.ast;

import ede.stl.interpreter.Environment;
import ede.stl.passes.ModuleVisitor;

public interface ModuleItem {
    /**
     * The ast node visitor will allow the user to pass down data through the argument
     * vector. The accept method is needed to know which visit method to run.
     * 
     * @author Jacob Bauer
     */
    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);
}


























































