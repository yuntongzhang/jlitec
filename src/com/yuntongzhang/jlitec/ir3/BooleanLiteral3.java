package com.yuntongzhang.jlitec.ir3;

import java.util.Objects;

import com.yuntongzhang.jlitec.ast.BooleanLiteral;

public class BooleanLiteral3 extends Idc3 {
    private boolean value;

    public BooleanLiteral3(BooleanLiteral booleanLiteral) {
        this.value = booleanLiteral.getValue();
    }

    public BooleanLiteral3(boolean b) {
        this.value = b;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooleanLiteral3 that = (BooleanLiteral3) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
