package ede.stl.ast;
import ede.stl.common.Position;
import ede.stl.ast.AstNode;
import ede.stl.ast.ModuleItem;
import ede.stl.passes.ModuleVisitor;

public class Unidentified {
    public class Declaration extends AstNode implements ModuleItem{
        public final String declaration;
        
        public Declaration (Position start, String name){
            super(start);
            this.declaration = name;
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
            return declaration;
        }

    }
}


























































