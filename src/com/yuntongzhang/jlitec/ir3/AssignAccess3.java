package com.yuntongzhang.jlitec.ir3;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AssignAccess3 extends Stmt3 {
    private Id3 lhsLeft;
    private Id3 lhsRight;
    private Exp3 rhs;

    public AssignAccess3(Id3 lhsLeft, Id3 lhsRight, Exp3 rhs) {
        this.lhsLeft = lhsLeft;
        this.lhsRight = lhsRight;
        this.rhs = rhs;
    }

    public Id3 getLhsLeft() {
        return lhsLeft;
    }

    public Id3 getLhsRight() {
        return lhsRight;
    }

    public Exp3 getRhs() {
        return rhs;
    }

    @Override
    public Set<Id3> getUseSet() {
        Set<Id3> result = new HashSet<>();
        result.add(lhsLeft);
        // Note: lhsRight is not added since it is not a variable
        result.addAll(rhs.getAllVariables());
        return result;
    }

    @Override
    public void replaceVars(Map<Id3, Id3> replacementScheme) {
        // lhs
        if (replacementScheme.containsKey(lhsLeft)) this.lhsLeft = replacementScheme.get(lhsLeft);
        // rhs
        if (rhs instanceof Id3) {
            if (replacementScheme.containsKey(rhs)) this.rhs = replacementScheme.get(rhs);
        }
        if (rhs instanceof BinaryOp3) {
            ((BinaryOp3) rhs).replaceVars(replacementScheme);
        }
        if (rhs instanceof RelationOp3) {
            ((RelationOp3) rhs).replaceVars(replacementScheme);
        }
        if (rhs instanceof MethodCallExp3) {
            ((MethodCallExp3) rhs).replaceVars(replacementScheme);
        }
        if (rhs instanceof UnaryOp3) {
            ((UnaryOp3) rhs).replaceVars(replacementScheme);
        }
        if (rhs instanceof Access3) {
            ((Access3) rhs).replaceVars(replacementScheme);
        }
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = (lhsLeft.toString() + "." + lhsRight.toString() + " = " + rhs.toString() + ";")
                .indent(indentation);
        System.out.print(toPrint);
    }
}
