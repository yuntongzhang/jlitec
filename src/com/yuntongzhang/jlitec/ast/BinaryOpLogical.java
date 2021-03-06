package com.yuntongzhang.jlitec.ast;

public class BinaryOpLogical extends BinaryOperation {
    public BinaryOpLogical(Operator operator, Expression left, Expression right, Node.Location loc) {
        super(operator, left, right, loc);
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
