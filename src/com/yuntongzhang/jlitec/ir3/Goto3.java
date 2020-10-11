package com.yuntongzhang.jlitec.ir3;

public class Goto3 extends Stmt3 {
    private Label3 label;

    public Goto3(Label3 label) {
        this.label = label;
    }

    public Label3 getLabel() {
        return label;
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = ("goto " + label.getNumber() + ";").indent(indentation);
        System.out.print(toPrint);
    }
}
