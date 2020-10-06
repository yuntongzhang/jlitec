package com.yuntongzhang.jlitec.ast;

public class VarDeclaration {
    private Type type;
    private Identifier id;

    public VarDeclaration(Type type, Identifier id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public String toString() {
        return type.toString() + " " + id.toString() +";";
    }
}
