package com.yuntongzhang.jlitec.ast;

import java.util.Set;

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

    public boolean isRelational() {
        Set<Operator> relationalOperators = Set.of(LT, GT, LEQ, GEQ, EQ, NEQ);
        return relationalOperators.contains(this);
    }

    public Operator relationalNegate() {
        switch (this) {
            case LT:
                return GT;
            case GT:
                return LT;
            case LEQ:
                return GEQ;
            case GEQ:
                return LEQ;
            case EQ:
                return NEQ;
            case NEQ:
                return EQ;
            default: // should never happen
                return this;
        }
    }

    public boolean relationalApplyTo(int left, int right) {
        switch (this) {
            case LT:
                return left < right;
            case GT:
                return left > right;
            case LEQ:
                return left <= right;
            case GEQ:
                return left >= right;
            case EQ:
                return left == right;
            case NEQ:
                return left != right;
            default: // should never happen
                return true;
        }
    }

    public boolean logicalApplyTo(boolean left, boolean right) {
        switch (this) {
            case AND:
                return left && right;
            case OR:
                return left || right;
            default: // should never happen
                return true;
        }
    }

    public int arithmeticApplyTo(int left, int right) {
        switch (this) {
            case PLUS:
                return left + right;
            case MINUS:
                return left - right;
            case TIMES:
                return left * right;
            default: // should never happen
                return 0;
        }
    }

    @Override
    public String toString() {
        return text;
    }
}
