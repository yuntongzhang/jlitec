package com.yuntongzhang.jlitec.ast;

public class BinaryOpLogical extends BinaryOperation {
    public BinaryOpLogical(Operator operator, Expression left, Expression right) {
        super(operator, left, right);
    }

    @Override
    boolean checkOneOperand(Expression operand) {
        return (operand instanceof BinaryOpLogical ||
                operand instanceof UnaryOpLogical ||
                operand instanceof BinaryOpComp ||
                operand instanceof BooleanLiteral ||
                operand instanceof Atom);
    }
}
