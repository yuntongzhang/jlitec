package com.yuntongzhang.jlitec.ast;

public class IntegerLiteral implements Expression {
    private int value;

    public IntegerLiteral(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
