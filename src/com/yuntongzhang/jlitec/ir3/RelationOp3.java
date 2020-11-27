package com.yuntongzhang.jlitec.ir3;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.yuntongzhang.jlitec.ast.Operator;

public class RelationOp3 extends RelationExp3 {
    private Operator operator;
    private Idc3 left;
    private Idc3 right;

    public RelationOp3(Operator operator, Idc3 left, Idc3 right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    // pre-condtion: operator of binaryOp3 must be relational
    public RelationOp3(BinaryOp3 binaryOp3) {
        this.operator = binaryOp3.getOperator();
        this.left = binaryOp3.getLeft();
        this.right = binaryOp3.getRight();
    }

    public Operator getOperator() {
        return operator;
    }

    public Idc3 getLeft() {
        return left;
    }

    public Idc3 getRight() {
        return right;
    }

    public void replaceVars(Map<Id3, Id3> replacementScheme) {
        if (left instanceof Id3 && replacementScheme.containsKey(left)) {
            this.left = replacementScheme.get(left);
        }
        if (right instanceof Id3 && replacementScheme.containsKey(right)) {
            this.right = replacementScheme.get(right);
        }
    }

    @Override
    public Set<Id3> getAllVariables() {
        Set<Id3> result = new HashSet<>();
        if (left instanceof Id3) {
            result.add((Id3) left);
        }
        if (right instanceof Id3) {
            result.add((Id3) right);
        }
        return result;
    }

    @Override
    public boolean containsVar(Id3 id3) {
        return id3.equals(left) || id3.equals(right);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationOp3 that = (RelationOp3) o;
        return operator == that.operator &&
                left.equals(that.left) &&
                right.equals(that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, left, right);
    }

    @Override
    public String toString() {
        return left.toString() + " " + operator.toString() + " " + right.toString();
    }
}
