package com.yuntongzhang.jlitec.ast;

public class Type extends Node {
    private String name;

    public Type(String name, Node.Location loc) {
        super(loc);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
