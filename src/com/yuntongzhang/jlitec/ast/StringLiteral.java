package com.yuntongzhang.jlitec.ast;

public class StringLiteral extends Expression {
    private String value;

    public StringLiteral(String value, Node.Location loc) {
        super(loc);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
