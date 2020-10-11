package com.yuntongzhang.jlitec.ir3;

import com.yuntongzhang.jlitec.ast.Operator;

/**
 * Relation operators (>, >=, ...) are also valid here.
 * Although they can also exist in RelationOp3
 */
public class BinaryOp3 extends Exp3 {
    private Operator operator;
    private Idc3 left;
    private Idc3 right;

    public BinaryOp3(Operator operator, Idc3 left, Idc3 right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return left.toString() + " " + operator.toString() + " " + right.toString();
    }
}
