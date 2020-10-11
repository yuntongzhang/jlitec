package com.yuntongzhang.jlitec.ir3;

public class AssignId3 extends Stmt3 {
    private Id3 lhs;
    private Exp3 rhs;

    public AssignId3(Id3 lhs, Exp3 rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = (lhs.toString() + " = " + rhs.toString() + ";").indent(indentation);
        System.out.print(toPrint);
    }
}
