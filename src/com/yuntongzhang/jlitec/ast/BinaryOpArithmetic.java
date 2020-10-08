package com.yuntongzhang.jlitec.ast;

public class BinaryOpArithmetic extends BinaryOperation {
    public BinaryOpArithmetic(Operator operator, Expression left, Expression right, Node.Location loc) {
        super(operator, left, right, loc);
    }

    @Override
    boolean checkOneOperand(Expression operand) {
        return (operand instanceof BinaryOpArithmetic ||
                operand instanceof UnaryOpArithmetic ||
                operand instanceof IntegerLiteral ||
                operand instanceof Atom);
    }
}
