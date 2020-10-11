package com.yuntongzhang.jlitec.ast;

public abstract class UnaryOperation extends Expression {
    protected Operator operator;
    protected Expression operand;

    public UnaryOperation(Operator operator, Expression operand, Node.Location loc) {
        super(loc);
        this.operator = operator;
        this.operand = operand;
    }

    public Operator getOperator() {
        return operator;
    }

    public Expression getOperand() {
        return operand;
    }

    public abstract boolean checkOperand();

    @Override
    public String toString() {
        return "(" + operator.toString() + ")[" + operand.toString() + "]";
    }
}
