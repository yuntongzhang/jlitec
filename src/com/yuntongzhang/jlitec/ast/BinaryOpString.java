package com.yuntongzhang.jlitec.ast;

public class BinaryOpString extends BinaryOperation {
    public BinaryOpString(Operator operator, Expression left, Expression right, Node.Location loc) {
        super(operator, left, right, loc);
    }

    @Override
    boolean checkOneOperand(Expression operand) {
        return (operand instanceof BinaryOpString ||
                operand instanceof StringLiteral ||
                operand instanceof Atom);
    }
}
