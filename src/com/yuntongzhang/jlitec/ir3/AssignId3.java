package com.yuntongzhang.jlitec.ir3;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.yuntongzhang.jlitec.analysis.AvailableExpAnalyzer;

public class AssignId3 extends Stmt3 {
    private Id3 lhs;
    private Exp3 rhs;

    public AssignId3(Id3 lhs, Exp3 rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Id3 getLhs() {
        return lhs;
    }

    public Exp3 getRhs() {
        return rhs;
    }

    public void setRhs(Exp3 exp) {
        this.rhs = exp;
    }

    @Override
    public Set<Id3> getDefSet() {
        Set<Id3> result = new HashSet<>();
        result.add(lhs);
        return result;
    }

    @Override
    public Set<Id3> getUseSet() {
        return new HashSet<>(rhs.getAllVariables());
    }

    @Override
    public void replaceVars(Map<Id3, Id3> replacementScheme) {
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
    public Set<AvailableExpAnalyzer.Assignment> getAssignment() {
        Set<AvailableExpAnalyzer.Assignment> result = new HashSet<>();
        if (!(rhs instanceof Id3) &&
                !(rhs instanceof BooleanLiteral3) &&
                !(rhs instanceof IntegerLiteral3) &&
                !(rhs instanceof StringLiteral3) &&
                !(rhs instanceof Access3)) {
            result.add(new AvailableExpAnalyzer.Assignment(lhs, rhs));
        }
        return result;
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = (lhs.toString() + " = " + rhs.toString() + ";").indent(indentation);
        System.out.print(toPrint);
    }
}
