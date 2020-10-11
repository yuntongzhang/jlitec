package com.yuntongzhang.jlitec.ast;

public abstract class BinaryOperation extends Expression {
    protected Operator operator;
    protected Expression leftOperand;
    protected Expression rightOperand;

    public BinaryOperation(Operator operator, Expression left, Expression right, Node.Location loc) {
        super(loc);
        this.operator = operator;
        this.leftOperand = left;
        this.rightOperand = right;
    }

    public Operator getOperator() {
        return operator;
    }

    public Expression getLeftOperand() {
        return leftOperand;
    }

    public Expression getRightOperand() {
        return rightOperand;
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
