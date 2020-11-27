package com.yuntongzhang.jlitec.ir3;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.yuntongzhang.jlitec.ast.Operator;

/**
 * Relation operators (>, >=, ...) are also valid here.
 * Although they can also exist in RelationOp3
 */
public class BinaryOp3 extends Exp3 {
    private Operator operator;
    private Idc3 left;
    private Idc3 right;

    public BinaryOp3(Operator operator, Idc3 left, Idc3 right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
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
        BinaryOp3 binaryOp3 = (BinaryOp3) o;
        return operator == binaryOp3.operator &&
                left.equals(binaryOp3.left) &&
                right.equals(binaryOp3.right);
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
