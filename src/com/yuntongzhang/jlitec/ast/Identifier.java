package com.yuntongzhang.jlitec.ast;

public class Identifier extends Atom {
    private String name;

    public Identifier(String name, Node.Location loc) {
        super(loc);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
