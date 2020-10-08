package com.yuntongzhang.jlitec.ast;

public class FmlItem extends Node {
    private Type type;
    private Identifier id;

    public FmlItem(Type type, Identifier id, Node.Location loc) {
        super(loc);
        this.type = type;
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }

    @Override
    public String toString() {
        return type.toString() + " " + id.toString();
    }
}
