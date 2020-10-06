package com.yuntongzhang.jlitec.ast;

public class BinaryOpComp extends BinaryOperation {
    public BinaryOpComp(Operator operator, Expression left, Expression right) {
        super(operator, left, right);
    }

    @Override
    boolean checkOneOperand(Expression operand) {
        return (operand instanceof BinaryOpArithmetic ||
                operand instanceof UnaryOpArithmetic ||
                operand instanceof IntegerLiteral ||
                operand instanceof Atom);
    }
}
