package com.yuntongzhang.jlitec.ast;

public class BooleanLiteral extends Expression {
    private boolean value;

    public BooleanLiteral(boolean value, Node.Location loc) {
        super(loc);
        this.value = value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
