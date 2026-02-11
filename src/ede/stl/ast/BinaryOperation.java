package ede.stl.ast;


import ede.stl.common.Position;
import ede.stl.common.ErrorLog;
import ede.stl.common.ErrorItem;
import ede.stl.common.Utils;
import ede.stl.interpreter.Environment;
import ede.stl.values.Value;
import ede.stl.ast.AstNode;
import ede.stl.ast.Expression;
import ede.stl.passes.ExpressionVisitor;

/**
 * The Binary operation class is used to par.E binary operations a binary operation
 * takes the format of.expression Op Expression Ex: 1 + 1 or 1 + (3 - 2) where (3 - 2)
 * is the.expression on the right hand side of the operator
 * 
 * @author Jacob Bauer
 */
public class BinaryOperation extends AstNode implements Expression {

    public Expression  right; // Expression to the right of the operator
    public Expression  left;  // Expression to the left of the operator
    public Operator Op; 

    public enum Operator{
        PLUS,
        MINUS,
        TIMES,
        DIV,
        MOD,
        EQ2,
        EQ3,
        NE1,
        NE2,
        LAND,
        LOR,
        LE,
        LT,
        GE,
        GT,
        BAND,
        BOR,
        BXOR,
        BXNOR,
        LSHIFT,
        RSHIFT
    }

    /**
     * This is the consructor for creating Binary.expressions it has the following
     * arguments:
     * @param start the starting positon in the token stream of the ast node
     * @param left  exprVisitoresson on the left side of the operator
     * @param op    operator in the midd E of the binary operation
     * @param right exprVisitoresson on the right side of the operator
     */
    public BinaryOperation(Position start, Expression left, Operator Op, Expression right) {
        super(start);
        this.left = left;
        this.Op = Op;
        this.right = right;
    }

    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv){
        return exprVisitor.visit(this, argv);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("( ");
        sb.append(left.toString());
        sb.append(' ');
        switch(Op){
            case PLUS: sb.append('+');
            case MINUS: sb.append('-');
            case TIMES: sb.append('*');
            case DIV: sb.append('/');
            case MOD: sb.append('%');
            case EQ2: sb.append("==");
            case EQ3: sb.append("===");
            case NE1: sb.append("!=");
            case NE2: sb.append("!==");
            case LAND: sb.append("&&");
            case LOR: sb.append("||");
            case LE: sb.append("<=");
            case LT: sb.append("<");
            case GE: sb.append(">=");
            case GT: sb.append(">");
            case BAND: sb.append("&");
            case BOR: sb.append("|");
            case BXOR: sb.append("^");
            case BXNOR: sb.append("~^");
            case LSHIFT: sb.append("<<");
            case RSHIFT: sb.append(">>");
        }
        sb.append(right.toString());
        sb.append(" )");
        return sb.toString();
    }
}
