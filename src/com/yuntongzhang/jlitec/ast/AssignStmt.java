package com.yuntongzhang.jlitec.ast;

public class AssignStmt extends Stmt {
    private Atom lhs;
    private Expression rhs;

    public AssignStmt(Atom lhs, Expression rhs, Node.Location loc) {
        super(loc);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Atom getLhs() {
        return lhs;
    }

    public Expression getRhs() {
        return rhs;
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = (lhs.toString() + "=" + rhs.toString() + ";").indent(indentation);
        System.out.print(toPrint);
    }
}
