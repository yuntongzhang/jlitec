package com.yuntongzhang.jlitec.ast;

public class FmlItem {
    private Type type;
    private Identifier id;

    public FmlItem(Type type, Identifier id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public String toString() {
        return type.toString() + " " + id.toString();
    }
}
