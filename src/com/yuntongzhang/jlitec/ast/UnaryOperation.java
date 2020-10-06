package com.yuntongzhang.jlitec.ast;

public abstract class UnaryOperation implements Expression {
    protected Operator operator;
    protected Expression operand;

    public UnaryOperation(Operator operator, Expression operand) {
        this.operator = operator;
        this.operand = operand;
    }

    public abstract boolean checkOperand();

    @Override
    public String toString() {
        return "(" + operator.toString() + ")[" + operand.toString() + "]";
    }
}
