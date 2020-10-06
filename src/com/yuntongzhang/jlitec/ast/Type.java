package com.yuntongzhang.jlitec.ast;

public class Type {
    private String name;

    public Type(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
