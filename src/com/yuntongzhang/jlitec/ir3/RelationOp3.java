package com.yuntongzhang.jlitec.ir3;

import com.yuntongzhang.jlitec.ast.Operator;

public class RelationOp3 extends RelationExp3 {
    private Operator operator;
    private Idc3 left;
    private Idc3 right;

    public RelationOp3(Operator operator, Idc3 left, Idc3 right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return left.toString() + " " + operator.toString() + " " + right.toString();
    }
}
