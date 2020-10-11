package com.yuntongzhang.jlitec.ir3;

import com.yuntongzhang.jlitec.ast.Operator;

public class UnaryOp3 extends Exp3 {
    private Operator operator;
    private Idc3 operand;

    public UnaryOp3(Operator operator, Idc3 operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public String toString() {
        return operator.toString() + operand.toString();
    }
}
