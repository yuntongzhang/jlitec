package com.yuntongzhang.jlitec.ast;

public class VarDeclaration extends Node {
    private SType type;
    private Identifier id;

    public VarDeclaration(SType type, Identifier id, Node.Location loc) {
        super(loc);
        this.type = type;
        this.id = id;
    }

    public SType getType() {
        return type;
    }

    public Identifier getId() {
        return id;
    }

    @Override
    public String toString() {
        return type.toString() + " " + id.toString() +";";
    }
}
