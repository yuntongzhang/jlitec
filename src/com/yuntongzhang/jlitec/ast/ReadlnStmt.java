package com.yuntongzhang.jlitec.ast;

public class ReadlnStmt extends Stmt {
    private Identifier arg;

    public ReadlnStmt(Identifier arg, Node.Location loc) {
        super(loc);
        this.arg = arg;
    }

    public Identifier getArg() {
        return arg;
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = ("Readln(" + arg.toString() + ");").indent(indentation);
        System.out.print(toPrint);
    }
}
