package com.yuntongzhang.jlitec.ast;

public class PrintlnStmt implements Stmt {
    private Expression arg;

    public PrintlnStmt(Expression arg) {
        this.arg = arg;
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = ("Println(" + arg.toString() + ");").indent(indentation);
        System.out.print(toPrint);
    }
}
