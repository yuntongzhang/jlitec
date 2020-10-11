package com.yuntongzhang.jlitec.ir3;

public class AssignDeclare3 extends Stmt3 {
    private Type3 type;
    private Id3 lhs;
    private Exp3 rhs;

    public AssignDeclare3(Type3 type, Id3 lhs, Exp3 rhs) {
        this.type = type;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = (type.toString() + " " + lhs.toString() + " = " + rhs.toString() + ";")
                .indent(indentation);
        System.out.print(toPrint);
    }
}
