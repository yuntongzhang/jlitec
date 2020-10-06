package com.yuntongzhang.jlitec.ast;

public class AssignStmt implements Stmt {
    private Atom lhs;
    private Expression rhs;

    public AssignStmt(Atom lhs, Expression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = (lhs.toString() + "=" + rhs.toString() + ";").indent(indentation);
        System.out.print(toPrint);
    }
}
