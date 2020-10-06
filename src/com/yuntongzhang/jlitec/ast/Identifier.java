package com.yuntongzhang.jlitec.ast;

public class Identifier extends Atom {
    private String name;

    public Identifier(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
