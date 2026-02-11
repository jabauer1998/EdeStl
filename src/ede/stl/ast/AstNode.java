package ede.stl.ast;

import ede.stl.common.Position;

public abstract class AstNode {
    public final Position position; // field to store the starting position of an ast node

    protected AstNode(Position position) { this.position = position; }

    public abstract String toString();
}


























































