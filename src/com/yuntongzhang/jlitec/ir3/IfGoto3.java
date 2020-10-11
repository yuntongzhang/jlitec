package com.yuntongzhang.jlitec.ir3;

public class IfGoto3 extends Stmt3 {
    private RelationExp3 condition;
    private Label3 label;

    public IfGoto3(RelationExp3 condition, Label3 label) {
        this.condition = condition;
        this.label = label;
    }

    public Label3 getLabel() {
        return label;
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = ("if (" + condition.toString() + ") goto " + label.getNumber() + ";")
                .indent(indentation);
        System.out.print(toPrint);
    }
}
