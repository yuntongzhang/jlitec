package com.yuntongzhang.jlitec.ir3;

public class Println3 extends Stmt3 {
    private Idc3 idc3;

    public Println3(Idc3 idc3) {
        this.idc3 = idc3;
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = ("println(" +  idc3.toString() + ");").indent(indentation);
        System.out.print(toPrint);
    }
}
