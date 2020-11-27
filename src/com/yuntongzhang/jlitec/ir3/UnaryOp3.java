package com.yuntongzhang.jlitec.ir3;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.yuntongzhang.jlitec.ast.Operator;

public class UnaryOp3 extends Exp3 {
    private Operator operator;
    private Idc3 operand;

    public UnaryOp3(Operator operator, Idc3 operand) {
        this.operator = operator;
        this.operand = operand;
    }

    public Operator getOperator() {
        return operator;
    }

    public Idc3 getOperand() {
        return operand;
    }

    public void replaceVars(Map<Id3, Id3> replacementScheme) {
        if (operand instanceof Id3 && replacementScheme.containsKey(operand)) {
            this.operand = replacementScheme.get(operand);
        }
    }

    @Override
    public boolean containsVar(Id3 id3) {
        return id3.equals(operand);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnaryOp3 unaryOp3 = (UnaryOp3) o;
        return operator == unaryOp3.operator &&
                operand.equals(unaryOp3.operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, operand);
    }

    @Override
    public Set<Id3> getAllVariables() {
        Set<Id3> result = new HashSet<>();
        if (operand instanceof Id3) {
            result.add((Id3) operand);
        }
        return result;
    }

    @Override
    public String toString() {
        return operator.toString() + operand.toString();
    }
}
