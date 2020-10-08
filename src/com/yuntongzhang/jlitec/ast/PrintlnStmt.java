package com.yuntongzhang.jlitec.ast;

public class PrintlnStmt extends Stmt {
    private Expression arg;

    public PrintlnStmt(Expression arg, Node.Location loc) {
        super(loc);
        this.arg = arg;
    }

    public Expression getArg() {
        return arg;
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = ("Println(" + arg.toString() + ");").indent(indentation);
        System.out.print(toPrint);
    }
}
