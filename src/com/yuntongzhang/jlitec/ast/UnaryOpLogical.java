package com.yuntongzhang.jlitec.ast;

public class UnaryOpLogical extends UnaryOperation {
    public UnaryOpLogical(Operator operator, Expression operand, Node.Location loc) {
        super(operator, operand, loc);
    }

    @Override
    public boolean checkOperand() {
        return (operand instanceof UnaryOpLogical ||
                operand instanceof BooleanLiteral ||
                operand instanceof Atom);
    }
}
