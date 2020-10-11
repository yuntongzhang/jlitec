package com.yuntongzhang.jlitec.ir3;

public class AssignAccess3 extends Stmt3 {
    private Id3 lhsLeft;
    private Id3 lhsRight;
    private Exp3 rhs;

    public AssignAccess3(Id3 lhsLeft, Id3 lhsRight, Exp3 rhs) {
        this.lhsLeft = lhsLeft;
        this.lhsRight = lhsRight;
        this.rhs = rhs;
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = (lhsLeft.toString() + "." + lhsRight.toString() + " = " + rhs.toString() + ";")
                .indent(indentation);
        System.out.print(toPrint);
    }
}
