package com.yuntongzhang.jlitec.ast;

public abstract class BinaryOperation implements Expression {
    protected Operator operator;
    protected Expression leftOperand;
    protected Expression rightOperand;

    public BinaryOperation(Operator operator, Expression left, Expression right) {
        this.operator = operator;
        this.leftOperand = left;
        this.rightOperand = right;
    }

    public boolean checkOperands() {
        return checkOneOperand(leftOperand) && checkOneOperand(rightOperand);
    }

    abstract boolean checkOneOperand(Expression operand);
    
    @Override
    public String toString() {
        return "[" + leftOperand.toString() + "," + rightOperand.toString() + "]("
                + operator.toString() + ")";
    }
}
