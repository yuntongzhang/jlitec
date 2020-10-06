package com.yuntongzhang.jlitec.ast;

public class BooleanLiteral implements Expression {
    private boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
