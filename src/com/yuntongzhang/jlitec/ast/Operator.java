package com.yuntongzhang.jlitec.ast;

public enum Operator {
    OR("||"),
    AND("&&"),
    LT("<"),
    GT(">"),
    LEQ("<="),
    GEQ(">="),
    EQ("=="),
    NEQ("!="),
    PLUS("+"),
    MINUS("-"),
    TIMES("*"),
    DIV("/"),
    NOT("!");

    private final String text;

    Operator(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
