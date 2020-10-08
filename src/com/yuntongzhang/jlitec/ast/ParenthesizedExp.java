package com.yuntongzhang.jlitec.ast;

public class ParenthesizedExp extends Atom {
    private Expression expression;

    public ParenthesizedExp(Expression expression, Node.Location loc) {
        super(loc);
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "(" + expression.toString() + ")";
    }
}
