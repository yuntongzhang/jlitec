package com.yuntongzhang.jlitec.ast;

public class UnaryOpArithmetic extends UnaryOperation {
    public UnaryOpArithmetic(Operator operator, Expression operand, Node.Location loc) {
        super(operator, operand, loc);
    }

    @Override
    public boolean checkOperand() {
        return (operand instanceof UnaryOpArithmetic ||
                operand instanceof IntegerLiteral ||
                operand instanceof Atom);
    }
}
