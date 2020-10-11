package com.yuntongzhang.jlitec.ast;

// + in this class also include the case where both operands are String type
// Thus, we treat String+String also as "Arithmetic"
// One problem is String+Int would be included
// Type checker would make sure the types of the operands are proper
public class BinaryOpArithmetic extends BinaryOperation {
    public BinaryOpArithmetic(Operator operator, Expression left, Expression right, Node.Location loc) {
        super(operator, left, right, loc);
    }

    @Override
    boolean checkOneOperand(Expression operand) {
        if (operator == Operator.PLUS) {
            return (operand instanceof StringLiteral ||
                    operand instanceof BinaryOpArithmetic ||
                    operand instanceof UnaryOpArithmetic ||
                    operand instanceof IntegerLiteral ||
                    operand instanceof Atom);
        } else {
            return (operand instanceof BinaryOpArithmetic ||
                    operand instanceof UnaryOpArithmetic ||
                    operand instanceof IntegerLiteral ||
                    operand instanceof Atom);
        }
    }
}
