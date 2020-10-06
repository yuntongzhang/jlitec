package com.yuntongzhang.jlitec.ast;

public class StringLiteral implements Expression {
    private String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
