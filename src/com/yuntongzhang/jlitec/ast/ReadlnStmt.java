package com.yuntongzhang.jlitec.ast;

public class ReadlnStmt implements Stmt {
    private Identifier arg;

    public ReadlnStmt(Identifier arg) {
        this.arg = arg;
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = ("Readln(" + arg.toString() + ");").indent(indentation);
        System.out.print(toPrint);
    }
}
