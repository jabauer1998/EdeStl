package ede.stl.ast;

import ede.stl.common.Position;
import ede.stl.ast.Expression;
import ede.stl.passes.ModuleVisitor;

public class Int {
    public class Array extends ArrayDeclaration{
        public Array(Position start, String lexeme, Expression index1, Expression index2){
            super(start, lexeme, index1, index2);
        }

        public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
            return modVisitor.visit(this, argv);
        }

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append("integer ");
            sb.append(super.toString());
            return sb.toString();
        }
    }
    public class Ident extends IdentDeclaration{
        public Ident(Position start, String lexeme){
            super(start, lexeme);
        }

        public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
            return modVisitor.visit(this, argv);
        }

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append("integer ");
            sb.append(super.toString());
            return sb.toString();
        }
    }
}


























































