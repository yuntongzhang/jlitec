package com.yuntongzhang.jlitec.ast;

public class UnaryOpArithmetic extends UnaryOperation {
    public UnaryOpArithmetic(Operator operator, Expression operand) {
        super(operator, operand);
    }

    @Override
    public boolean checkOperand() {
        return (operand instanceof UnaryOpArithmetic ||
                operand instanceof IntegerLiteral ||
                operand instanceof Atom);
    }
}
