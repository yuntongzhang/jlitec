package com.yuntongzhang.jlitec.ast;

public class IntegerLiteral extends Expression {
    private int value;

    public IntegerLiteral(int value, Node.Location loc) {
        super(loc);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
