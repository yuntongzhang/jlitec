package com.yuntongzhang.jlitec.ir3;

import com.yuntongzhang.jlitec.ast.ReadlnStmt;

public class Readln3 extends Stmt3 {
    private Id3 arg;

    public Readln3(ReadlnStmt readlnStmt) {
        this.arg = new Id3(readlnStmt.getArg());
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = ("readln(" + arg.toString() + ");").indent(indentation);
        System.out.print(toPrint);
    }
}
