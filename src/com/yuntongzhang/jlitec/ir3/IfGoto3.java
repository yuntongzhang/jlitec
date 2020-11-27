package com.yuntongzhang.jlitec.ir3;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IfGoto3 extends Stmt3 {
    private RelationExp3 condition;
    private Label3 label;

    public IfGoto3(RelationExp3 condition, Label3 label) {
        this.condition = condition;
        this.label = label;
    }

    public RelationExp3 getCondition() {
        return condition;
    }

    @Override
    public Set<Id3> getUseSet() {
        return new HashSet<>(condition.getAllVariables());
    }

    @Override
    public void replaceVars(Map<Id3, Id3> replacementScheme) {
        if (condition instanceof RelationOp3) {
            ((RelationOp3) condition).replaceVars(replacementScheme);
        }
        if (condition instanceof Id3) {
            this.condition = replacementScheme.get(condition);
        }
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
